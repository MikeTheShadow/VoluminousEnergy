package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class AluminumShellBlock extends Block {
    public AluminumShellBlock() {
        super(Properties.of(Material.METAL)
            .sound(SoundType.METAL)
            .strength(2.0f)
            .requiresCorrectToolForDrops()
            //.harvestTool(ToolType.PICKAXE)
            //.harvestLevel(1)
        );
        setRegistryName("aluminum_shell");
    }
}
