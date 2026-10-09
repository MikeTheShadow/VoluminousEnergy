package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CompressorScreen extends VEContainerScreen<VEContainer> {

    private VETileEntity tileEntity;
    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/compressor_gui.png");
    private static final Identifier GUI_TOOLS = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/guitools.png");


    public CompressorScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = screenContainer.getTileEntity();

    }

    @Override
    protected void init() {
        super.init();
        // Buttons
        renderIOMenu(this.tileEntity, 64 + (this.width / 2), this.topPos + 4);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("compressor"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 80, 13, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 80, 58, WHITE_TEXT_STYLE);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), Config.COMPRESSOR_MAX_POWER.get()), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
            matrixStack.setComponentTooltipForNextFrame(this.font, this.getTooltips(), mouseX, mouseY);
        }
        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(79, 31, 17, 24);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (tileEntity != null) {
            int progress = tileEntity.progressProcessingCounterPX(24);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 79, j + 31, 176, 0, 17, progress, 256, 256);
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power, 256, 256);
            drawIOSideHelper();
        }
        // Upgrade slot
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI_TOOLS, i + 153, j - 16, 0, 0, 18, 18, 256, 256);
    }

}