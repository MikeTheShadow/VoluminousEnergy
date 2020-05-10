package com.veteam.voluminousenergy.blocks.inventory.slots.CrusherSlots;

import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class CrusherInputSlot extends VEInsertSlot {
    public World world;
    public CrusherInputSlot(IItemHandler itemHandler, int index, int xPos, int yPos, World world){
        super(itemHandler,index,xPos,yPos);
        this.world = world;
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(stack), world).orElse(null);
        return checkRecipe(recipe,stack);
    }
}
