package com.veteam.voluminousenergy.tools.buttons.tanks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TankBoolButton extends VEIOButton {
    private boolean enable = false;
    private RelationalTank tank;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public TankBoolButton(RelationalTank tank, int x, int y, Button.OnPress onPress) {
        super(x, y, 16, 15, Component.nullToEmpty(""), button -> {
            ((TankBoolButton) button).cycle();
            onPress.onPress(button);
        });
        setX(x);
        setY(y);
        this.width = 16;
        this.height = 15;
        this.tank = tank;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        RenderSystem.setShaderTexture(0, texture);
        enable = this.tank.getSideStatus();
        if(!enable){
            blit(matrixStack, getX(), getY(), 213, 0, this.width, this.height);
        } else {
            blit(matrixStack, getX(), getY(), 213, 15, this.width, this.height);
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
