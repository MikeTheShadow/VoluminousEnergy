package com.veteam.voluminousenergy.blocks.screens.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.blocks.containers.tank.TankContainer;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolariumTankScreen extends TankScreen {
    public SolariumTankScreen(TankContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("solarium_tank"),  8, 6, WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }
}