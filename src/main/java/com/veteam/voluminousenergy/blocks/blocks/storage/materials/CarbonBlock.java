package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class CarbonBlock extends Block{
    public CarbonBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
                //.harvestTool(ToolType.PICKAXE)
                //.harvestLevel(0)
        );
        setRegistryName("carbon_block");
    }
}
