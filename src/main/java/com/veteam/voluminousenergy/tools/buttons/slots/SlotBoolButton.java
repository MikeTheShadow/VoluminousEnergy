package com.veteam.voluminousenergy.tools.buttons.slots;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlotBoolButton extends VEIOButton {
    private boolean enable = false;
    private VESlotManager slotManager;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public SlotBoolButton(VESlotManager slotManager, int x, int y, OnPress onPress) {
        super(x, y, 16, 15, Component.nullToEmpty(""), button -> {
            ((SlotBoolButton) button).cycle();
            onPress.onPress(button);
        });

        setX(x);
        setY(y);
        this.width = 16;
        this.height = 15;
        this.slotManager = slotManager;
    }

    @Override
    public void renderWidget(PoseStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        RenderSystem.setShaderTexture(0, texture);
        enable = slotManager.getStatus();
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
        this.slotManager.setStatus(enable);
        VENetwork.channel.sendToServer(new BoolButtonPacket(this.status(), this.getAssociatedSlotId()));
    }

    public boolean status(){ return enable; }

    public int getAssociatedSlotId(){
        return this.slotManager.getSlotNum();
    }

    public void setStatus(boolean status){
        enable = status;
        slotManager.setStatus(status);
    }
}
