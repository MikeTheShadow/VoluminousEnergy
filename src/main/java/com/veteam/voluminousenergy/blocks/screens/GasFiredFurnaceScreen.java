package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.GasFiredFurnaceContainer;
import com.veteam.voluminousenergy.blocks.tiles.GasFiredFurnaceTile;
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

import java.util.Arrays;
import java.util.List;

public class GasFiredFurnaceScreen extends VEContainerScreen<GasFiredFurnaceContainer> {
    private GasFiredFurnaceTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID,"textures/gui/gas_fired_furnace_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public GasFiredFurnaceScreen(GasFiredFurnaceContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (GasFiredFurnaceTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Bucket insert
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketInputSm, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketInputSm, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Bucket Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketOutputSm, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketOutputSm, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Furnace Insert
        addRenderableWidget(new SlotBoolButton(tileEntity.furnaceInputSm, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.furnaceInputSm, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // Furnace Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.furnaceOutputSm, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.furnaceOutputSm, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // Fuel Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getFuelTank(), (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getFuelTank(), (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack,int mouseX,int mouseY){
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateVEBlock("gas_fired_furnace"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 8, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 8, 49, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 53, 33, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 116, 33, WHITE_TEXT_STYLE);

        // Tanks
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 31, 18, WHITE_TEXT_STYLE);
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack,int mouseX,int mouseY){
        if(isHovering(31,18,12,49,mouseX,mouseY)){
            int amount = tileEntity.getFluidFromTank().getAmount();
            String name = tileEntity.getFluidFromTank().getTranslationKey();
            matrixStack.renderTooltip(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getFuelTooltipArea(),mouseX,mouseY)){
            matrixStack.renderComponentTooltip(this.font, getFuelTooltips(), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getCounterTooltipArea(),mouseX,mouseY)){
            matrixStack.renderComponentTooltip(this.font, getCounterTooltips(), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX,mouseY);
    }

    public Rect2i getFuelTooltipArea() {
        return new Rect2i(54, 54, 14, 14);
    }

    public List<Component> getFuelTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " + tileEntity.progressFuelCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getFuelCounter()));
    }

    public Rect2i getCounterTooltipArea() {
        return new Rect2i(81, 31, 9, 17);
    }

    public List<Component> getCounterTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getCounter()));
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks,int mouseX,int mouseY){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i=(this.width-this.imageWidth)/2;
        int j=(this.height-this.imageHeight)/2;
        matrixStack.blit(GUI,i,j,0,0,this.imageWidth,this.imageHeight);
        final int flameHeight = 14;
        if(tileEntity!=null){
            int progress = tileEntity.progressCounterPX(9);
            int fuelProgress = tileEntity.progressFuelCounterPX(flameHeight);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(GUI,i+81,j+31,176,0,progress,17);
            matrixStack.blit(GUI,i+54,j+(54 + (flameHeight - fuelProgress)),176,24 + (flameHeight - fuelProgress),flameHeight,fuelProgress);

            VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidFromTank(),tileEntity.getTankCapacity(),i+31,j+18,0,12,50);
            drawIOSideHelper();
        }
        RenderSystem.setShaderTexture(0, GUI_TOOLS);
        matrixStack.blit(GUI_TOOLS,i+153, j-16,0,0,18,18);
    }

}