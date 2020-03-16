package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class PrimitiveBlastFurnaceBlock extends Block {

    public PrimitiveBlastFurnaceBlock()
    {
        super(Properties.create(Material.ROCK)
                    .sound(SoundType.STONE)
                    .hardnessAndResistance(2.0f)
                    .lightValue(0)
            );
        setRegistryName("primitiveblastfurnaceblock");
    }
}
