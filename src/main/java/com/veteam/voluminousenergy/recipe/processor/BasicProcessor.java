package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.sounds.SoundSource;

public class BasicProcessor implements AbstractRecipeProcessor {

    @Override
    public void validateRecipe(VETileEntity tile) {
        tile.setPotentialRecipes(VERecipe.getPotentialRecipes(tile));

        if (tile.getPotentialRecipes().size() == 1) {
            VERecipe newRecipe = VERecipe.getCompleteRecipe(tile);

            if (newRecipe == null) {
                CounterLength counterLength = new CounterLength(0,0);
                tile.setData(VEAttachments.COUNTER_LENGTH,counterLength);
                tile.setChanged();
                tile.setSelectedRecipe(null);
                return;
            }

            int newLength = tile.updateCounter(newRecipe);

            if (tile.getSelectedRecipe() != newRecipe) {
                tile.setSelectedRecipe(newRecipe);
                CounterLength oldData = tile.getData(VEAttachments.COUNTER_LENGTH);
                CounterLength counterLength = new CounterLength(newLength,oldData.length());
                tile.setData(VEAttachments.COUNTER_LENGTH,counterLength);
                tile.setChanged();
            }
        } else {
            CounterLength counterLength = new CounterLength(0,0);
            tile.setData(VEAttachments.COUNTER_LENGTH,counterLength);
            tile.setChanged();
            tile.setSelectedRecipe(null);
        }
    }

    @Override
    public void processRecipe(VETileEntity tile) {
        if (tile.getSelectedRecipe() == null) return;
        if (!tile.canConsumeEnergy()) return;
        VERecipe recipe = tile.getSelectedRecipe();

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);
        int counter = counterLength.counter();

        if (counter == 1) {
            BasicParser parser = recipe.getParser();
            if (!parser.canCompleteRecipe(tile)) return;
            parser.completeRecipe(tile);
            tile.markRecipeDirty();
            tile.markFluidInputDirty();
            tile.setChanged();
        } else if (counter > 0) {
            int soundTick = tile.getData(VEAttachments.SOUND_TICK);
            if (++soundTick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
                soundTick = 0;
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.AQUEOULIZER, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            tile.setData(VEAttachments.SOUND_TICK, soundTick);
        }
        tile.setData(VEAttachments.COUNTER_LENGTH,new CounterLength(counter - 1,counterLength.length()));
        tile.consumeEnergy();
    }

}
