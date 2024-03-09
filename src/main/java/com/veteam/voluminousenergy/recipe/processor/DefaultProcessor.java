package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.RecipeParser;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class DefaultProcessor implements AbstractRecipeProcessor {

    @Override
    public void validateRecipe(VETileEntity tile) {
        tile.setPotentialRecipes(VERecipe.getPotentialRecipes(tile));

        if (tile.getPotentialRecipes().size() == 1) {
            VERecipe newRecipe = VERecipe.getCompleteRecipe(tile);
            if (newRecipe == null) {
                tile.setData("counter",0);
                tile.setData("length",0);
                tile.setSelectedRecipe(null);
                return;
            }

            int newLength;
            ItemStackHandler handler = tile.getInventoryHandler();
            if (tile.getEnergy() != null && handler != null) {
                newLength = tile.calculateCounter(newRecipe.getProcessTime(),
                        handler.getStackInSlot(tile.getEnergy().getUpgradeSlotId()).copy());
            } else {
                newLength = tile.calculateCounter(newRecipe.getProcessTime(), ItemStack.EMPTY);
            }

            double ratio = (double) tile.getData("length") / (double) newLength;
            tile.setData("length",newLength);
            tile.setData("counter", (int) (tile.getData("counter") / ratio));

            if (tile.getSelectedRecipe() != newRecipe) {
                tile.setSelectedRecipe(newRecipe);
                tile.setData("counter",newLength);
            }
        } else {
            tile.setData("counter",0);
            tile.setData("length",0);
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

            RecipeParser parser = recipe.getParser();
            if(!parser.canCompleteRecipe(tile)) return;
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
