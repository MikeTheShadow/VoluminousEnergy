package com.veteam.voluminousenergy.tools.buttons.slots;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SlotDirectionButton extends VEIOButton {
    private VESlotManager slotManager;
    private Direction direction;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public SlotDirectionButton(VESlotManager slotManager, int x, int y, OnPress onPress) {
        super(x, y, 96, 20, Component.nullToEmpty(""), button -> {
            ((SlotDirectionButton) button).cycle();
            onPress.onPress(button);
        });
        this.x = x;
        this.y = y;
        this.width = 96;
        this.height = 20;
        this.slotManager = slotManager;
        this.direction = slotManager.getDirection();
    }

    private void cycle(){
        switch(direction){
            case UP:
                direction = Direction.DOWN;
                break;
            case DOWN:
                direction = Direction.NORTH;
                break;
            case NORTH:
                direction = Direction.SOUTH;
                break;
            case SOUTH:
                direction = Direction.EAST;
                break;
            case EAST:
                direction = Direction.WEST;
                break;
            default:
                direction = Direction.UP;
        }
    }

    private void setDirection(Direction dir){
        this.direction = dir;
        this.slotManager.setDirection(dir);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        RenderSystem.setShaderTexture(0, texture);

        if(!isHovered){ // x: 96 y:20
            blit(matrixStack, this.x, this.y, 0, 166, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, 0, 186, this.width, this.height);
        }

        // Print text
        Component textComponent = TextUtil.slotNameWithDirection(slotManager.getTranslationKey(), slotManager.getDirection(), slotManager.getSlotNum());
        drawCenteredString(matrixStack, Minecraft.getInstance().font, textComponent.getString(),(this.x)+48,(this.y)+5,0xffffff);
    }

    @Override
    public void onPress(){
        if(!render) return;
        cycle();
        this.slotManager.setDirection(direction);
        VENetwork.channel.sendToServer(new DirectionButtonPacket(this.getDirection().get3DDataValue(),this.getAssociatedSlotId()));
    }

    public Direction getDirection(){
        return direction;
    }

    public int getAssociatedSlotId(){
        return this.slotManager.getSlotNum();
    }

    public void setDirectionFromInt(int sideInt){
        setDirection(IntToDirection.IntegerToDirection(sideInt));
    }
}
