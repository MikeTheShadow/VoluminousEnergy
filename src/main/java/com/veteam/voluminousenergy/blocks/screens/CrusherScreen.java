package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrusherScreen extends VEContainerScreen<CrusherContainer> {
    private CrusherTile tileEntity;
    private final static ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/crushergui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public CrusherScreen(CrusherContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (CrusherTile) screenContainer.getTileEntity();
        screenContainer.setCrusherScreen(this);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos + 4, button -> {
            // Do Nothing
        }));

        // Input Slot
        addRenderableWidget(new SlotBoolButton(tileEntity.inputSlotProp, (this.width/2)-198, this.topPos, button ->{
            // Do Nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.inputSlotProp, (this.width/2)-184, this.topPos, button ->{
            // Do Nothing
        }));

        // Output Slot
        addRenderableWidget(new SlotBoolButton(tileEntity.outputSlotProp, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.outputSlotProp, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // RNG slot
        addRenderableWidget(new SlotBoolButton(tileEntity.rngSlotProp, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.rngSlotProp, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Crusher",8,6,0xffffff);
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("crusher"), 8.0F, 6.0F, 16777215);

        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, Component.nullToEmpty(
                        veEnergyStorage.getEnergyStored()
                                + " FE / " + Config.CRUSHER_MAX_POWER.get()
                                + " FE"
                ), mouseX, mouseY);
            }));
        }/* else if (isHovering(152, 4, 20, 18, mouseX, mouseY)){
            if (openedIOGui){
                renderTooltip(matrixStack, TextUtil.translateString("voluminousenergy.ui.io_management.close"), mouseX, mouseY);
            } else {
                renderTooltip(matrixStack, TextUtil.translateString("voluminousenergy.ui.io_management.open"), mouseX, mouseY);
            }
        }*/
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null){
            int progress = tileEntity.progressCounterPX(24);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(matrixStack,i+79, j+31, 176, 0, 17, progress);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);
            drawIOSideHelper();
        }
        // Upgrade slot
        RenderSystem.setShaderTexture(0, GUI_TOOLS);
        this.blit(matrixStack,i+153, j-16,0,0,18,18);
    }

    public static ResourceLocation getGUI() {
        return GUI;
    }

}
