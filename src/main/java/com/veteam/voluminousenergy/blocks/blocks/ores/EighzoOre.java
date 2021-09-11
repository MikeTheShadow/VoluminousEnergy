package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class EighzoOre extends VEOreBlock {
    public EighzoOre(){
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(30F, 1200F)
                .requiresCorrectToolForDrops()
                //.harvestLevel(Config.EIGHZO_HARVEST_LEVEL.get())
                //.harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("eighzo_ore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}