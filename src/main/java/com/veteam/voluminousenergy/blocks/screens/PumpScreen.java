package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class PumpScreen extends VEContainerScreen<VEContainer> {

    private VETileEntity tileEntity;
    private final Identifier GUI = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/air_compressor_gui.png");


    public PumpScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = screenContainer.getTileEntity();

    }

    @Override
    protected void init() {
        super.init();
        renderIOMenu(tileEntity, 64 + (this.width / 2), this.topPos + 4);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("pump"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.extractLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        // Tank
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 93, 18, WHITE_TEXT_STYLE);

        // Slots handled by super
        super.renderSlotAndTankLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), Config.PUMP_MAX_POWER.get()), mouseX, mouseY);
        }

        if (isHovering(93, 18, 12, 50, mouseX, mouseY)) { // Oxidizer Tank
            FluidStack stack = tileEntity.getRelationalTank(0).getTank().getFluid();
            String name = stack.getHoverName().getString();
            int amount = stack.getAmount();
            matrixStack.setTooltipForNextFrame(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity(0)), mouseX, mouseY);
        }

        super.extractTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (tileEntity != null) {
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI, i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power, 256, 256);

            try {
                VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getRelationalTank(0).getTank().getFluid(), tileEntity.getTankCapacity(0), i + 93, j + 18, 0, 12, 50);
            } catch (Exception e) {
            }
            drawIOSideHelper();
        }
    }
}
