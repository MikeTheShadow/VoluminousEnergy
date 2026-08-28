package com.veteam.voluminousenergy.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public class SingleChunkFluid {

    public static final Codec<SingleChunkFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(SingleChunkFluid::getFluid),
            Codec.INT.fieldOf("amount").forGetter(SingleChunkFluid::getAmount)
    ).apply(instance, SingleChunkFluid::new));

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
