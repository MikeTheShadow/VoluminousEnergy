package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.StirlingGeneratorContainer;
import com.veteam.voluminousenergy.blocks.tiles.StirlingGeneratorTile;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class StirlingGeneratorScreen extends ContainerScreen<StirlingGeneratorContainer> {
    private StirlingGeneratorTile tileEntity;
    private ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID, "textures/gui/stirling_generator.png");

    public StirlingGeneratorScreen(StirlingGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer,inv,titleIn);
        tileEntity = (StirlingGeneratorTile) screenContainer.tileEntity;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();
        super.render(mouseX,mouseY,partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(Minecraft.getInstance().fontRenderer, "Stirling Generator",8,6,0xffffff);
        this.font.drawString(new TranslationTextComponent("container.inventory", new Object[0]).getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        if (isPointInRegion(11, 16, 12, 49, mouseX, mouseY)) {
            renderTooltip(container.getEnergy() + " FE / " + Config.STIRLING_GENERATOR_MAX_POWER.get() + " FE", mouseX, mouseY);
        } else if (isPointInRegion(79, 53, 18, 18, mouseX, mouseY)){
            renderTooltip("Percent burned: " + tileEntity.progressCounterPercent() + "%, Ticks Left: " + tileEntity.ticksLeft() + ", Production: " + tileEntity.getEnergyRate() + " FE/t", mouseX, mouseY);
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        if (tileEntity != null) {
            int progress = tileEntity.progressCounterPX(14);
            int power = container.powerScreen(49);

            /*Note for this.blit below:
                p_blit_1_ = starting x for blit on screen
                p_blit_2_ = starting y for blit on screen
                p_blit_3_ = starting x for blit to be stitched from in the file
                p_blit_4_ = starting y for blit to be stitched from in the file
                p_blit_5_ = width of the x for the blit to be drawn (make variable for progress illusion on the x)
                p_blit_6_ = width of the y for the blit to be drawn (make variable for progress illusion of the y)
             */
            this.blit(i + 81, j + (55 + (14-progress)), 176, (14-progress), 14, progress); // 55 = full, 55+14 = end
            this.blit(i + 11, j + (16 + (49-power)), 176, 14 + (49-power), 12, power);

        }
    }
}
