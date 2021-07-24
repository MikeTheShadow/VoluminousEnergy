package com.veteam.voluminousenergy.tools.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.minecraft.client.Minecraft.getInstance;

@OnlyIn(Dist.CLIENT)
public class ioMenuButton extends Button {
    private boolean cycled = false;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");
    private int x;
    private int y;

    public ioMenuButton(int x, int y, OnPress onPress){
        super(x, y, 20, 18, Component.nullToEmpty(""), button -> {
            ((ioMenuButton) button).cycleMode();
            onPress.onPress(button);
        });
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 18;
    }

    private void cycleMode(){ cycled = !cycled;}

    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        RenderSystem.setShaderTexture(0, texture);

        if(!isHovered){
            blit(matrixStack, this.x, this.y, 193, 0, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, 193, 19, this.width, this.height);
        }
        drawString(matrixStack, getInstance().font, "IO",(this.x)+5,(this.y)+5,0xffffff);
    }

    @Override
    public void onPress(){
        cycleMode();
        //VoluminousEnergy.LOGGER.debug(this.cycled);
    }

    public boolean shouldIOBeOpen(){
        return cycled;
    }
}
