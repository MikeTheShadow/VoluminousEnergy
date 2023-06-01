package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.randoms.JavaRandomSource;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.Mth.abs;

public class CrusherTile extends VoluminousTileEntity implements IVEPoweredTileEntity,IVECountable {

    // Slot Managers
    public VESlotManager inputSlotProp = new VESlotManager(0,Direction.UP,true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"input_slot");
    public VESlotManager outputSlotProp = new VESlotManager(1,Direction.DOWN,true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_slot");
    public VESlotManager rngSlotProp = new VESlotManager(2, Direction.NORTH,true, "slot.voluminousenergy.rng_slot",SlotType.OUTPUT,"rng_slot");

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(inputSlotProp);
        add(outputSlotProp);
        add(rngSlotProp);
    }};

    // Sided Item Handlers
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public CrusherTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CRUSHER_TILE.get(), pos, state);
    }

    @Deprecated
    public CrusherTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.CRUSHER_TILE.get(), pos, state);
    }

    public ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            CrusherRecipe recipe = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
            CrusherRecipe recipe1 = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if (slot == 0 && recipe != null){
                return recipe.ingredient.get().test(stack);
            } else if (slot == 1 && recipe1 != null){
                return stack.getItem() == recipe1.result.getItem();
            } else if (slot == 2 && recipe1 != null){
                return stack.getItem() == recipe1.getRngItem().getItem();
            } else if (slot == 3){
                return TagUtil.isTaggedMachineUpgradeItem(stack);
            }
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            CrusherRecipe recipe = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
            CrusherRecipe recipe1 = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

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
            } else if (slot == 2 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItem().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 3){
                if(TagUtil.isTaggedMachineUpgradeItem(stack)){
                    return super.insertItem(slot, stack, simulate);
                }
            }
            return stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate){

            if(level != null && slot > 0 && !simulate){
                JavaRandomSource rand = new JavaRandomSource(new Random().nextLong());

                Optional<CrusherRecipe> crusherRecipe = RecipeUtil.getCrusherRecipeFromAnyOutputAndTryInput(inventory.getStackInSlot(slot).copy().getItem(), inventory.getStackInSlot(0).getItem(), level);
                if (crusherRecipe.isPresent()){
                    if (!(crusherRecipe.get().getMinExperience() == 0)){
                        level.addFreshEntity(new ExperienceOrb(
                                level,
                                worldPosition.getX(),
                                worldPosition.getY(),
                                worldPosition.getZ(),
                                amount*Mth.nextInt(
                                        rand,
                                        crusherRecipe.get().getMinExperience(),
                                        crusherRecipe.get().getMaxExperience()
                                )
                        ));

                    }
                }
            }
            
            return super.extractItem(slot,amount,simulate);
        }

        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);
            CrusherTile.this.setChanged();
        }
    };

    @Override
    public void tick(){

        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();
        ItemStack rng = inventory.getStackInSlot(2).copy();

        CrusherRecipe recipe = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty()){
            if (output.getCount() + recipe.getOutputAmount() < 64 && rng.getCount() + recipe.getOutputRngAmount() < 64 && canConsumeEnergy()) {
                if (counter == 1){ //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.ingredientCount,false);

                    // Get output stack from the recipe
                    ItemStack newOutputStack = recipe.getResult().copy();

                    //LOGGER.debug("output: " + output + " rng: " + rng + " newOutputStack: "  + newOutputStack);

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                        if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                            output.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputAmount());
                        inventory.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                    }

                    // Manipulating the RNG slot
                    if (recipe.getChance() != 0){ // If the chance is ZERO, this functionality won't be used
                        ItemStack newRngStack = recipe.getRngItem().copy();

                        // Generate Random floats
                        float random = abs(0 + level.getRandom().nextFloat() * (0 - 1));

                        // ONLY manipulate the slot if the random float is under or is identical to the chance float
                        if(random <= recipe.getChance()){
                            if (rng.getItem() != recipe.getRngItem().getItem()){
                                if (rng.getItem() == Items.AIR){
                                    rng.setCount(1);
                                }
                                newRngStack.setCount(recipe.getOutputRngAmount());
                                inventory.insertItem(2, newRngStack.copy(),false); // CRASH the game if this is not empty!
                            } else { // Assuming the recipe output item is already in the output slot
                                rng.setCount(recipe.getOutputRngAmount()); // Simply change the stack to equal the output amount
                                inventory.insertItem(2,rng.copy(),false); // Place the new output stack on top of the old one
                            }
                        }
                    }
                    counter--;
                    consumeEnergy();
                    setChanged();
                } else if (counter > 0){ //In progress
                    counter--;
                    consumeEnergy();
                    if(++sound_tick == 19) {
                        sound_tick = 0;
                        if (Config.PLAY_MACHINE_SOUNDS.get()) {
                            level.playSound(null, this.getBlockPos(), VESounds.CRUSHER, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                } else { // Check if we should start processing
                    if (output.isEmpty() && rng.isEmpty() || output.isEmpty() && rng.getItem() == recipe.getRngItem().getItem() || output.getItem() == recipe.getResult().getItem() && rng.getItem() == recipe.getRngItem().getItem() || output.getItem() == recipe.getResult().getItem() && rng.isEmpty()){
                        counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                        length = counter;
                    } else {
                        counter = 0;
                    }
                }
            } else { // This is if we reach the maximum in the slots; or no power
                if (!canConsumeEnergy()){ // if no power
                    decrementSuperCounterOnNoPower();
                } else { // zero in other cases
                    counter = 0;
                }
            }
        } else { // this is if the input slot is empty
            counter = 0;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CrusherContainer(i,level,worldPosition,playerInventory,playerEntity);
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

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    public int ticksLeft(){
        return counter;
    }

    @Override
    public int getMaxPower() {
        return Config.CRUSHER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.CRUSHER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.CRUSHER_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 3;
    }
}
