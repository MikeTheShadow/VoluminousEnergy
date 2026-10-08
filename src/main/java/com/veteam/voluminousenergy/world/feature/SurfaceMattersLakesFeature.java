package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SurfaceMattersLakesFeature extends VELakesFeature {

    public SurfaceMattersLakesFeature(Codec<VELakesFeature.Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VELakesFeature.Configuration> context) {
        return context.level().canSeeSky(context.origin()) && super.place(context);
    }
}
