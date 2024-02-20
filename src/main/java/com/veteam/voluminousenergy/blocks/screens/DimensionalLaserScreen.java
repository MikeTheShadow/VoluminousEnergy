package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.DimensionalLaserTile;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DimensionalLaserScreen extends VEContainerScreen<VEContainer> {
    private final DimensionalLaserTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/dimensional_laser_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private static final ResourceLocation MULTIBLOCK_WARN = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/multiblock_invalid_warning.png");


    public DimensionalLaserScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = (DimensionalLaserTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width / 2), this.topPos - 18, buttons -> {

        }));

        // Bucket Top
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketTopSm, (this.width / 2) - 198, this.topPos, button -> {
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketTopSm, (this.width / 2) - 184, this.topPos, button -> {
            // Do nothing
        }));

        // Bucket Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketBottomSm, (this.width / 2) - 198, this.topPos + 20, button -> {
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketBottomSm, (this.width / 2) - 184, this.topPos + 20, button -> {
            // Do nothing
        }));

        // RFID slot
        addRenderableWidget(new SlotBoolButton(tileEntity.RFIDsm, (this.width / 2) - 198, this.topPos + 40, button -> {
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.RFIDsm, (this.width / 2) - 184, this.topPos + 40, button -> {
            // Do nothing
        }));

        // Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getRelationalTanks().get(0), (this.width / 2) - 198, this.topPos + 60, button -> {
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getRelationalTanks().get(0), (this.width / 2) - 184, this.topPos + 60, button -> {
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (tileEntity.getMultiblockValidity()) {
            TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateVEBlock("dimensional_laser"),  8, 6, WHITE_TEXT_STYLE);
            TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateString("container.inventory"),  8, (this.imageWidth - 96 - 8), WHITE_TEXT_STYLE);
        }
         super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  138, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")),  138, 49, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")),  38, 33, WHITE_TEXT_STYLE);

        // Tanks
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")),  119, 18, WHITE_TEXT_STYLE);
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), tileEntity.getEnergy().getMaxEnergyStored()), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
            matrixStack.renderComponentTooltip(this.font, this.getTooltips(), mouseX, mouseY);
        }

        if (isHovering(119, 18, 12, 50, mouseX, mouseY)) { // Tank
            int amount = this.getTank().getFluidAmount();
            String name = this.getFluidStackFromTank().getTranslationKey();
            matrixStack.renderTooltip(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(97, 34, 15, 16);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()));
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (tileEntity != null && tileEntity.getMultiblockValidity()) {
            int progress = tileEntity.progressProcessingCounterPX(17); // 17 vertical, 15 horz for Dimensional Laser
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(GUI, i + 97, j + 34, 176, 0, 15, progress);
            matrixStack.blit(GUI, i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power);

            VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(), this.getFluidStackFromTank(), tileEntity.getTankCapacity(), i + 119, j + 18, 0, 12, 50);

            drawIOSideHelper();
            // Upgrade slot
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            matrixStack.blit(GUI_TOOLS, i + 129, j - 16, 0, 0, 18, 18);
        } else {
            RenderSystem.setShaderTexture(0, MULTIBLOCK_WARN);
            matrixStack.blit(MULTIBLOCK_WARN, i, j, 0, 0, 174,82);
            TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("text.voluminousenergy.multiblock_warn"),  i + 48, j + 14, WHITE_TEXT_STYLE);
            TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("text.voluminousenergy.multiblock.dimensional_laser.requirements"),  i + 8, j + 32, WHITE_TEXT_STYLE);
            TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("text.voluminousenergy.multiblock.needed_below"),  i+8, j+48, WHITE_TEXT_STYLE);
        }

    }

    private FluidTank getTank(){
        return this.tileEntity.getRelationalTanks().get(0).getTank();
    }

    private FluidStack getFluidStackFromTank(){
        return this.getTank().getFluid();
    }
}