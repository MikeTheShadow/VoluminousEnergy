package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class VEItemStackHandler extends ItemStackHandler {

    private final VETileEntity tileEntity;
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

        if (slot == upgradeSlotLocation) tileEntity.markRecipeDirty();
        else if (slot < managers.size()) {
            SlotType slotType = tileEntity.getSlotManagers().get(slot).getSlotType();
            if (slotType == SlotType.INPUT) {
                tileEntity.markRecipeDirty();
            } else if (slotType.isFluidBucketIORelated()) {
                tileEntity.markFluidInputDirty();
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        // For simple custom validation.
        if (validator != null) {
            return validator.isItemValid(slot, stack,tileEntity);
        }
        if (slot == upgradeSlotLocation) return TagUtil.isTaggedMachineUpgradeItem(stack);
        VESlotManager manager = tileEntity.getSlotManagers().get(slot);
        if (manager.getSlotType() == SlotType.FLUID_INPUT && stack.getItem() instanceof BucketItem bucketItem) {
            if (bucketItem.getFluid() == Fluids.EMPTY) return true;
            VERelationalTank tank = tileEntity.getRelationalTanks().get(manager.getTankId());
            if (tank.getTankType() == TankType.OUTPUT) {
                return bucketItem.getFluid().isSame(Fluids.EMPTY);
            }
            for (VERecipe recipe : tileEntity.getPotentialRecipes()) {
                if (recipe.getFluidIngredient(tank.getRecipePos()).test(new FluidStack(bucketItem.getFluid(), 1))) {
                    return true;
                }
            }
        } else if (manager.getSlotType() == SlotType.INPUT || manager.getSlotType() == SlotType.OUTPUT) {
            for (VERecipe recipe : tileEntity.getPotentialRecipes()) {
                if (recipe.getParser().canInsertItem(slot, stack)) {
                    return true;
                }
            }
        } else return manager.getSlotType() == SlotType.FLUID_OUTPUT;
        return false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isItemValid(slot, stack)) return stack;
        return super.insertItem(slot, stack, simulate);
    }
}
