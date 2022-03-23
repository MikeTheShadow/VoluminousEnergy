package com.veteam.voluminousenergy.persistence;

import net.minecraft.world.level.material.Fluid;

public class SingleChunkFluid {

    private Fluid fluid;
    private int amount;

    public SingleChunkFluid(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
