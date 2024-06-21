package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ToolUtil {

    public static void SolariumTooltipAppend(ItemStack stack, List<Component> tooltip) {
        int bonus;

        Integer bonusDurability = stack.get(VEDataComponents.SOLARIUM_DURABILITY_BONUS);

        if (bonusDurability == null) {
            bonus = Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get();
            stack.set(VEDataComponents.SOLARIUM_DURABILITY_BONUS,bonus);
        } else {
            bonus = bonusDurability;
        }

        Component translatedComponent = TextUtil.translateString("text.voluminousenergy.protective_sheath");
        String translatedString = translatedComponent.getString();
        Component textComponent = Component.nullToEmpty(translatedString + ": " + bonus);
        tooltip.add(textComponent);
    }

}
