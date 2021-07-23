package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ToolType;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SaltpeterOre extends FallingBlock {
    public SaltpeterOre(){
        super(Properties.of(Material.SAND)
            .sound(SoundType.SAND)
            .strength(0.6f)
            .requiresCorrectToolForDrops()
            .harvestLevel(Config.SALTPETER_HARVEST_LEVEL.get())
            .harvestTool(ToolType.SHOVEL)
        );
        setRegistryName("saltpeterore");
    }

    public int xpOnDrop(Random rand) {
        return Mth.nextInt(rand, 1, 9);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.xpOnDrop(RANDOM)*(1+fortune) : 0;
    }
}
