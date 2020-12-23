package com.veteam.voluminousenergy.tools.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ioMenuButton extends Button {
    private boolean cycled = false;
    private final ResourceLocation guiLocation;
    private int blitTopLeft;
    private int x;
    private int y;

    public ioMenuButton(ResourceLocation guiLocation, int x, int y, int width, int height, int blitTopLeft, IPressable onPress){
        super(x, y, width, height, ITextComponent.getTextComponentOrEmpty(""), button -> {
            ((ioMenuButton) button).cycleMode();
            onPress.onPress(button);
        });
        this.guiLocation = guiLocation;
        this.blitTopLeft = blitTopLeft;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void cycleMode(){ cycled = !cycled;}

    @Override
    public void renderButton(MatrixStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(guiLocation);
        GlStateManager.disableDepthTest();
        blit(matrixStack, this.x, this.y, this.blitTopLeft, 0, this.width, this.height);
        GlStateManager.enableDepthTest();

        if(!isHovered){
            blit(matrixStack, this.x, this.y, this.blitTopLeft, 0, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, this.blitTopLeft, 19, this.width, this.height);
        }
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "IO",(this.x)+5,(this.y)+5,0xffffff);
    }

    @Override
    public void onPress(){
        cycleMode();
    }

    public boolean shouldIOBeOpen(){
        return cycled;
    }
}
