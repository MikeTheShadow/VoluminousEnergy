package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyItemStorage;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage != null) {
            return (float) storage.getEnergyStored() / storage.getMaxEnergyStored();
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

        IEnergyStorage storage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null) return;
        Component textComponent;
        if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
            textComponent = TextUtil.translateString("text.voluminousenergy.energy").copy().append(": " + NumberUtil.numberToString4FE(storage.getEnergyStored()) + " / " + NumberUtil.numberToString4FE(storage.getMaxEnergyStored()));
        } else {
            textComponent = TextUtil.translateString("text.voluminousenergy.energy").copy().append(": " + NumberUtil.formatNumber(storage.getEnergyStored()) + " FE / " + NumberUtil.formatNumber(storage.getMaxEnergyStored()) + " FE");
        }
        tooltip.add(textComponent);
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
