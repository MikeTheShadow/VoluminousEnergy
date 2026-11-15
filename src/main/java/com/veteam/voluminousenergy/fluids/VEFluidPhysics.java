package com.veteam.voluminousenergy.fluids;

/**
 * Movement and visibility presets shared by Voluminous Energy fluids. A preset decides how hard a fluid
 * is to move through and how far a player can see inside it; whether an entity sinks or floats comes
 * from the fluid type's own density.
 */
public enum VEFluidPhysics {
    GAS(true, 0.98F, 1.0F, 1.0F, 0.0F, false, 24.0F, OverlayStyle.NONE, 0.0F, 1.0F),
    THIN(false, 0.82F, 0.82F, 1.0F, 1.0F, true, 16.0F, OverlayStyle.TINT, 0.15F, 1.0F),
    WATERY(false, 0.8F, 0.8F, 1.0F, 1.0F, true, 12.0F, OverlayStyle.TINT, 0.25F, 1.0F),
    SYRUPY(false, 0.7F, 0.65F, 0.95F, 1.0F, false, 3.0F, OverlayStyle.TEXTURE, 0.35F, 1.0F),
    STICKY(false, 0.6F, 0.45F, 0.9F, 0.5F, false, 1.0F, OverlayStyle.TINT, 0.9F, 0.35F),
    MERCURY(false, 0.7F, 0.5F, 0.95F, 1.0F, false, 1.0F, OverlayStyle.TEXTURE, 0.6F, 1.0F);

    private final boolean gas;
    private final float horizontalRetention;
    private final float verticalRetention;
    private final float acceleration;
    private final float buoyancyScale;
    private final boolean usesSwimAids;
    private final float fogEnd;
    private final OverlayStyle overlayStyle;
    private final float overlayAlpha;
    private final float fogBrightness;

    /**
     * Creates a preset from its movement and visibility numbers.
     *
     * @param horizontalRetention fraction of horizontal speed kept each tick; for gases it applies on top of air drag
     * @param verticalRetention   fraction of vertical speed kept each tick; unused for gases
     * @param acceleration        movement input acceleration relative to vanilla water
     * @param usesSwimAids        whether Depth Strider, Dolphin's Grace, and sprinting speed the entity up as in water
     * @param fogEnd              distance in blocks at which fog hides terrain with the camera inside the fluid
     * @param fogBrightness       multiplier on the overlay colour when used as the fog colour
     */
    VEFluidPhysics(boolean gas, float horizontalRetention, float verticalRetention, float acceleration, float buoyancyScale,
                   boolean usesSwimAids, float fogEnd, OverlayStyle overlayStyle, float overlayAlpha, float fogBrightness) {
        this.gas = gas;
        this.horizontalRetention = horizontalRetention;
        this.verticalRetention = verticalRetention;
        this.acceleration = acceleration;
        this.buoyancyScale = buoyancyScale;
        this.usesSwimAids = usesSwimAids;
        this.fogEnd = fogEnd;
        this.overlayStyle = overlayStyle;
        this.overlayAlpha = overlayAlpha;
        this.fogBrightness = fogBrightness;
    }

    public boolean isGas() {
        return gas;
    }

    public float horizontalRetention() {
        return horizontalRetention;
    }

    public float verticalRetention() {
        return verticalRetention;
    }

    public float acceleration() {
        return acceleration;
    }

    public float buoyancyScale() {
        return buoyancyScale;
    }

    public boolean usesSwimAids() {
        return usesSwimAids;
    }

    public float fogEnd() {
        return fogEnd;
    }

    public OverlayStyle overlayStyle() {
        return overlayStyle;
    }

    public float overlayAlpha() {
        return overlayAlpha;
    }

    public float fogBrightness() {
        return fogBrightness;
    }

    /**
     * How the screen is covered with the camera inside the fluid.
     */
    public enum OverlayStyle {
        NONE,
        TINT,
        TEXTURE
    }
}
