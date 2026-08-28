package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEAttachments;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GasFiredFurnaceScreen extends VEContainerScreen<VEContainer> {
    private VETileEntity tileEntity;
    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/gas_fired_furnace_gui.png");
    private static final Identifier GUI_TOOLS = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/guitools.png");


    public GasFiredFurnaceScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = screenContainer.getTileEntity();

    }

    @Override
    protected void init() {
        super.init();
        renderIOMenu(this.tileEntity, 64 + (this.width / 2), this.topPos + 4);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("gas_fired_furnace"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 8, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 8, 49, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 53, 33, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 116, 33, WHITE_TEXT_STYLE);

        // Tanks
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 31, 18, WHITE_TEXT_STYLE);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        if (isHovering(31, 18, 12, 49, mouseX, mouseY)) {
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getHoverName().getString();
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity(0)), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getFuelTooltipArea(), mouseX, mouseY)) {
            matrixStack.setComponentTooltipForNextFrame(this.font, getFuelTooltips(), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getCounterTooltipArea(), mouseX, mouseY)) {
            matrixStack.setComponentTooltipForNextFrame(this.font, getCounterTooltips(), mouseX, mouseY);
        }

        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getFuelTooltipArea() {
        return new Rect2i(54, 54, 14, 14);
    }

    public List<Component> getFuelTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " +
                        tileEntity.progressCounterPercent(tileEntity.getData(VEAttachments.FUEL_COUNTER_LENGTH).counter(),tileEntity.getData(VEAttachments.FUEL_COUNTER_LENGTH).length()) + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getData(VEAttachments.FUEL_COUNTER_LENGTH).counter()));
    }

    public Rect2i getCounterTooltipArea() {
        return new Rect2i(81, 31, 9, 17);
    }

    public List<Component> getCounterTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getData(VEAttachments.COUNTER_LENGTH).counter()));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        final int flameHeight = 14;
        if (tileEntity != null) {
            int progress = tileEntity.progressProcessingCounterPX(9);
            int fuelProgress = tileEntity.progressBurnCounterPX(
                    flameHeight,tileEntity.getData(VEAttachments.FUEL_COUNTER_LENGTH).counter(),tileEntity.getData(VEAttachments.FUEL_COUNTER_LENGTH).length());

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 81, j + 31, 176, 0, progress, 17, 256, 256);
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 54, j + (54 + (flameHeight - fuelProgress)), 176, 24 + (flameHeight - fuelProgress), flameHeight, fuelProgress, 256, 256);

            VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getFluidStackFromTank(0), tileEntity.getTankCapacity(0), i + 31, j + 18, 0, 12, 50);
            drawIOSideHelper();
        }
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI_TOOLS, i + 153, j - 16, 0, 0, 18, 18, 256, 256);
    }

}