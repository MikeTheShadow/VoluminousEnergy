package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SolarPanelScreen extends VEContainerScreen<VEContainer> {

    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/solar_panel_gui.png");
    private final VETileEntity tileEntity;

    public SolarPanelScreen(VEContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        tileEntity = container.getTileEntity();
    }

    @Override
    protected void extractTooltip(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), /*temp modified*/Config.SOLAR_PANEL_MAX_POWER.get()), mouseX, mouseY);
        }
        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("solar_panel"), 8, 6, WHITE_TEXT_STYLE);
        if (tileEntity.getLevel().isBrightOutside())
            TextUtil.renderCenteredShadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergy().getProduction() + " FE/t"), 90, 32, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        //super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256); // Actual Gui
        if (tileEntity != null) {
            int power = menu.powerScreen(49);
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 11, j + (16 + (49 - power)), 176, 14 + (49 - power), 12, power, 256, 256);
        }
    }
}
