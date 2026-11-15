package com.veteam.voluminousenergy.client.renderers.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

/**
 * Draws the full-screen effect for a camera inside a Voluminous Energy fluid, either a flat colour tint
 * or the vanilla underwater texture in the fluid's colour. {@link ScreenEffectRenderer#renderFluid}
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

    public static void renderTint(Minecraft minecraft, PoseStack poseStack, MultiBufferSource buffers, int colour, float alpha) {
        int vertexColour = shadedColour(minecraft, colour, alpha);
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer buffer = buffers.getBuffer(SCREEN_TINT);
        buffer.addVertex(pose, -1.0F, -1.0F, QUAD_DEPTH).setColor(vertexColour);
        buffer.addVertex(pose, 1.0F, -1.0F, QUAD_DEPTH).setColor(vertexColour);
        buffer.addVertex(pose, 1.0F, 1.0F, QUAD_DEPTH).setColor(vertexColour);
        buffer.addVertex(pose, -1.0F, 1.0F, QUAD_DEPTH).setColor(vertexColour);
    }

    public static void renderTexture(Minecraft minecraft, PoseStack poseStack, MultiBufferSource buffers, int colour, float alpha) {
        int vertexColour = shadedColour(minecraft, colour, alpha);
        float scrollU = -minecraft.player.getYRot() / TEXTURE_SCROLL_RATE;
        float scrollV = minecraft.player.getXRot() / TEXTURE_SCROLL_RATE;
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer buffer = buffers.getBuffer(RenderTypes.blockScreenEffect(UNDERWATER_TEXTURE));
        buffer.addVertex(pose, -1.0F, -1.0F, QUAD_DEPTH).setUv(TEXTURE_REPEAT + scrollU, TEXTURE_REPEAT + scrollV).setColor(vertexColour);
        buffer.addVertex(pose, 1.0F, -1.0F, QUAD_DEPTH).setUv(scrollU, TEXTURE_REPEAT + scrollV).setColor(vertexColour);
        buffer.addVertex(pose, 1.0F, 1.0F, QUAD_DEPTH).setUv(scrollU, scrollV).setColor(vertexColour);
        buffer.addVertex(pose, -1.0F, 1.0F, QUAD_DEPTH).setUv(TEXTURE_REPEAT + scrollU, scrollV).setColor(vertexColour);
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

    private static int shadedColour(Minecraft minecraft, int colour, float alpha) {
        BlockPos eyePos = BlockPos.containing(minecraft.player.getX(), minecraft.player.getEyeY(), minecraft.player.getZ());
        float brightness = brightnessAt(minecraft.player.level(), eyePos);
        return ARGB.colorFromFloat(alpha, red(colour) * brightness, green(colour) * brightness, blue(colour) * brightness);
    }
}
