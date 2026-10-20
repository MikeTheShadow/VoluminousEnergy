package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class ToolingStationProcessor implements AbstractRecipeProcessor {

    @Override
    public void tick(VETileEntity tile) {
        if (tile.getInventory().getResource(2).getItem() instanceof Multitool) {
            VERelationalTank tank = tile.getRelationalTank(0);
            ResourceHandler<FluidResource> fluidHandler = VEItemCapabilities.getFluidHandler(tile.getInventory(), 2);
            if (fluidHandler == null)
                return;
            ResourceHandlerUtil.move(tank.getTank(), fluidHandler, fluid -> true, tank.getTank().getFluidAmount(), null);
        }
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new ToolingStationProcessor();
    }
}
