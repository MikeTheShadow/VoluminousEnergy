package com.veteam.voluminousenergy.items.upgrades;

import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MysteriousMultiplier extends Item {
    public MysteriousMultiplier() {
        super(new Item.Properties()
                .stacksTo(1)
                .tab(VESetup.itemGroup)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
        Component componentToAdd =  TextUtil.translateString("text.voluminousenergy.quality").copy().append(": ");

        if (stack.getTag() == null) {
            componentToAdd = componentToAdd.copy().append("NULL").withStyle(ChatFormatting.BOLD);
        } else {
            float multiplier = stack.getTag().getFloat("multiplier");

            if (multiplier >= 0.65F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.basic")).withStyle(ChatFormatting.WHITE);
            } else if (multiplier >= 0.5F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.grand")).withStyle(ChatFormatting.GREEN);
            } else if (multiplier >= 0.4F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.rare")).withStyle(ChatFormatting.BLUE);
            } else if (multiplier >= 0.3F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.arcane")).withStyle(ChatFormatting.LIGHT_PURPLE);
            } else if (multiplier >= 0.25F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.heroic")).withStyle(ChatFormatting.YELLOW);
            } else if (multiplier >= 0.2F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.unique")).withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(0xfc6b03));
            } else if (multiplier >= 0.1F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.celestial")).withStyle(ChatFormatting.RED);
            } else if (multiplier >= 0.075F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.divine")).withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(0xcf7d5d).withBold(true));
            } else if (multiplier >= 0.05F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.epic")).withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(0x8fa5ca).withBold(true));
            } else if (multiplier >= 0.025F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.legendary")).withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(0xbf7900).withBold(true));
            } else if (multiplier <= 0.025F){
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.mythic")).withStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(0xc90b0b).withBold(true));
            } else {
                componentToAdd = componentToAdd.copy().append("DEBUGWARN: NoneOfTheAbove: " + multiplier);
            }
//            tooltip.add(Component.nullToEmpty("DEBUG_MULTIPLIER: " + multiplier));
        }

        tooltip.add(componentToAdd);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int num, boolean bool) {
        if (stack.getTag() == null);
        else if (!stack.getTag().isEmpty()) return;

        float multiplier = level.getRandom().nextFloat() * (0.75F - 0.005F) + 0.005F;
        stack.getOrCreateTag().putFloat("multiplier", multiplier);
    }


    // If we use this outside of the Mysterious Multiplier, should be put in a Util class
    public enum QualityTier {
        NULL,
        BASIC,
        GRAND,
        RARE,
        ARCANE,
        HEROIC,
        UNIQUE,
        CELESTIAL,
        DIVINE,
        EPIC,
        LEGENDARY,
        MYTHIC
    }

    // Ugly, I know. You can fix this is you want
    public static QualityTier getQualityTier(float multiplier){
        if (multiplier >= 0.65F){           return QualityTier.BASIC;
        } else if (multiplier >= 0.5F){     return QualityTier.GRAND;
        } else if (multiplier >= 0.4F){     return QualityTier.RARE;
        } else if (multiplier >= 0.3F){     return QualityTier.ARCANE;
        } else if (multiplier >= 0.25F){    return QualityTier.HEROIC;
        } else if (multiplier >= 0.2F){     return QualityTier.UNIQUE;
        } else if (multiplier >= 0.1F){     return QualityTier.CELESTIAL;
        } else if (multiplier >= 0.075F){   return QualityTier.DIVINE;
        } else if (multiplier >= 0.05F){    return QualityTier.EPIC;
        } else if (multiplier >= 0.025F){   return QualityTier.LEGENDARY;
        } else if (multiplier <= 0.025F){   return QualityTier.MYTHIC;
        }
        return QualityTier.NULL;
    }

}
