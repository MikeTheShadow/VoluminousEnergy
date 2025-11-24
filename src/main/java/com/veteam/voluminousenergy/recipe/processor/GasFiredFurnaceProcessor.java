package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class GasFiredFurnaceProcessor implements AbstractRecipeProcessor {

    private SmeltingRecipe furnaceRecipe;
    private BlastingRecipe blastingRecipe;

    @Override
    public boolean processRecipe(VETileEntity tile) {

        CounterLength fuelCounterLength = tile.getData(VEAttachments.FUEL_COUNTER_LENGTH);

        int fuelCounter = fuelCounterLength.counter();
        int fuelLength = fuelCounterLength.length();

        FluidStack fuel = tile.getFluidStackFromTank(0);
        ItemStack stack = tile.getInventory().getStackInSlot(2);

        // Gas processing
        if (fuelCounter > 0) {
            fuelCounter--;
        } else if (!fuel.is(Fluids.EMPTY) && !stack.isEmpty()) {
            // Drain Input

            tile.getRelationalTank(0).getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
            fuelCounter = 400 * CombustibleFluidsData.getEnergyPerTick(fuel) / 4;
            VEItemStackHandler inventory = tile.getInventory();
            ItemStack upgradeItem = inventory.getStackInSlot(4);
            if (upgradeItem.getCount() > 0 && upgradeItem.getItem() == VEItems.QUARTZ_MULTIPLIER.get()) {
                fuelCounter = fuelCounter / (upgradeItem.getCount() ^ 2);
            } else if (!upgradeItem.isEmpty() && upgradeItem.has(VEDataComponents.MULTIPLIER_DATA)) {
                Float multiplier = upgradeItem.get(VEDataComponents.MULTIPLIER_DATA);
                multiplier = multiplier / 0.5F > 1 ? 1 : multiplier / 0.5F;
                fuelCounter = (int) (fuelCounter * multiplier);
            }
            fuelLength = fuelCounter;
            tile.setChanged();
        } else {
            return false;
        }
        tile.setData(VEAttachments.FUEL_COUNTER_LENGTH,new CounterLength(fuelCounter,fuelLength));
        if (blastingRecipe != null) processForRecipe(blastingRecipe, tile);
        else if (furnaceRecipe != null) processForRecipe(furnaceRecipe, tile);
        return true;
    }

    void processForRecipe(Recipe<?> recipe, VETileEntity tile) {
        if (!canInsertIntoResult(recipe, tile.getLevel().registryAccess(), tile.getInventory().getStackInSlot(3))) {
            return;
        }

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);

        int counter = counterLength.counter();
        int length = counterLength.length();

        if (counter == 1) {
            counter--;
            tile.getInventory().extractItem(2, 1, false);
            ItemStack output = recipe.getResultItem(tile.getLevel().registryAccess()).copy();
            tile.getInventory().insertItem(3, output, false);
        } else if (counter > 0) {
            counter--;
            int soundTick = tile.getData(VEAttachments.SOUND_TICK);
            if (++soundTick == 19) {
                soundTick = 0;
                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                    tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            tile.setData(VEAttachments.SOUND_TICK, soundTick);
        } else {
            counter = tile.updateCounter(200);
        }
        tile.setData(VEAttachments.COUNTER_LENGTH,new CounterLength(counter,length));
        tile.setChanged();
    }

    @Override
    public void validateRecipe(VETileEntity tile) {
        Level level = tile.getLevel();
        ItemStack furnaceInput = tile.getInventory().getStackInSlot(2);
        var blastingRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
        if (blastingRecipeNew != null) {
            blastingRecipe = blastingRecipeNew.value();
            tile.updateCounter(200);
            tile.setChanged();
            return;
        } else blastingRecipe = null;
        var furnaceRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
        if (furnaceRecipeNew != null) {
            furnaceRecipe = furnaceRecipeNew.value();
            tile.updateCounter(200);
            tile.setChanged();
            return;
        } else furnaceRecipe = null;

        tile.setData(VEAttachments.COUNTER_LENGTH,new CounterLength(0,0));
        tile.setChanged();
    }

    boolean canInsertIntoResult(Recipe<?> recipe, RegistryAccess access, ItemStack currentStack) {
        ItemStack result = recipe.getResultItem(access);
        if (!result.is(currentStack.getItem()) && !currentStack.isEmpty()) return false;
        return result.getCount() + currentStack.getCount() <= result.getMaxStackSize();
    }
}
