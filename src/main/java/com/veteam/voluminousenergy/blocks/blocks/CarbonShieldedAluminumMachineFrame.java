package com.veteam.voluminousenergy.blocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class CarbonShieldedAluminumMachineFrame extends Block {
    public CarbonShieldedAluminumMachineFrame() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
        );
        setRegistryName("carbon_shielded_aluminum_machine_frame");
    }
}