package com.veteam.voluminousenergy.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VESolarTile extends VoluminousTileEntity {

    public VESolarTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
    * Cosine curve based off the location of the Sun(? I think, at least it looks like that)
    * Noon is the Zenith, hence why we use a cosine curve, since cosine curves start at a max
    * amplitude, which of course is Noon/Zenith. We do manipulate the curve a bit to make it more "reasonable"
    */
    protected float solarIntensity(){
        if(level == null) return 0;
        float celestialAngle = this.level.getSunAngle(1.0f); // Zenith = 0rad

        if(celestialAngle > Math.PI) celestialAngle = (2 * ((float) Math.PI) - celestialAngle);

        float intensity = Mth.cos(0.2f + (celestialAngle / 1.2f));
        intensity = Mth.clamp(intensity, 0, 1);

        if(intensity > 0.1f) {
            intensity = intensity * 1.5f;
            if(intensity > 1f) intensity = 1f;
        }

        if(intensity > 0){
            if(this.level.isRaining()) return intensity * 0.6f;
            if(this.level.isThundering()) return intensity * 0.2f;
        }

        return intensity;
    }

    protected boolean isClear(){
        if (level == null) return false;
        return level.canSeeSky(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY()+1, this.getBlockPos().getZ()));
    }
}
