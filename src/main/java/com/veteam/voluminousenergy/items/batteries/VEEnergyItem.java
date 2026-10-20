package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class VEEnergyItem extends VEItem {
    private final int maxEnergy;
    private final int maxTransfer;

    @Deprecated
    public VEEnergyItem(Properties properties, int maxEnergy, int maxTransfer) {
        super(properties);
        this.maxEnergy = maxEnergy;
        this.maxTransfer = maxTransfer;
    }

    public static float getChargeRatio(ItemStack stack) {
        EnergyHandler storage = VEItemCapabilities.getEnergyHandler(stack);
        if (storage != null) {
            return (float) storage.getAmountAsInt() / storage.getCapacityAsInt();
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {

        EnergyHandler storage = VEItemCapabilities.getEnergyHandler(itemStack);
        if (storage == null) return;
        Component textComponent;
        if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
            textComponent = TextUtil.translateString("text.voluminousenergy.energy").copy().append(": " + NumberUtil.numberToString4FE(storage.getAmountAsInt()) + " / " + NumberUtil.numberToString4FE(storage.getCapacityAsInt()));
        } else {
            textComponent = TextUtil.translateString("text.voluminousenergy.energy").copy().append(": " + NumberUtil.formatNumber(storage.getAmountAsInt()) + " FE / " + NumberUtil.formatNumber(storage.getCapacityAsInt()) + " FE");
        }
        tooltip.accept(textComponent);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        //return Math.round(getChargeRatio(itemStack));
        return Math.round(13 * getChargeRatio(itemStack));
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = getChargeRatio(itemStack);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }
}
