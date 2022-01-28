package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.buttons.VEPowerIOManager;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSendOutPowerPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BatteryBoxSendOutPowerButton extends VEIOButton {

    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");

    private final BatteryBoxTile batteryBoxTile;
    private boolean sendOutPower;
    private int u= 0;
    private int v= 166;
    private final VEPowerIOManager powerIOManager;

    public BatteryBoxSendOutPowerButton(VEPowerIOManager powerIOManager, int x, int y, BatteryBoxTile batteryBoxTile, OnPress onPress) {
        super(x, y, 18, 20, Component.nullToEmpty(""), button -> {
            ((BatteryBoxSendOutPowerButton) button).cycle();
            onPress.onPress(button);
        });
        this.powerIOManager = powerIOManager;
        this.batteryBoxTile = batteryBoxTile;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 12;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        RenderSystem.setShaderTexture(0, GUI_TOOLS);

        if(!isHovered) u = 96;
        else u = 112;

        if(!sendOutPower) v = 178;
        else v = 166;

        blit(matrixStack, this.x, this.y, this.u, this.v, this.width, this.height);
    }

    private void cycle(){
        sendOutPower = !sendOutPower;
        powerIOManager.setFlipped(true);
        this.batteryBoxTile.updateSendOutPower(sendOutPower);
    }

    @Override
    public void onPress(){
        cycle();
        VENetwork.channel.sendToServer(new BatteryBoxSendOutPowerPacket(this.sendOutPower));
    }

    public void setStatus(boolean status){
        sendOutPower = status;
        powerIOManager.setFlipped(sendOutPower);
    }
}
