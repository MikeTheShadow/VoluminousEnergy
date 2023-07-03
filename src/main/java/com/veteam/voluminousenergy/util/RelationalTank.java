package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class RelationalTank {

    FluidTank tank;
    int id;

    int outputID;
    ItemStack input;
    ItemStack output;
    TankType tankType;
    List<Fluid> validFluids = new ArrayList<>();
    private boolean sideStatus = false;
    private Direction sideDirection = Direction.DOWN;
    private boolean allowAny = false;
    private boolean ignoreDirection = false;
    /**
     * nbtName follows the format TANKNAME:ENABLEDNAME
     * //TODO switch tank name to follow the ENABLEDNAME format of snake case and remove the need for tankname
     */
    private String nbt;

    public RelationalTank() {

    }

    public RelationalTank(FluidTank tank, int id, ItemStack input, ItemStack output, TankType tankType, String nbt) {
        this.tank = tank;
        this.id = id;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.nbt = nbt;
    }

    public RelationalTank(FluidTank tank, int id, ItemStack input, ItemStack output, TankType tankType, int outputID, String nbt) {
        this.tank = tank;
        this.id = id;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.outputID = outputID;
        this.nbt = nbt;
    }

    public void setIOItemstack(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    /**
     *
     * @param recipe The fluid recipe to pull the required data from
     * @param id The ID which is mapped to the output fluid id in the arraylist for the recipes outputfluids
     * @return true if the fluid can be inserted into the tank
     */
    public boolean canInsertOutputFluid(VEFluidRecipe recipe, int id) {
        return (this.getTank().isEmpty() || this.tank.getFluid().equals(recipe.getOutputFluids().get(id)))
                && this.getTank().getFluidAmount() + recipe.getOutputFluids().get(id).getAmount() <= this.tank.getCapacity();
    }

    /**
     *
     * @param recipe The fluid recipe to use
     * @param id The recipe ingredient ID to fill. Should be first checked with canInsertFluid separately
     *           especially if there are multiple outputs
     */
    public void fillOutput(VEFluidRecipe recipe, int id) {

        FluidStack stack = recipe.getOutputFluids().get(id);

        if (this.getTank().getFluid().getRawFluid() != stack.getRawFluid()) {
            this.getTank().setFluid(stack);
        } else {
            this.getTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    /**
     * The id will be located in the Recipe file itself.
     * To find the id the easiest way is to go to the fromJson in a recipe's serializer.
     * From there look at the fluidInputList being built and check for the position of
     * the fluid you're looking to subtract.
     * @param recipe The recipe to pull the input from
     * @param id The id of the input fluid
     */
    public void drainInput(VEFluidRecipe recipe, int id) {
        this.tank.drain(recipe.getInputFluids().get(id).getAmount(), IFluidHandler.FluidAction.EXECUTE);
    }

    public boolean isIgnoreDirection() {
        return ignoreDirection;
    }

    public void setIgnoreDirection(boolean ignoreDirection) {
        this.ignoreDirection = ignoreDirection;
    }

    public void setAllowAny(boolean allowAny) {
        this.allowAny = allowAny;
    }

    public boolean isAllowAny() {
        return allowAny;
    }

    public List<Fluid> getValidFluids() {
        return validFluids;
    }

    public void setValidFluids(List<Fluid> validFluids) {
        this.validFluids = validFluids;
    }

    public boolean isFluidValid(Fluid fluid) {
        if (this.allowAny) return true;
        return this.validFluids.contains(fluid);
    }

    public TankType getTankType() {
        return tankType;
    }

    public void setTankType(TankType tankType) {
        this.tankType = tankType;
    }

    public FluidTank getTank() {
        return tank;
    }

    public void setTank(FluidTank tank) {
        this.tank = tank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStack getInput() {
        return input;
    }

    public void setInput(ItemStack input) {
        this.input = input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    public int getOutputID() {
        return outputID;
    }

    public void setOutputID(int outputID) {
        this.outputID = outputID;
    }

    public boolean getSideStatus() {
        return sideStatus;
    }

    public void setSideStatus(boolean status) {
        sideStatus = status;
    }

    public Direction getSideDirection() {
        return sideDirection;
    }

    public void setSideDirection(Direction direction) {
        sideDirection = direction;
    }

    public String getTankName() {
        return nbt.split(":")[0];
    }

    public String getNBTPrefix() {
        return nbt.split(":")[1];
    }

    public String getTranslationKey() {
        if (tankType != null) {
            return switch (tankType) {
                case INPUT -> "tank.voluminousenergy.input_tank";
                case OUTPUT -> "tank.voluminousenergy.output_tank";
                case BOTH -> "tank.voluminousenergy.both_tank";
                default ->
                        throw new NotImplementedException("Warning! Tank type " + tankType + " does not have a valid key!");
            };
        }
        return "tank.voluminousenergy.null";
    }

    public void writeGuiProperties(CompoundTag nbt) {
        nbt.putBoolean(getNBTPrefix() + "_enabled", getSideStatus());
        nbt.putInt(getNBTPrefix() + "_direction", getSideDirection().get3DDataValue());
    }

    public void readGuiProperties(CompoundTag nbt) {
        setSideStatus(nbt.getBoolean(getNBTPrefix() + "_enabled"));
        int sideInt = nbt.getInt(getNBTPrefix() + "_direction");
        setSideDirection(IntToDirection.IntegerToDirection(sideInt));
    }

    public boolean isValidFluidsSet() {
        return allowAny || !this.validFluids.isEmpty();
    }

}
