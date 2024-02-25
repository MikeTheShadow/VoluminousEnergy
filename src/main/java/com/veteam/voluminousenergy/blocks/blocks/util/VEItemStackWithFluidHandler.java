package com.veteam.voluminousenergy.blocks.blocks.util;

import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VEItemStackWithFluidHandler extends ItemStackHandler {

    private final VESlotManager[] managers;
    private final VERelationalTank[] relationalTanks;
    private final Class<?> recipeType;
    private VERecipe recipe;
    private final Level level;
    private final int upgradeSlot;

    public VEItemStackWithFluidHandler(int size, int upgradeSlot, Class<?> recipeType, List<VERelationalTank> tankList, Level level, VESlotManager... managers) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.managers = managers;
        this.recipeType = recipeType;
        this.relationalTanks = tankList.toArray(VERelationalTank[]::new);
        this.level = level;
        this.upgradeSlot = upgradeSlot;
    }

    public VESlotManager[] getManagers() {
        return this.managers;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {

        if (slot == upgradeSlot) return TagUtil.isTaggedMachineUpgradeItem(stack);



        return false;
    }

    public VERelationalTank[] getRelationalTanks() {
        return relationalTanks;
    }

    public Level getLevel() {
        return level;
    }

    public Class<?> getRecipeType() {
        return recipeType;
    }
}
