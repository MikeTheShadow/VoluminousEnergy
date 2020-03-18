package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class PrimitiveBlastFurnaceBlock extends FaceableBlock {

    public PrimitiveBlastFurnaceBlock() {
        super(Properties.create(Material.ROCK)
                    .sound(SoundType.STONE)
                    .hardnessAndResistance(2.0f)
                    .lightValue(0)
                    .harvestLevel(1)
                    .harvestTool(ToolType.PICKAXE)
            );
        setRegistryName("primitiveblastfurnace");
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }
}
