package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class OxidizerProperties {
    private ItemStack bucketItem;
    private int processTime;

    public OxidizerProperties(ItemStack bucketStack, int pTime){
        this.bucketItem = bucketStack;
        this.processTime = pTime;
    }

    public FluidStack getOxidizerFluidStack(){
        Fluid fluid = ((BucketItem) this.bucketItem.copy().getItem()).getFluid();
        return new FluidStack(fluid,1000);
    }

    public ItemStack getBucketItem(){
        return this.bucketItem;
    }

    public int getProcessTime(){
        return this.processTime;
    }
}
