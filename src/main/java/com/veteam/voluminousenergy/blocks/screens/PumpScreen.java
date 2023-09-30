package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PumpContainer;
import com.veteam.voluminousenergy.blocks.tiles.PumpTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PumpScreen extends VEContainerScreen<PumpContainer> {

    private PumpTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/air_compressor_gui.png");
    

    public PumpScreen(PumpContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (PumpTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Input insert
        addRenderableWidget(new SlotBoolButton(tileEntity.slotManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.slotManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Input Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getTank(), (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getTank(), (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack,int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("pump"),  8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateString("container.inventory"),  8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  70, 49, WHITE_TEXT_STYLE);

        // Tank
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")),  93, 18, WHITE_TEXT_STYLE);
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(veEnergyStorage, Config.PUMP_MAX_POWER.get()), mouseX, mouseY);
            }));
        }

        if (isHovering(93, 18, 12, 50, mouseX, mouseY)){ // Oxidizer Tank
            String name = tileEntity.getAirTankFluid().getTranslationKey();
            int amount = tileEntity.getAirTankFluid().getAmount();
            matrixStack.renderTooltip(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI,i, j, 0, 0, this.imageWidth, this.imageHeight);
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
            matrixStack.blit(GUI,i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power);

            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getAirTankFluid(),tileEntity.getTankCapacity(), i + 93, j + 18, 0, 12, 50);
            } catch (Exception e){ }
            drawIOSideHelper();
        }
    }
}
