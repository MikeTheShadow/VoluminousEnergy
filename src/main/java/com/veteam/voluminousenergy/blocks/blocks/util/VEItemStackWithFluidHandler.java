package com.veteam.voluminousenergy.blocks.blocks.util;

import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;

public class VEItemStackWithFluidHandler extends ItemStacksResourceHandler {

    private final VESlotManager[] managers;
    private final VERelationalTank[] relationalTanks;
    private final Class<?> recipeType;
    private VERecipe recipe;
    private final Level level;
    private final int upgradeSlot;

    public VEItemStackWithFluidHandler(int size, int upgradeSlot, Class<?> recipeType, List<VERelationalTank> tankList, Level level, VESlotManager... managers) {
        super(size);
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
    public boolean isValid(int slot, ItemResource resource) {

        if (slot == upgradeSlot) return TagUtil.isTaggedMachineUpgradeItem(resource.toStack());


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
