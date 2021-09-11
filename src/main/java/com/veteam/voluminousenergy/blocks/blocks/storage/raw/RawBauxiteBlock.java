package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawBauxiteBlock extends Block {
    public RawBauxiteBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
                //.harvestTool(ToolType.PICKAXE)
                //.harvestLevel(Config.BAUXITE_HARVEST_LEVEL.get())
        );
        setRegistryName("raw_bauxite_block");
    }
}
