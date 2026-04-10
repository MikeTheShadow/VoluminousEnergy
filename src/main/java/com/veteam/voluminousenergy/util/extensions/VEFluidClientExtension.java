package com.veteam.voluminousenergy.util.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import javax.annotation.Nullable;

public class VEFluidClientExtension implements IClientFluidTypeExtensions {
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int colourTint;

    public VEFluidClientExtension(Identifier still, Identifier flowing, @Nullable Identifier overlay, int colourTint) {
        this.stillTexture = still;
        this.flowingTexture = flowing;
        this.overlayTexture = overlay;
        this.colourTint = colourTint;
    }

    @Override
    public Identifier getStillTexture() {
        return stillTexture;
    }

    @Override
    public Identifier getFlowingTexture() {
        return flowingTexture;
    }

    @Nullable
    @Override
    public Identifier getRenderOverlayTexture(Minecraft mc) {
        return overlayTexture;
    }

    @Override
    public int getTintColor() {
        return colourTint > 0 ? colourTint : IClientFluidTypeExtensions.super.getTintColor();
    }
}
