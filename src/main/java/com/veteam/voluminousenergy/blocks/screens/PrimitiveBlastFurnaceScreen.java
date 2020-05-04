package com.veteam.voluminousenergy.blocks.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.blocks.tiles.PrimitiveBlastFurnaceTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class PrimitiveBlastFurnaceScreen extends ContainerScreen<PrimitiveBlastFurnaceContainer>
{

    public ResourceLocation GUI = new ResourceLocation(VoluminousEnergy.MODID,"textures/gui/primitiveblastgui.png");

    public PrimitiveBlastFurnaceScreen(PrimitiveBlastFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        //this.font.drawString(new TranslationTextComponent("voluminousenergy:primtiveblastfurnace",new Object[0]).getFormattedText(),8.0F,6.0F,4210752);
        //this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 - 12), 4210752);
        drawString(Minecraft.getInstance().fontRenderer, "Primitive Blast Furnace",8,6,0xffffff);
        this.font.drawString(new TranslationTextComponent("container.inventory", new Object[0]).getFormattedText(), 8.0F, (float)(this.ySize - 96 - 12), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }

}
