package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class PrimitiveStirlingGeneratorBlock extends FaceableBlock{

    public PrimitiveStirlingGeneratorBlock() {

        super(Properties.create(Material.ROCK)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2.0f)
                .lightValue(0)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
        );

        setRegistryName("primitivestirlinggenerator");
    }
}
