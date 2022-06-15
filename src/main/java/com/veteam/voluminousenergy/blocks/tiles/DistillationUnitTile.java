package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DistillationUnitTile extends VEMultiBlockTileEntity implements IVEPoweredTileEntity,IVECountable {

    public VESlotManager iTopManager = new VESlotManager(0,Direction.UP,false,"slot.voluminousenergy.input_slot", SlotType.INPUT,"i_top_manager");
    public VESlotManager iBottomManager = new VESlotManager(1,Direction.DOWN,false,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"i_bottom_manager");
    public VESlotManager o0TopManager = new VESlotManager(2,Direction.UP,false,"slot.voluminousenergy.input_slot",SlotType.INPUT,"o_0_top_manager");
    public VESlotManager o0BottomManager = new VESlotManager(3,Direction.DOWN,false,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"o_0_top_manager");
    public VESlotManager o1TopManager = new VESlotManager(4,Direction.UP,false,"slot.voluminousenergy.input_slot",SlotType.INPUT,"0_1_top_manager");
    public VESlotManager o1BottomManager = new VESlotManager(5,Direction.DOWN,false,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"o_1_bottom_manager");
    public VESlotManager o2Manager = new VESlotManager(6,Direction.DOWN,false,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"o_2_manager");

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(iTopManager);
        add(iBottomManager);
        add(o0TopManager);
        add(o0BottomManager);
        add(o1TopManager);
        add(o1BottomManager);
        add(o2Manager);
    }};

    private final int tankCapacity = 4000;

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT,"inputTank:input_tank_gui");
    RelationalTank outputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY),1,null,null, TankType.OUTPUT,0,"outputTank0:output_tank_0_gui");
    RelationalTank outputTank1 = new RelationalTank(new FluidTank(TANK_CAPACITY), 2, null, null, TankType.OUTPUT, 1,"outputTank1:output_tank_1_gui");

    public List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(inputTank);
        add(outputTank0);
        add(outputTank1);
    }};

    private byte tick = 19;

    public ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    public DistillationUnitTile(BlockPos pos, BlockState state) {
        super(VEBlocks.DISTILLATION_UNIT_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!inputTank.isValidFluidsSet()) inputTank.setValidFluids(RecipeUtil.getDistillationInputFluids(level));
        if (!outputTank0.isValidFluidsSet()) outputTank0.setAllowAny(true);
        if (!outputTank1.isValidFluidsSet()) outputTank1.setAllowAny(true);

        updateClients();
        tick++;
        if (tick == 20){
            tick = 0;
            //validity = isMultiBlockValid(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK); TODO: UNDO Comment
        }
        if (!(validity)) {
            return;
        }
        ItemStack inputTop = inventory.getStackInSlot(0).copy();
        ItemStack inputBottom = inventory.getStackInSlot(1).copy();
        ItemStack firstOutputTop = inventory.getStackInSlot(2).copy();
        ItemStack firstOutputBottom = inventory.getStackInSlot(3).copy();
        ItemStack secondOutputTop = inventory.getStackInSlot(4).copy();
        ItemStack secondOutputBottom = inventory.getStackInSlot(5).copy();
        ItemStack thirdOutput = inventory.getStackInSlot(6).copy();

        inputTank.setIOItemstack(inputTop.copy(),inputBottom.copy());
        outputTank0.setIOItemstack(firstOutputTop.copy(),firstOutputBottom.copy());
        outputTank1.setIOItemstack(secondOutputTop.copy(),secondOutputBottom.copy());

        if(inputFluid(inputTank,0,1)) return;
        if(this.outputFluid(inputTank,0,1)) return;

        if(this.inputFluid(outputTank0,2,3)) return;
        if(this.outputFluid(outputTank0,2,3)) return;

        if(this.inputFluid(outputTank1,4,5)) return;
        if(this.outputFluid(outputTank1,4,5)) return;

        // Main Fluid Processing occurs here:
        if (inputTank != null || !inputTank.getTank().isEmpty()) {
            DistillationRecipe recipe = RecipeUtil.getDistillationRecipe(level, inputTank.getTank().getFluid());

            if (recipe != null) {
                if (outputTank0 != null && outputTank1 != null) {

                    // Tank fluid amount check + tank cap checks
                    if (thirdOutput.getCount() < recipe.getThirdResult().getMaxStackSize() && inputTank.getTank().getFluidAmount()
                            >= recipe.getInputAmount() && outputTank0.getTank().getFluidAmount()
                            + recipe.getOutputAmount() <= tankCapacity && outputTank1.getTank().getFluidAmount()
                            + recipe.getSecondFluid().getAmount() <= tankCapacity){
                        // Check for power
                        if (canConsumeEnergy()) {
                            if (counter == 1){

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // First Output Tank
                                if (outputTank0.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                    outputTank0.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank0.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                // Second Output Tank
                                if (outputTank1.getTank().getFluid().getRawFluid() != recipe.getSecondResult().getRawFluid()){
                                    outputTank1.getTank().setFluid(recipe.getSecondFluid().copy());
                                } else {
                                    outputTank1.getTank().fill(recipe.getSecondFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                if (thirdOutput.getItem() != recipe.getThirdResult().getItem()) {
                                    if (thirdOutput.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                                        thirdOutput.setCount(1);
                                    }
                                    inventory.insertItem(6, recipe.getThirdResult().copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    inventory.insertItem(6, recipe.getThirdResult().copy(),false); // Place the new output stack on top of the old one
                                }

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0){
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(7).copy());
                                length = counter;
                            }
                        } else { // Energy Check
                            decrementSuperCounterOnNoPower();
                        }
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                }
            }
        }

        // End of item handler

    }

    private IFluidHandler createInputFluidHandler() {
        return this.createFluidHandler(new DistillationRecipe(), inputTank);
    }

    private IFluidHandler createOutput0FluidHandler(){
        return this.createFluidHandler(new DistillationRecipe(), outputTank0);
    }

    private IFluidHandler createOutput1FluidHandler(){
        return this.createFluidHandler(new DistillationRecipe(), outputTank1);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(8) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(DistillationRecipe.RECIPE_TYPE,new SimpleContainer(stack),level).orElse(null);
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if (slot == 2 || slot == 3 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;

                    return RecipeUtil.getDistillationRecipeFromResult(level, new FluidStack(((BucketItem) stack.getItem()).getFluid(), 1000)) != null;
                } else if (slot == 4 || slot == 5 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;

                    return RecipeUtil.getDistillationRecipeFromSecondResult(level, new FluidStack(((BucketItem) stack.getItem()).getFluid(), 1000)) != null;
                } else if (slot == 6){
                    return RecipeUtil.getDistillationRecipeFromThirdResult(level, stack) != null;
                } else if (slot == 7){
                    return stack.getItem().equals(VEItems.QUARTZ_MULTIPLIER);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new DistillationUnitContainer(i, level, worldPosition, playerInventory, playerEntity);
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

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return inputTank.getTank().getFluid();
        } else if (num == 1){
            return outputTank0.getTank().getFluid();
        } else if (num == 2){
            return outputTank1.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return tankCapacity;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    public boolean getMultiblockValidity(){
        return validity;
    }

    public RelationalTank getInputTank(){
        return this.inputTank;
    }

    public RelationalTank getOutputTank0(){
        return this.outputTank0;
    }

    public RelationalTank getOutputTank1(){
        return this.outputTank1;
    }

    @Override
    public int getMaxPower() {
        return Config.DISTILLATION_UNIT_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.DISTILLATION_UNIT_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.DISTILLATION_UNIT_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}