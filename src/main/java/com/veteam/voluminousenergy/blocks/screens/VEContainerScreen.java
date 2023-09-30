package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UuidPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public abstract class VEContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public static final int WHITE_TEXT_COLOUR = 16777215;
    public static final int GREY_TEXT_COLOUR = 0x606060;

    public static final Style WHITE_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(WHITE_TEXT_COLOUR);
    public static final Style GREY_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(GREY_TEXT_COLOUR);

    public VEContainerScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        this.renderables.stream().filter(Renderable -> Renderable instanceof ioMenuButton).forEach(button -> {
            if (((ioMenuButton) button).shouldIOBeOpen()) {
                renderSlotAndTankLabels(matrixStack, mouseX, mouseY);
            }
        });
    }

    protected abstract void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY);

    /* GuiGraphics matrixStack, int i, int j, int mouseX, int mouseY, float partialTicks old arguments in case you want them back*/
    public void drawIOSideHelper(){

        for(Renderable Renderable : this.renderables){
            if (Renderable instanceof ioMenuButton){
                if (((ioMenuButton) Renderable).shouldIOBeOpen()) { // This means IO Should be open
                    this.renderables.forEach(button ->{
                        if (button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(true);
                        }
                    });
                } else {
                    this.renderables.forEach(button ->{
                        if(button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(false);
                        }
                    });
                }
            }
        }
    }

    public void updateButtonDirection(int direction, int slotId){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof SlotDirectionButton && ((SlotDirectionButton) Renderable).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) Renderable).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof SlotBoolButton && ((SlotBoolButton) Renderable).getAssociatedSlotId() == slotId){
                ((SlotBoolButton) Renderable).toggleRender(true);
                ((SlotBoolButton) Renderable).setStatus(status);
                ((SlotBoolButton) Renderable).toggleRender(false);
            }
        }
    }

    public void updateTankDirection(int direction, int id){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof TankDirectionButton && ((TankDirectionButton) Renderable).getId() == id ){
                ((TankDirectionButton) Renderable).setDirectionFromInt(direction);
            }
        }
    }

    public void updateTankStatus(boolean status, int id){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof TankBoolButton && ((TankBoolButton) Renderable).getId() == id){
                ((TankBoolButton) Renderable).toggleRender(true);
                ((TankBoolButton) Renderable).setStatus(status);
                ((TankBoolButton) Renderable).toggleRender(false);
            }
        }
    }

    public void informTileOfIOButton(boolean connection){
        UUID uuid = Minecraft.getInstance().player.getUUID();

        VENetwork.channel.send(new UuidPacket(uuid, connection), PacketDistributor.SERVER.noArg());
    }

    protected boolean isHovering(Rect2i rect, double x, double y) {
        return this.isHovering(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), x, y);
    }

}
