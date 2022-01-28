package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalSeparatorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CentrifugalSeparatorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.Mth.abs;

public class CentrifugalSeparatorTile extends VoluminousTileEntity implements IVEPoweredTileEntity,IVECountable {

    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory); // Main item handler

    public VESlotManager inputSm = new VESlotManager(0, Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT,"input_manager");
    public VESlotManager bucketSm = new VESlotManager(1,Direction.WEST,true,"slot.voluminousenergy.input_slot",SlotType.INPUT,"bucket_manager");
    public VESlotManager outputSm = new VESlotManager(2,Direction.DOWN,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_manager");
    public VESlotManager rngOneSm = new VESlotManager(3, Direction.NORTH, true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"rng_one_manager");
    public VESlotManager rngTwoSm = new VESlotManager(4,Direction.SOUTH,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"rng_two_manager");
    public VESlotManager rngThreeSm = new VESlotManager(5,Direction.EAST,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"rng_three_manager");

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(inputSm);
        add(bucketSm);
        add(outputSm);
        add(rngOneSm);
        add(rngTwoSm);
        add(rngThreeSm);
    }};

    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public CentrifugalSeparatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE, pos, state);
    }

    @Deprecated
    public CentrifugalSeparatorTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE, pos, state);
    }

    @Override
    public void tick(){

        updateClients();

        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy();
            ItemStack bucket = h.getStackInSlot(1).copy();
            ItemStack output = h.getStackInSlot(2).copy();
            ItemStack rngOne = h.getStackInSlot(3).copy();
            ItemStack rngTwo = h.getStackInSlot(4).copy();
            ItemStack rngThree = h.getStackInSlot(5).copy();

            CentrifugalSeparatorRecipe recipe = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
            inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

            if (usesBucket(recipe,bucket.copy())){
                if (!areSlotsFull(recipe,output.copy(),rngOne.copy(),rngTwo.copy(),rngThree.copy()) && canConsumeEnergy()) {
                    if (counter == 1){ //The processing is about to be complete
                        // Extract the inputted item
                        h.extractItem(0,recipe.ingredientCount,false);
                        // Extract bucket if it uses a bucket
                        if (recipe.needsBuckets() > 0){
                            h.extractItem(1,recipe.needsBuckets(),false);
                        }

                        // Get output stack from the recipe
                        ItemStack newOutputStack = recipe.getResult().copy();

                        // Manipulating the Output slot
                        if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                            if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                                output.setCount(1);
                            }
                            newOutputStack.setCount(recipe.getOutputAmount());
                            h.insertItem(2,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                        } else { // Assuming the recipe output item is already in the output slot
                            output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                            h.insertItem(2,output.copy(),false); // Place the new output stack on top of the old one
                        }

                        // Manipulating the RNG 0 slot
                        if (recipe.getChance0() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot0().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance0()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngOne.getItem() != recipe.getRngItemSlot0().getItem()){
                                    if (rngOne.getItem() == Items.AIR){
                                        rngOne.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount0());
                                    h.insertItem(3, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngOne.setCount(recipe.getOutputRngAmount0()); // Simply change the stack to equal the output amount
                                    h.insertItem(3,rngOne.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        // Manipulating the RNG 1 slot
                        if (recipe.getChance1() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot1().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance1()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngTwo.getItem() != recipe.getRngItemSlot1().getItem()){
                                    if (rngTwo.getItem() == Items.AIR){
                                        rngTwo.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount1());
                                    h.insertItem(4, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngTwo.setCount(recipe.getOutputRngAmount1()); // Simply change the stack to equal the output amount
                                    h.insertItem(4,rngTwo.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        // Manipulating the RNG 2 slot
                        if (recipe.getChance1() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot2().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance2()){
                                if (rngThree.getItem() != recipe.getRngItemSlot2().getItem()){
                                    if (rngThree.getItem() == Items.AIR){
                                        rngThree.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount2());
                                    h.insertItem(5, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngThree.setCount(recipe.getOutputRngAmount2()); // Simply change the stack to equal the output amount
                                    h.insertItem(5,rngThree.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        counter--;
                        consumeEnergy();
                        setChanged();
                    } else if (counter > 0){ //In progress
                        counter--;
                        consumeEnergy();
                    } else { // Check if we should start processing
                        if (areSlotsEmptyOrHaveCurrentItems(recipe,output,rngOne,rngTwo,rngThree)){
                            counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(6).copy());
                            length = counter;
                        } else {
                            counter = 0;
                        }
                    }
                } else { // This is if we reach the maximum in the slots
                    counter = 0;
                }
            } else { // this is if the input slot is empty
                counter = 0;
            }
        });
    }

    private boolean areSlotsFull(CentrifugalSeparatorRecipe recipe, ItemStack one, ItemStack two, ItemStack three, ItemStack four){

        if (one.getCount() + recipe.getOutputAmount() > one.getItem().getItemStackLimit(one.copy())){ // Main output slot
            return true;
        } else if (two.getCount() + recipe.getOutputRngAmount0() > two.getItem().getItemStackLimit(two.copy())){ // Rng Slot 0
            return true;
        } else if (three.getCount() + recipe.getOutputRngAmount1() > three.getItem().getItemStackLimit(three.copy())){ // Rng Slot 1
            return true;
        } else if (four.getCount() + recipe.getOutputRngAmount2() > four.getItem().getItemStackLimit(four.copy())){ // Rng Slot 2
            return true;
        } else {
            return false;
        }
    }

    private boolean usesBucket(CentrifugalSeparatorRecipe recipe, ItemStack bucket){
        if (recipe != null){ // If the recipe is null, don't bother processing
            if (recipe.needsBuckets() > 0){ // If it doesn't use a bucket, we know that it must have a valid recipe, return true
                if (!bucket.isEmpty() && bucket.getItem() == Items.BUCKET){
                    if(bucket.getCount() >= recipe.needsBuckets()) return true; // Needs a bucket, has enough buckets. Return true.
                    return false; // Needs a bucket, doesn't have enough buckets. Return false.
                } else {
                    return false; // Needs a bucket, doesn't have a bucket. Return false.
                }
            } else {
                return true; // Doesn't need a bucket, likely valid recipe. Return true.
            }
        }
        return false; // Likely empty slot, don't bother
    }

    private boolean areSlotsEmptyOrHaveCurrentItems(CentrifugalSeparatorRecipe recipe, ItemStack one, ItemStack two, ItemStack three, ItemStack four){
        ArrayList<ItemStack> outputList = new ArrayList<>();
        outputList.add(one.copy());
        outputList.add(two.copy());
        outputList.add(three.copy());
        outputList.add(four.copy());
        boolean isEmpty = true;
        boolean matchesRecipe = true;
        for (ItemStack x : outputList){
            if (!x.isEmpty()){
                //LOGGER.debug("Not Empty Slot!");
                isEmpty = false;
                if (one.getItem() != recipe.getResult().getItem() && one.getItem() != Items.AIR){
                    return false;
                } else if (two.getItem() != recipe.getRngItemSlot0().getItem() && two.getItem() != Items.AIR){
                    return false;
                } else if (three.getItem() != recipe.getRngItemSlot1().getItem() && three.getItem() != Items.AIR){
                    return false;
                } else if (four.getItem() != recipe.getRngItemSlot2().getItem() && four.getItem() != Items.AIR){
                    return false;
                } else {
                    return true;
                }
            }
        }
        return isEmpty;
    }

    public ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            //ItemStack referenceStack1 = inputItemStack.get().copy();
            //referenceStack1.setCount(64);
            CentrifugalSeparatorRecipe recipe = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
            CentrifugalSeparatorRecipe recipe1 = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if (slot == 0 && recipe != null){
                for (ItemStack testStack : recipe.ingredient.getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return true;
                    }
                }
            } else if (slot == 1 && stack.getItem() == Items.BUCKET){
                return true;
            } else if (slot == 2 && recipe1 != null){ // Output slot
                return stack.getItem() == recipe1.result.getItem();
            } else if (slot == 3 && recipe1 != null){ // RNG 0 slot
                return stack.getItem() == recipe1.getRngItemSlot0().getItem();
            } else if (slot == 4 && recipe1 != null){ // RNG 1 slot
                return stack.getItem() == recipe1.getRngItemSlot1().getItem();
            } else if (slot == 5 && recipe1 != null){ // RNG 2 slot
                return stack.getItem() == recipe1.getRngItemSlot2().getItem();
            } else if (slot == 6){
                return stack.getItem() == VEItems.QUARTZ_MULTIPLIER;
            }
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            CentrifugalSeparatorRecipe recipe = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack.copy()), level).orElse(null);
            CentrifugalSeparatorRecipe recipe1 = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if(slot == 0 && recipe != null) {
                for (ItemStack testStack : recipe.ingredient.getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            } else if ( slot == 1 && stack.getItem() == Items.BUCKET) {
                return super.insertItem(slot, stack, simulate);
            } else if (slot == 2 && recipe1 != null){
                if (stack.getItem() == recipe1.result.getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 3 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItemSlot0().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 4 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItemSlot1().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 5 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItemSlot2().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 6 && stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }
    };

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CentrifugalSeparatorContainer(i,level,worldPosition,playerInventory,playerEntity);
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

    @Override
    public int getMaxPower() {
        return Config.CENTRIFUGAL_SEPARATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.CENTRIFUGAL_SEPARATOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.CENTRIFUGAL_SEPARATOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 6;
    }
}