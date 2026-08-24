package com.veteam.voluminousenergy.tools.buttons.tanks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class TankBoolButton extends VEIOButton {
    private boolean enable = false;
    private VERelationalTank tank;
    private final Identifier texture = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public TankBoolButton(VERelationalTank tank, int x, int y, Button.OnPress onPress) {
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
    protected void extractContents(GuiGraphicsExtractor matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3) {
        if (!render) return;
        boolean currentStatus = this.tank.getSideStatus();
        if (!currentStatus) {
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 213, 0, this.width, this.height, 256, 256);
        } else {
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 213, 15, this.width, this.height, 256, 256);
        }
    }

    private void cycle() {
        enable = !enable;
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        if (!render) return;
        cycle();
        ClientPacketDistributor.sendToServer(new TankBoolPacket.TankBoolPacketPayload(this.status(),this.getId()));
    }

    public boolean status() {
        return enable;
    }

    public int getId() {
        return this.tank.getSlotNum();
    }

    public void setStatus(boolean status) {
        enable = status;
        this.tank.setSideStatus(status);
    }
}
