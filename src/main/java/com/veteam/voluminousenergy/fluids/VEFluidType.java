package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.util.extensions.VEFluidClientExtension;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class VEFluidType extends FluidType {

    private final Identifier STILL_TEXTURE;
    private final Identifier FLOWING_TEXTURE;
    private Identifier overlayTexture;
    private int colourTint;

    /**
     * Default constructor.
     *
     * @param properties     the general properties of the fluid type
     * @param stillTexture   is the still texture for the fluid
     * @param flowingTexture is the flowing texture for the fluid
     */
    public VEFluidType(Properties properties, Identifier stillTexture, Identifier flowingTexture) {
        super(properties);
        this.STILL_TEXTURE = stillTexture;
        this.FLOWING_TEXTURE = flowingTexture;
    }

    /**
     * Default constructor.
     *
     * @param properties     the general properties of the fluid type
     * @param stillTexture   is the still texture for the fluid
     * @param flowingTexture is the flowing texture for the fluid
     * @param overlayTexture is the overlay texture when an entity is in the fluid (I think)
     */
    public VEFluidType(Properties properties, Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture) {
        super(properties);
        this.STILL_TEXTURE = stillTexture;
        this.FLOWING_TEXTURE = flowingTexture;
        this.overlayTexture = overlayTexture;
    }

    public void setColourTint(int colourTint) {
        this.colourTint = colourTint;
    }

    public VEFluidClientExtension getFluidClientExtension() {
        return new VEFluidClientExtension(STILL_TEXTURE, FLOWING_TEXTURE, overlayTexture, colourTint);
    }
}
