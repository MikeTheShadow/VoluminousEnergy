package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.records.CounterLength;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class BasicProcessor implements AbstractRecipeProcessor {

    SoundEvent soundEvent = VESounds.GENERAL_MACHINE_NOISE;

    boolean isRecipeDirty = false;
    boolean isRecipeReady = false;

    VERecipe selectedRecipe = null;
    List<VERecipe> potentialRecipes = new ArrayList<>();

    public BasicProcessor() {

    }

    public BasicProcessor(SoundEvent event) {
        soundEvent = event;
    }

    @Override
    public void tick(VETileEntity tile) {
        if (this.isRecipeDirty) {
            if (validateRecipe(tile))
                isRecipeReady = true;
            this.isRecipeDirty = false;
        }

        if (!isRecipeReady)
            return;

        CounterLength currentTick = tile.getData(VEAttachments.COUNTER_LENGTH);

        if (currentTick.counter() != 0 && processRecipe(tile)) {
            CounterLength nextTick = new CounterLength(currentTick.counter() - 1, currentTick.length());
            tile.setData(VEAttachments.COUNTER_LENGTH, nextTick);
            tile.setChanged();
        } else if (currentTick.counter() == 0 && completeRecipe(tile)) {
            this.markRecipeDirty();
            tile.markFluidInputDirty();
            tile.setChanged();
        }
    }

    public boolean validateRecipe(VETileEntity tile) {
        this.potentialRecipes = VERecipe.getPotentialRecipes(tile);

        if (this.potentialRecipes.size() != 1) {
            tile.setLit(false);
            CounterLength counterLength = new CounterLength(0, 0);
            tile.setData(VEAttachments.COUNTER_LENGTH, counterLength);
            this.selectedRecipe = null;
            return false;
        }

        VERecipe newRecipe = VERecipe.getCompleteRecipe(tile);
        if (newRecipe == null) {
            CounterLength counterLength = new CounterLength(0, 0);
            tile.setData(VEAttachments.COUNTER_LENGTH, counterLength);
            this.selectedRecipe = null;
            return false;
        }
        tile.setLit(true);
        updateCounter(newRecipe,tile);
        return true;
    }

    public boolean processRecipe(VETileEntity tile) {
        if (!tile.consumeEnergy())
            return false;
        int soundTick = tile.getData(VEAttachments.SOUND_TICK);
        if (++soundTick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
            soundTick = 0;
            tile.getLevel().playSound(null, tile.getBlockPos(), soundEvent, SoundSource.BLOCKS, 1.0F,
                    1.0F);
        }
        tile.setData(VEAttachments.SOUND_TICK, soundTick);
        return true;
    }

    public boolean completeRecipe(VETileEntity tile) {
        BasicParser parser = selectedRecipe.getParser();
        if (!parser.canCompleteRecipe(tile))
            return false;
        parser.completeRecipe(tile);
        return true;
    }


    private void updateCounter(VERecipe recipe,VETileEntity tile) {
        int length;
        ItemStackHandler handler = tile.getInventory();
        VEEnergyStorage energy = tile.getEnergy();
        if (energy != null && handler != null && energy.getUpgradeSlotId() != -1) {
            length = calculateCounter(recipe.getProcessTime(),
                handler.getStackInSlot(energy.getUpgradeSlotId()).copy());
        } else {
            length = calculateCounter(recipe.getProcessTime(), ItemStack.EMPTY);
        }

        CounterLength newLength = new CounterLength(length, length);
        CounterLength oldLength = tile.getData(VEAttachments.COUNTER_LENGTH);
        if (selectedRecipe != recipe || oldLength.counter() == 0) {
            selectedRecipe = recipe;
        } else if (oldLength.length() == length) {
            newLength = oldLength;
        } else {
            float difference = (float) oldLength.length() / (float) newLength.length();
            int newCurrentCount = Math.round(oldLength.counter() / difference);
            newLength = new CounterLength(newCurrentCount, newLength.length());
        }
        tile.setData(VEAttachments.COUNTER_LENGTH, newLength);
        tile.setChanged();
    }

    private static int calculateCounter(int processTime, ItemStack upgradeStack) {

        float multiplier = upgradeStack.getOrDefault(VEDataComponents.MULTIPLIER_DATA, 0.0F);

        if (upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER.get()) {
            int count = upgradeStack.getCount();
            if (count == 4) {
                return 5;
            } else {
                return (-45 * upgradeStack.getCount()) + processTime;
            }
        } else if (multiplier != 0) {
            return (int) (processTime * multiplier);
        }
        return processTime;
    }

    /**
     * This updates the counter and takes into account an upgrade slot if
     * it exists.
     *
     * @param defaultProcessTime The base processing time in ticks.
     * @return The new length. Only need to use this if you potentially overwrite
     *         the changes here (this.setData("length") for example)
     */
    public static int updateCounter(int defaultProcessTime,VETileEntity tile) {
        int newLength;
        ItemStackHandler handler = tile.getInventory();
        VEEnergyStorage energy = tile.getEnergy();
        if (energy != null && handler != null) {
            newLength = calculateCounter(defaultProcessTime,
                handler.getStackInSlot(energy.getUpgradeSlotId()).copy());
        } else {
            newLength = calculateCounter(defaultProcessTime, ItemStack.EMPTY);
        }

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);

        double ratio = (double) counterLength.length() / (double) newLength;

        int ratioedCounter = (int) (counterLength.counter() / ratio);

        CounterLength newCounter = new CounterLength(ratioedCounter == 0 ? newLength : ratioedCounter, newLength);
        tile.setData(VEAttachments.COUNTER_LENGTH, newCounter);
        tile.setChanged();
        return newLength;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new BasicProcessor(soundEvent);
    }

    public boolean isRecipeDirty() {
        return isRecipeDirty;
    }

    public boolean isRecipeReady() {
        return isRecipeReady;
    }

    public VERecipe getSelectedRecipe() {
        return selectedRecipe;
    }

    public List<VERecipe> getPotentialRecipes() {
        return potentialRecipes;
    }

    public void markRecipeDirty() {
        this.isRecipeDirty = true;
    }
}
