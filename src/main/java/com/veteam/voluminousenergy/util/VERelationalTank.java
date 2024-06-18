package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.fluids.AbstractFluidValidator;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class VERelationalTank {

    FluidTank tank;
    int slotNum;
    int id;
    ItemStack input;
    ItemStack output;
    TankType tankType;
    private boolean sideStatus = false;
    private Direction sideDirection = Direction.DOWN;
    private boolean allowAny = false;
    private boolean ignoreDirection = false;
    private int recipePos;
    private AbstractFluidValidator validator;

    /**
     * nbtName follows the format TANKNAME:ENABLEDNAME
     */
    private String nbt;

    public VERelationalTank() {

    }

    @Deprecated
    public VERelationalTank(FluidTank tank, int slotNum, ItemStack input, ItemStack output, TankType tankType, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.nbt = nbt;
    }

    @Deprecated
    public VERelationalTank(FluidTank tank, int slotNum, ItemStack input, ItemStack output, TankType tankType, int id, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.id = id;
        this.nbt = nbt;
    }

    public VERelationalTank(FluidTank tank, int slotNum, int recipePos, TankType tankType, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.tankType = tankType;
        this.recipePos = recipePos;
        this.nbt = nbt;
    }

    public VERelationalTank(FluidTank tank, int slotNum, int recipePos, TankType tankType, String nbt, AbstractFluidValidator validator) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.tankType = tankType;
        this.recipePos = recipePos;
        this.nbt = nbt;
        this.validator = validator;
    }

    public VERelationalTank(FluidTank tank, int slotNum, TankType tankType, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.tankType = tankType;
        this.nbt = nbt;
    }

    /**
     * @param recipe The fluid recipe to pull the required data from
     * @param id     The ID which is mapped to the output fluid id in the arraylist for the recipes outputfluids
     * @return true if the fluid can be inserted into the tank
     */
    public boolean canInsertOutputFluid(VERecipe recipe, int id) {
        return (this.getTank().isEmpty() || this.tank.getFluid().equals(recipe.getOutputFluids().get(id)))
                && this.getTank().getFluidAmount() + recipe.getOutputFluids().get(id).getAmount() <= this.tank.getCapacity();
    }

    public void fillTank(FluidStack stack) {
        this.getTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }

    public int testFillTank(FluidStack stack) {
        return this.getTank().fill(stack, IFluidHandler.FluidAction.SIMULATE);
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

    public TankType getTankType() {
        return tankType;
    }

    public FluidTank getTank() {
        return tank;
    }

    public void setTank(FluidTank tank) {
        this.tank = tank;
    }

    public int getSlotNum() {
        return slotNum;
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

    public int getRecipePos() {
        return recipePos;
    }

    public AbstractFluidValidator getValidator() {
        return validator;
    }

    public String getTranslationKey() {
        if (tankType != null) {
            return switch (tankType) {
                case INPUT -> "tank.voluminousenergy.input_tank";
                case OUTPUT -> "tank.voluminousenergy.output_tank";
                case BOTH -> "tank.voluminousenergy.both_tank";
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
}
