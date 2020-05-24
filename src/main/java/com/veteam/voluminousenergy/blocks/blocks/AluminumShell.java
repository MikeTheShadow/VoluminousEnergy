package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class AluminumShell extends Block {
    public AluminumShell() {
        super(Properties.create(Material.IRON)
            .sound(SoundType.METAL)
            .hardnessAndResistance(2.0f)
            .harvestTool(ToolType.PICKAXE)
            .harvestLevel(1)
        );
        setRegistryName("aluminum_shell");
    }
}
