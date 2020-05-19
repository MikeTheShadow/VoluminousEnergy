package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalAgitatorContainer;
import com.veteam.voluminousenergy.blocks.tiles.CentrifugalAgitatorTile;
import com.veteam.voluminousenergy.tools.VERender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CentrifugalAgitatorScreen extends ContainerScreen<CentrifugalAgitatorContainer> {
    private CentrifugalAgitatorTile tileEntity;
    private ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png"); //TODO: Make new gui

    public CentrifugalAgitatorScreen(CentrifugalAgitatorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (CentrifugalAgitatorTile) screenContainer.tileEntity;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(Minecraft.getInstance().fontRenderer, "Centrifugal Agitator",8,6,0xffffff);
        //this.font.drawString(new TranslationTextComponent("container.inventory", new Object[0]).getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
        drawString(Minecraft.getInstance().fontRenderer, (float)container.getEnergy()/1000 + " kFE", 8, (this.ySize - 96 + 2), 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        if(tileEntity != null){
            int progress = tileEntity.progressCounterPX(24);
            int power = container.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(i+79, j+31, 176, 0, 17, progress);
            this.blit(i+11, j+16, 176,24, 12, power);

            VERender.renderGuiTank(tileEntity.tank0.get(),tileEntity.getTankCapacity(), i + 29, j + 18, 0, 12, 50);
            //VERender.renderGuiTank(tileEntity.tank1.get(),tileEntity.getTankCapacity(), i + 45, j + 18, 0, 12, 50);
            //VERender.renderGuiTank(tileEntity.tank2.get(),tileEntity.getTankCapacity(), i + 61, j + 18, 0, 12, 50);

        }

    }

}

