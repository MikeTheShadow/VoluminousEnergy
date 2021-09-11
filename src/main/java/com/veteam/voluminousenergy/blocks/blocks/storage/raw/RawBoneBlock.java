package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class RawBoneBlock extends Block{
    public RawBoneBlock() {
        super(Block.Properties.of(Material.STONE, MaterialColor.SAND)
                .sound(SoundType.BONE_BLOCK)
                .strength(2F)
                .requiresCorrectToolForDrops()
                //.harvestTool(ToolType.PICKAXE)
                //.harvestLevel(1)
        );
        setRegistryName("raw_bone_block");
    }
}
