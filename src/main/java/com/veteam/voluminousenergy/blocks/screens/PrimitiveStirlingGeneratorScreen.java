package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveStirlingGeneratorTile;
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

public class PrimitiveStirlingGeneratorScreen extends ContainerScreen<PrimitiveStirlingGeneratorContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/primitivestirlinggenerator_gui.png");
    private PrimitiveStirlingGeneratorTile tileEntity;
    private boolean openedIOGui = false;

    public PrimitiveStirlingGeneratorScreen(PrimitiveStirlingGeneratorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        tileEntity = (PrimitiveStirlingGeneratorTile) container.getTileEntity();
        container.setScreen(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderHoveredTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        this.addButton(new ioMenuButton(64 + (this.width/2), this.guiTop +4, buttons ->{

        }));

        // Input slot
        this.addButton(new SlotBoolButton(tileEntity.slotManager, (this.width/2)-198, this.guiTop, button->{
            // Do nothing
        }));

        this.addButton(new SlotDirectionButton(tileEntity.slotManager, (this.width/2)-184, this.guiTop, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack,int mouseX, int mouseY) {
        if (isPointInRegion(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(matrixStack, ITextComponent.getTextComponentOrEmpty(container.getEnergy() + " FE / " + Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get() + " FE"), mouseX, mouseY);
        } else if (isPointInRegion(79, 53, 18, 18, mouseX, mouseY)){
            renderTooltip(matrixStack, ITextComponent.getTextComponentOrEmpty("Percent burned: " + tileEntity.progressCounterPercent() + "%, Ticks Left: " + tileEntity.ticksLeft() + ", Production: " + tileEntity.getEnergyRate() + " FE/t"), mouseX, mouseY);
        }
        super.renderHoveredTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY){
        //this.font.drawString(this.title.getFormattedText(), 8.0F,6.0F,4210752);
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Primitive Stirling Generator",8,6,0xffffff);
        this.font.func_243246_a(matrixStack, TextUtil.translateVEBlock("primitivestirlinggenerator"), 8.0F, 6.0F, 16777215);

        int generationRate;
        if (tileEntity.ticksLeft() > 0) generationRate = Config.PRIMITIVE_STIRLING_GENERATOR_GENERATE.get();
        else generationRate = 0;
        drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Generating: " + generationRate + " FE/t", 50, 18, 0xffffff);
        //this.font.drawString(matrixStack,this.playerInventory.getDisplayName().getString(),8.0F, (float) (this.ySize - 96 - 12), 4210752);
        this.font.func_243246_a(matrixStack,new TranslationTextComponent("container.inventory"), 8.0F, (float)(this.ySize - 96 + 2), 16777215);
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Energy: " + container.getEnergy(), 10, 22, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.xSize, this.ySize); // Actual Gui
        if (tileEntity != null) {
            int power = container.powerScreen(49);
            int progress = tileEntity.progressCounterPX(14);
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
        UUID uuid = Minecraft.getInstance().player.getUniqueID();
        if(uuid != null){
            VENetwork.channel.sendToServer(new UuidPacket(uuid, connection));
        }
    }
}
