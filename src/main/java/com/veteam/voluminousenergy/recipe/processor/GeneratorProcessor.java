package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VEEnergyRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.RecipeParser;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class GeneratorProcessor implements RecipeProcessor {

    private int divisor = 1;
    private boolean allowOverflow = false;

    public GeneratorProcessor(boolean allowOverflow, int divisor) {
        this.allowOverflow = allowOverflow;
        this.divisor = divisor;
    }

    public GeneratorProcessor(int divisor) {
        this.divisor = divisor;
    }

    @Override
    public void validateRecipe(VETileEntity tile) {
        if (!tile.isRecipeDirty()) {
            return;
        }
        tile.setRecipeDirty(false);
        List<VERecipe> potentialRecipes = VERecipe.getPotentialRecipes(tile);
        tile.setPotentialRecipes(potentialRecipes);
        if (tile.getPotentialRecipes().size() == 1) {
            tile.setSelectedRecipe(VERecipe.getCompleteRecipe(tile));
        }
    }

    @Override
    public void processRecipe(VETileEntity tile) {
        VEEnergyStorage energy = tile.getEnergy();

        if (energy == null) throw new NotImplementedException("Missing energy impl for " + tile.getDisplayName());
        int counter = tile.getData("counter");

        if (counter > 0) {
            if (energy.getEnergyStored() + energy.getProduction() <= energy.getCapacity() || allowOverflow) {
                tile.setData("counter", --counter);
                energy.addEnergy(energy.getProduction());
            }

            if (Config.PLAY_MACHINE_SOUNDS.get()) {
                int sound_tick = tile.getData("sound_tick");
                if (++sound_tick == 19) {
                    sound_tick = 0;
                    tile.getLevel().playSound(null,
                            tile.getBlockPos(),
                            VESounds.GENERAL_MACHINE_NOISE,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                    tile.setData("sound_tick", sound_tick);
                }
            }
            tile.setChanged();
        } else if (counter == 0) {
            if (tile.getSelectedRecipe() instanceof VEEnergyRecipe veEnergyRecipe) {
                RecipeParser parser = veEnergyRecipe.getParser();
                if(!parser.canCompleteRecipe(tile)) return;
                // Check to see if the energy produced will overflow the tile
                if(tile.getEnergy().isFullyCharged()) return;

                VoluminousEnergy.LOGGER.info("recipe completed!");

                // Since we're a generator we want to subtract the amounts at the start rather than at the end
                veEnergyRecipe.getParser().completeRecipe(tile);

                tile.setData("counter", veEnergyRecipe.getProcessTime());
                tile.getEnergy().setProduction(veEnergyRecipe.getEnergyPerTick() / divisor);
                tile.setData("length", veEnergyRecipe.getProcessTime());
                tile.setSelectedRecipe(null);
                tile.setChanged();
            } else {
                tile.getEnergy().setProduction(0);
            }
        }
    }

}
