package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.datagen.VETagDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class SaltpeterOre extends FallingBlock {
    public SaltpeterOre(){
        super(Properties.of(Material.SAND)
            .sound(SoundType.SAND)
            .strength(0.6f)
            .requiresCorrectToolForDrops()
        );
        setRegistryName("saltpeterore");
        VETagDataGenerator.setRequiresShovel(this);
        VETagDataGenerator.setRequiresWood(this);
    }

    public int xpOnDrop(Random rand) {
        return Mth.nextInt(rand, 1, 9);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.xpOnDrop(RANDOM)*(1+fortune) : 0;
    }
}
