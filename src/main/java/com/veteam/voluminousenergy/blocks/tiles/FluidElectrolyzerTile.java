package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.FluidElectrolyzerContainer;
import com.veteam.voluminousenergy.recipe.FluidElectrolyzerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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

public class FluidElectrolyzerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"input_0_sm");
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"input_1_sm");
    public VESlotManager output0sm = new VESlotManager(2, Direction.EAST, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_0_sm");
    public VESlotManager output1sm = new VESlotManager(3, Direction.WEST, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_1_sm");
    public VESlotManager output2sm = new VESlotManager(4, Direction.NORTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_2_sm");
    public VESlotManager output3sm = new VESlotManager(5, Direction.SOUTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_3_sm");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(input0sm);
        add(input1sm);
        add(output0sm);
        add(output1sm);
        add(output2sm);
        add(output3sm);
    }};

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT,"inputTank:input_tank_gui");
    RelationalTank outputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY),1,null,null, TankType.OUTPUT,0,"outputTank0:output_tank_0_gui");
    RelationalTank outputTank1 = new RelationalTank(new FluidTank(TANK_CAPACITY),2,null,null, TankType.OUTPUT,1,"outputTank1:output_tank_1_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(inputTank);
        add(outputTank0);
        add(outputTank1);
    }};

    public FluidElectrolyzerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.FLUID_ELECTROLYZER_TILE.get(), pos, state);
        inputTank.setAllowAny(true);
        outputTank0.setAllowAny(true);
        outputTank1.setAllowAny(true);
    }

    public ItemStackHandler inventory = createHandler(7);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        ItemStack input0 = inventory.getStackInSlot(0).copy();
        ItemStack output0 = inventory.getStackInSlot(1).copy();
        ItemStack input1 = inventory.getStackInSlot(2).copy();
        ItemStack output1 = inventory.getStackInSlot(3).copy();
        ItemStack input2 = inventory.getStackInSlot(4).copy();
        ItemStack output2 = inventory.getStackInSlot(5).copy();

        inputTank.setInput(input0.copy());
        inputTank.setOutput(output0.copy());

        outputTank0.setInput(input1);
        outputTank0.setOutput(output1);

        outputTank1.setInput(input2);
        outputTank1.setOutput(output2);

        if(this.inputFluid(inputTank,0,1)) return;
        if(this.outputFluid(inputTank,0,1)) return;
        if(this.inputFluid(outputTank0,2,3)) return;
        if(this.outputFluid(outputTank0,2,3)) return;
        if(this.inputFluid(outputTank1, 4,5)) return;
        if(this.outputFluid(outputTank1,4,5)) return;
        // Main Fluid Processing occurs here
        if (inputTank != null) {

            VEFluidRecipe recipe = RecipeUtil.getFluidElectrolyzerRecipe(level,inputTank.getTank().getFluid().copy());
            if (recipe != null) {
                if (outputTank0 != null && outputTank1 != null) {

                    // Tank fluid amount check + tank cap checks
                    if (inputTank.getTank().getFluidAmount() >= recipe.getInputAmount()
                            && outputTank0.getTank().getFluidAmount() + recipe.getOutputAmount() <= TANK_CAPACITY
                            && outputTank1.getTank().getFluidAmount() + recipe.getFluids().get(1).getAmount() <= TANK_CAPACITY) {
                        // Check for power
                        if (canConsumeEnergy()) {
                            if (counter == 1) {

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // First Output Tank
                                if (outputTank0.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()) {
                                    outputTank0.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank0.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                // Second Output Tank
                                FluidElectrolyzerRecipe fluidElectrolyzerRecipe = (FluidElectrolyzerRecipe) recipe;
                                if (outputTank1.getTank().getFluid().getRawFluid() != fluidElectrolyzerRecipe.getSecondFluid().getRawFluid()) {
                                    outputTank1.getTank().setFluid(fluidElectrolyzerRecipe.getSecondFluid().copy());
                                } else {
                                    outputTank1.getTank().fill(fluidElectrolyzerRecipe.getSecondResult().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0) {
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(),inventory.getStackInSlot(this.getUpgradeSlotId()));
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
    }

    @Override
    public int getUpgradeSlotId() {
        return 6;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new FluidElectrolyzerContainer(i, level, worldPosition, playerInventory, playerEntity);
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

    // TODO abstract this to the fluid tile entity. This messes with the screen so be careful with that
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
        return TANK_CAPACITY;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
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
        return Config.FLUID_ELECTROLYZER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.FLUID_ELECTROLYZER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.FLUID_ELECTROLYZER_TRANSFER.get();
    }
}
