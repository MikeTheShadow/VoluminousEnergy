package com.veteam.voluminousenergy.tools;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class VERender {
    private static final Logger LOGGER = LogManager.getLogger();

    private VERender() {
        throw new IllegalAccessError("Utility class");
    }

    public static void renderGuiTank(Level level, BlockPos tilePos, IFluidHandler fluidHandler, int tank, double x, double y, double zLevel, double width, double height) {
        FluidStack stack = fluidHandler.getFluidInTank(tank);
        int tankCapacity = fluidHandler.getTankCapacity(tank);
        renderGuiTank(level, tilePos, stack, tankCapacity, x, y, zLevel, width, height);
    }

    public static void renderGuiTank(Level level, BlockPos tilePos, FluidStack stack, int tankCapacity, double x, double y, double zLevel, double width, double height) {
        // Originally Adapted from Ender IO by Silent's Mechanisms
        int amount;
        try{
            if (stack.getFluid() == null || stack.isEmpty()) {
                return;
            }
        } catch (Exception e){
            return;
        }

        try{
            amount = stack.getAmount();
        } catch (Exception e){
            LOGGER.warn("Exception e captured: " + e);
            amount = 0;

        }

        TextureAtlasSprite icon = getFluidTexture(stack);
        if (icon == null) {
            return;
        }

        int renderAmount = (int) Math.max(Math.min(height, amount * height / tankCapacity), 1);
        int posY = (int) (y + height - renderAmount);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        int color;
        if ((!Config.USE_BIOME_WATER_COLOUR.get()) || (stack.getFluid() != Fluids.WATER && stack.getFluid() != Fluids.FLOWING_WATER)){
            color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = ((color >> 24) & 0xFF) / 255f;
            RenderSystem.setShaderColor(r, g, b, a);
        } else {
            String colourString = Integer.toHexString(level.getBiome(tilePos).value().getWaterColor());
            color = Integer.valueOf(colourString, 16);
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = ((IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor() >> 24) & 0xFF) / 255f;
            RenderSystem.setShaderColor(r, g, b, a);
        }

        RenderSystem.enableBlend();
        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                int drawWidth = (int) Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (x + i);
                int drawY = posY + j;

                float minU = icon.getU0();//min
                float maxU = icon.getU1();//max
                float minV = icon.getV0();//min
                float maxV = icon.getV1();//max

                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder tes = tessellator.getBuilder();
                tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                tes.vertex(drawX, drawY + drawHeight, 0).uv(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                tes.vertex(drawX + drawWidth, drawY + drawHeight, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                tes.vertex(drawX + drawWidth, drawY, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
                tes.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
                tessellator.end();
            }
        }
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f,  1f);
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack stack) {
        TextureAtlasSprite[] sprites = ForgeHooksClient.getFluidSprites(Minecraft.getInstance().level, BlockPos.ZERO, stack.getFluid().defaultFluidState());
        return sprites.length > 0 ? sprites[0] : null;
    }
}
