package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ElectricFurnaceProcessor extends BasicProcessor {

    private RecipeHolder<SmeltingRecipe> furnaceRecipe;
    private RecipeHolder<BlastingRecipe> blastingRecipe;

    @Override
    public boolean validateRecipe(VETileEntity tile) {
        Level level = tile.getLevel();
        ItemStack furnaceInput = tile.getInventory().getStackInSlot(0);
        var blastingRecipeNew = ((ServerLevel) level).recipeAccess()
                .getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(furnaceInput.copy()), level).orElse(null);
        if (blastingRecipeNew != null) {
            blastingRecipe = blastingRecipeNew;
            updateCounter(200,tile);
            return true;
        } else
            blastingRecipe = null;
        var furnaceRecipeNew = ((ServerLevel) level).recipeAccess()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(furnaceInput.copy()), level).orElse(null);
        if (furnaceRecipeNew != null) {
            furnaceRecipe = furnaceRecipeNew;
            updateCounter(200,tile);
            return true;
        } else
            furnaceRecipe = null;
        tile.setData(VEAttachments.COUNTER_LENGTH, new CounterLength(0, 0));
        this.setRecipeReady(false);
        return false;
    }

    @Override
    public boolean processRecipe(VETileEntity tile) {
        if (!tile.consumeEnergy())
            return false;
        int soundTick = tile.getData(VEAttachments.SOUND_TICK);
        if (++soundTick == 19) {
            soundTick = 0;
            tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        tile.setData(VEAttachments.SOUND_TICK, soundTick);
        return true;
    }

    @Override
    public boolean completeRecipe(VETileEntity tile) {
        if (blastingRecipe != null) {
            boolean completed = createOutput(tile, blastingRecipe);
            if (completed) tile.recordRecipeUsed(blastingRecipe);
            return completed;
        } else if (furnaceRecipe != null) {
            boolean completed = createOutput(tile, furnaceRecipe);
            if (completed) tile.recordRecipeUsed(furnaceRecipe);
            return completed;
        }
        return false;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new ElectricFurnaceProcessor();
    }

    private boolean createOutput(VETileEntity tile, RecipeHolder<? extends Recipe<SingleRecipeInput>> recipeHolder) {
        Recipe<SingleRecipeInput> recipe = recipeHolder.value();
        SingleRecipeInput input = new SingleRecipeInput(tile.getInventory().getStackInSlot(0).copy());
        if (!canInsertIntoResult(recipe, input, tile.getInventory().getStackInSlot(1))) {
            return false;
        }
        tile.getInventory().extractItem(0, 1, false);
        ItemStack output = recipe.assemble(input).copy();
        tile.getInventory().insertItem(1, output, false);
        return true;
    }

    boolean canInsertIntoResult(Recipe<SingleRecipeInput> recipe, SingleRecipeInput input, ItemStack currentStack) {
        ItemStack result = recipe.assemble(input);
        if (!result.is(currentStack.getItem()) && !currentStack.isEmpty())
            return false;
        return result.getCount() + currentStack.getCount() <= result.getMaxStackSize();
    }
}
