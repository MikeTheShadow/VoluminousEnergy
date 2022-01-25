package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSlotPairPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BatteryBoxSlotPairButton extends VEIOButton {

    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/battery_box_gui.png");

    private int id;
    private BatteryBoxTile batteryBoxTile;
    private boolean isTopIngress;
    private int u= 0;
    private int v= 166;


    public BatteryBoxSlotPairButton(int x, int y, int id, BatteryBoxTile batteryBoxTile, OnPress onPress,boolean cycle) {
        super(x, y, 18, 20, Component.nullToEmpty(""), button -> {
            ((BatteryBoxSlotPairButton) button).cycle();
            onPress.onPress(button);
        });
        this.id = id;
        this.batteryBoxTile = batteryBoxTile;
        this.x = x;
        this.y = y;
        this.width = 18;
        this.height = 20;
        if(cycle) cycle();
    }

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        RenderSystem.setShaderTexture(0, GUI_TOOLS);

        if(!isHovered) v = 166;
        else v = 186;

        if(isTopIngress) u = 0;
        else u = 18;

        blit(matrixStack, this.x, this.y, this.u, this.v, this.width, this.height);
    }

    private void cycle(){
        isTopIngress = !isTopIngress;
    }

    @Override
    public void onPress(){
        cycle();
        batteryBoxTile.updateSlotPair(this.isTopIngress,this.id);
        batteryBoxTile.setChanged();
        VENetwork.channel.sendToServer(new BatteryBoxSlotPairPacket(this.isTopIngress, this.id));
    }

    public int getId(){
        return id;
    }

    public void setStatus(boolean status){
        isTopIngress = status;
    }
}
