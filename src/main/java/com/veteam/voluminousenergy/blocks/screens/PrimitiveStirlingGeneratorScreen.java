package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PrimitiveStirlingGeneratorScreen extends VEContainerScreen<VEContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/primitivestirlinggenerator_gui.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    private VETileEntity tileEntity;
    

    public PrimitiveStirlingGeneratorScreen(VEContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        tileEntity = container.getTileEntity();
        container.setScreen(this);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack,mouseX,mouseY,partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }

    @Override
    protected void init(){
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width/2), this.topPos +4, buttons ->{

        }));

        // Input tilePos
        addRenderableWidget(new SlotBoolButton(tileEntity.getSlotManagers().get(0), (this.width/2)-198, this.topPos, button->{
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.getSlotManagers().get(0), (this.width/2)-184, this.topPos, button ->{
            // Do nothing
        }));
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack,int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
                matrixStack.renderTooltip(this.font, TextUtil.powerBarTooltip(tileEntity.getEnergy(), tileEntity.getEnergy().getMaxEnergyStored()), mouseX, mouseY);
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)){
            matrixStack.renderComponentTooltip(this.font, getTooltips(), mouseX, mouseY);
        }
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(81, 55, 14, 14);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergy().getProduction() + " FE/t"));
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY){
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateVEBlock("primitivestirlinggenerator"),  8, 6, WHITE_TEXT_STYLE);

        int generationRate = tileEntity.getEnergy().getProduction();
        TextUtil.renderCenteredShadowedText(matrixStack,Minecraft.getInstance().font, Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + generationRate + " FE/t"), 90, 18, WHITE_TEXT_STYLE);
        TextUtil.renderShadowedText(matrixStack, this.font,TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);

        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        // Slots
        TextUtil.renderShadowedText(matrixStack, this.font,(TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")),  80, 35, WHITE_TEXT_STYLE);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack,float partialTicks, int mouseX, int mouseY){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI,i, j, 0, 0, this.imageWidth, this.imageHeight); // Actual Gui
        if (tileEntity != null) {
            int power = menu.powerScreen(49);
            int progress = tileEntity.progressBurnCounterPX(14);
            matrixStack.blit(GUI,i + 81, j + (55 + (14-progress)), 176, (14-progress), 14, progress); // 55 = full, 55+14 = end
            matrixStack.blit(GUI,i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            drawIOSideHelper();
        }
    }
}
