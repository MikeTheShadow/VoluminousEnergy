package com.veteam.voluminousenergy.world.surfaceBulider;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.ArrayList;

public class VESurfaceBuilders {
    public static ArrayList<SurfaceBuilder<?>> surfaceBuilders = new ArrayList<>();

    public static final SurfaceBuilder<SurfaceBuilderConfig> RED_DESERT = createSurfaceBuilder("red_desert", new RedDesertSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));

    public static void init(){}

    public static <SBC extends ISurfaceBuilderConfig, SB extends SurfaceBuilder<SBC>> SB createSurfaceBuilder(String id, SB surfaceBuilder) {
        ResourceLocation verl = new ResourceLocation(VoluminousEnergy.MODID, id);
        surfaceBuilder.setRegistryName(verl);
        surfaceBuilders.add(surfaceBuilder);
        return surfaceBuilder;
    }
}
