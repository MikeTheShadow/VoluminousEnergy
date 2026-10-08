package com.veteam.voluminousenergy.compat.jei.containerHandler;

import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VEIOContainerHandler<T extends VEContainerScreen<?>> implements IGuiContainerHandler<T> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull T screen) {
        return screen.getIOControlAreas();
    }
}
