package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.StirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.StirlingGeneratorTile;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.List;

public class StirlingGeneratorScreen extends VEContainerScreen<StirlingGeneratorContainer> {
    private final StirlingGeneratorTile tileEntity;
    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/stirling_generator.png");
    private static final ResourceLocation GUI_TOOLS = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/guitools.png");
    

    public StirlingGeneratorScreen(StirlingGeneratorContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = (StirlingGeneratorTile) screenContainer.getTileEntity();
        screenContainer.setScreen(this);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width / 2), this.topPos + 4, buttons -> {

        }));

        // Input insert
        addRenderableWidget(new SlotBoolButton(tileEntity.slotManager, (this.width / 2) - 198, this.topPos, button -> {
            // Do nothing
        }));

        addRenderableWidget(new SlotDirectionButton(tileEntity.slotManager, (this.width / 2) - 184, this.topPos, button -> {
            // Do nothing
        }));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("stirling_generator"), 8.0F, 6.0F, WHITE_TEXT_COLOUR);
        drawString(matrixStack, Minecraft.getInstance().font, TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergyRate() + " FE/t", 50, 18, WHITE_TEXT_COLOUR);
        this.font.drawShadow(matrixStack, new TranslatableComponent("container.inventory"), 8.0F, (float) (this.imageHeight - 96 + 2), WHITE_TEXT_COLOUR);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderSlotAndTankLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        // Slots
        this.font.drawShadow(matrixStack, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append("0")), 80F, 35F, WHITE_TEXT_COLOUR);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, TextUtil.powerBarTooltip(veEnergyStorage, Config.STIRLING_GENERATOR_MAX_POWER.get()), mouseX, mouseY);
            }));
        } else if (!VoluminousEnergy.JEI_LOADED && isHovering(getTooltipArea(), mouseX, mouseY)) {
        	renderComponentTooltip(matrixStack, this.getTooltips(), mouseX, mouseY);
        }
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public Rect2i getTooltipArea() {
        return new Rect2i(81, 55, 14, 14);
    }

    public List<Component> getTooltips() {
        return Arrays.asList(
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.percent_burned").getString() + ": " + tileEntity.progressCounterPercent() + "%"),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.ticks_left").getString() + ": " + tileEntity.ticksLeft()),
                Component.nullToEmpty(TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergyRate() + " FE/t"));
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
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
            this.blit(matrixStack, i + 81, j + (55 + (14 - progress)), 176, (14 - progress), 14, progress); // 55 = full, 55+14 = end
            this.blit(matrixStack, i + 11, j + (16 + (49 - power)), 176, 14 + (49 - power), 12, power);
            RenderSystem.setShaderTexture(0, GUI_TOOLS);
            drawIOSideHelper();
        }
    }

}
