package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.SawmillRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SawmillParser extends RecipeParser {

    private final HashMap<String, LogPlank> cache = new HashMap<>();
    private final SawmillRecipe recipe;

    public SawmillParser(VERecipe recipe) {
        super(recipe);
        this.recipe = (SawmillRecipe) recipe;
    }

    @Override
    public boolean isPartialRecipe(VETileEntity tile) {
        if (recipe.isLogRecipe()) {
            ItemStack stack = tile.getStackInSlot(0);
            if (stack.isEmpty()) return true;
            return getPlankFromLog(tile) != null;
        }
        return super.isPartialRecipe(tile);
    }

    @Override
    public boolean isCompleteRecipe(VETileEntity tile) {
        if (recipe.isLogRecipe()) {
            return getPlankFromLog(tile) != null;
        }
        return super.isCompleteRecipe(tile);
    }

    @Nullable
    ItemStack getPlankFromLog(VETileEntity tile) {
        ItemStack stack = tile.getStackInSlot(0);
        String path = RegistryLookups.lookupItem(stack).getPath();
        if (path.endsWith("_log")) {
            String id = path.replace("_log", "");
            if (getCache().containsKey(id)) {
                LogPlank plank = getCache().get(id);
                return plank.log.is(stack.getItem()) ? plank.plank : null;
            }
        }
        return null;
    }

    @Override
    public boolean canCompleteRecipe(VETileEntity tile) {
        if (recipe.isLogRecipe()) {
            ItemStack plank = getPlankFromLog(tile);
            ItemStack plankOutput = tile.getStackInSlot(1);
            ItemStack dustOutput = tile.getStackInSlot(2);
            boolean plankValid = plankOutput.isEmpty() ||
                    (plank.is(plankOutput.getItem()) && plankOutput.getCount() + plank.getCount() <= plankOutput.getMaxStackSize());
            boolean dustValid = dustOutput.isEmpty() ||
                    (dustOutput.is(recipe.getResult(0).getItem()) && dustOutput.getCount() + recipe.getResultCount(0) <= dustOutput.getMaxStackSize());
            return plankValid && dustValid && tile.getRelationalTank(0).canInsertOutputFluid(recipe,0);
        }
        return super.canCompleteRecipe(tile);
    }

    @Override
    public void completeRecipe(VETileEntity tile) {
        if (recipe.isLogRecipe()) {
            ItemStack plank = getPlankFromLog(tile);
            ItemStack dust = recipe.getResult(0);
            FluidStack stack = recipe.getOutputFluid(0);
            tile.getInventory().extractItem(0,1,false);
            tile.getInventory().insertItem(1,plank.copy(), false);
            tile.getInventory().insertItem(2,dust.copy(), false);
            tile.getRelationalTank(0).fillTank(stack.copy());
            return;
        }
        super.completeRecipe(tile);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (recipe.isLogRecipe() && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) {
            if (slot == 0) {
                String path = RegistryLookups.lookupItem(stack.getItem()).getPath();
                return path.endsWith("_log");
            }
            return true;
        }
        return super.canInsertItem(slot, stack);
    }

    HashMap<String, LogPlank> getCache() {
        if (cache.isEmpty()) buildCache();
        return cache;
    }

    void buildCache() {
        ForgeRegistries.ITEMS.getValues().forEach(registeredItem -> {
            String path = RegistryLookups.lookupItem(registeredItem).getPath();
            if (path.startsWith("stripped_")) return;
            if (path.endsWith("_log")) {
                String logName = path.replace("_log", "");
                if (cache.containsKey(logName)) {
                    cache.get(logName).log = new ItemStack(registeredItem, Config.SAWMILL_LOG_CONSUMPTION_RATE.get());
                } else {
                    LogPlank logPlank = new LogPlank();
                    logPlank.log = new ItemStack(registeredItem, Config.SAWMILL_LOG_CONSUMPTION_RATE.get());
                    cache.put(logName, logPlank);
                }
            } else if (path.endsWith("_planks")) {
                String logName = path.replace("_planks", "");
                if (cache.containsKey(logName)) {
                    cache.get(logName).plank = new ItemStack(registeredItem, Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get());
                } else {
                    LogPlank logPlank = new LogPlank();
                    logPlank.plank = new ItemStack(registeredItem, Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get());
                    cache.put(logName, logPlank);
                }
            }
        });
    }

    private static class LogPlank {
        ItemStack log;
        ItemStack plank;

        public LogPlank() {
        }

        public ItemStack getLog() {
            return log;
        }

        public void setLog(ItemStack log) {
            this.log = log;
        }

        public ItemStack getPlank() {
            return plank;
        }

        public void setPlank(ItemStack plank) {
            this.plank = plank;
        }
    }
}
