package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalSeparatorContainer;
import com.veteam.voluminousenergy.blocks.tiles.CentrifugalSeparatorTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UuidPacket;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class CentrifugalSeparatorScreen extends ContainerScreen<CentrifugalSeparatorContainer> {
    private CentrifugalSeparatorTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/centrifugal_separator_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private boolean openedIOGui = false;

    public CentrifugalSeparatorScreen(CentrifugalSeparatorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (CentrifugalSeparatorTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        this.addButton(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Input
        this.addButton(new SlotBoolButton(tileEntity.inputSm, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.inputSm, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Bucket Insert
        this.addButton(new SlotBoolButton(tileEntity.bucketSm, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.bucketSm, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Output
        this.addButton(new SlotBoolButton(tileEntity.outputSm, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.outputSm, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // RNG 1
        this.addButton(new SlotBoolButton(tileEntity.rngOneSm, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.rngOneSm, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // RNG 2
        this.addButton(new SlotBoolButton(tileEntity.rngTwoSm, (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.rngTwoSm, (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));

        // RNG 3
        this.addButton(new SlotBoolButton(tileEntity.rngThreeSm, (this.width/2)-198, this.topPos+100, button ->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.rngThreeSm, (this.width/2)-184, this.topPos+100, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack,int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("centrifugal_separator"), 8.0F, 6.0F, 16777215);

        this.font.drawShadow(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(matrixStack, ITextComponent.nullToEmpty(menu.getEnergy() + " FE" + " / " + Config.CENTRIFUGAL_SEPARATOR_MAX_POWER.get() + " FE"), mouseX, mouseY);
        }
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null){
            int progress = tileEntity.progressCounterPX(9);
            int power = menu.powerScreen(49);
            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(matrixStack,i+81,j+31,176,0,progress,17);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);
            drawIOSideHelper(matrixStack,i,j,mouseX,mouseY,partialTicks);
        }
        // Upgrade slot
        this.minecraft.getTextureManager().bind(GUI_TOOLS);
        this.blit(matrixStack,i+153, j-16,0,0,18,18);
    }

    private void drawIOSideHelper(MatrixStack matrixStack, int i, int j, int mouseX, int mouseY, float partialTicks){
        for(Widget widget : this.buttons){
            if (widget instanceof ioMenuButton){
                if (((ioMenuButton) widget).shouldIOBeOpen() && !openedIOGui) { // This means IO Should be open
                    this.buttons.forEach(button ->{
                        if (button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(true);
                            informTileOfIOButton(true);
                            openedIOGui = !openedIOGui;
                        }
                    });
                } else {
                    this.buttons.forEach(button ->{
                        if(button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(false);
                            informTileOfIOButton(false);
                            openedIOGui = !openedIOGui;
                        }
                    });
                }
            }
        }
    }

    public void updateButtonDirection(int direction, int slotId){
        for(Widget widget: this.buttons){
            if(widget instanceof SlotDirectionButton && ((SlotDirectionButton) widget).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) widget).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Widget widget: this.buttons){
            if(widget instanceof SlotBoolButton && ((SlotBoolButton) widget).getAssociatedSlotId() == slotId){
                //VoluminousEnergy.LOGGER.debug("About to update the status of the Status/boolean Button.");
                ((SlotBoolButton) widget).toggleRender(true);
                ((SlotBoolButton) widget).setStatus(status);
                ((SlotBoolButton) widget).toggleRender(false);
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