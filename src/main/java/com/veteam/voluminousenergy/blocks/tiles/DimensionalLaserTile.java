package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.client.renderers.VEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalLaserTile extends BlockEntity {

    public DimensionalLaserTile(BlockPos pos, BlockState state) {
        super(VEBlockEntities.DIMENSIONAL_LASER.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state, DimensionalLaserTile dimensionalLaserTile) {

//        float[] afloat = this.getBlockState().getBeaconColorMultiplier(level, pos, pos);
//
//        if(afloat != null) VoluminousEnergy.LOGGER.info(afloat.toString());
//        else VoluminousEnergy.LOGGER.info("Afloat null!");

//        TODO this is possibly for an achievement.
//        int i = this.getBlockPos().getX();
//        int j = this.getBlockPos().getY();
//        int k = this.getBlockPos().getZ();
//
//        for(ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, (new AABB(i, j, k, i, j - 4, k)).inflate(10.0D, 5.0D, 10.0D))) {
//            CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayer, 3);
//        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DimensionalLaserTile dimensionalLaserTile) {
        dimensionalLaserTile.tick(level,pos,state,dimensionalLaserTile);
    }


}
