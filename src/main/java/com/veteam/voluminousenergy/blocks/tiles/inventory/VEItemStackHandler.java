package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The inventory of a machine. Pipes and players go through the transfer API, which applies the slot
 * rules in {@link #isValid}; the machine itself changes its slots with the owner operations
 * ({@link #take}, {@link #add}, {@link #setStack}), which skip those rules. It saves in the
 * {@code Items}/{@code Size} format of NeoForge's former {@code ItemStackHandler}.
 */
public class VEItemStackHandler extends ItemStacksResourceHandler {

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
    protected void onContentsChanged(int slot, ItemStack previousContents) {
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
    public boolean isValid(int slot, ItemResource resource) {
        ItemStack stack = resource.toStack();
        if (validator != null)
            return validator.allowItemInsertion(slot, stack, tileEntity);
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

    /**
     * Returns a copy of the stack in the given slot.
     */
    public ItemStack getStack(int slot) {
        return stacks.get(slot).copy();
    }

    public void setStack(int slot, ItemStack stack) {
        set(slot, ItemResource.of(stack), stack.getCount());
    }

    /**
     * Removes up to {@code amount} items from the given slot.
     *
     * @return the items removed
     */
    public ItemStack take(int slot, int amount) {
        ItemStack current = stacks.get(slot);
        int taken = Math.min(amount, current.getCount());
        if (taken <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = current.copyWithCount(taken);
        setStack(slot, current.copyWithCount(current.getCount() - taken));
        return removed;
    }

    /**
     * Adds as much of the stack to the given slot as fits.
     *
     * @return the part of the stack that did not fit
     */
    public ItemStack add(int slot, ItemStack stack) {
        int added = Math.min(stack.getCount(), roomFor(slot, stack));
        if (added <= 0) {
            return stack.copy();
        }
        setStack(slot, stack.copyWithCount(stacks.get(slot).getCount() + added));
        return stack.copyWithCount(stack.getCount() - added);
    }

    /**
     * Returns whether the whole stack fits in the given slot.
     */
    public boolean fits(int slot, ItemStack stack) {
        return roomFor(slot, stack) >= stack.getCount();
    }

    private int roomFor(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        ItemStack current = stacks.get(slot);
        if (!current.isEmpty() && !ItemStack.isSameItemSameComponents(current, stack)) {
            return 0;
        }
        return getCapacity(slot, ItemResource.of(stack)) - current.getCount();
    }

    @Override
    public void serialize(ValueOutput output) {
        ValueOutput.TypedOutputList<ItemStackWithSlot> items = output.list("Items", ItemStackWithSlot.CODEC);
        for (int slot = 0; slot < stacks.size(); slot++) {
            ItemStack stack = stacks.get(slot);
            if (!stack.isEmpty()) {
                items.add(new ItemStackWithSlot(slot, stack));
            }
        }
        output.putInt("Size", stacks.size());
    }

    @Override
    public void deserialize(ValueInput input) {
        stacks.replaceAll(stack -> ItemStack.EMPTY);
        input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach(saved -> {
            if (saved.isValidInContainer(stacks.size())) {
                stacks.set(saved.slot(), saved.stack());
            }
        });
    }
}
