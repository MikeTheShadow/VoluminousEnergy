package com.veteam.voluminousenergy.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class RelationalTank {

    FluidTank tank;
    int id;

    int outputID;
    ItemStack input;
    ItemStack output;
    TankType tankType;

    private boolean sideStatus = false;
    private Direction sideDirection = Direction.DOWN;

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

    public boolean getSideStatus(){
        return sideStatus;
    }

    public void setSideStatus(boolean status){
        sideStatus = status;
    }

    public Direction getSideDirection(){
        return sideDirection;
    }

    public void setSideDirection(Direction direction){
        sideDirection = direction;
    }

    public String getTranslationKey(){
        if(tankType != null){
            switch (tankType){
                case INPUT:
                    return "tank.voluminousenergy.input_tank";
                case OUTPUT:
                    return "tank.voluminousenergy.output_tank";
                default:
                    return "tank.voluminousenergy.invalid";
            }
        }
        return "tank.voluminousenergy.null";
    }

    public void writeGuiProperties(CompoundTag nbt, String prefix){
        nbt.putBoolean(prefix + "_enabled", getSideStatus());
        nbt.putInt(prefix+"_direction", getSideDirection().get3DDataValue());
    }

    public void readGuiProperties(CompoundTag nbt, String prefix){
        setSideStatus(nbt.getBoolean(prefix + "_enabled"));
        int sideInt = nbt.getInt(prefix + "_direction");
        setSideDirection(IntToDirection.IntegerToDirection(sideInt));
    }

}
