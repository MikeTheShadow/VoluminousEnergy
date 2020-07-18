package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PrimitiveStirlingGeneratorScreen extends ContainerScreen<PrimitiveStirlingGeneratorContainer> {

    private ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/primitivestirlinggenerator_gui.png");

    public PrimitiveStirlingGeneratorScreen(PrimitiveStirlingGeneratorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        //this.font.drawString(this.title.getFormattedText(), 8.0F,6.0F,4210752);
        drawString(Minecraft.getInstance().fontRenderer, "Primitive Stirling Generator",8,6,0xffffff);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),8.0F, (float) (this.ySize - 96 - 12), 4210752);
        drawString(Minecraft.getInstance().fontRenderer, "Energy: " + container.getEnergy(), 10, 22, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
