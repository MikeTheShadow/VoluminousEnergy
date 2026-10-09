package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ToolingStationScreen extends VEContainerScreen<VEContainer> {
    private VETileEntity tileEntity;
    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/tooling_station_gui.png");

    public ToolingStationScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
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
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("tooling_station"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)) { // Input Tank
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getHoverName().getString();
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity(0)), mouseX, mouseY);
        }

        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(109, 18, 22, 47);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (tileEntity != null) {

            boolean lightArrow = false;

            if(tileEntity.getRecipeProcessor() instanceof BasicProcessor processor)
                lightArrow = (processor.getSelectedRecipe() == null);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            if (lightArrow) matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 109, j + 18, 188, 0, 22, 47, 256, 256);
            VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getFluidStackFromTank(0), tileEntity.getTankCapacity(0), i + 61, j + 18, 0, 12, 50);

            /*try{
                VERender.renderGuiTank(matrixStack, tileEntity.getFluidStackFromTank(1),tileEntity.getTankCapacity(), i + 157, j + 18, 0, 12, 50);
            } catch (Exception ignored){ }*/
            drawIOSideHelper();
        }

    }
}