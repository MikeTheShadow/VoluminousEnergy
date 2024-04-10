package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

public class GasFiredFurnaceValidator extends FurnaceInventoryValidator {

    @Override
    public boolean isItemValid(int slot, ItemStack stack, VETileEntity tile) {
        if(tile.getEnergy() != null && tile.getEnergy().getUpgradeSlotId() == slot) return TagUtil.isTaggedMachineUpgradeItem(stack);

        if(slot == 0) {
            if(stack.getItem() instanceof BucketItem item) {
                return item.getFluid().isSame(Fluids.EMPTY) || CombustibleFluidsData.isCombustible(item.getFluid());
            }
            return false;
        }
        if (slot == 1) return stack.getItem() instanceof BucketItem;
        if (slot != 2) return true;
        Level level = tile.getLevel();
        var furnaceRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,
                new SimpleContainer(stack.copy()), level).orElse(null);
        if(furnaceRecipeNew != null) return true;
        var blastingRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING,
                new SimpleContainer(stack.copy()), level).orElse(null);
        return blastingRecipeNew != null;
    }

}
