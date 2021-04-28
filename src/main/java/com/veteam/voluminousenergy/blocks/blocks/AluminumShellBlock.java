package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class AluminumShellBlock extends Block {
    public AluminumShellBlock() {
        super(Properties.of(Material.METAL)
            .sound(SoundType.METAL)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            .harvestTool(ToolType.PICKAXE)
            .harvestLevel(1)
        );
        setRegistryName("aluminum_shell");
    }
}
