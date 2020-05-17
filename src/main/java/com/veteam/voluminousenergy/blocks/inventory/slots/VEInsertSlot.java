package com.veteam.voluminousenergy.blocks.inventory.slots;

import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VEInsertSlot  extends SlotItemHandler {

    public VEInsertSlot(IItemHandler itemHandler, int index, int xPos, int yPos){
        super(itemHandler, index, xPos, yPos);
    }

    public <C extends IInventory, T extends IRecipe<C>> boolean checkRecipe(VERecipe veRecipe, ItemStack stack) {
        if (veRecipe == null) return false;
        for (ItemStack testStack : veRecipe.getIngredient().getMatchingStacks()){
            if(stack.getItem() == testStack.getItem()){
                return true;
            }
        }
        return false;
    }
}
