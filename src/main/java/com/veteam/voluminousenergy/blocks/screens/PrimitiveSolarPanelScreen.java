package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveSolarPanelContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveSolarPanelTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PrimitiveSolarPanelScreen extends AbstractContainerScreen<PrimitiveSolarPanelContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/solar_panel_gui.png");
    private PrimitiveSolarPanelTile tileEntity;

    public PrimitiveSolarPanelScreen(PrimitiveSolarPanelContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        tileEntity = (PrimitiveSolarPanelTile) container.getTileEntity();
        container.setScreen(this);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(matrixStack, Component.nullToEmpty(menu.getEnergy() + " FE / " + Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get() + " FE"), mouseX, mouseY);
        }
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY){
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("primitive_solar_panel"), 8.0F, 6.0F, 16777215);
        if (tileEntity.getLevel().isDay())
        drawString(matrixStack,Minecraft.getInstance().font, TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getGeneration() + " FE/t", 50, 32, 0xffffff);
        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY){
        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight); // Actual Gui
        if (tileEntity != null) {
            int power = menu.powerScreen(49);
            //int progress = tileEntity.progressCounterPX(14);
            //this.blit(matrixStack,i + 81, j + (55 + (14-progress)), 176, (14-progress), 14, progress); // 55 = full, 55+14 = end
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);
        }
    }
}
