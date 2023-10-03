package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Due to potential modification of already-existing objects
 * this class exists to provide an extracted check for
 * item size and fluid type for recipe checks in tile entities
 */
public class RecipeFluid {

    Fluid fluid = null;
    int amount = -1;

    // Create an empty instance of the class
    public RecipeFluid() {

    }

    public RecipeFluid(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    /**
     * This method provides the benefit of updating the object as well to provide cleaner
     * frontend code
     *
     * @param fluidStack The new fluidstack
     * @return returns a value based on if the item size or material has changed (true) or
     * has remained unchanged (false)
     */
    public boolean isDifferent(FluidStack fluidStack) {

        if(this.fluid == null) {
            if(fluidStack == null) return false;
            this.fluid = fluidStack.getFluid();
            this.amount = fluidStack.getAmount();
            return true;
        }

        if (amount != fluidStack.getAmount() || !fluidStack.getFluid().isSame(fluid)) {
            amount = fluidStack.getAmount();
            fluid = fluidStack.getFluid();
            return true;
        } else {
            return false;
        }
    }

}
