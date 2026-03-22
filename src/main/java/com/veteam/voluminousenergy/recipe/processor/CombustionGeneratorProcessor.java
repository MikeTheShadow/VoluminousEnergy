package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.records.CounterLength;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class CombustionGeneratorProcessor extends BasicProcessor {
    public static final int COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT = 250;
    public static final int COMBUSTION_GENERATOR_PROCESS_TIME = 1600;

    @Override
    public boolean validateRecipe(VETileEntity tile) {

        CounterLength counterLength = tile.getData(VEAttachments.COUNTER_LENGTH);
        if (counterLength.counter() > 0) {
            return true;
        }
        this.potentialRecipes = VERecipe.getPotentialRecipes(tile);

        if (this.potentialRecipes.size() == 1) {
            VEEnergyStorage storage = tile.getEnergy();
            VERecipe newRecipe = VERecipe.getCompleteRecipe(tile);
            if (newRecipe == null) {
                this.selectedRecipe = null;
                return false;
            }
            if (selectedRecipe != newRecipe) {
                this.selectedRecipe = newRecipe;
            }
            FluidStack fuel = tile.getFluidStackFromTank(0);
            FluidStack oxi = tile.getFluidStackFromTank(1);

            VERelationalTank fuelTank = tile.getRelationalTank(0);
            VERelationalTank oxiTank = tile.getRelationalTank(1);

            int powerGeneration = CombustibleFluidsData.getEnergyPerTick(fuel);
            float multiplier = OxidizerFluidsData.getOxidizerMultiplier(oxi);

            if (fuelTank.getTank().getFluidAmount() < COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT
                    || oxiTank.getTank().getFluidAmount() < COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT) {
                return false;
            }

            fuelTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
            oxiTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);

            int counter;
            if (Config.COMBUSTION_GENERATOR_BALANCED_MODE.get()) {
                counter = COMBUSTION_GENERATOR_PROCESS_TIME / 4;
            } else {
                counter = Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get() / 4;
            }

            int production = (int) (powerGeneration * multiplier);
            tile.setData(VEAttachments.COUNTER_LENGTH, new CounterLength(counter, counter));
            storage.setProduction(production);
            return true;
        } else {
            this.selectedRecipe = null;
        }
        return false;
    }

    @Override
    public boolean processRecipe(VETileEntity tile) {
        VEEnergyStorage storage = tile.getEnergy();
        if (storage.getEnergyStored() + storage.getProduction() > Config.COMBUSTION_GENERATOR_MAX_POWER.get())
            return false;

        storage.addEnergy(storage.getProduction());
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
        VEEnergyStorage storage = tile.getEnergy();
        storage.setProduction(0);
        return true;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new CombustionGeneratorProcessor();
    }
}
