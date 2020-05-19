package com.veteam.voluminousenergy.tools.api;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Just extends {@link IInventory} and {@link IFluidHandler}, to make {@link IFluidRecipe}
 * implementation a bit cleaner.
 */
public interface IFluidInventory extends IInventory, IFluidHandler {
}