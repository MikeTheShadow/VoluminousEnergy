package com.veteam.voluminousenergy.items.upgrades;

import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class MysteriousMultiplier extends Item {
    public MysteriousMultiplier() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(1)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext pContext, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        Component componentToAdd = TextUtil.translateString("text.voluminousenergy.quality").copy().append(": ");

        float multiplier = stack.getOrDefault(VEDataComponents.MULTIPLIER_DATA,0.0f);

        if (multiplier == 0) {
            componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.unidentified")).withStyle(ChatFormatting.BOLD);
        } else {
            if (multiplier >= 0.65F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.basic")).withStyle(ChatFormatting.WHITE);
            } else if (multiplier >= 0.5F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.grand")).withStyle(ChatFormatting.GREEN);
            } else if (multiplier >= 0.4F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.rare")).withStyle(ChatFormatting.BLUE);
            } else if (multiplier >= 0.3F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.arcane")).withStyle(ChatFormatting.LIGHT_PURPLE);
            } else if (multiplier >= 0.25F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.heroic")).withStyle(ChatFormatting.YELLOW);
            } else if (multiplier >= 0.2F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.unique")).withStyle(Style.EMPTY.withColor(0xfc6b03));
            } else if (multiplier >= 0.1F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.celestial")).withStyle(ChatFormatting.RED);
            } else if (multiplier >= 0.075F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.divine")).withStyle(Style.EMPTY.withColor(0xcf7d5d).withBold(true));
            } else if (multiplier >= 0.05F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.epic")).withStyle(Style.EMPTY.withColor(0x8fa5ca).withBold(true));
            } else if (multiplier >= 0.025F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.legendary")).withStyle(Style.EMPTY.withColor(0xbf7900).withBold(true));
            } else if (multiplier <= 0.025F) {
                componentToAdd = componentToAdd.copy().append(TextUtil.translateString("text.voluminousenergy.quality.mythic")).withStyle(Style.EMPTY.withColor(0xc90b0b).withBold(true));
            } else {
                componentToAdd = componentToAdd.copy().append("DEBUGWARN: NoneOfTheAbove: " + multiplier);
            }
//            tooltip.add(Component.nullToEmpty("DEBUG_MULTIPLIER: " + multiplier));
        }

        tooltip.accept(componentToAdd);
        tooltip.accept(appendInfoForJEI(Component.nullToEmpty(""), stack));
    }

    public Component appendInfoForJEI(Component tooltip, ItemStack stack) {

        boolean isJei = stack.getOrDefault(VEDataComponents.IS_JEI,false);
        float value = stack.getOrDefault(VEDataComponents.MULTIPLIER_DATA,0.0f);

        if (isJei) {
            QualityTier tier = getQualityTier(value);
            tooltip = switch (tier) {
                case NULL -> tooltip.copy();
                case BASIC -> tooltip.copy().append("1x ~ 0.65x");
                case GRAND -> tooltip.copy().append("0.65x ~ 0.5x");
                case RARE -> tooltip.copy().append("0.5x ~ 0.4x");
                case ARCANE -> tooltip.copy().append("0.4x ~ 0.3x");
                case HEROIC -> tooltip.copy().append("0.3x ~ 0.25x");
                case UNIQUE -> tooltip.copy().append("0.25x ~ 0.2x");
                case CELESTIAL -> tooltip.copy().append("0.2x ~ 0.1x");
                case DIVINE -> tooltip.copy().append("0.1x ~ 0.075x");
                case EPIC -> tooltip.copy().append("0.075x ~ 0.05x");
                case LEGENDARY -> tooltip.copy().append("0.05x ~ 0.025x");
                case MYTHIC -> tooltip.copy().append("<0.025x");
            };
        }
        return tooltip;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, net.minecraft.server.level.ServerLevel level, @NotNull Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        if(stack.has(VEDataComponents.MULTIPLIER_DATA)) return;
        float multiplier = level.getRandom().nextFloat() * (0.75F - 0.005F) + 0.005F;
        stack.set(VEDataComponents.MULTIPLIER_DATA,multiplier);
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
    public static QualityTier getQualityTier(float multiplier) {
        if (multiplier >= 0.65F) {
            return QualityTier.BASIC;
        } else if (multiplier >= 0.5F) {
            return QualityTier.GRAND;
        } else if (multiplier >= 0.4F) {
            return QualityTier.RARE;
        } else if (multiplier >= 0.3F) {
            return QualityTier.ARCANE;
        } else if (multiplier >= 0.25F) {
            return QualityTier.HEROIC;
        } else if (multiplier >= 0.2F) {
            return QualityTier.UNIQUE;
        } else if (multiplier >= 0.1F) {
            return QualityTier.CELESTIAL;
        } else if (multiplier >= 0.075F) {
            return QualityTier.DIVINE;
        } else if (multiplier >= 0.05F) {
            return QualityTier.EPIC;
        } else if (multiplier >= 0.025F) {
            return QualityTier.LEGENDARY;
        } else if (multiplier <= 0.025F) {
            return QualityTier.MYTHIC;
        }
        return QualityTier.NULL;
    }

    public static final List<QualityTier> QUALITY_TIERS = List.of(
            MysteriousMultiplier.QualityTier.BASIC,
            MysteriousMultiplier.QualityTier.GRAND,
            MysteriousMultiplier.QualityTier.RARE,
            MysteriousMultiplier.QualityTier.ARCANE,
            MysteriousMultiplier.QualityTier.HEROIC,
            MysteriousMultiplier.QualityTier.UNIQUE,
            MysteriousMultiplier.QualityTier.CELESTIAL,
            MysteriousMultiplier.QualityTier.DIVINE,
            MysteriousMultiplier.QualityTier.EPIC,
            MysteriousMultiplier.QualityTier.LEGENDARY,
            MysteriousMultiplier.QualityTier.MYTHIC
    );

    public static final HashMap<QualityTier, Float> REFERENCE_MULTIPLIER_VALUES = new HashMap<>() {{
        put(QualityTier.BASIC, 0.66F);
        put(QualityTier.GRAND, 0.55F);
        put(QualityTier.RARE, 0.45F);
        put(QualityTier.ARCANE, 0.35F);
        put(QualityTier.HEROIC, 0.27F);
        put(QualityTier.UNIQUE, 0.21F);
        put(QualityTier.CELESTIAL, 0.15F);
        put(QualityTier.DIVINE, 0.08F);
        put(QualityTier.EPIC, 0.06F);
        put(QualityTier.LEGENDARY, 0.03F);
        put(QualityTier.MYTHIC, 0.005F);
    }};
}
