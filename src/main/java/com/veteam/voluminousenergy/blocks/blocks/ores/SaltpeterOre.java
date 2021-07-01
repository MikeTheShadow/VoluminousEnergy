package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;

import java.util.Random;

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
        return MathHelper.nextInt(rand, 1, 9);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.xpOnDrop(RANDOM)*(1+fortune) : 0;
    }
}
