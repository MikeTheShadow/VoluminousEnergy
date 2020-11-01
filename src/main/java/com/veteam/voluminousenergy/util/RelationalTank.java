package com.veteam.voluminousenergy.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class RelationalTank {

    FluidTank tank;
    int id;

    int outputID;
    ItemStack input;
    ItemStack output;
    TankType tankType;

    public RelationalTank() {

    }

    public RelationalTank(FluidTank tank, int id, ItemStack input, ItemStack output,TankType tankType) {
        this.tank = tank;
        this.id = id;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
    }

    public RelationalTank(FluidTank tank, int id, ItemStack input, ItemStack output,TankType tankType,int outputID) {
        this.tank = tank;
        this.id = id;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.outputID = outputID;
    }

    public void setIOItemstack(ItemStack input,ItemStack output) {
        this.input = input;
        this.output = output;
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
}