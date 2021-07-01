package com.veteam.voluminousenergy.tools.buttons.tanks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TankBoolButton extends VEIOButton {
    private boolean enable = false;
    private RelationalTank tank;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public TankBoolButton(RelationalTank tank, int x, int y, Button.IPressable onPress) {
        super(x, y, 16, 15, ITextComponent.nullToEmpty(""), button -> {
            ((TankBoolButton) button).cycle();
            onPress.onPress(button);
        });
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 15;
        this.tank = tank;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        Minecraft.getInstance().getTextureManager().bind(texture);
        enable = this.tank.getSideStatus();
        if(!enable){
            blit(matrixStack, this.x, this.y, 213, 0, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, 213, 15, this.width, this.height);
        }
    }

    private void cycle(){ enable = !enable; }

    @Override
    public void onPress(){
        if(!render) return;
        cycle();
        this.tank.setSideStatus(enable);
        VENetwork.channel.sendToServer(new TankBoolPacket(this.status(), this.getId()));
    }

    public boolean status(){ return enable; }

    public int getId(){
        return this.tank.getId();
    }

    public void setStatus(boolean status) {
        enable = status;
        this.tank.setSideStatus(status);
    }
}
