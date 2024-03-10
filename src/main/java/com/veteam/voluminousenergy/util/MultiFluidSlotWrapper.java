package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.blocks.tiles.tank.TankTile;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MultiFluidSlotWrapper implements IFluidHandler {

    List<RelationalTank> tanks = new ArrayList<>();
    VEFluidTileEntity tileEntity;

    public MultiFluidSlotWrapper(List<RelationalTank> tanks, VEFluidTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.tanks.addAll(tanks);
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        VoluminousEnergy.LOGGER.info("Call to getFluidInTank " + tank);

        if (tank >= tanks.size()) {
            return FluidStack.EMPTY;
        }

        RelationalTank fluidTank = tanks.get(tank);
        return fluidTank.getTank() == null ? FluidStack.EMPTY : fluidTank.getTank().getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        VoluminousEnergy.LOGGER.info("Call to getTankCapacity " + tank);
        if (tank < tanks.size()) {
            return 0;
        }
        RelationalTank fluidTank = tanks.get(tank);
        if (fluidTank.getTank() != null)
            VoluminousEnergy.LOGGER.info("Capacity: " + fluidTank.getTank().getCapacity());
        return fluidTank.getTank() == null ? 0 : fluidTank.getTank().getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        for(RelationalTank t : tanks) {
            if(t.getSlotNum() == tank) {
                if (t.isAllowAny()) return true;
                for (Recipe<?> recipe : tileEntity.getPotentialRecipes()) {
                    VEFluidRecipe veFluidRecipe = (VEFluidRecipe) recipe;
                    if (veFluidRecipe.getFluidIngredient(t.getRecipePos()).test(stack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for (RelationalTank tank : tanks) {
            if (tank.getTankType() == TankType.OUTPUT) continue;
            if (isFluidValid(tank.getSlotNum(), resource) && (tank.getTank().isEmpty() || resource.isFluidEqual(tank.getTank().getFluid()))) {
                if (!tank.getSideStatus() && !(tileEntity instanceof TankTile)) return 0;
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().fill(resource.copy(), action);
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        for (RelationalTank tank : tanks) {
            if (!tank.getSideStatus() && !tank.isIgnoreDirection()) continue;
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) continue;
            }
            if (resource.isFluidEqual(tank.getTank().getFluid())) {
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().drain(resource.copy(), action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (RelationalTank tank : tanks) {
            if (!tank.getSideStatus() && !(tileEntity instanceof TankTile)) continue;
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) continue;
            }
            if (tank.getTank().getFluidAmount() > 0) {
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().drain(maxDrain, action);
            }
        }
        return FluidStack.EMPTY;
    }

    public void addRelationalTank(RelationalTank tank) {
        tanks.add(tank);
    }

    public void removeRelationalTank(RelationalTank tank) {
        tanks.remove(tank);
    }
}