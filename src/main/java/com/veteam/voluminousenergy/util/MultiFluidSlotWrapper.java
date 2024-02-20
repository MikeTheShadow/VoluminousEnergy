package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.tank.TankTile;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class MultiFluidSlotWrapper implements IFluidHandler {

    HashMap<Integer, VERelationalTank> tankHashMap = new HashMap<>();
    List<VERelationalTank> tanks;
    VETileEntity tileEntity;

    public MultiFluidSlotWrapper(List<VERelationalTank> tanks, VETileEntity tileEntity) {
        this.tanks = tanks;
        this.tileEntity = tileEntity;
        tanks.forEach(m -> tankHashMap.put(m.getSlotNum(), m));
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tankHashMap.containsKey(tank)) {
            VERelationalTank fluidTank = tankHashMap.get(tank);
            return fluidTank.getTank() == null ? FluidStack.EMPTY : fluidTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tankHashMap.containsKey(tank)) {
            VERelationalTank fluidTank = tankHashMap.get(tank);
            return fluidTank.getTank() == null ? 0 : fluidTank.getTank().getCapacity();
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        VERelationalTank relationalTank = tankHashMap.get(tank);
        if(relationalTank.isAllowAny()) return true;
        for (Recipe<?> recipe : tileEntity.getPotentialRecipes()) {
            VEFluidRecipe veFluidRecipe = (VEFluidRecipe) recipe;
            if (veFluidRecipe.getFluidIngredient(relationalTank.getRecipePos()).test(stack)) {
                return true;
            }
        }
        return relationalTank.getTank().isFluidValid(stack);
    }


    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for(VERelationalTank tank : tanks) {
            if(tank.getTankType() == TankType.OUTPUT) continue;
            if (isFluidValid(tank.getSlotNum(), resource) && (tank.getTank().isEmpty() || resource.isFluidEqual(tank.getTank().getFluid()))) {
                if(!tank.getSideStatus() && !(tileEntity instanceof TankTile)) return 0;
                if(tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
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
        for(VERelationalTank tank : tanks) {
            if(!tank.getSideStatus() && !tank.isIgnoreDirection()) continue;
            if(!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if(tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) continue;
            }
            if (resource.isFluidEqual(tank.getTank().getFluid())) {
                if(tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().drain(resource.copy(), action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for(VERelationalTank tank : tanks) {
            if(!tank.getSideStatus() && !(tileEntity instanceof TankTile)) continue;
            if(!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if(tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) continue;
            }
            if (tank.getTank().getFluidAmount() > 0) {
                if(tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().drain(maxDrain, action);
            }
        }
        return FluidStack.EMPTY;
    }

    public void addRelationalTank(VERelationalTank tank) {
        tankHashMap.put(tank.getSlotNum(),tank);
        tanks.add(tank);
    }
    public void removeRelationalTank(VERelationalTank tank) {
        tankHashMap.remove(tank.getSlotNum());
        tanks.remove(tank);
    }
}