package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveStirlingGeneratorTile;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PrimitiveStirlingGeneratorScreen extends ContainerScreen<PrimitiveStirlingGeneratorContainer> {

    private final ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/primitivestirlinggenerator_gui.png");
    private PrimitiveStirlingGeneratorTile tileEntity;

    public PrimitiveStirlingGeneratorScreen(PrimitiveStirlingGeneratorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        tileEntity = (PrimitiveStirlingGeneratorTile) container.getTileEntity();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        if (isPointInRegion(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(container.getEnergy() + " FE / " + Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get() + " FE", mouseX, mouseY);
        } else if (isPointInRegion(79, 53, 18, 18, mouseX, mouseY)){
            renderTooltip("Percent burned: " + tileEntity.progressCounterPercent() + "%, Ticks Left: " + tileEntity.ticksLeft() + ", Production: " + tileEntity.getEnergyRate() + " FE/t", mouseX, mouseY);
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        drawString(Minecraft.getInstance().fontRenderer, "Primitive Stirling Generator",8,6,0xffffff);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize); // Actual Gui
        if (tileEntity != null) {
            int power = container.powerScreen(49);
            int progress = tileEntity.progressCounterPX(14);
            this.blit(i + 81, j + (55 + (14-progress)), 176, (14-progress), 14, progress); // 55 = full, 55+14 = end
            this.blit(i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);
        }

    }
}
