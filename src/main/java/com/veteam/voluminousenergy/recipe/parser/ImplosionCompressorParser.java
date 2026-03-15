package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ImplosionCompressorParser extends BasicParser {

    public ImplosionCompressorParser(VERecipe recipe) {
        super(recipe);
    }

    @Override
    public boolean isCompleteRecipe(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStackInSlot(1);
        if (stack.getItem() != Items.GUNPOWDER) return false;
        return super.isCompleteRecipe(tile);
    }

    @Override
    public void completeRecipe(VETileEntity tile) {
        super.completeRecipe(tile);
        tile.getInventory().extractItem(1, 1, false);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot == 1)
            return stack.getItem() == Items.GUNPOWDER;
        return super.canInsertItem(slot, stack);
    }
}
