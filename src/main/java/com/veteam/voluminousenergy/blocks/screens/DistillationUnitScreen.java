package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.blocks.tiles.DistillationUnitTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DistillationUnitScreen extends ContainerScreen<DistillationUnitContainer> {
    private DistillationUnitTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/distillation_unit_gui.png");

    public DistillationUnitScreen(DistillationUnitContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (DistillationUnitTile) screenContainer.tileEntity;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderHoveredTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY) {
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Distillation Unit",8,6,0xffffff);
        this.font.func_243246_a(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.ySize - 96 + 2), 16777215);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack,int mouseX, int mouseY) {
        if (isPointInRegion(11, 16, 12, 49, mouseX, mouseY)){
            renderTooltip(matrixStack, ITextComponent.getTextComponentOrEmpty(container.getEnergy() + " FE" + " / " + Config.DISTILLATION_UNIT_MAX_POWER.get() + " FE"), mouseX, mouseY);
        }

        if (isPointInRegion(61, 18, 12, 50, mouseX, mouseY)){ // Input Tank
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isPointInRegion(119, 11, 12, 50, mouseX, mouseY)){ // First Output Tank
            int amount = tileEntity.getFluidStackFromTank(1).getAmount();
            String name = tileEntity.getFluidStackFromTank(1).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isPointInRegion(157, 11, 12, 50, mouseX, mouseY)){ // Second Output Tank
            int amount = tileEntity.getFluidStackFromTank(2).getAmount();
            String name = tileEntity.getFluidStackFromTank(2).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderHoveredTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.xSize, this.ySize);
        if(tileEntity != null && tileEntity.getMultiblockValidity()){
            int progress = tileEntity.progressCounterPX(9);
            int power = container.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(matrixStack,i+81, j+31, 176, 0, progress, 17);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);

            VERender.renderGuiTank(tileEntity.getFluidStackFromTank(0),tileEntity.getTankCapacity(), i + 61, j + 18, 0, 12, 50);

            try{
                VERender.renderGuiTank(tileEntity.getFluidStackFromTank(1),tileEntity.getTankCapacity(), i + 119, j + 11, 0, 12, 50);
            } catch (Exception e){ }

            try{
                VERender.renderGuiTank(tileEntity.getFluidStackFromTank(2),tileEntity.getTankCapacity(), i + 157, j + 11, 0, 12, 50);
            } catch (Exception e){ }

        } else {
            this.font.drawString(matrixStack, "MULTIBLOCK IS INVALID! Please build a 3x3x3 of Aluminum Machine Casings BEHIND the Distillation Unit",8.0F, (float)(this.ySize - 96 + 20), 16777215);
        }

    }

}