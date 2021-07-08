package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ToolUtil {
    
    public static void SolariumTooltipAppend(ItemStack stack, List<ITextComponent> tooltip){
        int bonus = 0;
        CompoundNBT tag = stack.getTag();

        if (tag == null) {
            bonus = Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get();
            stack.getOrCreateTag().putInt("bonus", bonus);
        } else {
            bonus = tag.getInt("bonus");
        }

        ITextComponent translatedComponent = TextUtil.translateString("text.voluminousenergy.protective_sheath");
        String translatedString = translatedComponent.getString();
        ITextComponent textComponent = new StringTextComponent(translatedString + ": " + bonus);
        tooltip.add(textComponent);
    }
    
}
