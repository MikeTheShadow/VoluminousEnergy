package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class ToolUtil {
    
    public static void SolariumTooltipAppend(ItemStack stack, List<Component> tooltip){
        int bonus = 0;
        CompoundTag tag = stack.getTag();

        if (tag == null) {
            bonus = Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get();
            stack.getOrCreateTag().putInt("bonus", bonus);
        } else {
            bonus = tag.getInt("bonus");
        }

        Component translatedComponent = TextUtil.translateString("text.voluminousenergy.protective_sheath");
        String translatedString = translatedComponent.getString();
        Component textComponent = new TextComponent(translatedString + ": " + bonus);
        tooltip.add(textComponent);
    }
    
}
