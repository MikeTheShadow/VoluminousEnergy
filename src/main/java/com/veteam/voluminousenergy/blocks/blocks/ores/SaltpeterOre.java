package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SaltpeterOre extends FallingBlock {
    public SaltpeterOre(){
        super(Properties.create(Material.SAND)
            .sound(SoundType.SAND)
            .hardnessAndResistance(0.6f)
            .harvestLevel(0)
            .harvestTool(ToolType.SHOVEL)
        );
        setRegistryName("saltpeterore");
    }
}
