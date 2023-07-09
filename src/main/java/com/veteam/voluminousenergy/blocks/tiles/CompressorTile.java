package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CompressorContainer;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CompressorTile extends VETileEntity implements IVEPoweredTileEntity,IVECountable {

    public VESlotManager inputSlotManager = new VESlotManager(0,Direction.UP,true, SlotType.INPUT);
    public VESlotManager outputSlotManager = new VESlotManager(1, Direction.DOWN, true,SlotType.OUTPUT);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(inputSlotManager);
       add(outputSlotManager);
    }};

    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public CompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.COMPRESSOR_TILE.get(), pos, state,null);
    }

    @Deprecated
    public CompressorTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.COMPRESSOR_TILE.get(), pos, state,null);
    }

    private final ItemStackHandler inventory = createHandler();

    @Override
    public void tick(){
        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();

        CompressorRecipe recipe = level.getRecipeManager().getRecipeFor(CompressorRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty()){
            if (output.getCount() + recipe.getOutputAmount() < 64 && canConsumeEnergy()) {
                if (this.counter == 1){ //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.ingredientCount,false);

                    // Get output stack and RNG stack from the recipe
                    ItemStack newOutputStack = recipe.getResult().copy();

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem()) {
                        if (output.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                            output.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputAmount());
                        inventory.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                    }

                    this.counter--;
                    consumeEnergy();
                    setChanged();
                } else if (this.counter > 0){ //In progress
                    this.counter--;
                    consumeEnergy();
                    if(++sound_tick == 19) {
                        sound_tick = 0;
                        if (Config.PLAY_MACHINE_SOUNDS.get()) {
                            level.playSound(null, this.getBlockPos(), VESounds.COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                } else { // Check if we should start processing
                    if (output.isEmpty() || output.getItem() == recipe.getResult().getItem()){
                        this.counter = recipe.getProcessTime();
                        this.counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()));
                        this.length = this.counter;
                    } else {
                        this.counter = 0;
                    }
                }
            } else { // This is if we reach the maximum in the slots; or no power
                if (!canConsumeEnergy()){ // if no power
                    decrementSuperCounterOnNoPower();
                } else { // zero in other cases
                    counter = 0;
                }
            }
        } else { // This is if the input slot is empty
            this.counter = 0;
        }
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                CompressorRecipe recipe = level.getRecipeManager().getRecipeFor(CompressorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
                CompressorRecipe recipe1 = level.getRecipeManager().getRecipeFor(CompressorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.get().test(stack);
                } else if (slot == 1 && recipe1 != null){
                    return stack.getItem() == recipe1.result.getItem();
                } else if (slot == 2){
                    return TagUtil.isTaggedMachineUpgradeItem(stack);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                CompressorRecipe recipe = level.getRecipeManager().getRecipeFor(CompressorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
                CompressorRecipe recipe1 = level.getRecipeManager().getRecipeFor(CompressorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.get().getItems()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if (slot == 1 && recipe1 != null){
                    if (stack.getItem() == recipe1.result.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                } else if (slot == 2 && TagUtil.isTaggedMachineUpgradeItem(stack)){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity)
    {
        return new CompressorContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == inputSlotManager.getSlotNum()){
            inputSlotManager.setStatus(status);
        } else if (slotId == outputSlotManager.getSlotNum()){
            outputSlotManager.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == inputSlotManager.getSlotNum()){
            inputSlotManager.setDirection(direction);
        } else if (slotId == outputSlotManager.getSlotNum()){
            outputSlotManager.setDirection(direction);
        }
    }

    @Override
    public int getMaxPower() {
        return Config.COMPRESSOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.COMPRESSOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.COMPRESSOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 2;
    }
}
