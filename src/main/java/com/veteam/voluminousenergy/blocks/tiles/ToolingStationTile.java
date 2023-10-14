package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ToolingStationContainer;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.ToolingRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.stringtemplate.v4.ST;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.veteam.voluminousenergy.items.tools.multitool.CombustionMultitool.isCombustibleFuel;

public class ToolingStationTile extends VEFluidTileEntity implements IVEPoweredTileEntity {

    // Slot Managers
    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0));
        add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.NORTH, true, SlotType.OUTPUT));
        add(new VESlotManager(3, Direction.SOUTH, true, SlotType.INPUT));
        add(new VESlotManager(4, Direction.EAST, true, SlotType.INPUT));
    }};

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "fuel_tank:fuel_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(fuelTank);
    }};

    private final ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public ToolingStationTile(BlockPos pos, BlockState state) {
        super(VEBlocks.TOOLING_STATION_TILE.get(), pos, state, null);
    }

    VEFluidRecipe fuelRecipe;

    @Override
    public void tick() {
        updateClients();
        processFluidIO();
        validateRecipe();
        ItemStack mainTool = inventory.getStackInSlot(2); // This will act like a POINTER, not a clone
        ItemStack toolBit = inventory.getStackInSlot(3).copy(); // this is where the bit would be put into
        ItemStack toolBase = inventory.getStackInSlot(4).copy(); // this is where the base of the tool would be put into

        if (fuelRecipe != null) {
            // Logic for refueling the base
            if (!mainTool.isEmpty()) {
                mainTool.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
                    FluidStack itemFluid = fluid.getFluidInTank(0);
                    FluidStack toolingStationFluid = this.fuelTank.getTank().getFluid().copy();
                    int tankCapacity = fluid.getTankCapacity(0);

                    if (itemFluid.getAmount() < tankCapacity && (itemFluid.isFluidEqual(toolingStationFluid) || itemFluid.isEmpty())) {
                        int toTransfer;

                        if (!itemFluid.isEmpty()) {
                            toTransfer = Math.min(toolingStationFluid.getAmount(), itemFluid.getAmount()); // Which amount is smaller
                            toTransfer = Math.min(toTransfer, (tankCapacity - itemFluid.getAmount())); // Previous value versus the delta between the tankCapacity in the item and the current fluid amount
                        } else { // Clean slate, check only against the tank capacity
                            toTransfer = Math.min(toolingStationFluid.getAmount(), tankCapacity);
                        }

                        if (toTransfer > 0) {
                            // Drain the fluid from the Tooling Station
                            this.fuelTank.getTank().drain(toTransfer, IFluidHandler.FluidAction.EXECUTE);
                            toolingStationFluid.setAmount(toTransfer); // Set the fluid that is going to go into the item
                            // Fill the item
                            fluid.fill(toolingStationFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                            // Fill the fluid in the base as well
                            inventory.getStackInSlot(4).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                                    .ifPresent(baseFluid -> baseFluid.fill(toolingStationFluid, IFluidHandler.FluidAction.EXECUTE));
                        }
                    }
                });
            }
        }

        if (mainTool.isEmpty() && inventory.getStackInSlot(2).isEmpty()) {
            if (!toolBit.isEmpty() && !toolBase.isEmpty()) {
                ToolingRecipe toolingRecipe = RecipeUtil.getToolingRecipeFromBitAndBase(level, toolBit.copy(), toolBase.copy());
                if (toolingRecipe != null) {
                    ItemStack craftedTool = new ItemStack(toolingRecipe.getResult(0).getItem(), 1);

                    // Fill the crafted Multitool with fluid from the emptyMultitool
                    craftedTool.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(
                            fluidTool -> toolBase.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidBase -> {
                                FluidStack baseFluid = fluidBase.getFluidInTank(0).copy();
                                fluidTool.fill(baseFluid, IFluidHandler.FluidAction.EXECUTE);
                            }));

                    inventory.setStackInSlot(2, craftedTool);
                }
            }
        } else if (!mainTool.isEmpty() && toolBase.isEmpty() && toolBit.isEmpty()) {
            ToolingRecipe toolingRecipe = RecipeUtil.getToolingRecipeFromResult(level, mainTool.copy());
            if (toolingRecipe != null) {
                inventory.setStackInSlot(3, new ItemStack(toolingRecipe.getBits().get(0)));
                ItemStack baseStack = new ItemStack(toolingRecipe.getBases().get(0));

                // Fill the base with the same fluid as the mainTool
                baseStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(baseFluid ->
                        mainTool.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(toolFluid -> {
                            FluidStack fluidTool = toolFluid.getFluidInTank(0).copy();
                            baseFluid.fill(fluidTool, IFluidHandler.FluidAction.EXECUTE);
                        }));

                inventory.setStackInSlot(4, baseStack);
                inventory.setStackInSlot(2, mainTool.copy());
            }
        }
    }

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;
        fuelRecipe =
                RecipeCache.getFluidRecipeFromCache(level, CombustionGeneratorFuelRecipe.RECIPE_TYPE,
                        Collections.singletonList(this.fuelTank.getTank().getFluid()), new ArrayList<>());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot == 2 && this.getStackInSlot(2).isEmpty()) { // If the crafted multitool is removed, delete the components
                    if (this.getStackInSlot(3).isEmpty() || this.getStackInSlot(4).isEmpty()) {
                    } else {
                        this.setStackInSlot(3, ItemStack.EMPTY);
                        this.setStackInSlot(4, ItemStack.EMPTY);
                    }
                } else if ((slot == 3 || slot == 4) && (!this.getStackInSlot(2).isEmpty())) {
                    if (this.getStackInSlot(3).isEmpty() || this.getStackInSlot(4).isEmpty()) { // If one of the components of the multitool is removed, delete the multitool
                        this.setStackInSlot(2, ItemStack.EMPTY);
                    }
                }
                markFluidInputDirty();
                markRecipeDirty();
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0) {
                    if (stack.getItem() instanceof BucketItem bucketItem && !bucketItem.getFluid().isSame(Fluids.EMPTY)) {
                        return (isCombustibleFuel(bucketItem.getFluid()));
                    }
                    return stack.getItem() instanceof BucketItem;
                } else if (slot == 1) {
                    return stack.getItem() instanceof BucketItem;
                }
                if (slot == 2)
                    return stack.getItem() instanceof Multitool && stack.getItem() != VEMultitools.EMPTY_MULTITOOL.get(); // TODO: Remove Multitool base?
                if (slot == 3) return stack.getItem() instanceof BitItem;
                if (slot == 4)
                    return (stack.getItem() == VEMultitools.EMPTY_MULTITOOL.get()); // TODO: Remove Multitool base?
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new ToolingStationContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public boolean hasValidRecipe() {
        return (inventory.getStackInSlot(2) != ItemStack.EMPTY)
                && (inventory.getStackInSlot(3) != ItemStack.EMPTY)
                && (inventory.getStackInSlot(4) != ItemStack.EMPTY);
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank() {
        return this.fuelTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.TOOLING_STATION_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() { // Tooling Station atm doesn't use power. Transfer to recharge electric tools (if support added) should be capped by this#getTransferRate();
        return 0;
    }

    @Override
    public int getTransferRate() {
        return Config.TOOLING_STATION_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}