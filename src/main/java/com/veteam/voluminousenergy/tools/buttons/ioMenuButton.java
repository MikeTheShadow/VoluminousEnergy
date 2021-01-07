package com.veteam.voluminousenergy.tools.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.minecraft.client.Minecraft.getInstance;

@OnlyIn(Dist.CLIENT)
public class ioMenuButton extends Button {
    private boolean cycled = false;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");
    private int x;
    private int y;

    public ioMenuButton(int x, int y, IPressable onPress){
        super(x, y, 20, 18, ITextComponent.getTextComponentOrEmpty(""), button -> {
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
    public void renderButton(MatrixStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        Minecraft minecraft = getInstance();
        minecraft.getTextureManager().bindTexture(texture);

        if(!isHovered){
            blit(matrixStack, this.x, this.y, 193, 0, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, 193, 19, this.width, this.height);
        }
        drawString(matrixStack, getInstance().fontRenderer, "IO",(this.x)+5,(this.y)+5,0xffffff);
    }

    @Override
    public void onPress(){
        cycleMode();
        VoluminousEnergy.LOGGER.debug(this.cycled);
    }

    public boolean shouldIOBeOpen(){
        return cycled;
    }
}
