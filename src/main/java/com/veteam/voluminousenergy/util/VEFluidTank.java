package com.veteam.voluminousenergy.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

/**
 * A single machine tank. Side rules live in the side views that wrap it; the machine itself changes
 * the tank with the owner operations ({@link #fill}, {@link #drain}, {@link #setFluid}). It saves in
 * the {@code Fluid} format of NeoForge's former {@code FluidTank}.
 */
public class VEFluidTank extends FluidStacksResourceHandler {

    public VEFluidTank(int capacity) {
        super(1, capacity);
    }

    /**
     * Returns a copy of the fluid in the tank.
     */
    public FluidStack getFluid() {
        return stacks.getFirst().copy();
    }

    public int getFluidAmount() {
        return stacks.getFirst().getAmount();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return stacks.getFirst().isEmpty();
    }

    public void setFluid(FluidStack stack) {
        set(0, FluidResource.of(stack), stack.getAmount());
    }

    /**
     * Adds as much of the fluid as fits.
     *
     * @return the amount added
     */
    public int fill(FluidStack stack) {
        int filled = Math.min(stack.getAmount(), roomFor(stack));
        if (filled <= 0) {
            return 0;
        }
        setFluid(stack.copyWithAmount(getFluidAmount() + filled));
        return filled;
    }

    /**
     * Returns how much of the fluid would be added by {@link #fill}.
     */
    public int fillableAmount(FluidStack stack) {
        return Math.min(stack.getAmount(), roomFor(stack));
    }

    /**
     * Removes up to {@code amount} of the fluid in the tank.
     *
     * @return the fluid removed
     */
    public FluidStack drain(int amount) {
        FluidStack current = stacks.getFirst();
        int drained = Math.min(amount, current.getAmount());
        if (drained <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack removed = current.copyWithAmount(drained);
        setFluid(current.copyWithAmount(current.getAmount() - drained));
        return removed;
    }

    private int roomFor(FluidStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        FluidStack current = stacks.getFirst();
        if (!current.isEmpty() && !FluidStack.isSameFluidSameComponents(current, stack)) {
            return 0;
        }
        return capacity - current.getAmount();
    }

    @Override
    public void serialize(ValueOutput output) {
        if (!isEmpty()) {
            output.store("Fluid", FluidStack.CODEC, stacks.getFirst());
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        stacks.set(0, input.read("Fluid", FluidStack.CODEC).orElse(FluidStack.EMPTY));
    }
}
