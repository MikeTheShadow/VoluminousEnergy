package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

public class ToolingStationProcessor implements AbstractRecipeProcessor {

    @Nullable
    private static IFluidHandler getFluidHandler(ItemStack itemStack) {
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(itemStack).getCapability(Capabilities.Fluid.ITEM);
        return handler == null ? null : IFluidHandler.of(handler);
    }

    @Override
    public void tick(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStackInSlot(2);

        if (stack.getItem() instanceof Multitool) {
            VERelationalTank tank = tile.getRelationalTank(0);
            IFluidHandler fluidHandler = getFluidHandler(stack);

            int capacity = fluidHandler.getTankCapacity(0);
            int current = fluidHandler.getFluidInTank(0).getAmount();
            int remaining = capacity - current;
            if (remaining <= 0)
                return;
            FluidStack fluidStack = tank.getTank().drain(remaining, IFluidHandler.FluidAction.EXECUTE);
            if (fluidStack.isEmpty())
                return;
            fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new ToolingStationProcessor();
    }
}
