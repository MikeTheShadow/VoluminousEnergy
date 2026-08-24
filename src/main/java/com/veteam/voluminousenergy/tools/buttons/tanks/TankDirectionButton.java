package com.veteam.voluminousenergy.tools.buttons.tanks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket.TankDirectionPayload;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class TankDirectionButton extends VEIOButton {
    private VERelationalTank tank;
    private Direction direction;
    private final Identifier texture = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public TankDirectionButton(VERelationalTank tank, int x, int y, OnPress onPress) {
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

    private void cycle() {
        switch (direction) {
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

    private void setDirection(Direction dir) {
        this.direction = dir;
        this.tank.setSideDirection(dir);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3) {
        if (!render) return;

        if (!isHovered) { // x: 96 y:20
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 166, this.width, this.height, 256, 256);
        } else {
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 186, this.width, this.height, 256, 256);
        }

        // Print text
        Component textComponent = TextUtil.slotNameWithDirection(tank.getHoverName(), tank.getSideDirection(), tank.getSlotNum());
        matrixStack.centeredText(Minecraft.getInstance().font, textComponent.getString(), (getX()) + 48, (getY()) + 5, 0xffffff);
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        if (!render) return;
        cycle();
        ClientPacketDistributor.sendToServer(new TankDirectionPayload(this.getDirection().get3DDataValue(), this.getId()));
    }

    public Direction getDirection() {
        return direction;
    }

    public int getId() {
        return this.tank.getSlotNum();
    }

    public void setDirectionFromInt(int sideInt) {
        setDirection(IntToDirection.IntegerToDirection(sideInt));
    }
}
