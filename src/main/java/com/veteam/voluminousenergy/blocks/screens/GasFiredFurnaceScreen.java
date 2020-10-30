package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.GasFiredFurnaceContainer;
import com.veteam.voluminousenergy.blocks.tiles.GasFiredFurnaceTile;
import com.veteam.voluminousenergy.tools.VERender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GasFiredFurnaceScreen extends ContainerScreen<GasFiredFurnaceContainer> {
private GasFiredFurnaceTile tileEntity;
private final ResourceLocation GUI=new ResourceLocation(VoluminousEnergy.MODID,"textures/gui/gas_fired_furnace_gui.png");

    public GasFiredFurnaceScreen(GasFiredFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (GasFiredFurnaceTile) screenContainer.tileEntity;
    }

    @Override
    public void render(int mouseX,int mouseY,float partialTicks){
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX,int mouseY){
        drawString(Minecraft.getInstance().fontRenderer,"Gas Fired Furnace",8,6,0xffffff);
        this.font.drawString(new TranslationTextComponent("container.inventory",new Object[0]).getFormattedText(),8.0F,(float)(this.ySize-96+2),4210752);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX,int mouseY){
        if(isPointInRegion(31,18,12,49,mouseX,mouseY)){
            int amount=tileEntity.getFluidFromTank().getAmount();
            String name=new TranslationTextComponent(tileEntity.getFluidFromTank().getTranslationKey(),new Object[0]).getFormattedText();
            renderTooltip(name+", "+amount+" mB / "+tileEntity.getTankCapacity()+" mB",mouseX,mouseY);
        } else if (isPointInRegion(54,54,16,16,mouseX,mouseY)){
            renderTooltip("Percent burned: " + tileEntity.progressFuelCounterPercent() + "%, Ticks Left: " + tileEntity.getFuelCounter(), mouseX, mouseY);
        } else if (isPointInRegion(81,32,9,17,mouseX,mouseY)){
            renderTooltip("Percent complete: " + tileEntity.progressCounterPercent() + "%, Ticks Left: " + tileEntity.getCounter(), mouseX, mouseY);
        }

        super.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks,int mouseX,int mouseY){
        RenderSystem.color4f(1.0F,1.0F,1.0F,1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i=(this.width-this.xSize)/2;
        int j=(this.height-this.ySize)/2;
        this.blit(i,j,0,0,this.xSize,this.ySize);
        final int flameHeight = 14;
        if(tileEntity!=null){
            int progress = tileEntity.progressCounterPX(9);
            int fuelProgress = tileEntity.progressFuelCounterPX(flameHeight);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(i+81,j+31,176,0,progress,17);
            this.blit(i+54,j+(54 + (flameHeight - fuelProgress)),176,24 + (flameHeight - fuelProgress),flameHeight,fuelProgress);

            VERender.renderGuiTank(tileEntity.getFluidFromTank(),tileEntity.getTankCapacity(),i+31,j+18,0,12,50);


        }

    }
}