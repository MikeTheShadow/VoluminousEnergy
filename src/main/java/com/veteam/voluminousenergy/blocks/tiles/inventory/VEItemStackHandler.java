package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VEItemStackHandler extends ItemStackHandler {

    private final VETileEntity tileEntity;

    public VETileEntity getTileEntity() {
        return tileEntity;
    }
    private final int upgradeSlotLocation;
    private AbstractItemStackValidator validator;

    public VEItemStackHandler(VETileEntity tileEntity, int slots) {
        super(slots);
        this.tileEntity = tileEntity;
        upgradeSlotLocation = -1;
    }

    public VEItemStackHandler(VETileEntity tileEntity, int slots, int upgradeSlotLocation) {
        super(slots);
        this.tileEntity = tileEntity;
        this.upgradeSlotLocation = upgradeSlotLocation;
    }

    public void setValidator(AbstractItemStackValidator validator) {
        this.validator = validator;
    }

    @Override
    protected void onContentsChanged(int slot) {
        tileEntity.setChanged();
        if (tileEntity.getRecipeProcessor() instanceof BasicProcessor processor)
            processor.markRecipeDirty();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (validator != null)
            return validator.allowItemInsertion(slot, stack, false, tileEntity);
        if (slot == upgradeSlotLocation)
            return TagUtil.isTaggedMachineUpgradeItem(stack);
        VESlotManager manager = tileEntity.getSlotManagers().get(slot);

        List<VERecipe> recipes = new ArrayList<>();
        if(tileEntity.getRecipeProcessor() instanceof BasicProcessor processor)
            recipes = processor.getPotentialRecipes();

        if (manager.getSlotType() == SlotType.INPUT || manager.getSlotType() == SlotType.OUTPUT) {
            for (VERecipe recipe : recipes) {
                if (recipe.getParser().canInsertItem(slot, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (validator != null && !validator.allowItemExtraction(slot, amount, simulate, tileEntity))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
