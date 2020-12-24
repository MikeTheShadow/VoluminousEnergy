package com.veteam.voluminousenergy.tools.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.VESidedItemManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class boolButton extends VEIOButton{
    private boolean enable = false;
    private VESidedItemManager slotManager;
    private final ResourceLocation texture = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");

    public boolButton(VESidedItemManager slotManager, int x, int y, IPressable onPress) {
        super(x, y, 16, 15, ITextComponent.getTextComponentOrEmpty(""), button -> {
            ((boolButton) button).cycle();
            onPress.onPress(button);
        });
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 15;
        this.slotManager = slotManager;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3){
        if(!render) return;
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        enable = slotManager.getStatus();
        if(!enable){
            blit(matrixStack, this.x, this.y, 213, 0, this.width, this.height);
        } else {
            blit(matrixStack, this.x, this.y, 213, 15, this.width, this.height);
        }
    }

    private void cycle(){ enable = !enable; }

    @Override
    public void onPress(){
        if(!render) return;
        cycle();
        this.slotManager.setStatus(enable);
    }

    public boolean status(){ return enable; }
}
