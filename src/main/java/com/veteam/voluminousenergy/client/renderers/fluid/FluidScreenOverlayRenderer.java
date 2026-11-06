package com.veteam.voluminousenergy.client.renderers.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * Draws the full-screen effect for a camera inside a Voluminous Energy fluid, either a flat colour tint
 * or the vanilla underwater texture in the fluid's colour. {@link ScreenEffectRenderer#renderFluid}
 * only scales its texture by light, so it cannot colour it.
 */
@OnlyIn(Dist.CLIENT)
public class FluidScreenOverlayRenderer {
    private static final ResourceLocation UNDERWATER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
    private static final float TEXTURE_REPEAT = 4.0F;
    private static final float TEXTURE_SCROLL_RATE = 64.0F;
    private static final float QUAD_DEPTH = -0.5F;

    private FluidScreenOverlayRenderer() {
    }

    /**
     * Returns the light-dependent brightness at a position, as vanilla uses to darken underwater effects.
     */
    public static float brightnessAt(Level level, BlockPos pos) {
        return LightTexture.getBrightness(level.dimensionType(), level.getMaxLocalRawBrightness(pos));
    }

    public static void renderTint(Minecraft minecraft, PoseStack poseStack, int colour, float alpha) {
        float brightness = eyeBrightness(minecraft);
        float red = red(colour) * brightness;
        float green = green(colour) * brightness;
        float blue = blue(colour) * brightness;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        Matrix4f pose = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(pose, -1.0F, -1.0F, QUAD_DEPTH).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, 1.0F, -1.0F, QUAD_DEPTH).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, 1.0F, 1.0F, QUAD_DEPTH).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, -1.0F, 1.0F, QUAD_DEPTH).setColor(red, green, blue, alpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void renderTexture(Minecraft minecraft, PoseStack poseStack, int colour, float alpha) {
        float brightness = eyeBrightness(minecraft);
        float scrollU = -minecraft.player.getYRot() / TEXTURE_SCROLL_RATE;
        float scrollV = minecraft.player.getXRot() / TEXTURE_SCROLL_RATE;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, UNDERWATER_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(red(colour) * brightness, green(colour) * brightness, blue(colour) * brightness, alpha);
        Matrix4f pose = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(pose, -1.0F, -1.0F, QUAD_DEPTH).setUv(TEXTURE_REPEAT + scrollU, TEXTURE_REPEAT + scrollV);
        buffer.addVertex(pose, 1.0F, -1.0F, QUAD_DEPTH).setUv(scrollU, TEXTURE_REPEAT + scrollV);
        buffer.addVertex(pose, 1.0F, 1.0F, QUAD_DEPTH).setUv(scrollU, scrollV);
        buffer.addVertex(pose, -1.0F, 1.0F, QUAD_DEPTH).setUv(TEXTURE_REPEAT + scrollU, scrollV);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
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

    private static float eyeBrightness(Minecraft minecraft) {
        BlockPos eyePos = BlockPos.containing(minecraft.player.getX(), minecraft.player.getEyeY(), minecraft.player.getZ());
        return brightnessAt(minecraft.player.level(), eyePos);
    }
}
