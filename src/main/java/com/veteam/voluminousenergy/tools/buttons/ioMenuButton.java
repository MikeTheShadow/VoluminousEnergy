package com.veteam.voluminousenergy.tools.buttons;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ioMenuButton extends Button {
    private boolean cycled = false;

    public ioMenuButton(int x, int y, int width, int height, IPressable onPress){
        super(x, y, width, height, "", button -> {
            ((ioMenuButton) button).cycleMode();
            onPress.onPress(button);
        });
    }

    private void cycleMode(){ cycled = !cycled;}

    public boolean openGui(){
        return cycled;
    }
}
