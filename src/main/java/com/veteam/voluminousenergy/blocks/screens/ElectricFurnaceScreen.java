package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.ElectricFurnaceContainer;
import com.veteam.voluminousenergy.blocks.tiles.ElectricFurnaceTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.List;

public class ElectricFurnaceScreen extends VEContainerScreen<ElectricFurnaceContainer> {

    private ElectricFurnaceTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/electric_furnace_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public ElectricFurnaceScreen(ElectricFurnaceContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (ElectricFurnaceTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Input insert
        addRenderableWidget(new SlotBoolButton(tileEntity.inputSlotManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.inputSlotManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Input Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.outputSlotManager, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.outputSlotManager, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateVEBlock("electric_furnace"),  8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  53, 33, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")),  116, 33, WHITE_TEXT_STYLE);

    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(veEnergyStorage, Config.ELECTRIC_FURNACE_MAX_POWER.get()), mouseX, mouseY);
            }));
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
            matrixStack.renderComponentTooltip(this.font, this.getTooltips(), mouseX, mouseY);
        }
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(81, 31, 9, 17);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getCounter()));
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null){
            int progress = tileEntity.progressCounterPX(9);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(GUI,i+81,j+31,176,0,progress,17);
            matrixStack.blit(GUI,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);
            drawIOSideHelper();
        }
        // Upgrade slot
        RenderSystem.setShaderTexture(0, GUI_TOOLS);
        matrixStack.blit(GUI_TOOLS,i+153, j-16,0,0,18,18);
    }
}
