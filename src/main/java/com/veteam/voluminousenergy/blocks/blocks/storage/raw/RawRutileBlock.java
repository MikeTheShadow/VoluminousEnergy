package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawRutileBlock extends Block {
    public RawRutileBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(4F)
                .requiresCorrectToolForDrops()
                //.harvestTool(ToolType.PICKAXE)
                //.harvestLevel(Config.RUTILE_HARVEST_LEVEL.get())
        );
        setRegistryName("raw_rutile_block");
    }
}
