package com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots;

import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class CrusherInputSlot extends VEInsertSlot {
    public Level world;
    public CrusherInputSlot(IItemHandler itemHandler, int index, int xPos, int yPos, Level world){
        super(itemHandler,index,xPos,yPos);
        this.world = world;
    }

    @Override
    public boolean mayPlace(ItemStack stack){
        ItemStack referenceStack = stack.copy();
        referenceStack.setCount(64);
        CrusherRecipe recipe = world.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), world).orElse(null);
        return checkRecipe(recipe,referenceStack);
    }
}
