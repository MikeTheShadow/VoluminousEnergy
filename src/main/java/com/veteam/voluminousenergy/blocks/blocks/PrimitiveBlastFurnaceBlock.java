package com.veteam.voluminousenergy.blocks.blocks;


import com.veteam.voluminousenergy.blocks.tiles.PrimitiveBlastFurnaceTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class PrimitiveBlastFurnaceBlock extends FaceableBlock {

    public PrimitiveBlastFurnaceBlock() {
        super(Properties.of(Material.STONE)
                    .sound(SoundType.METAL)
                    .strength(2.0f)
                    .lightLevel(l -> 0)
                    .requiresCorrectToolForDrops()
                    .harvestLevel(1)
                    .harvestTool(ToolType.PICKAXE)
            );
        setRegistryName("primitiveblastfurnace");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PrimitiveBlastFurnaceTile();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Primitive Blast named container provider is missing!");
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }
}
