package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class EighzoOre extends VEOreBlock {
    public EighzoOre(){
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(30F, 1200F)
                .requiresCorrectToolForDrops()
                .harvestLevel(Config.BAUXITE_HARVEST_LEVEL.get()) // TODO: Config
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("eighzo_ore");
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return 0;
    }
}