package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSlotPairPacket.BatteryBoxSlotPairPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class BatteryBoxSlotPairButton extends VEIOButton {

    private static final Identifier GUI_TOOLS = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");

    private int id;
    private boolean isTopIngress;
    private int u = 0;
    private int v = 166;
    private final VEBatterySwitchManager veBatterySwitchManager;

    public BatteryBoxSlotPairButton(VEBatterySwitchManager veBatterySwitchManager, int x, int y, int id, OnPress onPress) {
        super(x, y, 18, 20, Component.nullToEmpty(""), button -> {
            ((BatteryBoxSlotPairButton) button).cycle();
            onPress.onPress(button);
        });
        this.veBatterySwitchManager = veBatterySwitchManager;
        this.id = id;
        setX(x);
        setY(y);
        this.width = 18;
        this.height = 20;
        this.isTopIngress = veBatterySwitchManager.isFlipped();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3) {
        if (!isHovered) v = 166;
        else v = 186;

        if (isTopIngress) u = 0;
        else u = 18;

        matrixStack.blit(RenderPipelines.GUI_TEXTURED, GUI_TOOLS, getX(), getY(), this.u, this.v, this.width, this.height, 256, 256);
    }

    private void cycle() {
        isTopIngress = !isTopIngress;
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        cycle();
        veBatterySwitchManager.setFlipped(isTopIngress);
        ClientPacketDistributor.sendToServer(new BatteryBoxSlotPairPayload(isTopIngress, this.id));
    }

    public int getId() {
        return id;
    }

    public void setStatus(boolean status) {
        isTopIngress = status;
        this.veBatterySwitchManager.setFlipped(status);
    }
}
