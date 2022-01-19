package com.veteam.voluminousenergy.util;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TextUtil {
    /*
    *   TranslationTextComponent name = new TranslationTextComponent(fluidName);
    *   ITextComponent textComponent = ITextComponent.getTextComponentOrEmpty(", " + amount + " mB / " + tankCapacity + " mB");
    *   return name.append(textComponent);
    */
    public static Component tankTooltip(String fluidName, int amount, int tankCapacity){
        return new TranslatableComponent(fluidName).append(Component.nullToEmpty(", " + amount + " mB / " + tankCapacity + " mB"));
    }

    public static Component slotName(String slotName){
        return new TranslatableComponent(slotName);
    }

    public static Component translateDirection(Direction direction){
        //return new TranslatableComponent("direction.voluminousenergy." + direction.name().toLowerCase());
        return new TranslatableComponent("direction.voluminousenergy." + directionToLocalDirection(direction));
    }

    public static String directionToLocalDirection(Direction direction) {
        switch (direction) {
            case UP -> {return "up";}
            case DOWN -> {return "down";}
            case NORTH -> {return "back";}
            case EAST -> {return "right";}
            case SOUTH -> {return "front";}
            default -> {return "left";}
        }
    }

    public static Component slotNameWithDirection(String slotName, Direction direction, int ordinal){
        Component translatedSlot = slotName(slotName);
        Component translatedDirection = translateDirection(direction);
        return new TextComponent(translatedSlot.getString() + " " + ordinal + " " +translatedDirection.getString());
    }

    public static Component translateString(String toTranslate){
        return new TranslatableComponent(toTranslate);
    }

    public static Component translateVEBlock(String block){
        return new TranslatableComponent("block.voluminousenergy." + block);
    }
}
