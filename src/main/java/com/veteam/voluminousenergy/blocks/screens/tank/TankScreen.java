package com.veteam.voluminousenergy.blocks.screens.tank;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TankScreen extends VEContainerScreen<VEContainer> {
    private VETileEntity tileEntity;
    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/tank_gui.png");
    private static final ResourceLocation GUI_TOOLS = ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/guitools.png");


    public TankScreen(VEContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntity = screenContainer.getTileEntity();
        addTankArea(0, 82, 18);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        renderIOMenu(tileEntity, 64 + (this.width / 2), this.topPos + 4);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY) {
        TextUtil.renderShadowedText(matrixStack, this.font, TextUtil.translateString("container.inventory"), 8, (this.imageHeight - 96 + 2), WHITE_TEXT_STYLE);
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, this.GUI);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.blit(GUI, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (tileEntity != null) {
            // Tank render
            renderTank(matrixStack, 0);

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
}
