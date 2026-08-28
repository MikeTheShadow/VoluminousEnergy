package com.veteam.voluminousenergy.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class VERender {
    private static final Logger LOGGER = LogManager.getLogger();

    private VERender() {
        throw new IllegalAccessError("Utility class");
    }

    // 1. ADDED GuiGraphicsExtractor: Essential for modern GUI rendering so the tank scales and moves correctly with the menu.
    public static void renderGuiTank(GuiGraphicsExtractor guiGraphics, Level level, BlockPos tilePos, IFluidHandler fluidHandler, int tank, double x, double y, double zLevel, double width, double height) {
        FluidStack stack = fluidHandler.getFluidInTank(tank);
        int tankCapacity = fluidHandler.getTankCapacity(tank);
        renderGuiTank(guiGraphics, level, tilePos, stack, tankCapacity, x, y, zLevel, width, height);
    }

    public static void renderGuiTank(GuiGraphicsExtractor guiGraphics, Level level, BlockPos tilePos, FluidStack stack, int tankCapacity, double x, double y, double zLevel, double width, double height) {
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
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(stack.getFluid().defaultFluidState());
        int tintColor = fluidModel.tintSource() != null ? fluidModel.tintSource().color(Blocks.AIR.defaultBlockState()) : 0xFFFFFFFF;

        if (!Config.USE_BIOME_WATER_COLOUR.get() || (stack.getFluid() != Fluids.WATER && stack.getFluid() != Fluids.FLOWING_WATER)) {
            color = tintColor;
        } else {
            // 2. FIXED REDUNDANT MATH: The water color is already an integer. No need to convert to Hex string and back!
            int waterColor = level.getBiome(tilePos).value().getWaterColor();
            int alpha = tintColor & 0xFF000000; // Preserve the original alpha
            color = alpha | (waterColor & 0x00FFFFFF);
        }

        // 3. Tile the fluid sprite in 16x16 blocks via the GUI extraction pipeline, matching the old
        // per-vertex tiled quad behaviour without needing raw Tesselator/BufferUploader access (both removed).
        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                int drawWidth = (int) Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (x + i);
                int drawY = posY + j;

                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, drawX, drawY, drawWidth, drawHeight, color);
            }
        }
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack stack) {
        // 6. GUI-SAFE TEXTURE FETCH: querying the baked FluidModel directly avoids needing block/level context.
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(stack.getFluid().defaultFluidState());
        return fluidModel.stillMaterial().sprite();
    }
}