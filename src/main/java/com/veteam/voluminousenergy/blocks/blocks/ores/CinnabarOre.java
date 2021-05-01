package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class CinnabarOre extends VEOreBlock {
    public CinnabarOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            .harvestLevel(Config.CINNABAR_HARVEST_LEVEL.get())
            .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("cinnabarore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
