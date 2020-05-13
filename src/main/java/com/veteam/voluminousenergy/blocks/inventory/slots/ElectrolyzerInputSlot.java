package com.veteam.voluminousenergy.blocks.inventory.slots;

import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class ElectrolyzerInputSlot extends VEInsertSlot {
    public World world;
    public ElectrolyzerInputSlot(IItemHandler itemHandler, int index, int xPos, int yPos, World world){
        super(itemHandler,index,xPos,yPos);
        this.world = world;
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        ElectrolyzerRecipe recipe = world.getRecipeManager().getRecipe(ElectrolyzerRecipe.recipeType, new Inventory(stack), world).orElse(null);
        return checkRecipe(recipe,stack);
    }
}
