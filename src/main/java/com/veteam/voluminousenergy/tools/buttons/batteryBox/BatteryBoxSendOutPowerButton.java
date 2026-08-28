package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class BatteryBoxSendOutPowerButton extends VEIOButton {

    private static final Identifier GUI_TOOLS = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");

    private final VETileEntity tile;
    private boolean sendOutPower;
    private int u = 0;
    private int v = 166;

    public BatteryBoxSendOutPowerButton(int x, int y, VETileEntity tile, OnPress onPress) {
        super(x, y, 18, 20, Component.nullToEmpty(""), button -> {
            ((BatteryBoxSendOutPowerButton) button).cycle();
            onPress.onPress(button);
        });
        this.tile = tile;
        setX(x);
        setY(y);
        this.width = 16;
        this.height = 12;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3) {
        if (!isHovered) u = 96;
        else u = 112;

        if (!sendOutPower) v = 178;
        else v = 166;

        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI_TOOLS, getX(), getY(), this.u, this.v, this.width, this.height, 256, 256);
    }

    private void cycle() {
        sendOutPower = !sendOutPower;
        tile.setSendsOutPower(sendOutPower);
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        cycle();
        ClientPacketDistributor.sendToServer(new BatteryBoxSendOutPowerPayload(this.sendOutPower));
    }

    public void setStatus(boolean status) {
        sendOutPower = status;
    }
}
