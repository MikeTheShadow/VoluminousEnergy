package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class ToolingStationProcessor implements AbstractRecipeProcessor {

    @Override
    public void validateRecipe(VETileEntity tile) {

    }

    @Override
    public boolean processRecipe(VETileEntity tile) {

        ItemStack stack = tile.getInventory().getStackInSlot(2);

        if(stack.getItem() instanceof Multitool) {
            VERelationalTank tank = tile.getRelationalTank(0);
            IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

            int capacity = fluidHandler.getTankCapacity(0);
            int current = fluidHandler.getFluidInTank(0).getAmount();
            int remaining = capacity - current;

            if(remaining <= 0) return false;

            FluidStack fluidStack = tank.getTank().drain(remaining, IFluidHandler.FluidAction.EXECUTE);
            if(fluidStack.isEmpty()) return false;
            fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }

        return false;
    }

}
