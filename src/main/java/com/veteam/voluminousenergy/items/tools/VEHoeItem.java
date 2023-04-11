package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.ToolUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.Mth.abs;

public class VEHoeItem extends HoeItem {
    public VEHoeItem(Tier p_i231595_1_, int p_i231595_2_, float p_i231595_3_, Properties p_i231595_4_) {
        super(p_i231595_1_, p_i231595_2_, p_i231595_3_, p_i231595_4_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
        ToolUtil.SolariumTooltipAppend(stack, tooltip);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public void setDamage(ItemStack stack, int damage){
        CompoundTag tag = stack.getTag();

        if (tag == null){
            stack.getOrCreateTag().putInt("bonus", Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get());
            return;
        }

        if (tag.getInt("bonus") > 0){
            int bonus = tag.getInt("bonus");
            if (bonus >= damage){
                stack.getOrCreateTag().putInt("bonus", (bonus-damage));
            } else {
                int difference = damage - bonus;
                stack.getOrCreateTag().putInt("bonus", difference);
                super.setDamage(stack, difference);
            }
        } else {
            super.setDamage(stack, damage);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int num, boolean bool) {
        if (stack.getTag() == null || level.isClientSide()) return;

        int bonus = stack.getTag().getInt("bonus");

        if (level.canSeeSky(entity.getOnPos().above(2)) && bonus < Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get()) {

            float random = abs(0 + level.getRandom().nextFloat() * (0 - 1));
            if (random >= Config.SOLARIUM_SHEATH_REGENERATION_CHANCE.get().floatValue()) return; // Inversed due to returning (not executing) if condition is true

            bonus++;
            stack.getTag().putInt("bonus", Math.min(bonus,Config.SOLARIUM_PROTECTIVE_SHEATH_HITS.get()));
        }
    }
}
