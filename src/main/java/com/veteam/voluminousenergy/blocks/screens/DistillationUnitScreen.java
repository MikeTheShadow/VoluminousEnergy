package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.blocks.tiles.DistillationUnitTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.List;

public class DistillationUnitScreen extends VEContainerScreen<DistillationUnitContainer> {
    private DistillationUnitTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/distillation_unit_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private static final ResourceLocation MULTIBLOCK_WARN = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/multiblock_invalid_warning.png");
    

    public DistillationUnitScreen(DistillationUnitContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (DistillationUnitTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
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

        // Input Top
        addRenderableWidget(new SlotBoolButton(tileEntity.iTopManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.iTopManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Input Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.iBottomManager, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.iBottomManager, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Output 0 Top
        addRenderableWidget(new SlotBoolButton(tileEntity.o0TopManager, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o0TopManager, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // Output 0 Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.o0BottomManager, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o0BottomManager, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // Output 1 Top
        addRenderableWidget(new SlotBoolButton(tileEntity.o1TopManager, (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o1TopManager, (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));

        // Output 1 Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.o1BottomManager, (this.width/2)-198, this.topPos+100, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o1BottomManager, (this.width/2)-184, this.topPos+100, button ->{
            // Do nothing
        }));

        // Output 2
        addRenderableWidget(new SlotBoolButton(tileEntity.o2Manager, (this.width/2)-198, this.topPos+120, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o2Manager, (this.width/2)-184, this.topPos+120, button ->{
            // Do nothing
        }));

        // Input Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getInputTank(), (this.width/2)-198, this.topPos+140, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getInputTank(), (this.width/2)-184, this.topPos+140, button ->{
            // Do nothing
        }));

        // Output Tank 0
        addRenderableWidget(new TankBoolButton(tileEntity.getOutputTank0(), (this.width/2)-198, this.topPos+160, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getOutputTank0(), (this.width/2)-184, this.topPos+160, button ->{
            // Do nothing
        }));

        // Output Tank 1
        addRenderableWidget(new TankBoolButton(tileEntity.getOutputTank1(), (this.width/2)-198, this.topPos+180, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getOutputTank1(), (this.width/2)-184, this.topPos+180, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        if (tileEntity.getMultiblockValidity()){
            this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("distillation_unit"), 8.0F, 6.0F, WHITE_TEXT_COLOUR);
            this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), WHITE_TEXT_COLOUR);
        }
         super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        // Slots
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 38F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 38F, 49F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 96F, 11F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 96F, 42F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("4")), 137F, 11F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("5")), 137F, 42F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("6")), 122F, 64F, WHITE_TEXT_COLOUR);

        // Tanks
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 61F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("1")), 119F, 11F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("2")), 157F, 11F, WHITE_TEXT_COLOUR);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)){
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, TextUtil.powerBarTooltip(veEnergyStorage, Config.DISTILLATION_UNIT_MAX_POWER.get()), mouseX, mouseY);
            }));
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
            renderComponentTooltip(matrixStack, this.getTooltips(), mouseX, mouseY);
        }

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)){ // Input Tank
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(119, 11, 12, 50, mouseX, mouseY)){ // First Output Tank
            int amount = tileEntity.getFluidStackFromTank(1).getAmount();
            String name = tileEntity.getFluidStackFromTank(1).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(157, 11, 12, 50, mouseX, mouseY)){ // Second Output Tank
            int amount = tileEntity.getFluidStackFromTank(2).getAmount();
            String name = tileEntity.getFluidStackFromTank(2).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(81, 31, 9, 17);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()));
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null && tileEntity.getMultiblockValidity()){
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
            this.blit(matrixStack,i+81, j+31, 176, 0, progress, 17);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);

            VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidStackFromTank(0),tileEntity.getTankCapacity(), i + 61, j + 18, 0, 12, 50);

            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidStackFromTank(1),tileEntity.getTankCapacity(), i + 119, j + 11, 0, 12, 50);
            } catch (Exception e){ }

            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getFluidStackFromTank(2),tileEntity.getTankCapacity(), i + 157, j + 11, 0, 12, 50);
            } catch (Exception e){ }
            drawIOSideHelper();
            // Upgrade slot
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            this.blit(matrixStack,i+129, j-16,0,0,18,18);
        } else {
            RenderSystem.setShaderTexture(0, MULTIBLOCK_WARN);
            this.blit(matrixStack, i, j, 0, 0, 174,82);
            this.font.drawShadow(matrixStack, TextUtil.translateString("text.voluminousenergy.multiblock_warn"), i + 48, j + 14, WHITE_TEXT_COLOUR);
            this.font.drawShadow(matrixStack, TextUtil.translateString("text.voluminousenergy.multiblock.distillation_unit.requirements"), i + 8, j + 32, WHITE_TEXT_COLOUR);
            this.font.drawShadow(matrixStack, TextUtil.translateString("text.voluminousenergy.multiblock.needed_behind"), i+8, j+48, WHITE_TEXT_COLOUR);
        }

    }

}