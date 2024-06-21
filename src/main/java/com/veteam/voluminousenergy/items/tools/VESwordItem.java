package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.ToolUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.Mth.abs;

public class VESwordItem extends SwordItem {
    public VESwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ToolUtil.SolariumTooltipAppend(stack, tooltip);
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        Integer solariumBonus = stack.get(VEDataComponents.SOLARIUM_DURABILITY_BONUS);

        if (solariumBonus == null) {
            stack.set(VEDataComponents.SOLARIUM_DURABILITY_BONUS, Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get());
            return;
        }

        if (solariumBonus > 0) {
            if (solariumBonus >= damage) {
                stack.set(VEDataComponents.SOLARIUM_DURABILITY_BONUS, (solariumBonus - damage));
            } else {
                int difference = damage - solariumBonus;
                stack.set(VEDataComponents.SOLARIUM_DURABILITY_BONUS, difference);
                super.setDamage(stack, difference);
            }
        } else {
            super.setDamage(stack, damage);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int num, boolean bool) {
        if (level.isClientSide() || !stack.has(VEDataComponents.SOLARIUM_DURABILITY_BONUS) ) return;

        int bonus = stack.get(VEDataComponents.SOLARIUM_DURABILITY_BONUS);

        if (level.canSeeSky(entity.getOnPos().above(2)) && bonus < Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get()) {

            float random = abs(0 + level.getRandom().nextFloat() * (0 - 1));
            if (random >= Config.SOLARIUM_SHEATH_REGENERATION_CHANCE.get().floatValue())
                return; // Inversed due to returning (not executing) if condition is true

            bonus++;
            stack.set(VEDataComponents.SOLARIUM_DURABILITY_BONUS, Math.min(bonus, Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get()));
        }
    }
}
