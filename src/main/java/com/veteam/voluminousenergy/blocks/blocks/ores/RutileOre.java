package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RutileOre extends Block {
    public RutileOre(){
        super(Properties.create(Material.ROCK)
            .sound(SoundType.STONE)
            .hardnessAndResistance(2.0f)
            .harvestLevel(Config.RUTILE_HARVEST_LEVEL.get())
            .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("rutileore");
    }
}
