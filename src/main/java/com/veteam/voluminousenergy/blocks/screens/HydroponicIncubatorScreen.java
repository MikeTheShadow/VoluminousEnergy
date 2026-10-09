package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
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

public class HydroponicIncubatorScreen extends VEContainerScreen<VEContainer> {
    private final VETileEntity tileEntity;
    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/hydroponic_incubator_gui.png");
    private static final Identifier GUI_TOOLS = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/guitools.png");


    public HydroponicIncubatorScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
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
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("hydroponic_incubator"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 38, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 38, 49, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 83, 34, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 123, 8, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("4")), 123, 26, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("5")), 123, 44, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("6")), 123, 62, WHITE_TEXT_STYLE);

        // Tanks
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 61, 18, WHITE_TEXT_STYLE);

    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), Config.HYDROPONIC_INCUBATOR_MAX_POWER.get()), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
            matrixStack.setComponentTooltipForNextFrame(this.font, this.getTooltips(), mouseX, mouseY);
        }

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)) { // Input Tank
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getHoverName().getString();
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity(0)), mouseX, mouseY);
        }

        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(106, 31, 14, 20);
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
            int progress = tileEntity.progressProcessingCounterPX(9);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 109, j + 32, 176, 0, progress, 17, 256, 256);
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power, 256, 256);

            VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getFluidStackFromTank(0), tileEntity.getTankCapacity(0), i + 61, j + 18, 0, 12, 50);

            drawIOSideHelper();
            // Upgrade slot
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI_TOOLS, i + 153, j - 16, 0, 0, 18, 18, 256, 256);
        }

    }

}