package com.veteam.voluminousenergy.client.renderers.fluid;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.CustomBlockScreenEffectRenderer;

/**
 * Draws the full-screen effect for a camera inside a Voluminous Energy fluid, either a flat colour tint
 * or the vanilla underwater texture in the fluid's colour. {@link ScreenEffectRenderer#submitFluid}
 * fixes its colour to grey, so it cannot colour the texture.
 */
public class FluidScreenOverlayRenderer {
    private static final Identifier UNDERWATER_TEXTURE = Identifier.withDefaultNamespace("textures/misc/underwater.png");
    private static final RenderType SCREEN_TINT = RenderType.create("voluminousenergy_screen_tint", RenderSetup.builder(RenderPipelines.GUI).createRenderSetup());
    private static final float TEXTURE_REPEAT = 4.0F;
    private static final float TEXTURE_SCROLL_RATE = 64.0F;
    private static final float QUAD_DEPTH = -0.5F;

    private FluidScreenOverlayRenderer() {
    }

    /**
     * Returns the light-dependent brightness at a position, as vanilla uses to darken underwater effects.
     */
    public static float brightnessAt(Level level, BlockPos pos) {
        return Lightmap.getBrightness(level.dimensionType(), level.getMaxLocalRawBrightness(pos));
    }

    /**
     * Returns a screen effect that draws a flat tint, captured from the colour and eye brightness at
     * extraction.
     */
    public static CustomBlockScreenEffectRenderer tint(int colour, float alpha, float brightness) {
        int vertexColour = shadedColour(colour, alpha, brightness);
        return (_, _, submitNodeCollector, poseStack, _, _, _) -> {
            submitNodeCollector.submitCustomGeometry(poseStack, SCREEN_TINT, (pose, buffer) -> {
                buffer.addVertex(pose, -1.0F, -1.0F, QUAD_DEPTH).setColor(vertexColour);
                buffer.addVertex(pose, 1.0F, -1.0F, QUAD_DEPTH).setColor(vertexColour);
                buffer.addVertex(pose, 1.0F, 1.0F, QUAD_DEPTH).setColor(vertexColour);
                buffer.addVertex(pose, -1.0F, 1.0F, QUAD_DEPTH).setColor(vertexColour);
            });
            return true;
        };
    }

    /**
     * Returns a screen effect that draws the underwater texture in the given colour, scrolled by the
     * player's view direction at extraction.
     */
    public static CustomBlockScreenEffectRenderer texture(LocalPlayer player, int colour, float alpha, float brightness) {
        int vertexColour = shadedColour(colour, alpha, brightness);
        float scrollU = -player.getYRot() / TEXTURE_SCROLL_RATE;
        float scrollV = player.getXRot() / TEXTURE_SCROLL_RATE;
        return (_, _, submitNodeCollector, poseStack, _, _, _) -> {
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.blockScreenEffect(UNDERWATER_TEXTURE), (pose, buffer) -> {
                buffer.addVertex(pose, -1.0F, -1.0F, QUAD_DEPTH).setUv(TEXTURE_REPEAT + scrollU, TEXTURE_REPEAT + scrollV).setColor(vertexColour);
                buffer.addVertex(pose, 1.0F, -1.0F, QUAD_DEPTH).setUv(scrollU, TEXTURE_REPEAT + scrollV).setColor(vertexColour);
                buffer.addVertex(pose, 1.0F, 1.0F, QUAD_DEPTH).setUv(scrollU, scrollV).setColor(vertexColour);
                buffer.addVertex(pose, -1.0F, 1.0F, QUAD_DEPTH).setUv(TEXTURE_REPEAT + scrollU, scrollV).setColor(vertexColour);
            });
            return true;
        };
    }

    public static float red(int colour) {
        return (colour >> 16 & 0xFF) / 255.0F;
    }

    public static float green(int colour) {
        return (colour >> 8 & 0xFF) / 255.0F;
    }

    public static float blue(int colour) {
        return (colour & 0xFF) / 255.0F;
    }

    private static int shadedColour(int colour, float alpha, float brightness) {
        return ARGB.colorFromFloat(alpha, red(colour) * brightness, green(colour) * brightness, blue(colour) * brightness);
    }
}
