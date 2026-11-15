package com.veteam.voluminousenergy.util.extensions;

import com.veteam.voluminousenergy.client.renderers.fluid.FluidScreenOverlayRenderer;
import com.veteam.voluminousenergy.fluids.VEFluidPhysics;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector4f;

import javax.annotation.Nullable;

/**
 * Client rendering for a Voluminous Energy fluid type: the fog and screen effect chosen by the fluid's
 * {@link VEFluidPhysics} preset when the camera is inside it.
 */
public class VEFluidClientExtension implements IClientFluidTypeExtensions {
    private static final float FOG_START = 0.0F;

    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int colourTint;
    private final VEFluidPhysics physics;
    private final int overlayColour;

    public VEFluidClientExtension(Identifier still, Identifier flowing, @Nullable Identifier overlay, int colourTint, VEFluidPhysics physics, int overlayColour) {
        this.stillTexture = still;
        this.flowingTexture = flowing;
        this.overlayTexture = overlay;
        this.colourTint = colourTint;
        this.physics = physics;
        this.overlayColour = overlayColour;
    }

    public Identifier getStillTexture() {
        return stillTexture;
    }

    public Identifier getFlowingTexture() {
        return flowingTexture;
    }

    @Nullable
    @Override
    public Identifier getRenderOverlayTexture(Minecraft mc) {
        return overlayTexture;
    }

    @Nullable
    public Identifier getOverlayTexture() {
        return overlayTexture;
    }

    public int getTintColor() {
        return colourTint > 0 ? colourTint : 0xFFFFFFFF;
    }

    @Override
    public void extractOverlay(Minecraft minecraft, LocalPlayer player, PlayerRenderState playerRenderState, BlockPos eyePos, float brightness) {
        switch (physics.overlayStyle()) {
            case TINT -> playerRenderState.customFluidOverlayRenderer = FluidScreenOverlayRenderer.tint(overlayColour, physics.overlayAlpha(), brightness);
            case TEXTURE -> playerRenderState.customFluidOverlayRenderer = FluidScreenOverlayRenderer.texture(player, overlayColour, physics.overlayAlpha(), brightness);
            case NONE -> {
            }
        }
    }

    /**
     * Replaces the fog colour with the overlay colour scaled by the light at the camera. NeoForge applies
     * this after vanilla's darkness and night vision adjustments, so the fluid would otherwise glow in an
     * unlit cave.
     */
    @Override
    public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
        if (camera.entity().isSpectator()) {
            return;
        }
        float brightness = physics.fogBrightness() * FluidScreenOverlayRenderer.brightnessAt(level, camera.blockPosition());
        fluidFogColor.set(FluidScreenOverlayRenderer.red(overlayColour) * brightness,
                FluidScreenOverlayRenderer.green(overlayColour) * brightness,
                FluidScreenOverlayRenderer.blue(overlayColour) * brightness,
                fluidFogColor.w());
    }

    @Override
    public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
        if (camera.entity().isSpectator()) {
            return;
        }
        fogData.environmentalStart = FOG_START;
        fogData.environmentalEnd = Math.min(physics.fogEnd(), fogData.renderDistanceEnd);
        fogData.skyEnd = fogData.environmentalEnd;
        fogData.cloudEnd = fogData.environmentalEnd;
    }
}
