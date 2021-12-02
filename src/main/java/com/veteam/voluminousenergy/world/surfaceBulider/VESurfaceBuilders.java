package com.veteam.voluminousenergy.world.surfaceBulider;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import java.util.ArrayList;

public class VESurfaceBuilders {
    public static ArrayList<SurfaceBuilder<?>> surfaceBuilders = new ArrayList<>();

    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> RED_DESERT = createSurfaceBuilder("red_desert", new RedDesertSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));

    public static void init(){}

    public static <SBC extends SurfaceBuilderConfiguration, SB extends SurfaceBuilder<SBC>> SB createSurfaceBuilder(String id, SB surfaceBuilder) {
        ResourceLocation verl = new ResourceLocation(VoluminousEnergy.MODID, id);
        surfaceBuilder.setRegistryName(verl);
        surfaceBuilders.add(surfaceBuilder);
        return surfaceBuilder;
    }
}
