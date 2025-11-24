package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.function.Supplier;

public class MuiltiToolFluidHandler extends FluidHandlerItemStack {

    /**
     * @param componentType The data component type to use for data storage.
     * @param container     The container itemStack, data is stored on it directly under a component.
     */
    public MuiltiToolFluidHandler(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container) {
        super(componentType, container, VETileEntity.DEFAULT_TANK_CAPACITY); //TODO add a config option for capacity
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }
}
