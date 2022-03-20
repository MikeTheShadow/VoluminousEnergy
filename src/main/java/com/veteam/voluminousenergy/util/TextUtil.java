package com.veteam.voluminousenergy.util;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TextUtil {

    // Slots
    public static Component TRANSLATED_INPUT_SLOT = TextUtil.translateString("slot.voluminousenergy.input_slot");
    public static Component TRANSLATED_OUTPUT_SLOT = TextUtil.translateString("slot.voluminousenergy.output_slot");
    public static Component TRANSLATED_RNG_SLOT = TextUtil.translateString("slot.voluminousenergy.rng_slot");
    public static Component TRANSLATED_BUCKET_SLOT = TextUtil.translateString("slot.voluminousenergy.bucket_slot");

    // Tanks
    public static Component TRANSLATED_INPUT_TANK = TextUtil.translateString("tank.voluminousenergy.input_tank");
    public static Component TRANSLATED_OUTPUT_TANK = TextUtil.translateString("tank.voluminousenergy.output_tank");
    public static Component TRANSLATED_BOTH_TANK = TextUtil.translateString("tank.voluminousenergy.both_tank");

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
