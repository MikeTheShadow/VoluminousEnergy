package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
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
import java.util.concurrent.atomic.AtomicReference;


public class PrimitiveBlastFurnaceTile extends VETileEntity implements IVECountable {

    public VESlotManager inputSm = new VESlotManager(0,Direction.UP,true, SlotType.INPUT);
    public VESlotManager outputSm = new VESlotManager(1, Direction.DOWN,true,SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(inputSm);
       add(outputSm);
    }};

    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public PrimitiveBlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE.get(), pos, state,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE);
    }

    @Deprecated
    public PrimitiveBlastFurnaceTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE.get(), pos, state,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE);
    }

    private ItemStackHandler inventory = createHandler();

    @Override
    public void tick() { //Tick method to run every tick
        updateClients();
        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();

        PrimitiveBlastFurnaceRecipe recipe = level.getRecipeManager().getRecipeFor(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty()){
            if (output.getCount() + recipe.getResultCount(0) < 64) {
                if (this.counter == 1){ //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.getIngredientCount(0),false);

                    // Get output stack and RNG stack from the recipe
                    ItemStack newOutputStack = recipe.getResult(0).copy();

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem()) {
                        if (output.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                            output.setCount(1);
                        }
                        inventory.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(newOutputStack.getCount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                    }
                    this.counter--;
                    setChanged();
                } else if (this.counter > 0){ //In progress
                    this.counter--;
                } else { // Check if we should start processing
                    if (output.isEmpty() || output.getItem() == recipe.getResult(0).getItem()){
                        this.counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(2).copy());
                        this.length = this.counter;
                    } else {
                        this.counter = 0;
                    }
                }
            } else { // This is if we reach the maximum in the slots
                this.counter = 0;
            }
        } else { // This is if the input slot is empty
            this.counter = 0;
        }
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                PrimitiveBlastFurnaceRecipe recipeOutput = (PrimitiveBlastFurnaceRecipe) RecipeCache.getRecipeFromCache(level,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE,inputItemStack.get().copy());
                PrimitiveBlastFurnaceRecipe recipe = (PrimitiveBlastFurnaceRecipe) RecipeCache.getRecipeFromCache(level,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE,referenceStack);

                if (slot == 0 && recipe != null){
                    return recipe.getIngredient(0).test(stack);
                } else if (slot == 1 && recipeOutput != null){
                    return stack.getItem() == recipeOutput.getResult(0).getItem();
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
                if(isItemValid(slot,stack)) return stack;
                return super.insertItem(slot,stack,simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new PrimitiveBlastFurnaceContainer(i,level,worldPosition,playerInventory,playerEntity);
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

    @Nullable
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return null;
    }

    public int getCounter(){ return counter; }

}