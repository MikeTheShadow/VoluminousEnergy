package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RFIDChip extends Item {

    public RFIDChip() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.create("ELECTRONIC", ChatFormatting.GREEN))
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> componentList, @NotNull TooltipFlag tooltipFlag) {
        CompoundTag tag = itemStack.getOrCreateTag();

        if (tag.contains("ve_x")) {

            int x = tag.getInt("ve_x");
            int z = tag.getInt("ve_z");
            ChunkFluid fluid = new ChunkFluid(tag);
            if(fluid == null) {
                componentList.add(TextUtil.translateString("text.voluminousenergy.rfid.chunk_data_error"));
            } else {
                componentList.add(Component.nullToEmpty(""));
                fluid.getFluids().forEach(f -> {
                    Component translatedComponent = TextUtil.translateString(f.getFluid().getFluidType().getDescriptionId());
                    String translatedString = translatedComponent.getString();
                    if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
                        Component textComponent = Component.nullToEmpty(ChatFormatting.DARK_PURPLE + translatedString + ": " + ChatFormatting.LIGHT_PURPLE + NumberUtil.numberToString4Fluids(f.getAmount()));
                        componentList.add(textComponent);
                    } else {
                        Component textComponent = Component.nullToEmpty(ChatFormatting.DARK_PURPLE + translatedString + ": " + ChatFormatting.LIGHT_PURPLE + NumberUtil.formatNumber(f.getAmount()) + " mB");
                        componentList.add(textComponent);
                    }


                });
            }


            componentList.add(
                    TextUtil.translateString("text.voluminousenergy.chunk").copy()
                            .append(" X: " + x + " | ")
                            .append(TextUtil.translateString("text.voluminousenergy.chunk").copy().append(" Z: " + z)));
        }
        super.appendHoverText(itemStack, level, componentList, tooltipFlag);
    }

}
