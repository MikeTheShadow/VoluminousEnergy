package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;

public class PrimitiveBlastFurnaceBlock extends FaceableBlock {

    public PrimitiveBlastFurnaceBlock() {
        super(Properties.create(Material.ROCK)
                    .sound(SoundType.STONE)
                    .hardnessAndResistance(2.0f)
                    .lightValue(0)
                    .harvestLevel(1)
            );
        setRegistryName("primitiveblastfurnace");
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(BlockStateProperties.FACING);
    }
}
