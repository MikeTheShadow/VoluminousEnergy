package com.veteam.voluminousenergy.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

public class VEToolFluidHandler {

    // Fluid handler for the CombustionMultitool
    public static IFluidHandler createToolFluidHandler(ItemStack itemStack, int tankCapacity){
        CompoundTag initCompound = itemStack.getOrCreateTag().getCompound("tank");

        if (initCompound.isEmpty()){
            FluidTank newTank = new FluidTank(tankCapacity);
            CompoundTag compoundTag = new CompoundTag();
            newTank.writeToNBT(compoundTag);

            itemStack.getOrCreateTag().put("tank", compoundTag);
        }

        return VEToolFluidHandler.createFluidHandler(itemStack, tankCapacity);
    }


    public static IFluidHandler createFluidHandler(ItemStack itemStack, final int TANK_CAPACITY) { // Adapted from VEFluidTileEntity

        return new IFluidHandler() {
            public FluidTank getTank(){
                CompoundTag compoundTag = itemStack.getOrCreateTag().getCompound("tank");
                return new FluidTank(TANK_CAPACITY).readFromNBT(compoundTag);
            }

            public void writeChanges(FluidTank modifiedTank){
                CompoundTag newTankData = new CompoundTag();
                modifiedTank.writeToNBT(newTankData);
                itemStack.getOrCreateTag().put("tank", newTankData);
            }

            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return getTank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return true; // Should check recipe, but no access to level
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                FluidTank tempTank = getTank();
                if(tempTank.isEmpty() || resource.isFluidEqual(tempTank.getFluid())) {

                    int delta = tempTank.fill(resource, action);
                    writeChanges(tempTank);
                    return delta;
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                FluidTank tempTank = getTank();
                if(resource.isFluidEqual(tempTank.getFluid())) {
                    FluidStack drainStack = tempTank.drain(resource,action);

                    writeChanges(tempTank);
                    return drainStack;
                }

                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if(getTank().getFluidAmount() > 0) {
                    FluidTank tempTank = getTank();
                    FluidStack stack = tempTank.drain(maxDrain, action);

                    writeChanges(tempTank);
                    return stack;
                }
                return FluidStack.EMPTY;
            }
        };
    }
}
