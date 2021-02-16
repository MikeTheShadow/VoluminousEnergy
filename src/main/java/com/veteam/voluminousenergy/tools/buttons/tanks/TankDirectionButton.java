package com.veteam.voluminousenergy.tools.buttons.tanks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TankDirectionButton extends VEIOButton {
    private RelationalTank tank;
    private Direction direction;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public TankDirectionButton(RelationalTank tank, int x, int y, IPressable onPress) {
        super(x, y, 96, 20, ITextComponent.getTextComponentOrEmpty(""), button -> {
            ((TankDirectionButton) button).cycle();
            onPress.onPress(button);
        });
        this.x = x;
        this.y = y;
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
    public void renderButton(MatrixStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        Minecraft.getInstance().getTextureManager().bindTexture(texture);

        if(!isHovered){ // x: 96 y:20
            blit(matrixStack, this.x, this.y, 0, 166, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, 0, 186, this.width, this.height);
        }

        // Print text
        ITextComponent textComponent = TextUtil.slotNameWithDirection(tank.getTranslationKey(), tank.getSideDirection(), tank.getId());
        drawCenteredString(matrixStack, Minecraft.getInstance().fontRenderer, textComponent.getString(),(this.x)+48,(this.y)+5,0xffffff);
    }

    @Override
    public void onPress(){
        if(!render) return;
        cycle();
        this.tank.setSideDirection(direction);
        VENetwork.channel.sendToServer(new TankDirectionPacket(this.getDirection().getIndex(),this.getId()));
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
