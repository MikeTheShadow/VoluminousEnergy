package com.veteam.voluminousenergy.util.extensions;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.veteam.voluminousenergy.client.renderers.fluid.GasFluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import javax.annotation.Nullable;

public class VEFluidClientExtension implements IClientFluidTypeExtensions {
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    private final int colourTint;
    private final boolean ceilingFlush;

    public VEFluidClientExtension(ResourceLocation still, ResourceLocation flowing, @Nullable ResourceLocation overlay, int colourTint) {
        this(still, flowing, overlay, colourTint, false);
    }

    public VEFluidClientExtension(ResourceLocation still, ResourceLocation flowing, @Nullable ResourceLocation overlay, int colourTint, boolean ceilingFlush) {
        this.stillTexture = still;
        this.flowingTexture = flowing;
        this.overlayTexture = overlay;
        this.colourTint = colourTint;
        this.ceilingFlush = ceilingFlush;
    }

    @Override
    public boolean renderFluid(FluidState fluidState, BlockAndTintGetter getter, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState) {
        if (!ceilingFlush) {
            return false;
        }
        GasFluidRenderer.tesselate(getter, pos, vertexConsumer, blockState, fluidState);
        return true;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    @Nullable
    @Override
    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    @Override
    public int getTintColor() {
        return colourTint > 0 ? colourTint : IClientFluidTypeExtensions.super.getTintColor();
    }
}
