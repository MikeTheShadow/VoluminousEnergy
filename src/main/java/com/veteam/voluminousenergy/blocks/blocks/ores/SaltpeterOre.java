package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class SaltpeterOre extends ColoredFallingBlock {
    public SaltpeterOre() {
        super(new ColorRGBA(14406560),
            Properties.of().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentBlockId())
                .sound(SoundType.SAND)
                .strength(0.6f)
                .requiresCorrectToolForDrops()
        );
        VETagDataGenerator.setRequiresShovel(this);
        VETagDataGenerator.setRequiresWoodAndBlacklistLowerTiers(this);
    }

    public SaltpeterOre(ColorRGBA colorRGBA, Properties properties) {
        super(colorRGBA, properties);
    }

    public int xpOnDrop(RandomSource randomSource) {
        return Mth.nextInt(randomSource, 1, 9);
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);

        RegistryAccess registries = level.registryAccess();

        int xpOnDrop = Mth.nextInt(level.getRandom(), 1, 5);
        Holder<Enchantment> fortune = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(fortune, tool);

        if (fortuneLevel > 0) {
            xpOnDrop = xpOnDrop * (1 + fortuneLevel);
        }
        this.popExperience(level, pos, xpOnDrop);

    }
}
