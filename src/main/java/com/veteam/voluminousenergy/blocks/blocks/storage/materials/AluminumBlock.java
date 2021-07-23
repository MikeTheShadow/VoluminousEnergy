package com.veteam.voluminousenergy.blocks.blocks.storage.materials;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

public class AluminumBlock extends Block {
    public AluminumBlock() {
        super(Block.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2F)
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(Config.BAUXITE_HARVEST_LEVEL.get())
        );
        setRegistryName("aluminum_block");
    }
}