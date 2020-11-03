package com.veteam.voluminousenergy.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TextUtil {
    /*
    *   TranslationTextComponent name = new TranslationTextComponent(fluidName);
    *   ITextComponent textComponent = ITextComponent.getTextComponentOrEmpty(", " + amount + " mB / " + tankCapacity + " mB");
    *   return name.append(textComponent);
    */
    public static ITextComponent tankTooltip(String fluidName, int amount, int tankCapacity){
        return new TranslationTextComponent(fluidName).append(ITextComponent.getTextComponentOrEmpty(", " + amount + " mB / " + tankCapacity + " mB"));
    }
}
