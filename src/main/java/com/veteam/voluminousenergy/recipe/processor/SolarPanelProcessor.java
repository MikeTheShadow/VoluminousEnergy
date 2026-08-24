package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;

public class SolarPanelProcessor implements AbstractRecipeProcessor {

    int generationAmount;

    public SolarPanelProcessor(int generationAmount) {
        this.generationAmount = generationAmount;
    }

    @Override
    public void tick(VETileEntity tile) {
        int generation = (int) (generationAmount * solarIntensity(tile.getLevel(), tile.getBlockPos()));

        if (generation != tile.getEnergy().getProduction()) {
            tile.getEnergy().setProduction(generation);
            tile.setLit(generation > 0);
            tile.setChanged();
        }

        if (generation > 0) {
            tile.getEnergy().addEnergy(generation);
        }
    }

    /**
     * Cosine curve based off the location of the Sun(? I think, at least it looks
     * like that)
     * Noon is the Zenith, hence why we use a cosine curve, since cosine curves
     * start at a max
     * amplitude, which of course is Noon/Zenith. We do manipulate the curve a bit
     * to make it more "reasonable"
     */
    protected float solarIntensity(Level level, BlockPos pos) {
        if (!level.canSeeSky(pos.above()))
            return 0.0f;

        // Zenith = 0rad. SUN_ANGLE is in degrees; convert to radians like the old getSunAngle(1.0f) did.
        float celestialAngle = (float) (level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, pos) * (Math.PI / 180.0));

        if (celestialAngle > Math.PI)
            celestialAngle = (2 * ((float) Math.PI) - celestialAngle);

        float intensity = Mth.cos(0.2f + (celestialAngle / 1.2f));
        intensity = Mth.clamp(intensity, 0, 1);

        if (intensity > 0.1f) {
            intensity = intensity * 1.5f;
            if (intensity > 1f)
                intensity = 1f;
        }

        if (intensity > 0) {
            if (level.isRaining())
                return intensity * 0.6f;
            if (level.isThundering())
                return intensity * 0.2f;
        }

        return intensity;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new SolarPanelProcessor(generationAmount);
    }
}
