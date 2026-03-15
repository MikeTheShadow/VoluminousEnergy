package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class FurnaceInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack stack,boolean simulate, VETileEntity tile) {
        if(tile.getEnergy() != null && tile.getEnergy().getUpgradeSlotId() == slot) return TagUtil.isTaggedMachineUpgradeItem(stack);
        if(slot != 0) return true;
        Level level = tile.getLevel();
        var furnaceRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,
                new SimpleContainer(stack.copy()), level).orElse(null);
        if(furnaceRecipeNew != null) return true;
        var blastingRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING,
                new SimpleContainer(stack.copy()), level).orElse(null);
        return blastingRecipeNew != null;
    }

    @Override
    public boolean allowItemExtraction(int slot, int amount, boolean simulate, VETileEntity tile) {
        return true;
    }
}
