package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class TitaniumMachineCasingBlock extends Block {
    public TitaniumMachineCasingBlock() {
        super(Block.Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .setRequiresTool()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
        );
        setRegistryName("titanium_machine_casing");
    }
}
