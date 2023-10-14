package com.veteam.voluminousenergy.util.items;

import com.veteam.voluminousenergy.util.cache.Cache;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.RegistryFluidIngredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CombustionFuelRecipeCache implements Cache {

    private static final List<CombustionRecipePair> ingredients = new ArrayList<>();
    private static HashMap<Fluid, Integer> map = null;

    public static void addPair(CombustionRecipePair pair) {
        if (ingredients.stream()
                .map(CombustionRecipePair::ingredient)
                .anyMatch(ingredient -> ingredient.tag().equals(pair.ingredient.tag()))) return;
        ingredients.add(pair);
    }

    public static int getVolumetricEnergyFromFluid(Fluid fluid) {
        if (map == null) {
            map = new HashMap<>();
            for (CombustionRecipePair recipePair : ingredients) {
                for (FluidStack stack : recipePair.ingredient.getIngredient().getFluids()) {
                    map.put(stack.getFluid(), recipePair.volumetricEnergy);
                }
            }
        }
        return map.getOrDefault(fluid, 0);
    }

    @Override
    public void invalidate() {
        map = null;
        ingredients.clear();
    }

    public record CombustionRecipePair(RegistryFluidIngredient ingredient, int volumetricEnergy) {

    }
}
