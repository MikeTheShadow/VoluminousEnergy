package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class ToolingStationProcessor implements AbstractRecipeProcessor {

    @Override
    public void tick(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStackInSlot(2);

        if (stack.getItem() instanceof Multitool) {
            VERelationalTank tank = tile.getRelationalTank(0);
            IFluidHandler fluidHandler = VEItemCapabilities.getFluidHandler(stack);

            int capacity = fluidHandler.getTankCapacity(0);
            int current = fluidHandler.getFluidInTank(0).getAmount();
            int remaining = capacity - current;
            if (remaining <= 0)
                return;
            FluidStack available = tank.getTank().drain(remaining, IFluidHandler.FluidAction.SIMULATE);
            if (available.isEmpty())
                return;
            int filled = fluidHandler.fill(available, IFluidHandler.FluidAction.EXECUTE);
            tank.getTank().drain(filled, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new ToolingStationProcessor();
    }
}
