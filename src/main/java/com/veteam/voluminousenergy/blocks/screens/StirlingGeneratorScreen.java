package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.StirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.StirlingGeneratorTile;
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

public class StirlingGeneratorScreen extends ContainerScreen<StirlingGeneratorContainer> {
    private StirlingGeneratorTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/stirling_generator.png");
    private boolean openedIOGui = false;

    public StirlingGeneratorScreen(StirlingGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (StirlingGeneratorTile) screenContainer.tileEntity;
        screenContainer.setScreen(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Buttons
        this.addButton(new ioMenuButton(64 + (this.width / 2), this.topPos + 4, buttons -> {

        }));

        // Input insert
        this.addButton(new SlotBoolButton(tileEntity.slotManager, (this.width / 2) - 198, this.topPos, button -> {
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.slotManager, (this.width / 2) - 184, this.topPos, button -> {
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack,int mouseX, int mouseY) {
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Stirling Generator",8,6,0xffffff);
        this.font.draw(matrixStack, TextUtil.translateVEBlock("stirling_generator"), 8.0F, 6.0F, 16777215);

        drawString(matrixStack,Minecraft.getInstance().font, "Generating: " + tileEntity.getEnergyRate() + " FE/t", 50, 18, 0xffffff);
        this.font.draw(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(matrixStack, ITextComponent.nullToEmpty(menu.getEnergy() + " FE / " + Config.STIRLING_GENERATOR_MAX_POWER.get() + " FE"), mouseX, mouseY);
        } else if (isHovering(79, 53, 18, 18, mouseX, mouseY)){
            renderTooltip(matrixStack, ITextComponent.nullToEmpty("Percent burned: " + tileEntity.progressCounterPercent() + "%, Ticks Left: " + tileEntity.ticksLeft() + ", Production: " + tileEntity.getEnergyRate() + " FE/t"), mouseX, mouseY);
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
        if (tileEntity != null) {
            int progress = tileEntity.progressCounterPX(14);
            int power = menu.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(matrixStack,i + 81, j + (55 + (14-progress)), 176, (14-progress), 14, progress); // 55 = full, 55+14 = end
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);
            drawIOSideHelper(matrixStack,i,j,mouseX,mouseY,partialTicks);
        }
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
