package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.BatteryBoxContainer;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.VEPowerIOManager;
import com.veteam.voluminousenergy.tools.buttons.batteryBox.BatteryBoxSendOutPowerButton;
import com.veteam.voluminousenergy.tools.buttons.batteryBox.BatteryBoxSlotPairButton;
import com.veteam.voluminousenergy.tools.buttons.batteryBox.VEBatterySwitchManager;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BatteryBoxScreen extends VEContainerScreen<BatteryBoxContainer> {

    private final BatteryBoxTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public BatteryBoxScreen(BatteryBoxContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (BatteryBoxTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Top row
        addRenderableWidget(new SlotBoolButton(tileEntity.topManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.topManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Bottom Row
        addRenderableWidget(new SlotBoolButton(tileEntity.bottomManager, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bottomManager, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));


        VEBatterySwitchManager[] switchManagers = this.tileEntity.getSwitchManagers();
        VEPowerIOManager vePowerIOManager = this.tileEntity.getPowerIOManager();
        // Slot arrows
        addRenderableWidget(new BatteryBoxSlotPairButton(switchManagers[0],(this.width/2)-54,topPos + 34, 0, button -> { }));
        addRenderableWidget(new BatteryBoxSlotPairButton(switchManagers[1],(this.width/2)-36,topPos + 34, 1, button -> { }));
        addRenderableWidget(new BatteryBoxSlotPairButton(switchManagers[2],(this.width/2)-18,topPos + 34, 2, button -> { }));
        addRenderableWidget(new BatteryBoxSlotPairButton(switchManagers[3],(this.width/2), topPos + 34, 3, button -> { }));
        addRenderableWidget(new BatteryBoxSlotPairButton(switchManagers[4],(this.width/2)+18,topPos + 34, 4, button -> { }));
        addRenderableWidget(new BatteryBoxSlotPairButton(switchManagers[5],(this.width/2)+36,topPos + 34, 5, button -> { }));

        // Send Out Power Button
        addRenderableWidget(new BatteryBoxSendOutPowerButton(vePowerIOManager,(this.width/2)-79,topPos + 3, tileEntity,button -> { }));
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("battery_box"),  34, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack,this.font, TextUtil.translateString("container.inventory"),  8, (this.imageWidth - 96 - 8), WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Top Row
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 35, 17, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  53, 17, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  71, 17, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  89, 17, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  107, 17, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  125, 17, WHITE_TEXT_STYLE);

        // Bottom Row
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 35, 54, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 53, 54, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 71, 54, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 89, 54, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 107, 54, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 125, 54, WHITE_TEXT_STYLE);

    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(veEnergyStorage, Config.BATTERY_BOX_MAX_POWER.get()), mouseX, mouseY);
            }));
        }

        if (isHovering(10,4,15,10, mouseX, mouseY)){
            if(tileEntity.getPowerIOManager().isFlipped()) {
                matrixStack.renderTooltip(this.font,TextUtil.translateString("text.voluminousenergy.battery_box.sending_out_power"), mouseX, mouseY);
            } else {
                matrixStack.renderTooltip(this.font, TextUtil.translateString("text.voluminousenergy.battery_box.receiving_power"), mouseX, mouseY);
            }
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
        matrixStack.blit(this.GUI,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null){
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            //this.blit(matrixStack,i+81,j+31,176,0,progress,17);
            matrixStack.blit(this.GUI,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);
            drawIOSideHelper();
            for (VEBatterySwitchManager switchManager : tileEntity.getSwitchManagers()) {
                updateSlotPairButton(switchManager.isFlipped(),switchManager.getSlot());
            }
            updateSendOutPowerButton(tileEntity.getPowerIOManager().isFlipped());
        }
    }

    public void updateSlotPairButton(boolean status, int id){
        for(Renderable widget : this.renderables){
            if(widget instanceof BatteryBoxSlotPairButton batteryBoxSlotPairButton){
                if(batteryBoxSlotPairButton.getId() == id){
                    batteryBoxSlotPairButton.setStatus(status);
                }
            }
        }
    }

    public void updateSendOutPowerButton(boolean status){
        for(Renderable widget : this.renderables){
            if(widget instanceof BatteryBoxSendOutPowerButton batteryBoxSendOutPowerButton){
                batteryBoxSendOutPowerButton.setStatus(status);
            }
        }
    }
}
