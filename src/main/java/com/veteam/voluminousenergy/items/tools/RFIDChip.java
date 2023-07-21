package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.persistence.ChunkFluid;
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
            //componentList.add(new TextComponent(""));
            fluid.getFluids().forEach(f -> componentList.add(TextUtil.fluidNameAndAmountWithUnitsAndColours(f)));

            componentList.add(
                    TextUtil.translateString("text.voluminousenergy.chunk").copy()
                            .append(" X: " + x + " | ")
                            .append(TextUtil.translateString("text.voluminousenergy.chunk").copy().append(" Z: " + z)));
        }
        super.appendHoverText(itemStack, level, componentList, tooltipFlag);
    }

}
