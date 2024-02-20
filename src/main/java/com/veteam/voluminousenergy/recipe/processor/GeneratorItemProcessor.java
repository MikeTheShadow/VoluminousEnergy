package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VEEnergyRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.NotImplementedException;

public class GeneratorItemProcessor implements RecipeProcessor {

    private int divisor = 1;
    private boolean allowOverflow = false;

    public GeneratorItemProcessor(boolean allowOverflow,int divisor) {
        this.allowOverflow = allowOverflow;
        this.divisor = divisor;
    }

    public GeneratorItemProcessor() {
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
            if (tile.getSelectedRecipe() instanceof VEEnergyRecipe recipe) {
                tile.getInventory().extractItem(0, recipe.getIngredientCount(0), false);
                tile.setData("counter", recipe.getProcessTime());
                tile.getEnergy().setProduction(recipe.getEnergyPerTick() / divisor);
                tile.setData("length", recipe.getProcessTime());
                tile.setSelectedRecipe(null);
                tile.setChanged();
            } else {
                tile.getEnergy().setProduction(0);
            }
        }
    }

}
