package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class SaltpeterOre extends Block {
    public SaltpeterOre(){
        super(Properties.create(Material.SAND)
            .sound(SoundType.SAND)
            .hardnessAndResistance(0.6f)
        );
        setRegistryName("saltpeterore");
    }
}
