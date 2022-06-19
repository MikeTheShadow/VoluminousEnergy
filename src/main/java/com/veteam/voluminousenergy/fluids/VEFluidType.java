package com.veteam.voluminousenergy.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class VEFluidType extends FluidType {

    private final ResourceLocation STILL_TEXTURE;
    private final ResourceLocation FLOWING_TEXTURE;
    private ResourceLocation overlayTexture;
    private int colourTint;

    /**
     * Default constructor.
     *
     * @param properties the general properties of the fluid type
     * @param stillTexture is the still texture for the fluid
     * @param flowingTexture is the flowing texture for the fluid
     */
    public VEFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties);
        this.STILL_TEXTURE = stillTexture;
        this.FLOWING_TEXTURE = flowingTexture;
    }

    /**
     * Default constructor.
     *
     * @param properties the general properties of the fluid type
     * @param stillTexture is the still texture for the fluid
     * @param flowingTexture is the flowing texture for the fluid
     * @param overlayTexture is the overlay texture when an entity is in the fluid (I think)
     */
    public VEFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture) {
        super(properties);
        this.STILL_TEXTURE = stillTexture;
        this.FLOWING_TEXTURE = flowingTexture;
        this.overlayTexture = overlayTexture;
    }

    public void setColourTint(int colourTint) {
        this.colourTint = colourTint;
    }

    @Override
    public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer){
        consumer.accept(new IFluidTypeRenderProperties() {
            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }

            @Nullable
            @Override
            public ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public int getColorTint() {
                return colourTint > 0 ? colourTint : IFluidTypeRenderProperties.super.getColorTint();
            }
        });
    }
}
