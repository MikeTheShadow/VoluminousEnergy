package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class PrimitiveBlastFurnaceBlock extends FaceableBlock
{

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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> state) {
        state.add(FACING);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PrimitiveBlastFurnaceTile();
    }
}
