package com.veteam.voluminousenergy.blocks.screens.tank;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.tank.TankContainer;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.blocks.tiles.tank.TankTile;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UuidPacket;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.UUID;

public class TankScreen extends VEContainerScreen<TankContainer> {
    private TankTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/tank_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public TankScreen(TankContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (TankTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos + 4, buttons ->{

        }));

        // Input Bucket Slot
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketTopSlotManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketTopSlotManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Output Bucket Slot
        addRenderableWidget(new SlotBoolButton(tileEntity.bucketBottomSlotManager, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.bucketBottomSlotManager, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Output Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getTank(), (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getTank(), (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateString("container.inventory"), this.imageWidth,8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), this.imageWidth,70, 19, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("1")), this.imageWidth,70, 50, WHITE_TEXT_STYLE);

        // Tank
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.tank_short").copy().append("0")), this.imageWidth,93, 18, WHITE_TEXT_STYLE);
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isHovering(93, 18, 12, 50, mouseX, mouseY)){
            int amount = tileEntity.getTank().getTank().getFluid().getAmount();
            String name = tileEntity.getTank().getTank().getFluid().getTranslationKey();
            matrixStack.renderTooltip(this.font, TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (tileEntity != null) {
            // Tank render
            try{
                VERender.renderGuiTank(tileEntity.getLevel(), tileEntity.getBlockPos(),tileEntity.getTank().getTank(),tileEntity.getTankCapacity(), i + 93, j + 18, 0, 12, 50);
            } catch (Exception e){ }

            // IO Rendering
            RenderSystem.setShaderTexture(0, GUI_TOOLS);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */

            //this.blit(matrixStack,i+153, j-16,0,0,18,18); // Upgrade Slot
            drawIOSideHelper();
        }
    }

    public void updateButtonDirection(int direction, int slotId){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof SlotDirectionButton && ((SlotDirectionButton) Renderable).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) Renderable).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof SlotBoolButton && ((SlotBoolButton) Renderable).getAssociatedSlotId() == slotId){
                //VoluminousEnergy.LOGGER.debug("About to update the status of the Status/boolean Button.");
                ((SlotBoolButton) Renderable).toggleRender(true);
                ((SlotBoolButton) Renderable).setStatus(status);
                ((SlotBoolButton) Renderable).toggleRender(false);
            }
        }
    }

    public void updateTankDirection(int direction, int id){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof TankDirectionButton && ((TankDirectionButton) Renderable).getId() == id ){
                ((TankDirectionButton) Renderable).setDirectionFromInt(direction);
            }
        }
    }

    public void updateTankStatus(boolean status, int id){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof TankBoolButton && ((TankBoolButton) Renderable).getId() == id){
                //VoluminousEnergy.LOGGER.debug("About to update the status of the Status/boolean Button.");
                ((TankBoolButton) Renderable).toggleRender(true);
                ((TankBoolButton) Renderable).setStatus(status);
                ((TankBoolButton) Renderable).toggleRender(false);
            }
        }
    }

    public void informTileOfIOButton(boolean connection){
        UUID uuid = Minecraft.getInstance().player.getUUID();
        if(uuid != null){
            VENetwork.channel.sendToServer(new UuidPacket(uuid, connection));
        }
    }

}