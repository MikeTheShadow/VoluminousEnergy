package com.veteam.voluminousenergy.util;

import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

    public static ITextComponent slotName(String slotName){
        return new TranslationTextComponent(slotName);
    }

    public static ITextComponent translateDirection(Direction direction){
        return new TranslationTextComponent("direction.voluminousenergy." + direction.name().toLowerCase());
    }

    public static ITextComponent slotNameWithDirection(String slotName, Direction direction, int ordinal){
        ITextComponent translatedSlot = slotName(slotName);
        ITextComponent translatedDirection = translateDirection(direction);
        return new StringTextComponent(translatedSlot.getString() + " " + ordinal + " " +translatedDirection.getString());
    }

    public static ITextComponent translateString(String toTranslate){
        return new TranslationTextComponent(toTranslate);
    }

    public static ITextComponent translateVEBlock(String block){
        return new TranslationTextComponent("block.voluminousenergy." + block);
    }
}
