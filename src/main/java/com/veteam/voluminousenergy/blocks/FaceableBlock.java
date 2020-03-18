package com.veteam.voluminousenergy.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public class FaceableBlock extends Block
{
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public FaceableBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
    }
}
