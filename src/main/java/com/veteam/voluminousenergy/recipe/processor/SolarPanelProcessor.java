package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class SolarPanelProcessor implements AbstractRecipeProcessor{

    int generationAmount;

    public SolarPanelProcessor(int generationAmount) {
        this.generationAmount = generationAmount;
    }

    @Override
    public void validateRecipe(VETileEntity tile) {

    }

    @Override
    public void processRecipe(VETileEntity tile) {

        int generation = (int) (generationAmount * solarIntensity(tile.getLevel()));
        tile.getEnergy().setProduction(generation);
        tile.getEnergy().addEnergy(generation);
    }

    /**
     * Cosine curve based off the location of the Sun(? I think, at least it looks like that)
     * Noon is the Zenith, hence why we use a cosine curve, since cosine curves start at a max
     * amplitude, which of course is Noon/Zenith. We do manipulate the curve a bit to make it more "reasonable"
     */
    protected float solarIntensity(Level level) {

        float celestialAngle = level.getSunAngle(1.0f); // Zenith = 0rad

        if(celestialAngle > Math.PI) celestialAngle = (2 * ((float) Math.PI) - celestialAngle);

        float intensity = Mth.cos(0.2f + (celestialAngle / 1.2f));
        intensity = Mth.clamp(intensity, 0, 1);

        if(intensity > 0.1f) {
            intensity = intensity * 1.5f;
            if(intensity > 1f) intensity = 1f;
        }

        if(intensity > 0){
            if(level.isRaining()) return intensity * 0.6f;
            if(level.isThundering()) return intensity * 0.2f;
        }

        return intensity;
    }
}
