package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class BatteryBoxScreen extends ContainerScreen<BatteryBoxContainer> {

    private BatteryBoxTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private boolean openedIOGui = false;

    public BatteryBoxScreen(BatteryBoxContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (BatteryBoxTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        this.addButton(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Top row
        this.addButton(new SlotBoolButton(tileEntity.topManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.topManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Bottom Row
        this.addButton(new SlotBoolButton(tileEntity.bottomManager, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.bottomManager, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Slot arrows
        this.addButton(new BatteryBoxSlotPairButton((this.width/2)-54,topPos + 34, 0, tileEntity, button -> { }));
        this.addButton(new BatteryBoxSlotPairButton((this.width/2)-36,topPos + 34, 1, tileEntity, button -> { }));
        this.addButton(new BatteryBoxSlotPairButton((this.width/2)-18,topPos + 34, 2, tileEntity, button -> { }));
        this.addButton(new BatteryBoxSlotPairButton((this.width/2), topPos + 34, 3, tileEntity, button -> { }));
        this.addButton(new BatteryBoxSlotPairButton((this.width/2)+18,topPos + 34, 4, tileEntity, button -> { }));
        this.addButton(new BatteryBoxSlotPairButton((this.width/2)+36,topPos + 34, 5, tileEntity, button -> { }));

        // Send Out Power Button
        this.addButton(new BatteryBoxSendOutPowerButton((this.width/2)-79,topPos + 3, tileEntity,button -> { }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("battery_box"), 34.0F, 6.0F, 16777215);
        this.font.drawShadow(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.imageWidth - 96 - 8), 16777215);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY))
            renderTooltip(matrixStack, ITextComponent.nullToEmpty(menu.getEnergy() + " FE" + " / " + Config.BATTERY_BOX_MAX_POWER.get() + " FE"), mouseX, mouseY);
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
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

    private void drawIOSideHelper(MatrixStack matrixStack, int i, int j, int mouseX, int mouseY, float partialTicks){
        for(Widget widget : this.buttons){
            if (widget instanceof ioMenuButton){
                if (((ioMenuButton) widget).shouldIOBeOpen() && !openedIOGui) { // This means IO Should be open
                    this.buttons.forEach(button ->{
                        if (button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(true);
                            //informTileOfIOButton(true);
                            openedIOGui = !openedIOGui;
                        }
                    });
                } else {
                    this.buttons.forEach(button ->{
                        if(button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(false);
                            //informTileOfIOButton(false);
                            openedIOGui = !openedIOGui;
                        }
                    });
                }
            }
        }
    }

    public void updateButtonDirection(int direction, int slotId){
        for(Widget widget: this.buttons){
            if(widget instanceof SlotDirectionButton && ((SlotDirectionButton) widget).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) widget).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Widget widget: this.buttons){
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
        if(uuid != null){
            VENetwork.channel.sendToServer(new UuidPacket(uuid, connection));
        }
    }

    public void updateSlotPairButton(boolean status, int id){
        for(Widget widget : this.buttons){
            if(widget instanceof BatteryBoxSlotPairButton){
                if(((BatteryBoxSlotPairButton) widget).getId() == id){
                    ((BatteryBoxSlotPairButton) widget).setStatus(status);
                }
            }
        }
    }

    public void updateSendOutPowerButton(boolean status){
        for(Widget widget : this.buttons){
            if(widget instanceof BatteryBoxSendOutPowerButton){
                ((BatteryBoxSendOutPowerButton) widget).setStatus(status);
            }
        }
    }
}
