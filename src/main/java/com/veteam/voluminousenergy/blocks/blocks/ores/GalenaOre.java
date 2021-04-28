package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class GalenaOre extends VEOreBlock {
    public GalenaOre(){
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .lightLevel(l -> Config.GALENA_GLOW.get())
                .requiresCorrectToolForDrops()
                .strength(2.0f)
                .harvestLevel(Config.GALENA_HARVEST_LEVEL.get())
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("galena_ore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
