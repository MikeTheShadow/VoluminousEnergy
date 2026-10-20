package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class BucketInputOutputUtil {

    public static void processBucketInput(ItemStack oxidizerInput, ItemStack oxidizerOutput, VERelationalTank oxidizerTank, List<Fluid> rawFluidInputList, int tankCapacity, VEItemStackHandler inventory) {
        // Input fluid into the oxidizer tank
        if (oxidizerInput.copy() != ItemStack.EMPTY && oxidizerOutput.copy() == ItemStack.EMPTY) {
            if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).content;
                if (rawFluidInputList.contains(fluid) && (
                        oxidizerTank.getTank().isEmpty()
                                || oxidizerTank.getTank().getFluid().is(fluid)
                                && oxidizerTank.getTank().getFluidAmount() + 1000 <= tankCapacity)) {
                    oxidizerTank.getTank().fill(new FluidStack(fluid, 1000));
                    inventory.take(0, 1);
                    inventory.add(1, new ItemStack(Items.BUCKET, 1));
                }
            }
        }
    }

}
