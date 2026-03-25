package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
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
        List<VESlotManager> managers = tileEntity.getSlotManagers();

        if (tileEntity.getRecipeProcessor() instanceof BasicProcessor processor)
            processor.markRecipeDirty();

        if (slot >= managers.size()) return;
        SlotType slotType = tileEntity.getSlotManagers().get(slot).getSlotType();
        if (slotType.isFluidBucketIORelated()) {
            tileEntity.markFluidInputDirty();
        }
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

        if (manager.getSlotType() == SlotType.FLUID_INPUT && stack.getItem() instanceof BucketItem bucketItem) {
            if (bucketItem.content == Fluids.EMPTY)
                return true;
            VERelationalTank tank = tileEntity.getRelationalTanks().get(manager.getTankId());
            if (tank.getTankType() == TankType.OUTPUT) {
                return bucketItem.content.isSame(Fluids.EMPTY);
            }
            for (VERecipe recipe : recipes) {
                if (recipe.getFluidIngredient(tank.getRecipePos()).test(new FluidStack(bucketItem.content, 1))) {
                    return true;
                }
            }
        } else if (manager.getSlotType() == SlotType.INPUT || manager.getSlotType() == SlotType.OUTPUT) {
            for (VERecipe recipe : recipes) {
                if (recipe.getParser().canInsertItem(slot, stack)) {
                    return true;
                }
            }
        } else
            return manager.getSlotType() == SlotType.FLUID_OUTPUT;
        return false;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (validator != null && !validator.allowItemExtraction(slot, amount, simulate, tileEntity))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
