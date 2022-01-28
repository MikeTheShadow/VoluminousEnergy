package com.veteam.voluminousenergy.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
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

    public RelationalTank(FluidTank tank, int id, ItemStack input, ItemStack output,TankType tankType,String nbt) {
        this.tank = tank;
        this.id = id;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.nbt = nbt;
    }

    public RelationalTank(FluidTank tank, int id, ItemStack input, ItemStack output,TankType tankType,int outputID, String nbt) {
        this.tank = tank;
        this.id = id;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.outputID = outputID;
        this.nbt = nbt;
    }

    public void setIOItemstack(ItemStack input,ItemStack output) {
        this.input = input;
        this.output = output;
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

    public String getTankName() {
        return nbt.split(":")[0];
    }

    public String getNBTPrefix() {
        return nbt.split(":")[1];
    }

    public String getTranslationKey(){
        if(tankType != null){
            return switch (tankType) {
                case INPUT -> "tank.voluminousenergy.input_tank";
                case OUTPUT -> "tank.voluminousenergy.output_tank";
                case BOTH -> "tank.voluminousenergy.both_tank";
                default -> throw new NotImplementedException("Warning! Tank type " + tankType + " does not have a valid key!");
            };
        }
        return "tank.voluminousenergy.null";
    }

    public void writeGuiProperties(CompoundTag nbt){
        nbt.putBoolean(getNBTPrefix() + "_enabled", getSideStatus());
        nbt.putInt(getNBTPrefix() +"_direction", getSideDirection().get3DDataValue());
    }

    public void readGuiProperties(CompoundTag nbt){
        setSideStatus(nbt.getBoolean(getNBTPrefix()  + "_enabled"));
        int sideInt = nbt.getInt(getNBTPrefix()  + "_direction");
        setSideDirection(IntToDirection.IntegerToDirection(sideInt));
    }

    public boolean isValidFluidsSet(){
        return allowAny || !this.validFluids.isEmpty();
    }

}
