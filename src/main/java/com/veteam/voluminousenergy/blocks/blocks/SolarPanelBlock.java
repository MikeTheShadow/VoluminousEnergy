package com.veteam.voluminousenergy.blocks.blocks;

import com.veteam.voluminousenergy.blocks.tiles.SolarPanelTile;
import com.veteam.voluminousenergy.tools.Config;
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

public class SolarPanelBlock extends FaceableBlock {
    public SolarPanelBlock() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .setLightLevel(l -> 0)
                .setRequiresTool()
                .harvestLevel(Config.SOLAR_PANEL_HARVEST_LEVEL.get())
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("solar_panel");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new SolarPanelTile();}

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
        if(!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Solar Panel named container provider is missing!");
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }
}
