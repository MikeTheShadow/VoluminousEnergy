package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class RelationalTank {

    FluidTank tank;
    int slotNum;
    int id;
    ItemStack input;
    ItemStack output;
    TankType tankType;
    List<Fluid> validFluids = new ArrayList<>();
    private boolean sideStatus = false;
    private Direction sideDirection = Direction.DOWN;
    private boolean allowAny = false;
    private boolean ignoreDirection = false;
    private int recipePos;
    /**
     * nbtName follows the format TANKNAME:ENABLEDNAME
     * //TODO switch tank name to follow the ENABLEDNAME format of snake case and remove the need for tankname
     */
    private String nbt;

    public RelationalTank() {

    }

    @Deprecated
    public RelationalTank(FluidTank tank, int slotNum, ItemStack input, ItemStack output, TankType tankType, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.nbt = nbt;
    }

    @Deprecated
    public RelationalTank(FluidTank tank, int slotNum, ItemStack input, ItemStack output, TankType tankType, int id, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.id = id;
        this.nbt = nbt;
    }

    public RelationalTank(FluidTank tank, int slotNum,int recipePos, TankType tankType, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.input = input;
        this.output = output;
        this.tankType = tankType;
        this.recipePos = recipePos;
        this.nbt = nbt;
    }

    public RelationalTank(FluidTank tank, int slotNum, TankType tankType, String nbt) {
        this.tank = tank;
        this.slotNum = slotNum;
        this.tankType = tankType;
        this.nbt = nbt;
    }

    public void setIOItemstack(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    /**
     * @param recipe The fluid recipe to pull the required data from
     * @param id     The ID which is mapped to the output fluid id in the arraylist for the recipes outputfluids
     * @return true if the fluid can be inserted into the tank
     */
    public boolean canInsertOutputFluid(VEFluidRecipe recipe, int id) {
        return (this.getTank().isEmpty() || this.tank.getFluid().equals(recipe.getOutputFluids().get(id)))
                && this.getTank().getFluidAmount() + recipe.getOutputFluids().get(id).getAmount() <= this.tank.getCapacity();
    }

    public boolean addBucket(ItemStack item,VEFluidTileEntity tile) {

        if(item.getItem() instanceof BucketItem bucketItem) {
            Fluid fluid = bucketItem.getFluid();
            FluidStack stack = new FluidStack(fluid, 1000);

            if (this.getTank().getFluid().isEmpty()) {
                this.getTank().setFluid(stack);
                return true;
            }

            if (this.getTank().getFluidAmount() + 1000 <= this.tank.getCapacity()
                    && this.getTank().getFluid().isFluidEqual(stack)) {
                this.getTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

    /**
     * // TODO make sure that anything that calls this method properly marks the tiles recipe as dirty!
     * @param recipe The fluid recipe to use
     * @param id     The recipe ingredient ID to fill. Should be first checked with canInsertFluid separately
     *               especially if there are multiple outputs
     */
    @Deprecated()
    public void fillOutput(VEFluidRecipe recipe, int id) {

        FluidStack stack = recipe.getOutputFluid(id);

        if (this.getTank().getFluid().getRawFluid() != stack.getRawFluid()) {
            this.getTank().setFluid(stack);
        } else {
            this.getTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public void setValidator(VEFluidTileEntity tileEntity,boolean isInput) {
        this.tank.setValidator(t -> {
            for(Recipe<?> recipe : tileEntity.getPotentialRecipes()) {
                VEFluidRecipe veFluidRecipe = (VEFluidRecipe) recipe;
                if(isInput && veFluidRecipe.getFluidIngredient(this.getRecipePos()).test(t))  {
                    return true;
                } else if(veFluidRecipe.getOutputFluid(this.getRecipePos()).isFluidEqual(t)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * TODO make sure that anything that calls this method properly marks the tiles recipe as dirty!
     * The id will be located in the Recipe file itself.
     * To find the id the easiest way is to go to the fromJson in a recipe's serializer.
     * From there look at the fluidInputList being built and check for the position of
     * the fluid you're looking to subtract.
     *
     * @param recipe The recipe to pull the input from
     * @param id     The id of the input fluid
     */
    @Deprecated
    public void drainInput(VEFluidRecipe recipe, int id) {
        this.tank.drain(recipe.getFluidIngredients().get(id).getFluids()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
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

    public int getSlotNum() {
        return slotNum;
    }

    public void setSlotNum(int slotNum) {
        this.slotNum = slotNum;
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
