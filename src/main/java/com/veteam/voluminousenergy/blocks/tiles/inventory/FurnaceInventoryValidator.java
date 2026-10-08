package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;

public class FurnaceInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack stack,boolean simulate, VETileEntity tile) {
        if(tile.getEnergy() != null && tile.getEnergy().getUpgradeSlotId() == slot) return TagUtil.isTaggedMachineUpgradeItem(stack);
        if(slot != 0) return true;
        return isSmeltable(tile.getLevel(), stack);
    }

    protected static boolean isSmeltable(Level level, ItemStack stack) {
        RecipeAccess recipeAccess = level.recipeAccess();
        return recipeAccess.propertySet(RecipePropertySet.FURNACE_INPUT).test(stack)
                || recipeAccess.propertySet(RecipePropertySet.BLAST_FURNACE_INPUT).test(stack);
    }

    @Override
    public boolean allowItemExtraction(int slot, int amount, boolean simulate, VETileEntity tile) {
        return true;
    }
}
