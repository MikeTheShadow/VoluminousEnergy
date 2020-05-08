package com.veteam.voluminousenergy.blocks.inventory.slots.CrusherSlots;

import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CrusherInputSlot extends SlotItemHandler {
    public World world;
    public CrusherInputSlot(IItemHandler itemHandler, int index, int xPos, int yPos, World world){
        super(itemHandler,index,xPos,yPos);
        this.world = world;
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(stack), world).orElse(null);
        for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
            try{
                if(stack.getItem() == testStack.getItem()){
                    return true;
                }
            } catch (Exception e){
                return false;
            }
        }
        return false;
    }
}
