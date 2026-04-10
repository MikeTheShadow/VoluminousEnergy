package com.veteam.voluminousenergy.tools;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class VERender {
    private static final Logger LOGGER = LogManager.getLogger();

    private VERender() {
        throw new IllegalAccessError("Utility class");
    }

    // 1. ADDED GuiGraphics: Essential for modern GUI rendering so the tank scales and moves correctly with the menu.
    public static void renderGuiTank(GuiGraphics guiGraphics, Level level, BlockPos tilePos, IFluidHandler fluidHandler, int tank, double x, double y, double zLevel, double width, double height) {
        FluidStack stack = fluidHandler.getFluidInTank(tank);
        int tankCapacity = fluidHandler.getTankCapacity(tank);
        renderGuiTank(guiGraphics, level, tilePos, stack, tankCapacity, x, y, zLevel, width, height);
    }

    public static void renderGuiTank(GuiGraphics guiGraphics, Level level, BlockPos tilePos, FluidStack stack, int tankCapacity, double x, double y, double zLevel, double width, double height) {
        if (stack.isEmpty() || stack.getFluid() == null || tankCapacity <= 0) {
            return;
        }

        int amount;
        try {
            amount = stack.getAmount();
        } catch (Exception e) {
            LOGGER.warn("Exception e captured: " + e);
            amount = 0;
        }

        TextureAtlasSprite icon = getFluidTexture(stack);
        if (icon == null) {
            return;
        }

        int renderAmount = (int) Math.max(Math.min(height, amount * height / tankCapacity), 1);
        int posY = (int) (y + height - renderAmount);

        int color;
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(stack.getFluid());

        if (!Config.USE_BIOME_WATER_COLOUR.get() || (stack.getFluid() != Fluids.WATER && stack.getFluid() != Fluids.FLOWING_WATER)) {
            color = clientFluid.getTintColor(stack);
        } else {
            // 2. FIXED REDUNDANT MATH: The water color is already an integer. No need to convert to Hex string and back!
            int waterColor = level.getBiome(tilePos).value().getWaterColor();
            int alpha = clientFluid.getTintColor(stack) & 0xFF000000; // Preserve the original alpha
            color = alpha | (waterColor & 0x00FFFFFF);
        }

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        // 3. UPDATED SHADERS: 1.21 requires explicitly setting the GameRenderer shader for GUI rendering
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();

        // 4. ADDED POSE STACK: Pull the matrix from GuiGraphics so the fluid renders inside the GUI properly
        Matrix4f pose = guiGraphics.pose().last().pose();

        // 5. UPDATED BUFFER BUILDER: Modern 1.21 Tesselator API
        BufferBuilder tes = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                int drawWidth = (int) Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (x + i);
                int drawY = posY + j;

                float minU = icon.getU0();
                float maxU = icon.getU1();
                float minV = icon.getV0();
                float maxV = icon.getV1();

                float partialMaxU = minU + (maxU - minU) * drawWidth / 16F;
                float partialMaxV = minV + (maxV - minV) * drawHeight / 16F;

                tes.addVertex(pose, drawX, drawY + drawHeight, (float) zLevel).setColor(r, g, b, a).setUv(minU, partialMaxV);
                tes.addVertex(pose, drawX + drawWidth, drawY + drawHeight, (float) zLevel).setColor(r, g, b, a).setUv(partialMaxU, partialMaxV);
                tes.addVertex(pose, drawX + drawWidth, drawY, (float) zLevel).setColor(r, g, b, a).setUv(partialMaxU, minV);
                tes.addVertex(pose, drawX, drawY, (float) zLevel).setColor(r, g, b, a).setUv(minU, minV);
            }
        }

        // Finalize drawing
        BufferUploader.drawWithShader(tes.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack stack) {
        // 6. GUI-SAFE TEXTURE FETCH: FluidSpriteCache requires block context which can crash in GUIs.
        // Using getStillTexture() is 100% GUI safe.
        Identifier fluidStill = IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack);
        if (fluidStill == null) return null;
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
    }
}