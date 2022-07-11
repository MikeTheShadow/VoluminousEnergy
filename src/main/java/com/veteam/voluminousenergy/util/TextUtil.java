package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

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
        if (Config.SHORTEN_TANK_GUI_VALUES.get()){
            return TextUtil.translateString(fluidName).copy().append(": " + NumberUtil.numberToString4Fluids(amount) + " / " + NumberUtil.numberToString4Fluids(tankCapacity));
        }
        String stringAmount = String.valueOf(amount);
        String stringTankCapacity = String.valueOf(tankCapacity);

        // Assume mB units
        if (amount >= 1_000) {
            stringAmount = NumberUtil.formatNumber(stringAmount);
        }
        if (tankCapacity >= 1_000) {
            stringTankCapacity = NumberUtil.formatNumber(stringTankCapacity);
        }

        return Component.translatable(fluidName).append(Component.nullToEmpty(": " + stringAmount + " mB / " + stringTankCapacity + " mB"));
    }

    public static Component powerBarTooltip(VEEnergyStorage veEnergyStorage, int configuredMaxPower){
        return Config.SHORTEN_POWER_BAR_VALUES.get() ?
                Component.nullToEmpty(
                        NumberUtil.numberToString4FE(veEnergyStorage.getEnergyStored())
                                + " / " + NumberUtil.numberToString4FE(configuredMaxPower)
                )
                :
                Component.nullToEmpty(
                        NumberUtil.formatNumber(veEnergyStorage.getEnergyStored())
                                + " FE / " + NumberUtil.formatNumber(configuredMaxPower)
                                + " FE"
                );
    }

    public static Component slotName(String slotName){
        return Component.translatable(slotName);
    }

    public static Component translateDirection(Direction direction){
        //return new TranslatableComponent("direction.voluminousenergy." + direction.name().toLowerCase());
        return Component.translatable("direction.voluminousenergy." + directionToLocalDirection(direction));
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
        return Component.nullToEmpty(translatedSlot.getString() + " " + ordinal + " " +translatedDirection.getString());
    }

    public static Component translateString(String toTranslate){
        return Component.translatable(toTranslate);
    }

    public static Component translateString(ChatFormatting chatFormatting, String toTranslate){
        return Component.nullToEmpty(toTranslate).copy().withStyle(chatFormatting);
    }

    public static Component translateVEBlock(String block){
        return Component.translatable("block.voluminousenergy." + block);
    }
}
