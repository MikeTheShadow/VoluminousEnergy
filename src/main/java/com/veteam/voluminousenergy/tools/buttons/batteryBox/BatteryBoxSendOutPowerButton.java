package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class BatteryBoxSendOutPowerButton extends VEIOButton {

    private static final ResourceLocation GUI_TOOLS = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");

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
    public void renderWidget(GuiGraphics matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3) {
        RenderSystem.setShaderTexture(0, GUI_TOOLS);

        if (!isHovered) u = 96;
        else u = 112;

        if (!sendOutPower) v = 178;
        else v = 166;

        matrixStack.blit(GUI_TOOLS, getX(), getY(), this.u, this.v, this.width, this.height);
    }

    private void cycle() {
        sendOutPower = !sendOutPower;
        tile.setSendsOutPower(sendOutPower);
    }

    @Override
    public void onPress() {
        cycle();
        PacketDistributor.sendToServer(new BatteryBoxSendOutPowerPayload(this.sendOutPower));
    }

    public void setStatus(boolean status) {
        sendOutPower = status;
    }
}
