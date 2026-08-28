package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CombustionGeneratorScreen extends VEContainerScreen<VEContainer> {
    private VETileEntity tileEntity;
    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/combustion_generator_gui.png");
    private static final Identifier GUI_TOOLS = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/guitools.png");


    public CombustionGeneratorScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = screenContainer.getTileEntity();

    }

    @Override
    protected void init() {
        super.init();
        renderIOMenu(this.tileEntity, 64 + (this.width / 2), this.topPos - 18);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("combustion_generator"), 8, 6, WHITE_TEXT_STYLE);

        TextUtil.renderCenteredShadowedText(matrixStack, Minecraft.getInstance().font, Component.nullToEmpty(tileEntity.getEnergy().getProduction() + " FE/t"), 96, 18, WHITE_TEXT_STYLE);

        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 38, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 38, 49, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 138, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 138, 49, WHITE_TEXT_STYLE);

        // Tanks
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 61, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("1")), 119, 18, WHITE_TEXT_STYLE);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), Config.COMBUSTION_GENERATOR_MAX_POWER.get()), mouseX, mouseY);
        }

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)) { // Oxidizer Tank
            String name = tileEntity.getFluidStackFromTank(0).getHoverName().getString();
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity(0)), mouseX, mouseY);
        }

        if (isHovering(119, 18, 12, 50, mouseX, mouseY)) { // Fuel Tank
            String name = tileEntity.getFluidStackFromTank(1).getHoverName().getString();
            int amount = tileEntity.getFluidStackFromTank(1).getAmount();
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity(1)), mouseX, mouseY);
        }

        if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) { // Flame blit
            matrixStack.setComponentTooltipForNextFrame(this.font, getTooltips(), mouseX, mouseY);
        }

        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(89, 36, 14, 14);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergy().getProduction() + " FE/t"));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (tileEntity != null) {
            int progress = tileEntity.progressBurnCounterPX(14);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 89, j + (36 + (14 - progress)), 176, (14 - progress), 14, progress, 256, 256);
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power, 256, 256);

            try {
                VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getFluidStackFromTank(0), tileEntity.getTankCapacity(0), i + 61, j + 18, 0, 12, 50);
            } catch (Exception e) {
            }

            try {
                VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getFluidStackFromTank(1), tileEntity.getTankCapacity(1), i + 119, j + 18, 0, 12, 50);
            } catch (Exception e) {
            }
            drawIOSideHelper();
        }

    }
}