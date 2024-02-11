package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class SaltpeterOre extends ColoredFallingBlock {
    public SaltpeterOre(){
        super(new ColorRGBA(14406560),
            Properties.of()
            .sound(SoundType.SAND)
            .strength(0.6f)
            .requiresCorrectToolForDrops()
        );
        VETagDataGenerator.setRequiresShovel(this);
        VETagDataGenerator.setRequiresWood(this);
    }

    public int xpOnDrop(RandomSource randomSource) {
        return Mth.nextInt(randomSource, 1, 9);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.xpOnDrop(randomSource)*(1+fortune) : 0;
    }
}
