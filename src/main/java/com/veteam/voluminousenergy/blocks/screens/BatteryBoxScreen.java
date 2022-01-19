package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.BatteryBoxContainer;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.buttons.batteryBox.BatteryBoxSendOutPowerButton;
import com.veteam.voluminousenergy.tools.buttons.batteryBox.BatteryBoxSlotPairButton;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UuidPacket;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.UUID;

public class BatteryBoxScreen extends AbstractContainerScreen<BatteryBoxContainer> {

    private final BatteryBoxTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private boolean openedIOGui = false;

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


        boolean[] slotValues = this.tileEntity.getDoDischargeInstead();

        // Slot arrows
        addRenderableWidget(new BatteryBoxSlotPairButton((this.width/2)-54,topPos + 34, 0, tileEntity, button -> { },slotValues[0]));
        addRenderableWidget(new BatteryBoxSlotPairButton((this.width/2)-36,topPos + 34, 1, tileEntity, button -> { },slotValues[1]));
        addRenderableWidget(new BatteryBoxSlotPairButton((this.width/2)-18,topPos + 34, 2, tileEntity, button -> { },slotValues[2]));
        addRenderableWidget(new BatteryBoxSlotPairButton((this.width/2), topPos + 34, 3, tileEntity, button -> { },slotValues[3]));
        addRenderableWidget(new BatteryBoxSlotPairButton((this.width/2)+18,topPos + 34, 4, tileEntity, button -> { },slotValues[4]));
        addRenderableWidget(new BatteryBoxSlotPairButton((this.width/2)+36,topPos + 34, 5, tileEntity, button -> { },slotValues[5]));

        // Send Out Power Button
        addRenderableWidget(new BatteryBoxSendOutPowerButton((this.width/2)-79,topPos + 3, tileEntity,button -> { }));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("battery_box"), 34.0F, 6.0F, 16777215);
        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageWidth - 96 - 8), 16777215);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY))
            renderTooltip(matrixStack, Component.nullToEmpty(menu.getEnergy() + " FE" + " / " + Config.BATTERY_BOX_MAX_POWER.get() + " FE"), mouseX, mouseY);
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
            informTileOfIOButton(true);
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
            drawIOSideHelper(matrixStack,i,j,mouseX,mouseY,partialTicks);
        }
    }

    // TODO this method is literally copypasta'd everywhere replace it
    private void drawIOSideHelper(PoseStack matrixStack, int i, int j, int mouseX, int mouseY, float partialTicks){
        for(Widget widget : this.renderables){
            if (widget instanceof ioMenuButton){
                if (((ioMenuButton) widget).shouldIOBeOpen() && !openedIOGui) { // This means IO Should be open
                    this.renderables.forEach(button ->{
                        if (button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(true);
                            informTileOfIOButton(true);
                            //openedIOGui = !openedIOGui;
                        }
                    });
                } else {
                    this.renderables.forEach(button ->{
                        if(button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(false);
                            informTileOfIOButton(false);
                            //openedIOGui = !openedIOGui;
                        }
                    });
                }
            }
        }
    }

    public void updateButtonDirection(int direction, int slotId){
        for(Widget widget: this.renderables){
            if(widget instanceof SlotDirectionButton && ((SlotDirectionButton) widget).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) widget).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Widget widget: this.renderables){
            if(widget instanceof SlotBoolButton && ((SlotBoolButton) widget).getAssociatedSlotId() == slotId){
                //VoluminousEnergy.LOGGER.debug("About to update the status of the Status/boolean Button.");
                ((SlotBoolButton) widget).toggleRender(true);
                ((SlotBoolButton) widget).setStatus(status);
                ((SlotBoolButton) widget).toggleRender(false);
            }
        }
    }

    public void informTileOfIOButton(boolean connection){
        UUID uuid = Minecraft.getInstance().player.getUUID();
        VENetwork.channel.sendToServer(new UuidPacket(uuid, connection));
    }

    public void updateSlotPairButton(boolean status, int id){
        System.out.println("receiving update");
        for(Widget widget : this.renderables){
            if(widget instanceof BatteryBoxSlotPairButton){
                if(((BatteryBoxSlotPairButton) widget).getId() == id){
                    ((BatteryBoxSlotPairButton) widget).setStatus(status);
                }
            }
        }
    }

    public void updateSendOutPowerButton(boolean status){
        for(Widget widget : this.renderables){
            if(widget instanceof BatteryBoxSendOutPowerButton){
                ((BatteryBoxSendOutPowerButton) widget).setStatus(status);
            }
        }
    }
}
