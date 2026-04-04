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

import java.util.List;

public class GeneratorProcessor extends BasicProcessor {

    private int divisor = 1;
    private boolean allowOverflow = false;

    public GeneratorProcessor(boolean allowOverflow, int divisor) {
        this.allowOverflow = allowOverflow;
        this.divisor = divisor;
    }

    public GeneratorProcessor() {
    }

    @Override
    public boolean validateRecipe(VETileEntity tile) {

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);
        if (counterLength.counter() > 0) {
            this.setRecipeReady(true);
            return true;
        }

        List<VERecipe> potentialRecipes = VERecipe.getPotentialRecipes(tile);
        this.potentialRecipes = potentialRecipes;

        if (this.potentialRecipes.size() == 1) {
            this.selectedRecipe = VERecipe.getCompleteRecipe(tile);
            VEEnergyRecipe veEnergyRecipe = (VEEnergyRecipe) this.selectedRecipe;
            BasicParser parser = this.selectedRecipe.getParser();
            parser.completeRecipe(tile);
            tile.getEnergy().setProduction(veEnergyRecipe.getEnergyPerTick() / divisor);
            tile.setData(VEAttachments.COUNTER_LENGTH,
                    new CounterLength(veEnergyRecipe.getProcessTime(), veEnergyRecipe.getProcessTime()));
            tile.setLit(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean processRecipe(VETileEntity tile) {
        VEEnergyStorage energy = tile.getEnergy();

        if (allowOverflow && energy.getEnergyStored() + energy.getProduction() > energy.getCapacity())
            return false;
        energy.addEnergy(energy.getProduction());
        int sound_tick = tile.getData(VEAttachments.SOUND_TICK);
        if (++sound_tick == 19) {
            sound_tick = 0;
            tile.getLevel().playSound(null,
                    tile.getBlockPos(),
                    VESounds.GENERAL_MACHINE_NOISE,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            tile.setData(VEAttachments.SOUND_TICK, sound_tick);
        }
        return true;
    }

    @Override
    public boolean completeRecipe(VETileEntity tile) {
        tile.getEnergy().setProduction(0);
        tile.setLit(false);
        tickCounter(tile,tile.getData(VEAttachments.COUNTER_LENGTH));
        return true;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new GeneratorProcessor(allowOverflow, divisor);
    }
}
