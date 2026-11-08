package com.veteam.voluminousenergy.util.extensions;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.veteam.voluminousenergy.client.renderers.fluid.FluidScreenOverlayRenderer;
import com.veteam.voluminousenergy.client.renderers.fluid.GaseousFluidRenderer;
import com.veteam.voluminousenergy.fluids.VEFlowingGasFluid;
import com.veteam.voluminousenergy.fluids.VEFluidPhysics;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector3f;

import javax.annotation.Nullable;

/**
 * Client rendering for a Voluminous Energy fluid type: gases drawn against the ceiling, and the fog and
 * screen effect chosen by the fluid's {@link VEFluidPhysics} preset when the camera is inside it.
 */
public class VEFluidClientExtension implements IClientFluidTypeExtensions {
    private static final float FOG_START = 0.0F;

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    private final int colourTint;
    private final VEFluidPhysics physics;
    private final int overlayColour;

    public VEFluidClientExtension(ResourceLocation still, ResourceLocation flowing, @Nullable ResourceLocation overlay, int colourTint, VEFluidPhysics physics, int overlayColour) {
        this.stillTexture = still;
        this.flowingTexture = flowing;
        this.overlayTexture = overlay;
        this.colourTint = colourTint;
        this.physics = physics;
        this.overlayColour = overlayColour;
    }

    @Override
    public boolean renderFluid(FluidState fluidState, BlockAndTintGetter getter, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState) {
        if (!(fluidState.getType() instanceof VEFlowingGasFluid)) {
            return false;
        }
        GaseousFluidRenderer.tesselate(getter, pos, vertexConsumer, blockState, fluidState);
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

    @Override
    public void renderOverlay(Minecraft minecraft, PoseStack poseStack) {
        switch (physics.overlayStyle()) {
            case TINT -> FluidScreenOverlayRenderer.renderTint(minecraft, poseStack, overlayColour, physics.overlayAlpha());
            case TEXTURE -> FluidScreenOverlayRenderer.renderTexture(minecraft, poseStack, overlayColour, physics.overlayAlpha());
            case NONE -> {
            }
        }
    }

    /**
     * Returns the overlay colour scaled by the light at the camera. NeoForge applies this after vanilla's
     * darkness and night vision adjustments, so the fluid would otherwise glow in an unlit cave.
     */
    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        if (camera.getEntity().isSpectator()) {
            return fluidFogColor;
        }
        float brightness = physics.fogBrightness() * FluidScreenOverlayRenderer.brightnessAt(level, camera.getBlockPosition());
        return new Vector3f(FluidScreenOverlayRenderer.red(overlayColour) * brightness,
                FluidScreenOverlayRenderer.green(overlayColour) * brightness,
                FluidScreenOverlayRenderer.blue(overlayColour) * brightness);
    }

    @Override
    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
        if (camera.getEntity().isSpectator()) {
            return;
        }
        RenderSystem.setShaderFogStart(FOG_START);
        RenderSystem.setShaderFogEnd(Math.min(physics.fogEnd(), farDistance));
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }
}
