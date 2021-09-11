package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class BauxiteOre extends VEOreBlock {
    public BauxiteOre(){
        super(Properties.of(Material.STONE)
            .sound(SoundType.STONE)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            //.harvestLevel(Config.BAUXITE_HARVEST_LEVEL.get())
            //.harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("bauxiteore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }

}
