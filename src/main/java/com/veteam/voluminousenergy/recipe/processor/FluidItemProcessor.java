package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VEFluidRNGRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

import static net.minecraft.util.Mth.abs;

public class FluidItemProcessor implements RecipeProcessor {

    @Override
    public void processRecipe(VETileEntity tile) {
        if (tile.getSelectedRecipe() == null) return;
        if (!tile.canConsumeEnergy()) return;
        VEFluidRecipe recipe = tile.getSelectedRecipe();

        int counter = tile.getData("counter");

        if (counter == 1) {
            // Validate output
            for (VERelationalTank relationalTank : tile.getRelationalTanks()) {
                if (relationalTank.getTankType() == TankType.OUTPUT) {
                    FluidStack recipeFluid = recipe.getOutputFluid(relationalTank.getRecipePos());
                    FluidTank tank = relationalTank.getTank();
                    FluidStack currentFluid = tank.getFluid();
                    if (currentFluid.isEmpty()) continue;
                    // If the output fluid amount won't fit, then you must acquit
                    if (!recipeFluid.isFluidEqual(currentFluid)
                            || tank.getFluidAmount() + recipeFluid.getAmount() > tank.getCapacity()) {
                        return;
                    }
                }
            }

            ItemStackHandler handler = tile.getInventoryHandler();

            if (handler != null) {
                // Validate output
                for (VESlotManager slotManager : tile.getSlotManagers()) {
                    if (slotManager.getSlotType() != SlotType.OUTPUT) continue;
                    ItemStack recipeStack = recipe.getResult(slotManager.getRecipePos());
                    ItemStack currentItem = slotManager.getItem(handler);
                    if (currentItem.isEmpty()) continue;
                    // If the output item amount won't fit, then you must acquit
                    if (!recipeStack.is(currentItem.getItem())
                            || recipeStack.getCount() + currentItem.getCount() > currentItem.getMaxStackSize()) {
                        return;
                    }
                }

                VEFluidRNGRecipe irngRecipe = null;
                if (recipe instanceof VEFluidRNGRecipe rec) {
                    irngRecipe = rec;
                }
                Random r = new Random();

                // process recipe
                for (VESlotManager slotManager : tile.getSlotManagers()) {
                    if (slotManager.getSlotType() == SlotType.OUTPUT) {
                        ItemStack output = recipe.getResult(slotManager.getRecipePos());
                        ItemStack currentStack = slotManager.getItem(handler);
                        // rng calculations
                        if (irngRecipe != null) {
                            float randomness = irngRecipe.getOutputChance(slotManager.getRecipePos());
                            if (randomness != 1) {
                                float random = abs(0 + r.nextFloat() * (-1));
                                if (random > randomness) continue;
                            }

                        }
                        if (currentStack.isEmpty()) slotManager.setItem(output, handler);
                        else currentStack.setCount(currentStack.getCount() + output.getCount());
                    } else if (slotManager.getSlotType() == SlotType.INPUT) {
                        Ingredient ingredient = recipe.getIngredient(slotManager.getRecipePos());
                        ItemStack currentStack = slotManager.getItem(handler);
                        currentStack.setCount(currentStack.getCount() - ingredient.getItems()[0].getCount());
                    }
                }
            }

            // process recipe
            for (VERelationalTank relationalTank : tile.getRelationalTanks()) {
                if (relationalTank.getTankType() == TankType.OUTPUT) {
                    relationalTank.fillOutput(recipe, relationalTank.getRecipePos());
                } else if (relationalTank.getTankType() == TankType.INPUT) {
                    relationalTank.drainInput(recipe, relationalTank.getRecipePos());
                }
            }

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
