package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class CinnabarOre extends Block {
    public CinnabarOre(){
        super(Properties.create(Material.ROCK)
            .sound(SoundType.STONE)
            .hardnessAndResistance(2.0f)
            .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("cinnabarore");
    }
}
