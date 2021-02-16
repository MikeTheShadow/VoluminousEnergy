package com.veteam.voluminousenergy.tools.buttons;


import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

// Dummy class
public class VEIOButton extends Button {
    protected boolean render = false;

    public VEIOButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction) {
        super(x, y, width, height, title, pressedAction);
    }

    public void toggleRender(boolean bool){
        render = bool;
    }
}
