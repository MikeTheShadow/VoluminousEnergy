package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("battery_box"), 34.0F, 6.0F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageWidth - 96 - 8), WHITE_TEXT_COLOUR);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        // Top Row
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 35F, 17F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 53F, 17F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 71F, 17F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 89F, 17F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 107F, 17F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 125F, 17F, WHITE_TEXT_COLOUR);

        // Bottom Row
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 35F, 54F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 53F, 54F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 71F, 54F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 89F, 54F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 107F, 54F, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), 125F, 54F, WHITE_TEXT_COLOUR);

    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, Component.nullToEmpty(
                        veEnergyStorage.getEnergyStored()
                                + " FE / " + Config.BATTERY_BOX_MAX_POWER.get()
                                + " FE"
                ), mouseX, mouseY);
            }));
        }

        if (isHovering(10,4,15,10, mouseX, mouseY)){
            if(tileEntity.getPowerIOManager().isFlipped()) {
                renderTooltip(matrixStack,TextUtil.translateString("text.voluminousenergy.battery_box.sending_out_power"), mouseX, mouseY);
            } else {
                renderTooltip(matrixStack, TextUtil.translateString("text.voluminousenergy.battery_box.receiving_power"), mouseX, mouseY);
            }
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
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
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);
            drawIOSideHelper();
            for (VEBatterySwitchManager switchManager : tileEntity.getSwitchManagers()) {
                updateSlotPairButton(switchManager.isFlipped(),switchManager.getSlot());
            }
            updateSendOutPowerButton(tileEntity.getPowerIOManager().isFlipped());
        }
    }

    public void updateSlotPairButton(boolean status, int id){
        for(Widget widget : this.renderables){
            if(widget instanceof BatteryBoxSlotPairButton batteryBoxSlotPairButton){
                if(batteryBoxSlotPairButton.getId() == id){
                    batteryBoxSlotPairButton.setStatus(status);
                }
            }
        }
    }

    public void updateSendOutPowerButton(boolean status){
        for(Widget widget : this.renderables){
            if(widget instanceof BatteryBoxSendOutPowerButton batteryBoxSendOutPowerButton){
                batteryBoxSendOutPowerButton.setStatus(status);
            }
        }
    }
}
