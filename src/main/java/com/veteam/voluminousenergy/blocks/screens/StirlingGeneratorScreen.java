package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.UUID;

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
        //drawString(matrixStack,Minecraft.getInstance().fontRenderer, "Stirling Generator",8,6,0xffffff);
        this.font.drawShadow(matrixStack, TextUtil.translateVEBlock("stirling_generator"), 8.0F, 6.0F, 16777215);

        drawString(matrixStack, Minecraft.getInstance().font, TextUtil.translateString("text.voluminousenergy.generating").getString() + ": " + tileEntity.getEnergyRate() + " FE/t", 50, 18, 0xffffff);
        this.font.drawShadow(matrixStack, new TranslatableComponent("container.inventory"), 8.0F, (float) (this.imageHeight - 96 + 2), 16777215);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (isHovering(11, 16, 12, 49, mouseX, mouseY)) {
            tileEntity.getEnergy().ifPresent((veEnergyStorage -> {
                renderTooltip(matrixStack, Component.nullToEmpty(
                        veEnergyStorage.getEnergyStored()
                                + " FE / " + Config.STIRLING_GENERATOR_MAX_POWER.get()
                                + " FE"
                ), mouseX, mouseY);
            }));
        } else if (isHovering(79, 53, 18, 18, mouseX, mouseY)) {
            renderTooltip(matrixStack, Component.nullToEmpty(TextUtil
                    .translateString("text.voluminousenergy.percent_burned").getString()
                    + ": " + tileEntity.progressCounterPercent()
                    + "%, " + TextUtil.translateString("text.voluminousenergy.ticks_left").getString()
                    + ": " + tileEntity.ticksLeft() + ", " + TextUtil.translateString("text.voluminousenergy.generating").getString()
                    + " : " + tileEntity.getEnergyRate() + " FE/t"), mouseX, mouseY);
        }
        super.renderTooltip(matrixStack, mouseX, mouseY);
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
