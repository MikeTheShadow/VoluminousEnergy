package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.SolarPanelContainer;
import com.veteam.voluminousenergy.blocks.tiles.SolarPanelTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SolarPanelScreen extends VEContainerScreen<SolarPanelContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/solar_panel_gui.png");
    private final SolarPanelTile tileEntity;

    public SolarPanelScreen(SolarPanelContainer  container, Inventory inv, Component name) {
        super(container, inv, name);
        tileEntity = (SolarPanelTile) container.getTileEntity();
        container.setScreen(this);
    }

    @Override
    public void render(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(veEnergyStorage, /*temp modified*/Config.SOLAR_PANEL_MAX_POWER.get()), mouseX, mouseY);
            }));
        }
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }
    @Override
    protected void renderLabels(GuiGraphics matrixStack,int mouseX, int mouseY){
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("solar_panel"),  8, 6, WHITE_TEXT_STYLE);
        if (tileEntity.getLevel().isDay())
            TextUtil.renderCenteredShadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getGeneration() + " FE/t"), 90, 32, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        //super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {}
    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks, int mouseX, int mouseY){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI,i, j, 0, 0, this.imageWidth, this.imageHeight); // Actual Gui
        if (tileEntity != null) {
            int power = menu.powerScreen(49);
            matrixStack.blit(GUI,i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);
        }
    }
}
