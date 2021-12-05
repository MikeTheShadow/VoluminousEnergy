package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.bits.MultitoolBit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CombustionMultitool extends Multitool {

    public final int TANK_CAPACITY = VEFluidTileEntity.TANK_CAPACITY;

    public CombustionMultitool(MultitoolBit bit, String registryName, Properties itemProperties) {
        super(bit, registryName, itemProperties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
        if(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) return; // sanity check
        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluid -> {
            FluidStack fluidStack = fluid.getFluidInTank(0).copy();
            tooltip.add(
                    new TranslatableComponent(fluidStack.getTranslationKey())
                            .append(": "
                                    + fluidStack.getAmount()
                                    + " mB / "
                                    + this.TANK_CAPACITY
                                    + " mB"
                            )
            );
        });
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack){
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack){
        AtomicInteger fluidInTank = new AtomicInteger(0);
        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluid -> {
            FluidStack fluidStack = fluid.getFluidInTank(0).copy();
            fluidInTank.set(fluidStack.getAmount());
        });

        return (int)Math.round(13 * (fluidInTank.get() / (double)this.TANK_CAPACITY));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        AtomicReference<Float> ratio = new AtomicReference<>(0F);
        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluid -> {
            ratio.set(fluid.getFluidInTank(0).getAmount() / (float)this.TANK_CAPACITY);
        });
        return Mth.hsvToRgb(ratio.get() / 3.0F, 1.0F, 1.0F);
    }

    // This should initialize the FluidHandler and also allow one to get the fluidHandler from this item
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt){
        return new FluidHandlerItemStack(itemStack, this.TANK_CAPACITY);
    }
}
