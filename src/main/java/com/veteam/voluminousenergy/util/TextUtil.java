package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

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
        return translateString(toTranslate).copy().withStyle(chatFormatting);
    }

    public static Component translateVEBlock(String block){
        return Component.translatable("block.voluminousenergy." + block);
    }

    public static Component fluidNameAndAmountWithUnitsAndColours(FluidStack fluidStack) {
        return fluidNameAndAmountWithUnitsAndColours(fluidStack.getTranslationKey(), fluidStack.getAmount());
    }

    public static Component fluidNameAndAmountWithUnitsAndColours(SingleChunkFluid singleChunkFluid) {
        return fluidNameAndAmountWithUnitsAndColours(singleChunkFluid.getFluid(), singleChunkFluid.getAmount());
    }

    public static Component fluidNameAndAmountWithUnitsAndColours(Fluid fluid, int amount) {
        return fluidNameAndAmountWithUnitsAndColours(new FluidStack(fluid, amount).getTranslationKey(), amount);
    }

    public static Component fluidNameAndAmountWithUnitsAndColours(String fluidTranslationKey, int amount) {
        String translateString = TextUtil.translateString(fluidTranslationKey).getString();

        if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
            return Component.nullToEmpty(""
                    + ChatFormatting.DARK_PURPLE
                    + translateString
                    + ": "
                    + ChatFormatting.LIGHT_PURPLE
                    + NumberUtil.numberToString4Fluids(((float)amount / (float)Config.DIMENSIONAL_LASER_PROCESS_TIME.get()))
                    + "/t"
            );
        } else {
            return Component.nullToEmpty(""
                    + ChatFormatting.DARK_PURPLE
                    + translateString
                    + ": "
                    + ChatFormatting.LIGHT_PURPLE
                    + NumberUtil.formatNumber(((float)amount / (float)Config.DIMENSIONAL_LASER_PROCESS_TIME.get()))
                    + " mB/t"
            );
        }
    }

    public static int computeTextRenderWidth(int imageWidth, Font font, Component component) {
        return imageWidth - 8 - font.width(component) - 2;
    }

    public static void renderShadowedText(GuiGraphics graphics, Font font, Component component, int x, int y, @Nullable Style styleOptional) {
        veRenderGuiText(graphics, font, component,  x, y, styleOptional, true);
    }

    public static void renderShadowedText(GuiGraphics graphics, Font font, String string, int x, int y, @Nullable Style styleOptional){
        renderShadowedText(graphics, font, Component.nullToEmpty(string), x, y, styleOptional);
    }

    public static void renderUnshadowedText(GuiGraphics graphics, Font font, Component component, int x, int y, @Nullable Style styleOptional) {
        veRenderGuiText(graphics, font, component, x, y, styleOptional, false);
    public static void renderCenteredShadowedText(GuiGraphics graphics, Font font, Component component, int x, int y, @Nullable Style styleOptional) {
        renderShadowedText(graphics, font, component, 0, x - font.width(component.getVisualOrderText()) / 2, y, styleOptional);
    }

    public static void renderCenteredShadowedText(GuiGraphics graphics, Font font, String string, int x, int y, @Nullable Style styleOptional) {
        Component component = Component.nullToEmpty(string);
        renderShadowedText(graphics, font, component, 0, x - font.width(component.getVisualOrderText()) / 2, y, styleOptional);
    }

    public static void renderUnshadowedText(GuiGraphics graphics, Font font, Component component, @Deprecated int imageWidth, int x, int y, @Nullable Style styleOptional) {
        veRenderGuiText(graphics, font, component, imageWidth, x, y, styleOptional, false);
    }

    public static void renderUnshadowedText(GuiGraphics graphics, Font font, String string, int x, int y, @Nullable Style styleOptional){
        renderUnshadowedText(graphics, font, Component.nullToEmpty(string), x, y, styleOptional);
    }

    private static void veRenderGuiText(GuiGraphics graphics, Font font, Component component, int x, int y, @Nullable Style styleOptional, boolean shouldShadow) {
    public static void renderCenteredUnshadowedText(GuiGraphics graphics, Font font, Component component, int x, int y, @Nullable Style styleOptional) {
        renderUnshadowedText(graphics, font, component, 0, x - font.width(component.getVisualOrderText()) / 2, y, styleOptional);
    }

    public static void renderCenteredUnshadowedText(GuiGraphics graphics, Font font, String string, int x, int y, @Nullable Style styleOptional) {
        Component component = Component.nullToEmpty(string);
        renderUnshadowedText(graphics, font, component, 0, x - font.width(component.getVisualOrderText()) / 2, y, styleOptional);
    }

    private static void veRenderGuiText(GuiGraphics graphics, Font font, Component component, @Deprecated int imageWidth, int x, int y, @Nullable Style styleOptional, boolean shouldShadow) {
        Component componentToRender = component.copy();
        if (styleOptional != null) {
            componentToRender = componentToRender.copy().withStyle(styleOptional);
        }

        graphics.drawString(font, componentToRender, x, y, 0x0, shouldShadow);
    }
}
