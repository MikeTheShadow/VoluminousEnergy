package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class GasFiredFurnaceProcessor extends BasicProcessor {

    private RecipeHolder<SmeltingRecipe> furnaceRecipe;
    private RecipeHolder<BlastingRecipe> blastingRecipe;

    @Override
    public void tick(VETileEntity tile) {
        if (isRecipeDirty()) {
            if (validateRecipe(tile))
                this.setRecipeReady(true);
            this.setRecipeDirty(false);
        }

        CounterLength fuelCounterLength = tile.getData(VEAttachments.FUEL_COUNTER_LENGTH);
        int fuelCounter = fuelCounterLength.counter();
        if (fuelCounter > 0) {
            fuelCounter--;
            tile.setData(VEAttachments.FUEL_COUNTER_LENGTH, new CounterLength(fuelCounter, fuelCounterLength.length()));
        }

        tile.setLit(fuelCounter > 0);

        if (!isRecipeReady())
            return;

        super.tick(tile);
    }

    @Override
    public boolean processRecipe(VETileEntity tile) {

        CounterLength fuelCounterLength = tile.getData(VEAttachments.FUEL_COUNTER_LENGTH);

        int fuelCounter = fuelCounterLength.counter();
        int fuelLength = fuelCounterLength.length();

        FluidStack fuel = tile.getFluidStackFromTank(0);

        // Gas processing
        if (fuelCounter <= 0) {
            if (!fuel.is(Fluids.EMPTY)) {
                tile.getRelationalTank(0).getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
                fuelCounter = 400 * CombustibleFluidsData.getEnergyPerTick(fuel) / 4;
                VEItemStackHandler inventory = tile.getInventory();
                ItemStack upgradeItem = inventory.getStackInSlot(4);
                if (upgradeItem.getCount() > 0 && upgradeItem.getItem() == VEItems.QUARTZ_MULTIPLIER.get()) {
                    fuelCounter = fuelCounter / (upgradeItem.getCount() * upgradeItem.getCount());
                } else if (!upgradeItem.isEmpty() && upgradeItem.has(VEDataComponents.MULTIPLIER_DATA)) {
                    Float multiplier = upgradeItem.get(VEDataComponents.MULTIPLIER_DATA);
                    multiplier = multiplier / 0.5F > 1 ? 1 : multiplier / 0.5F;
                    fuelCounter = (int) (fuelCounter * multiplier);
                }
                fuelLength = fuelCounter;
                tile.setData(VEAttachments.FUEL_COUNTER_LENGTH, new CounterLength(fuelCounter, fuelLength));
                tile.setLit(true);
                tile.setChanged();
            } else {
                return false;
            }
        }

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
    public boolean validateRecipe(VETileEntity tile) {
        Level level = tile.getLevel();
        ItemStack furnaceInput = tile.getInventory().getStackInSlot(2);
        var blastingRecipeNew = level.getRecipeManager()
                .getRecipeFor(RecipeType.BLASTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
        if (blastingRecipeNew != null) {
            blastingRecipe = blastingRecipeNew;
            updateCounter(200,tile);
            return true;
        } else
            blastingRecipe = null;
        var furnaceRecipeNew = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
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
        throw new IllegalStateException("Unable to complete a recipe as none were selected");
    }

    private boolean createOutput(VETileEntity tile, RecipeHolder<? extends Recipe<?>> recipeHolder) {
        Recipe<?> recipe = recipeHolder.value();
        if (!canInsertIntoResult(recipe, tile.getLevel().registryAccess(), tile.getInventory().getStackInSlot(3))) {
            return false;
        }
        tile.getInventory().extractItem(2, 1, false);
        ItemStack output = recipe.getResultItem(tile.getLevel().registryAccess()).copy();
        tile.getInventory().insertItem(3, output, false);
        return true;
    }

    boolean canInsertIntoResult(Recipe<?> recipe, RegistryAccess access, ItemStack currentStack) {
        ItemStack result = recipe.getResultItem(access);
        if (!result.is(currentStack.getItem()) && !currentStack.isEmpty())
            return false;
        return result.getCount() + currentStack.getCount() <= result.getMaxStackSize();
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new GasFiredFurnaceProcessor();
    }
}
