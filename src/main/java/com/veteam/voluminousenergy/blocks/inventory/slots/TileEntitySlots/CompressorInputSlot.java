package com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots;

import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class CompressorInputSlot extends VEInsertSlot {
    public World world;
    public CompressorInputSlot(IItemHandler itemHandler, int index, int xPos, int yPos, World world) {
        super(itemHandler, index, xPos, yPos);
        this.world = world;
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        ItemStack referenceStack = stack.copy();
        referenceStack.setCount(64);
        CompressorRecipe recipe = world.getRecipeManager().getRecipe(CompressorRecipe.RECIPE_TYPE, new Inventory(referenceStack), world).orElse(null);
        return checkRecipe(recipe,referenceStack);
    }
}
