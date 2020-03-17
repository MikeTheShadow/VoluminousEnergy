package com.veteam.voluminousenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PrimitiveBlastFurnaceBlock extends Block {

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
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PrimitiveBlastFurnaceTile();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        if(entity != null) {
            world.setBlockState(pos,state.with(BlockStateProperties.FACING, getFacingFromEntity(pos, entity)),2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getFacingFromVector((float) (entity.posX - clickedBlock.getX()),(float) (entity.posY - clickedBlock.getY()),(float) (entity.posZ - clickedBlock.getZ()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(BlockStateProperties.FACING);
    }
}
