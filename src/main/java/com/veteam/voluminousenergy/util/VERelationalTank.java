package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.fluids.AbstractFluidValidator;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class VERelationalTank implements VEIOPort {

    FluidTank tank;
    int slotNum;
    TankType tankType;
    private @Nullable Direction sideDirection = Direction.DOWN;
    private boolean allowAny = false;
    private int recipePos;
    private AbstractFluidValidator validator;

    /**
     * nbtName follows the format TANKNAME:ENABLEDNAME
     */
    private String nbt;

    public VERelationalTank() {

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
        return (this.getTank().isEmpty() || this.tank.getFluid().is(recipe.getOutputFluids().get(id).getFluid()))
                && this.getTank().getFluidAmount() + recipe.getOutputFluids().get(id).getAmount() <= this.tank.getCapacity();
    }

    public void fillTank(FluidStack stack) {
        this.getTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }

    public int testFillTank(FluidStack stack) {
        return this.getTank().fill(stack, IFluidHandler.FluidAction.SIMULATE);
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

    public @Nullable Direction getSideDirection() {
        return sideDirection;
    }

    public void setSideDirection(@Nullable Direction direction) {
        sideDirection = direction;
    }

    public String getTankName() {
        return nbt.split(":")[0];
    }

    public String getNBTPrefix() {
        return nbt.replace(":", "_");
    }

    public int getRecipePos() {
        return recipePos;
    }

    public AbstractFluidValidator getValidator() {
        return validator;
    }

    public String getHoverName() {
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
        nbt.putInt(getNBTPrefix() + "_direction", isAssigned() ? getSideDirection().get3DDataValue() : -1);
    }

    public void readGuiProperties(CompoundTag nbt) {
        int sideInt = nbt.getInt(getNBTPrefix() + "_direction");
        setSideDirection(sideInt == -1 ? null : IntToDirection.IntegerToDirection(sideInt));
    }

    @Override
    public boolean isFluid() {
        return true;
    }

    @Override
    public @Nullable Direction getDirection() {
        return getSideDirection();
    }

    @Override
    public void setDirection(@Nullable Direction direction) {
        setSideDirection(direction);
    }

    @Override
    public boolean canPush() {
        return tankType == TankType.OUTPUT || tankType == TankType.BOTH;
    }

    @Override
    public boolean canPull() {
        return tankType == TankType.INPUT || tankType == TankType.BOTH;
    }
}
