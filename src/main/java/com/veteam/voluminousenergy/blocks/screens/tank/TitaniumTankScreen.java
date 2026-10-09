package com.veteam.voluminousenergy.blocks.screens.tank;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class TitaniumTankScreen extends TankScreen {
    public TitaniumTankScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("titanium_tank"), 8, 6, WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }
}