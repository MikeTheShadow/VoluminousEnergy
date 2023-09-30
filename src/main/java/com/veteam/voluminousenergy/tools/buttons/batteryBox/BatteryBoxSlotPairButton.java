package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSlotPairPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.PacketDistributor;

public class BatteryBoxSlotPairButton extends VEIOButton {

    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");

    private int id;
    private boolean isTopIngress;
    private int u= 0;
    private int v= 166;
    private final VEBatterySwitchManager  veBatterySwitchManager;

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
    public void renderWidget(GuiGraphics matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        RenderSystem.setShaderTexture(0, GUI_TOOLS);

        if(!isHovered) v = 166;
        else v = 186;

        if(isTopIngress) u = 0;
        else u = 18;

        matrixStack.blit(GUI_TOOLS, getX(), getY(), this.u, this.v, this.width, this.height);
    }

    private void cycle(){isTopIngress = !isTopIngress;}

    @Override
    public void onPress(){
        cycle();
        veBatterySwitchManager.setFlipped(isTopIngress);
        VENetwork.channel.send(new BatteryBoxSlotPairPacket(isTopIngress, this.id), PacketDistributor.SERVER.noArg());
    }

    public int getId(){
        return id;
    }

    public void setStatus(boolean status){
        isTopIngress = status;
        this.veBatterySwitchManager.setFlipped(status);
    }
}
