package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

public class TungstenSteelBlock extends Block {
    public TungstenSteelBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(3)
        );
        setRegistryName("tungsten_steel_block");
    }
}
