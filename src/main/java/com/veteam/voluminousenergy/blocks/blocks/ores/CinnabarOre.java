package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class CinnabarOre extends VEOreBlock {
    public CinnabarOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(1.5f)
            .requiresCorrectToolForDrops()
            //.harvestLevel(Config.CINNABAR_HARVEST_LEVEL.get())
            //.harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("cinnabarore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}
