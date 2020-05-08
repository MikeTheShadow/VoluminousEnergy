package com.veteam.voluminousenergy.blocks.inventory.slots.PrimitiveBlastFurnaceSlots;

import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PrimitiveBlastFurnaceInsertSlot extends SlotItemHandler {
    World world;
    public PrimitiveBlastFurnaceInsertSlot(IItemHandler itemHandler, int index, int xPos, int yPos, World world){
        super(itemHandler,index,xPos,yPos);
        this.world = world;
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.recipeType, new Inventory(stack), world).orElse(null);
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
