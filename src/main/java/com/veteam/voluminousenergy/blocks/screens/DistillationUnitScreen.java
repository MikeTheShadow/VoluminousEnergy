package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.blocks.tiles.DistillationUnitTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UuidPacket;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.UUID;

public class DistillationUnitScreen extends VEContainerScreen<DistillationUnitContainer> {
    private DistillationUnitTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/distillation_unit_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private boolean openedIOGui = false;

    public DistillationUnitScreen(DistillationUnitContainer screenContainer, Inventory inv, Component titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (DistillationUnitTile) screenContainer.getTileEntity();
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
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos -18, buttons ->{

        }));

        // Input Top
        addRenderableWidget(new SlotBoolButton(tileEntity.iTopManager, (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.iTopManager, (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));

        // Input Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.iBottomManager, (this.width/2)-198, this.topPos+20, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.iBottomManager, (this.width/2)-184, this.topPos+20, button ->{
            // Do nothing
        }));

        // Output 0 Top
        addRenderableWidget(new SlotBoolButton(tileEntity.o0TopManager, (this.width/2)-198, this.topPos+40, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o0TopManager, (this.width/2)-184, this.topPos+40, button ->{
            // Do nothing
        }));

        // Output 0 Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.o0BottomManager, (this.width/2)-198, this.topPos+60, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o0BottomManager, (this.width/2)-184, this.topPos+60, button ->{
            // Do nothing
        }));

        // Output 1 Top
        addRenderableWidget(new SlotBoolButton(tileEntity.o1TopManager, (this.width/2)-198, this.topPos+80, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o1TopManager, (this.width/2)-184, this.topPos+80, button ->{
            // Do nothing
        }));

        // Output 1 Bottom
        addRenderableWidget(new SlotBoolButton(tileEntity.o1BottomManager, (this.width/2)-198, this.topPos+100, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o1BottomManager, (this.width/2)-184, this.topPos+100, button ->{
            // Do nothing
        }));

        // Output 2
        addRenderableWidget(new SlotBoolButton(tileEntity.o2Manager, (this.width/2)-198, this.topPos+120, button ->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.o2Manager, (this.width/2)-184, this.topPos+120, button ->{
            // Do nothing
        }));

        // Input Tank
        addRenderableWidget(new TankBoolButton(tileEntity.getInputTank(), (this.width/2)-198, this.topPos+140, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getInputTank(), (this.width/2)-184, this.topPos+140, button ->{
            // Do nothing
        }));

        // Output Tank 0
        addRenderableWidget(new TankBoolButton(tileEntity.getOutputTank0(), (this.width/2)-198, this.topPos+160, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getOutputTank0(), (this.width/2)-184, this.topPos+160, button ->{
            // Do nothing
        }));

        // Output Tank 1
        addRenderableWidget(new TankBoolButton(tileEntity.getOutputTank1(), (this.width/2)-198, this.topPos+180, button ->{
            // Do nothing
        }));

        addRenderableWidget(new TankDirectionButton(tileEntity.getOutputTank1(), (this.width/2)-184, this.topPos+180, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Distillation Unit",8,6,0xffffff);
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("distillation_unit"), 8.0F, 6.0F, 16777215);

        this.font.drawShadow(matrixStack,new TranslatableComponent("container.inventory"), 8.0F, (float)(this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)){
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, Component.nullToEmpty(
                        veEnergyStorage.getEnergyStored()
                                + " FE / " + Config.DISTILLATION_UNIT_MAX_POWER.get()
                                + " FE"
                ), mouseX, mouseY);
            }));
        }

        if (isHovering(61, 18, 12, 50, mouseX, mouseY)){ // Input Tank
            int amount = tileEntity.getFluidStackFromTank(0).getAmount();
            String name = tileEntity.getFluidStackFromTank(0).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(119, 11, 12, 50, mouseX, mouseY)){ // First Output Tank
            int amount = tileEntity.getFluidStackFromTank(1).getAmount();
            String name = tileEntity.getFluidStackFromTank(1).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        if (isHovering(157, 11, 12, 50, mouseX, mouseY)){ // Second Output Tank
            int amount = tileEntity.getFluidStackFromTank(2).getAmount();
            String name = tileEntity.getFluidStackFromTank(2).getTranslationKey();
            renderTooltip(matrixStack,TextUtil.tankTooltip(name, amount, tileEntity.getTankCapacity()), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack,i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(tileEntity != null && tileEntity.getMultiblockValidity()){
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
            this.blit(matrixStack,i+81, j+31, 176, 0, progress, 17);
            this.blit(matrixStack,i + 11, j + (16 + (49-power)), 176, 24 + (49-power), 12, power);

            VERender.renderGuiTank(tileEntity.getFluidStackFromTank(0),tileEntity.getTankCapacity(), i + 61, j + 18, 0, 12, 50);

            try{
                VERender.renderGuiTank(tileEntity.getFluidStackFromTank(1),tileEntity.getTankCapacity(), i + 119, j + 11, 0, 12, 50);
            } catch (Exception e){ }

            try{
                VERender.renderGuiTank(tileEntity.getFluidStackFromTank(2),tileEntity.getTankCapacity(), i + 157, j + 11, 0, 12, 50);
            } catch (Exception e){ }
            drawIOSideHelper();
            // Upgrade slot
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            this.blit(matrixStack,i+129, j-16,0,0,18,18);
        } else {
            this.font.drawShadow(matrixStack, TextUtil.translateString("text.voluminousenergy.multiblock_warn"), 8.0F, 6.0F, 16777215);
            this.font.drawShadow(matrixStack, TextUtil.translateString("text.voluminousenergy.multiblock.distillation_unit.requirements"), 8.0F, 16.0F, 16777215);
        }

    }

    public void updateButtonDirection(int direction, int slotId){
        for(Widget widget: this.renderables){
            if(widget instanceof SlotDirectionButton && ((SlotDirectionButton) widget).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) widget).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Widget widget: this.renderables){
            if(widget instanceof SlotBoolButton && ((SlotBoolButton) widget).getAssociatedSlotId() == slotId){
                //VoluminousEnergy.LOGGER.debug("About to update the status of the Status/boolean Button.");
                ((SlotBoolButton) widget).toggleRender(true);
                ((SlotBoolButton) widget).setStatus(status);
                ((SlotBoolButton) widget).toggleRender(false);
            }
        }
    }

    public void updateTankDirection(int direction, int id){
        for(Widget widget: this.renderables){
            if(widget instanceof TankDirectionButton && ((TankDirectionButton) widget).getId() == id ){
                ((TankDirectionButton) widget).setDirectionFromInt(direction);
            }
        }
    }

    public void updateTankStatus(boolean status, int id){
        for(Widget widget: this.renderables){
            if(widget instanceof TankBoolButton && ((TankBoolButton) widget).getId() == id){
                //VoluminousEnergy.LOGGER.debug("About to update the status of the Status/boolean Button.");
                ((TankBoolButton) widget).toggleRender(true);
                ((TankBoolButton) widget).setStatus(status);
                ((TankBoolButton) widget).toggleRender(false);
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