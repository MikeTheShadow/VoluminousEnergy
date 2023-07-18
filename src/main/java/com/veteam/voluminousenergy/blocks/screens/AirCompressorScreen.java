package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.blocks.tiles.AirCompressorTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AirCompressorScreen extends VEContainerScreen<AirCompressorContainer> {

    private AirCompressorTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/air_compressor_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");

    public AirCompressorScreen(AirCompressorContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (AirCompressorTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons go here
        renderIOMenu(tileEntity);

    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack,int mouseX, int mouseY) {

        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateVEBlock("air_compressor"), 8, 6, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageWidth - 96 - 8), WHITE_TEXT_STYLE);

        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack,int mouseX, int mouseY){
        // Tank
        TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), 93, 18, WHITE_TEXT_STYLE);

        // Slots handeled by super
        super.renderSlotAndTankLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(veEnergyStorage, Config.AIR_COMPRESSOR_MAX_POWER.get()), mouseX, mouseY);
            }));
        }

        if (isHovering(93, 18, 12, 50, mouseX, mouseY)){ // Oxidizer Tank
            String name = tileEntity.getAirTankFluid().getTranslationKey();
            int amount = tileEntity.getAirTankFluid().getAmount();
            matrixStack.renderTooltip(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit( this.GUI,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (tileEntity != null) {
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            matrixStack.blit(this.GUI,i + 11, j + (16 + (49 - power)), 176, 24 + (49 - power), 12, power);

            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getAirTankFluid(),tileEntity.getTankCapacity(), i + 93, j + 18, 0, 12, 50);
            } catch (Exception e){ }
            // Upgrade slot
            RenderSystem.setShaderTexture(0, GUI_TOOLS);

            matrixStack.blit(GUI_TOOLS,i+153, j-16,0,0,18,18);
            drawIOSideHelper();
        }
    }
}
