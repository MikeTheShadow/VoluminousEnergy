package com.veteam.voluminousenergy.blocks.screens.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.blocks.containers.tank.TankContainer;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AluminumTankScreen extends TankScreen {
    public AluminumTankScreen(TankContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("aluminum_tank"), 8.0F, 6.0F, 16777215);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }
}
