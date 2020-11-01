package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PrimitiveStirlingGeneratorScreen extends ContainerScreen<PrimitiveStirlingGeneratorContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/primitivestirlinggenerator_gui.png");

    public PrimitiveStirlingGeneratorScreen(PrimitiveStirlingGeneratorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderHoveredTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY){
        //this.font.drawString(this.title.getFormattedText(), 8.0F,6.0F,4210752);
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Primitive Stirling Generator",8,6,0xffffff);
        this.font.drawString(matrixStack,this.playerInventory.getDisplayName().getString(),8.0F, (float) (this.ySize - 96 - 12), 4210752);
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Energy: " + container.getEnergy(), 10, 22, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrixStack,relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
