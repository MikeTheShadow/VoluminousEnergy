package com.veteam.voluminousenergy.blocks.blocks.ores;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class GalenaOre extends Block {
    public GalenaOre(){
        super(Properties.create(Material.ROCK)
                .sound(SoundType.STONE)
                .lightValue(Config.GALENA_GLOW.get())
                .hardnessAndResistance(2.0f)
                .harvestLevel(Config.GALENA_HARVEST_LEVEL.get())
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("galena_ore");
    }
}
