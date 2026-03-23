package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class ElectricFurnaceProcessor extends BasicProcessor {

    private SmeltingRecipe furnaceRecipe;
    private BlastingRecipe blastingRecipe;

    @Override
    public boolean validateRecipe(VETileEntity tile) {
        Level level = tile.getLevel();
        ItemStack furnaceInput = tile.getInventory().getStackInSlot(0);
        var blastingRecipeNew = level.getRecipeManager()
                .getRecipeFor(RecipeType.BLASTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
        if (blastingRecipeNew != null) {
            blastingRecipe = blastingRecipeNew.value();
            updateCounter(200,tile);
            return true;
        } else
            blastingRecipe = null;
        var furnaceRecipeNew = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
        if (furnaceRecipeNew != null) {
            furnaceRecipe = furnaceRecipeNew.value();
            updateCounter(200,tile);
            return true;
        } else
            furnaceRecipe = null;
        tile.setData(VEAttachments.COUNTER_LENGTH, new CounterLength(0, 0));
        return false;
    }

    @Override
    public boolean processRecipe(VETileEntity tile) {
        if (!tile.consumeEnergy())
            return false;
        int soundTick = tile.getData(VEAttachments.SOUND_TICK);
        if (++soundTick == 19) {
            soundTick = 0;
            if (Config.PLAY_MACHINE_SOUNDS.get()) {
                tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        tile.setData(VEAttachments.SOUND_TICK, soundTick);
        return true;
    }

    @Override
    public boolean completeRecipe(VETileEntity tile) {
        if (blastingRecipe != null)
            return createOutput(tile, blastingRecipe);
        else if (furnaceRecipe != null)
            return createOutput(tile, furnaceRecipe);
        return false;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new ElectricFurnaceProcessor();
    }

    private boolean createOutput(VETileEntity tile, Recipe<?> recipe) {
        if (!canInsertIntoResult(recipe, tile.getLevel().registryAccess(), tile.getInventory().getStackInSlot(1))) {
            return false;
        }
        tile.getInventory().extractItem(0, 1, false);
        ItemStack output = recipe.getResultItem(tile.getLevel().registryAccess()).copy();
        tile.getInventory().insertItem(1, output, false);
        return true;
    }

    boolean canInsertIntoResult(Recipe<?> recipe, RegistryAccess access, ItemStack currentStack) {
        ItemStack result = recipe.getResultItem(access);
        if (!result.is(currentStack.getItem()) && !currentStack.isEmpty())
            return false;
        return result.getCount() + currentStack.getCount() <= result.getMaxStackSize();
    }
}
