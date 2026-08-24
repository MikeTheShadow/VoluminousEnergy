package com.veteam.voluminousenergy.tools.buttons;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import static net.minecraft.client.Minecraft.getInstance;

public class ioMenuButton extends Button {
    private boolean cycled = false;
    private final Identifier texture = Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, "textures/gui/crushergui.png");
    private final int x;
    private final int y;

    public ioMenuButton(int x, int y, OnPress onPress) {
        super(x, y, 20, 18, Component.nullToEmpty(""), button -> {
            ((ioMenuButton) button).cycleMode();
            onPress.onPress(button);
        }, DEFAULT_NARRATION);
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 18;
    }

    private void cycleMode() {
        cycled = !cycled;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor matrixStack, int p_renderButton1, int p_renderButton2, float p_renderButton3) {
        if (!isHovered) {
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, this.x, this.y, 193, 0, this.width, this.height, 256, 256);
        } else {
            matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, this.x, this.y, 193, 19, this.width, this.height, 256, 256);
        }
        TextUtil.renderShadowedText(matrixStack, getInstance().font, Component.nullToEmpty("IO"), (this.x) + 5, (this.y) + 5, Style.EMPTY.withColor(0xffffff));
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        cycleMode();
    }

    public boolean shouldIOBeOpen() {
        return cycled;
    }
}
