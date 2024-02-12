package com.veteam.voluminousenergy.blocks.inventory.slots;

import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VEInsertSlot extends SlotItemHandler {

    public VEInsertSlot(IItemHandler itemHandler, int index, int xPos, int yPos){
        super(itemHandler, index, xPos, yPos);
    }

    public <C extends Container, T extends Recipe<C>> boolean checkRecipe(VERecipe veRecipe, ItemStack stack) {
        if (veRecipe == null) return false;
        for (ItemStack testStack : veRecipe.getIngredient(0).getItems()){
            if(stack.getItem() == testStack.getItem()){
                return true;
            }
        }
        return false;
    }
}
