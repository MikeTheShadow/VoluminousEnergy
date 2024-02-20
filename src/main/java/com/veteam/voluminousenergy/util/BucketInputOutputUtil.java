package com.veteam.voluminousenergy.util;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class BucketInputOutputUtil {

    public static void processBucketInput(ItemStack oxidizerInput, ItemStack oxidizerOutput, VERelationalTank oxidizerTank, List<Fluid> rawFluidInputList, int tankCapacity, IItemHandler inventory) {
        // Input fluid into the oxidizer tank
        if (oxidizerInput.copy() != ItemStack.EMPTY && oxidizerOutput.copy() == ItemStack.EMPTY) {
            if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).getFluid();
                if (rawFluidInputList.contains(fluid) && (
                        oxidizerTank.getTank().isEmpty()
                                || oxidizerTank.getTank().getFluid().isFluidEqual(new FluidStack(fluid, 1000))
                                && oxidizerTank.getTank().getFluidAmount() + 1000 <= tankCapacity)) {
                    oxidizerTank.getTank().fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    inventory.extractItem(0, 1, false);
                    inventory.insertItem(1, new ItemStack(Items.BUCKET, 1), false);
                }
            }
        }
    }

}
