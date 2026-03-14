package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VEEnergyRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class GeneratorProcessor implements AbstractRecipeProcessor {

    private int divisor = 1;
    private boolean allowOverflow = false;

    public GeneratorProcessor(boolean allowOverflow, int divisor) {
        this.allowOverflow = allowOverflow;
        this.divisor = divisor;
    }

    public GeneratorProcessor() {
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
    public boolean processRecipe(VETileEntity tile) {
        VEEnergyStorage energy = tile.getEnergy();

        if (energy == null)
            throw new NotImplementedException("Missing energy impl for " + tile.getDisplayName());

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);

        int counter = counterLength.counter();
        int length = counterLength.length();

        if (counter > 0) {
            tile.setLit(true);
            if (energy.getEnergyStored() + energy.getProduction() <= energy.getCapacity() || allowOverflow) {
                counter--;
                tile.setData(VEAttachments.COUNTER_LENGTH, new CounterLength(counter, length));
                energy.addEnergy(energy.getProduction());
            }

            if (Config.PLAY_MACHINE_SOUNDS.get()) {
                int sound_tick = tile.getData(VEAttachments.SOUND_TICK);
                if (++sound_tick == 19) {
                    sound_tick = 0;
                    tile.getLevel().playSound(null,
                            tile.getBlockPos(),
                            VESounds.GENERAL_MACHINE_NOISE,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                    tile.setData(VEAttachments.SOUND_TICK, sound_tick);
                }
            }
            tile.setChanged();
        } else if (counter == 0) {
            if (tile.getSelectedRecipe() instanceof VEEnergyRecipe veEnergyRecipe) {
                BasicParser parser = veEnergyRecipe.getParser();
                if (!parser.canCompleteRecipe(tile))
                    return false;
                // Check to see if the energy produced will overflow the tile
                if (tile.getEnergy().isFullyCharged())
                    return false;
                // Since we're a generator we want to subtract the amounts at the start rather
                // than at the end
                veEnergyRecipe.getParser().completeRecipe(tile);
                tile.getEnergy().setProduction(veEnergyRecipe.getEnergyPerTick() / divisor);
                tile.setData(VEAttachments.COUNTER_LENGTH,
                        new CounterLength(veEnergyRecipe.getProcessTime(), veEnergyRecipe.getProcessTime()));
                tile.setSelectedRecipe(null);
                tile.setChanged();
            } else {
                tile.getEnergy().setProduction(0);
                tile.setLit(false);
            }
        }
        return true;
    }

}
