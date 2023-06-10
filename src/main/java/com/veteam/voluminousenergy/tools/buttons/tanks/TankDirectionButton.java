package com.veteam.voluminousenergy.tools.buttons.tanks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TankDirectionButton extends VEIOButton {
    private RelationalTank tank;
    private Direction direction;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public TankDirectionButton(RelationalTank tank, int x, int y, OnPress onPress) {
        super(x, y, 96, 20, Component.nullToEmpty(""), button -> {
            ((TankDirectionButton) button).cycle();
            onPress.onPress(button);
        });
        setX(x);
        setY(y);
        this.width = 96;
        this.height = 20;
        this.tank = tank;
        this.direction = tank.getSideDirection();
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
        this.tank.setSideDirection(dir);
    }

    @Override
    public void renderWidget(GuiGraphics matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        RenderSystem.setShaderTexture(0, texture);

        if(!isHovered){ // x: 96 y:20
            matrixStack.blit(texture, getX(), getY(), 0, 166, this.width, this.height);
        } else {
            matrixStack.blit(texture, getX(), getY(), 0, 186, this.width, this.height);
        }

        // Print text
        Component textComponent = TextUtil.slotNameWithDirection(tank.getTranslationKey(), tank.getSideDirection(), tank.getId());
        matrixStack.drawCenteredString(Minecraft.getInstance().font, textComponent.getString(),(getX())+48,(getY())+5,0xffffff);
    }

    @Override
    public void onPress(){
        if(!render) return;
        cycle();
        this.tank.setSideDirection(direction);
        VENetwork.channel.sendToServer(new TankDirectionPacket(this.getDirection().get3DDataValue(),this.getId()));
    }

    public Direction getDirection(){
        return direction;
    }

    public int getId(){
        return this.tank.getId();
    }

    public void setDirectionFromInt(int sideInt){
        setDirection(IntToDirection.IntegerToDirection(sideInt));
    }
}
