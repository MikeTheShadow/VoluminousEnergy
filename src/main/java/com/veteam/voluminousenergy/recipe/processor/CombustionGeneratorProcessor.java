package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;


public class CombustionGeneratorProcessor extends BasicProcessor {
    public static final int COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT = 250;
    public static final int COMBUSTION_GENERATOR_PROCESS_TIME = 1600;

    @Override
    public void validateRecipe(VETileEntity tile) {
        tile.setPotentialRecipes(VERecipe.getPotentialRecipes(tile));
        if (tile.getPotentialRecipes().size() == 1) {
            VERecipe newRecipe = VERecipe.getCompleteRecipe(tile);
            if (newRecipe == null) {
                tile.setSelectedRecipe(null);
                return;
            }
            if (tile.getSelectedRecipe() != newRecipe) {
                tile.setSelectedRecipe(newRecipe);
            }
        } else {
            tile.setSelectedRecipe(null);
        }
    }

    @Override
    public void processRecipe(VETileEntity tile) {
        VERecipe recipe = tile.getSelectedRecipe();
        int counter = tile.getData("counter");
        VEEnergyStorage storage = tile.getEnergy();
        if (storage.getEnergyStored() + storage.getProduction() > Config.COMBUSTION_GENERATOR_MAX_POWER.get())
            return;

        if (counter > 0) {
            counter--;
            storage.addEnergy(storage.getProduction());
            int soundTick = tile.getData("sound_tick");
            if (++soundTick == 19) {
                soundTick = 0;
                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                    tile.getLevel().playSound(null, tile.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            tile.setData("sound_tick", soundTick);
            tile.setChanged();
        } else if (recipe != null) {
            FluidStack fuel = tile.getFluidStackFromTank(0);
            FluidStack oxi = tile.getFluidStackFromTank(1);

            VERelationalTank fuelTank = tile.getRelationalTank(0);
            VERelationalTank oxiTank = tile.getRelationalTank(1);

            int powerGeneration = CombustibleFluidsData.getEnergyProduced(fuel);
            float multiplier = OxidizerFluidsData.getOxidizerMultiplier(oxi);

            if(fuelTank.getTank().getFluidAmount() < COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT || oxiTank.getTank().getFluidAmount() < COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT) {
                return;
            }

            fuelTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
            oxiTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);

            if (Config.COMBUSTION_GENERATOR_BALANCED_MODE.get()) {
                counter = COMBUSTION_GENERATOR_PROCESS_TIME / 4;
            } else {
                counter = Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get() / 4;
            }

            int production = (int) (powerGeneration * multiplier);

            VoluminousEnergy.LOGGER.info("Setting production to: " + production);

            storage.setProduction(production);
            tile.setData("length",counter);
            tile.setChanged();
        } else {
            storage.setProduction(0);
        }
        tile.setData("counter", counter);
    }

}
