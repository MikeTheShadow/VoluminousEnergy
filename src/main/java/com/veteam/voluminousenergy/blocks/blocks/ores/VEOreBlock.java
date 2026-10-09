package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.blocks.blocks.VEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class VEOreBlock extends VEBlock {
    public VEOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected int xpOnDrop(RandomSource randomSource) {
        return Mth.nextInt(randomSource, 2, 5);
    }

    @Override
    protected void tryDropExperience(ServerLevel level, BlockPos pos, ItemStack heldItem, IntProvider amount) {
        RegistryAccess registries = level.registryAccess();

        Holder<Enchantment> silkTouch = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        if (EnchantmentHelper.getItemEnchantmentLevel(silkTouch, heldItem) > 0) {
            return;
        }
        int xpOnDrop = Mth.nextInt(level.getRandom(), 1, 5);
        Holder<Enchantment> fortune = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(fortune, heldItem);

        if (fortuneLevel > 0) {
            xpOnDrop = xpOnDrop * (1 + fortuneLevel);
        }
        super.tryDropExperience(level, pos, heldItem, ConstantInt.of(xpOnDrop));
    }
}
