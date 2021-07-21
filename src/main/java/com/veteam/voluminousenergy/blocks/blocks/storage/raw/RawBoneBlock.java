package com.veteam.voluminousenergy.blocks.blocks.storage.raw;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class RawBoneBlock extends Block{
    public RawBoneBlock() {
        super(Block.Properties.of(Material.STONE, MaterialColor.SAND)
                .sound(SoundType.BONE_BLOCK)
                .strength(2F)
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
        );
        setRegistryName("raw_bone_block");
    }
}
