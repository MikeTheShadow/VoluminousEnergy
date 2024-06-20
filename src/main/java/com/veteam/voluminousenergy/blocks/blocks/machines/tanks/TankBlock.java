package com.veteam.voluminousenergy.blocks.blocks.machines.tanks;

import com.veteam.voluminousenergy.blocks.blocks.machines.VEFaceableMachineBlock;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public abstract class TankBlock extends VEFaceableMachineBlock {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");

    public TankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext tooltipContext, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, tooltipContext, tooltip, flag);

        FluidStack fluid = stack.get(VEDataComponents.FLUID_STACK_DATA);

        if(fluid == null) {
            return;
        }

        int tankCapacity = this.getTankCapacity() * 1000;

        if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
            tooltip.add(
                    TextUtil.translateString(fluid.getHoverName().getString()).copy()
                            .append(": ")
                            .append(NumberUtil.numberToString4Fluids(fluid.getAmount()))
                            .append(" / ")
                            .append(NumberUtil.numberToString4Fluids(tankCapacity)
                            ));
        } else {
            String amount = String.format("%s mB", DECIMAL_FORMAT.format(fluid.getAmount()));
            String capacity = String.format("%s mB", DECIMAL_FORMAT.format(tankCapacity));
            tooltip.add(TextUtil.translateString(fluid.getHoverName().getString()).copy().append(": " + amount + " / " + capacity));
        }
    }

    public abstract int getTankCapacity();
}
