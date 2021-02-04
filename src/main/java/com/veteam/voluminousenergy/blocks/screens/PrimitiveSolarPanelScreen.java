package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveSolarPanelContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveSolarPanelTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PrimitiveSolarPanelScreen extends ContainerScreen<PrimitiveSolarPanelContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/primitivestirlinggenerator_gui.png");
    private PrimitiveSolarPanelTile tileEntity;

    public PrimitiveSolarPanelScreen(PrimitiveSolarPanelContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        tileEntity = (PrimitiveSolarPanelTile) container.getTileEntity();
        container.setScreen(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderHoveredTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack,int mouseX, int mouseY) {
        if (isPointInRegion(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(matrixStack, ITextComponent.getTextComponentOrEmpty(container.getEnergy() + " FE / " + Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get() + " FE"), mouseX, mouseY);
        }
        super.renderHoveredTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY){
        this.font.func_243246_a(matrixStack, TextUtil.translateVEBlock("primitive_solar_panel"), 8.0F, 6.0F, 16777215);
        if (tileEntity.getWorld().isDaytime())
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Generating: " + tileEntity.getGeneration() + " FE/t", 50, 18, 0xffffff);
        this.font.func_243246_a(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.ySize - 96 + 2), 16777215);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.xSize, this.ySize); // Actual Gui
        if (tileEntity != null) {
            int power = container.powerScreen(49);
            //int progress = tileEntity.progressCounterPX(14);
            //this.blit(matrixStack,i + 81, j + (55 + (14-progress)), 176, (14-progress), 14, progress); // 55 = full, 55+14 = end
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);
        }
    }
}
