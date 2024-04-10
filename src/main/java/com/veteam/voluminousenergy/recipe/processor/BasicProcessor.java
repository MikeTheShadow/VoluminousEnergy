package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.sounds.SoundSource;

public class BasicProcessor implements AbstractRecipeProcessor {

    @Override
    public void validateRecipe(VETileEntity tile) {
        tile.setPotentialRecipes(VERecipe.getPotentialRecipes(tile));

        if (tile.getPotentialRecipes().size() == 1) {
            VERecipe newRecipe = VERecipe.getCompleteRecipe(tile);
            if (newRecipe == null) {
                tile.setData("counter", 0);
                tile.setData("length", 0);
                tile.setSelectedRecipe(null);
                return;
            }

            int newLength = tile.updateCounter(newRecipe);

            if (tile.getSelectedRecipe() != newRecipe) {
                tile.setSelectedRecipe(newRecipe);
                tile.setData("counter", newLength);
            }
        } else {
            tile.setData("counter", 0);
            tile.setData("length", 0);
            tile.setSelectedRecipe(null);
        }
    }

    @Override
    public void processRecipe(VETileEntity tile) {
        if (tile.getSelectedRecipe() == null) return;
        if (!tile.canConsumeEnergy()) return;
        VERecipe recipe = tile.getSelectedRecipe();

        int counter = tile.getData("counter");

        if (counter == 1) {

            BasicParser parser = recipe.getParser();
            if (!parser.canCompleteRecipe(tile)) return;
            parser.completeRecipe(tile);
            tile.markRecipeDirty();
            tile.markFluidInputDirty();
            tile.setChanged();
        } else if (counter > 0) {
            int soundTick = tile.getData("sound_tick");
            if (++soundTick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
                soundTick = 0;
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.AQUEOULIZER, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            tile.setData("sound_tick", soundTick);
        } else {
            tile.setData("counter", tile.getData("length"));
        }
        tile.setData("counter", tile.getData("counter") - 1);
        tile.consumeEnergy();
    }

}
