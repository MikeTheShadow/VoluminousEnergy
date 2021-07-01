package com.veteam.voluminousenergy.blocks.blocks.materials;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SolariumBlock extends Block {
    public SolariumBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(25.0F, 1200.0F)
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(7)
        );
        setRegistryName("solarium_block");
    }
}