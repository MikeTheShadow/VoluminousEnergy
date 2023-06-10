package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.FluidMixerContainer;
import com.veteam.voluminousenergy.blocks.tiles.FluidMixerTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.List;

public class FluidMixerScreen extends VEContainerScreen<FluidMixerContainer> {
    private final FluidMixerTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/fluid_mixer_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");


    public FluidMixerScreen(FluidMixerContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (FluidMixerTile) screenContainer.getTileEntity();
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
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos -18, buttons ->{

        }));

        // Input0 Bucket Top
        addRenderableWidget(new SlotBoolButton(tileEntity.input0sm, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.input0sm, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Input0 Bucket Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.input1sm, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.input1sm, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Input1 Bucket Top
        addRenderableWidget(new SlotBoolButton(tileEntity.input2sm, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.input2sm, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // Input1 Bucket Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.input3sm, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.input3sm, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // Output Bucket Top
        addRenderableWidget(new SlotBoolButton(tileEntity.output0sm, (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.output0sm, (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));

        // Output Bucket Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.output1sm, (this.width/2)-198, this.topPos+100, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.output1sm, (this.width/2)-184, this.topPos+100, button ->{
            // Do nothing
        }));

        // Input Tank 0
        addRenderableWidget(new TankBoolButton(tileEntity.getInputTank0(), (this.width/2)-198, this.topPos+120, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getInputTank0(), (this.width/2)-184, this.topPos+120, button ->{
            // Do nothing
        }));

        // Input Tank 1
        addRenderableWidget(new TankBoolButton(tileEntity.getInputTank1(), (this.width/2)-198, this.topPos+140, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getInputTank1(), (this.width/2)-184, this.topPos+140, button ->{
            // Do nothing
        }));

        // Output Tank 0
        addRenderableWidget(new TankBoolButton(tileEntity.getOutputTank0(), (this.width/2)-198, this.topPos+160, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getOutputTank0(), (this.width/2)-184, this.topPos+160, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("fluid_mixer"), 8.0F, 6.0F, WHITE_TEXT_COLOUR);
        drawString(matrixStack, Minecraft.getInstance().font, "+", 78, 34, GREY_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, TextUtil.translateString("container.inventory"), 8.0F, (float)(this.imageWidth - 96 - 8), WHITE_TEXT_COLOUR);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Slots
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 38F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 38F, 49F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 86F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 86F, 49F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("4")), 136F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("5")), 136F, 49F, WHITE_TEXT_COLOUR);

        // Tanks
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 61F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("1")), 109F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("2")), 159F, 18F, WHITE_TEXT_COLOUR);

    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)){
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, TextUtil.powerBarTooltip(veEnergyStorage, Config.FLUID_MIXER_MAX_POWER.get()), mouseX, mouseY);
            }));
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
            renderComponentTooltip(matrixStack, this.getTooltips(), mouseX, mouseY);
        }

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)){ // First Input Tank
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(109, 18, 12, 50, mouseX, mouseY)){ // Second input Tank
            int amount = tileEntity.getFluidStackFromTank(1).getAmount();
            String name = tileEntity.getFluidStackFromTank(1).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(159, 18, 12, 50, mouseX, mouseY)){ // Second Output Tank
            int amount = tileEntity.getFluidStackFromTank(2).getAmount();
            String name = tileEntity.getFluidStackFromTank(2).getTranslationKey();
            renderTooltip(matrixStack, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(126, 32, 9, 17);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()));
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight);
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
            this.blit(matrixStack,i+127, j+31, 176, 0, progress, 17);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);

            VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidStackFromTank(0),tileEntity.getTankCapacity(), i + 61, j + 18, 0, 12, 50);

            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidStackFromTank(1),tileEntity.getTankCapacity(), i + 109, j + 18, 0, 12, 50);
            } catch (Exception e){ }

            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidStackFromTank(2),tileEntity.getTankCapacity(), i + 159, j + 18, 0, 12, 50);
            } catch (Exception e){ }
            drawIOSideHelper();
            // Upgrade slot
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            this.blit(matrixStack,i+129, j-16,0,0,18,18);
        }

    }

}