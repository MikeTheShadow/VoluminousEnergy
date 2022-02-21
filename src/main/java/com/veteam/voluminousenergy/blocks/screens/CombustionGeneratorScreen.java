package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.CombustionGeneratorTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CombustionGeneratorScreen extends VEContainerScreen<CombustionGeneratorContainer> {
    private CombustionGeneratorTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/combustion_generator_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public CombustionGeneratorScreen(CombustionGeneratorContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (CombustionGeneratorTile) screenContainer.getTileEntity();
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

        // Oxidizer insert
        addRenderableWidget(new SlotBoolButton(tileEntity.oxiInSm, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.oxiInSm, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Oxidizer Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.oxiOutSm, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.oxiOutSm, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Fuel Insert
        addRenderableWidget(new SlotBoolButton(tileEntity.fuelInSm, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.fuelInSm, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // Fuel Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.fuelOutSm, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.fuelOutSm, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // Oxidizer Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getOxidizerTank(), (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getOxidizerTank(), (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));

        // Fuel Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getFuelTank(), (this.width/2)-198, this.topPos+100, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getFuelTank(), (this.width/2)-184, this.topPos+100, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("combustion_generator"), 8.0F, 6.0F, WHITE_TEXT_COLOUR);

        if (tileEntity.getEnergyRate() < 10) {
            drawString(matrixStack,Minecraft.getInstance().font, tileEntity.getEnergyRate() + " FE/t", 80, 18, WHITE_TEXT_COLOUR);
        } else if (tileEntity.getEnergyRate() < 100){
            drawString(matrixStack,Minecraft.getInstance().font, tileEntity.getEnergyRate() + " FE/t", 77, 18, WHITE_TEXT_COLOUR);
        } else {
            drawString(matrixStack,Minecraft.getInstance().font, tileEntity.getEnergyRate() + " FE/t", 75, 18, WHITE_TEXT_COLOUR);
        }

        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), WHITE_TEXT_COLOUR);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        // Slots
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 38F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 38F, 49F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("2")), 138F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("3")), 138F, 49F, WHITE_TEXT_COLOUR);

        // Tanks
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 61F, 18F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("1")), 119F, 18F, WHITE_TEXT_COLOUR);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)){
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, Component.nullToEmpty(
                        veEnergyStorage.getEnergyStored()
                                + " FE / " + Config.COMBUSTION_GENERATOR_MAX_POWER.get()
                                + " FE"
                ), mouseX, mouseY);
            }));
        }

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)){ // Oxidizer Tank
            String name = tileEntity.getFluidStackFromTank(0).getTranslationKey();
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            renderTooltip(matrixStack, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(119, 18, 12, 50, mouseX, mouseY)){ // Fuel Tank
            String name = tileEntity.getFluidStackFromTank(1).getTranslationKey();
            int amount = tileEntity.getFluidStackFromTank(1).getAmount();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(87, 34, 17, 18, mouseX, mouseY)){ // Flame blit
            renderTooltip(matrixStack, Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " + tileEntity.progressCounterPercent() + "%, " + TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft() + ", " + TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergyRate() + " FE/t"), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null){
            int progress = tileEntity.progressCounterPX(14);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(matrixStack,i + 89, j + (36 + (14-progress)), 176, (14-progress), 14, progress);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);

            try{
                VERender.renderGuiTank(tileEntity.getFluidStackFromTank(0),tileEntity.getTankCapacity(), i + 61, j + 18, 0, 12, 50);
            } catch (Exception e){ }

            try{
                VERender.renderGuiTank(tileEntity.getFluidStackFromTank(1),tileEntity.getTankCapacity(), i + 119, j + 18, 0, 12, 50);
            } catch (Exception e){ }
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            drawIOSideHelper();
        }

    }
}