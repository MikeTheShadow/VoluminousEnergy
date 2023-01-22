package com.veteam.voluminousenergy.tools.buttons;


import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class VEIOButton extends Button {
    protected boolean render = false;

    public VEIOButton(int x, int y, int width, int height, Component title, OnPress pressedAction) {
        super(x, y, width, height, title, pressedAction, DEFAULT_NARRATION);
    }

    public void toggleRender(boolean bool){
        render = bool;
    }
}
