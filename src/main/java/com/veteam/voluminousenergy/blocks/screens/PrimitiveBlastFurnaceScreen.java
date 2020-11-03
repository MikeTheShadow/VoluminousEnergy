package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveBlastFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PrimitiveBlastFurnaceScreen extends ContainerScreen<PrimitiveBlastFurnaceContainer> {
    private PrimitiveBlastFurnaceTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID,"textures/gui/primitiveblastgui.png");

    public PrimitiveBlastFurnaceScreen(PrimitiveBlastFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = (PrimitiveBlastFurnaceTile) screenContainer.tileEntity;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY) {
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Primitive Blast Furnace",8,6,0xffffff);
        this.font.func_243246_a(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.ySize - 96 + 2), 16777215);
        drawString(matrixStack,Minecraft.getInstance().fontRenderer,"Ticks:" + tileEntity.counter(),8,36,0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.xSize, this.ySize);
        if(tileEntity != null){
            int progress = tileEntity.progressCounterPX(24);
            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(matrixStack,i+84, j+24, 180, 0, progress, 16);
            //this.blit(i,j,180,1,progress,15);
        }

    }

}
