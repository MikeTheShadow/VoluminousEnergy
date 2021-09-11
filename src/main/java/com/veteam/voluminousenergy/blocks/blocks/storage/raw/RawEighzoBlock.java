package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RawEighzoBlock extends Block{
    public RawEighzoBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(30F, 1200F)
                .requiresCorrectToolForDrops()
                //.harvestTool(ToolType.PICKAXE)
                //.harvestLevel(Config.EIGHZO_HARVEST_LEVEL.get())
        );
        setRegistryName("raw_eighzo_block");
    }
}
