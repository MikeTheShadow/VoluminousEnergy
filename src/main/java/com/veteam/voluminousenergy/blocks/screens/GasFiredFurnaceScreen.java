package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.GasFiredFurnaceContainer;
import com.veteam.voluminousenergy.blocks.tiles.GasFiredFurnaceTile;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GasFiredFurnaceScreen extends VEContainerScreen<GasFiredFurnaceContainer> {
    private GasFiredFurnaceTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID,"textures/gui/gas_fired_furnace_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public GasFiredFurnaceScreen(GasFiredFurnaceContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (GasFiredFurnaceTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Bucket insert
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketInputSm, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketInputSm, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Bucket Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketOutputSm, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketOutputSm, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Furnace Insert
        addRenderableWidget(new SlotBoolButton(tileEntity.furnaceInputSm, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.furnaceInputSm, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // Furnace Extract
        addRenderableWidget(new SlotBoolButton(tileEntity.furnaceOutputSm, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.furnaceOutputSm, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // Fuel Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getFuelTank(), (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getFuelTank(), (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX,int mouseY){
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer,"Gas Fired Furnace",8,6,0xffffff);
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("gas_fired_furnace"), 8.0F, 6.0F, 16777215);

        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderSlotAndTankLabels(PoseStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX,int mouseY){
        if(isHovering(31,18,12,49,mouseX,mouseY)){
            int amount = tileEntity.getFluidFromTank().getAmount();
            String name = tileEntity.getFluidFromTank().getTranslationKey();
            renderTooltip(matrixStack, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        } else if (isHovering(54,54,16,16,mouseX,mouseY)){
            renderTooltip(matrixStack, Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " + tileEntity.progressFuelCounterPercent() + "%, " + TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getFuelCounter()), mouseX, mouseY);
        } else if (isHovering(81,32,9,17,mouseX,mouseY)){
            renderTooltip(matrixStack, Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_complete").getString() + ": " + tileEntity.progressCounterPercent() + "%, "+ TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.getCounter()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks,int mouseX,int mouseY){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i=(this.width-this.imageWidth)/2;
        int j=(this.height-this.imageHeight)/2;
        this.blit(matrixStack,i,j,0,0,this.imageWidth,this.imageHeight);
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
            this.blit(matrixStack,i+81,j+31,176,0,progress,17);
            this.blit(matrixStack,i+54,j+(54 + (flameHeight - fuelProgress)),176,24 + (flameHeight - fuelProgress),flameHeight,fuelProgress);

            VERender.renderGuiTank(tileEntity.getFluidFromTank(),tileEntity.getTankCapacity(),i+31,j+18,0,12,50);
            drawIOSideHelper();
        }
        RenderSystem.setShaderTexture(0, GUI_TOOLS);
        this.blit(matrixStack,i+153, j-16,0,0,18,18);
    }

}