package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.BlastFurnaceContainer;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlastFurnaceTile extends VEMultiBlockTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager heatTankItemTopManager = new VESlotManager(0, Direction.UP, false, "slot.voluminousenergy.input_slot", SlotType.INPUT, "heat_top_manager");
    public VESlotManager heatTankItemBottomManager = new VESlotManager(1, Direction.DOWN, false, "slot.voluminousenergy.output_slot", SlotType.OUTPUT, "heat_bottom_manager");
    public VESlotManager firstInputSlotManager = new VESlotManager(2, Direction.EAST, false, "slot.voluminousenergy.input_slot", SlotType.INPUT, "first_input_manager");
    public VESlotManager secondInputSlotManager = new VESlotManager(3, Direction.WEST, false, "slot.voluminousenergy.input_slot", SlotType.INPUT, "second_input_manager");
    public VESlotManager outputSlotManager = new VESlotManager(4, Direction.NORTH, false, "slot.voluminousenergy.output_slot", SlotType.OUTPUT, "output_manager");

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(heatTankItemTopManager);
            add(heatTankItemBottomManager);
            add(firstInputSlotManager);
            add(secondInputSlotManager);
            add(outputSlotManager);
        }
    };

    RelationalTank heatTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "heatTank:heat_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(heatTank);
            heatTank.setAllowAny(true);
        }
    };

    private byte tick = 19;

    public ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return this.slotManagers;
    }

    public BlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.BLAST_FURNACE_TILE.get(), pos, state);
    }

    private IndustrialBlastingRecipe recipe;
    private Item lastFirstItem;
    private Item lastSecondItem;

    @Override
    public void tick() {
        updateClients();
        tick++;
        if (tick == 20) {
            tick = 0;
            validity = isMultiBlockValid(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK.get());
        }
        if (!(validity)) {
            return;
        }

        // Main idea: Heat --> Needs to be "high enough" to work, 2 Item Inputs, 1 item output.
        ItemStack heatTankItemInputTop = inventory.getStackInSlot(0).copy();
        ItemStack heatTankItemInputBottom = inventory.getStackInSlot(1).copy();
        ItemStack firstItemInput = inventory.getStackInSlot(2).copy();
        ItemStack secondItemInput = inventory.getStackInSlot(3).copy();
        ItemStack itemOutput = inventory.getStackInSlot(4).copy();

        heatTank.setIOItemstack(heatTankItemInputTop.copy(), heatTankItemInputBottom.copy());

        if (inputFluid(heatTank, 0, 1)) return;
        if (this.outputFluid(heatTank, 0, 1)) return;

        // Main Processing occurs here:
        if (heatTank != null || !heatTank.getTank().isEmpty()) {

            // Recipe check
            if (!firstItemInput.is(lastFirstItem) || !secondItemInput.is(lastSecondItem)) {
                recipe = (IndustrialBlastingRecipe) RecipeCache.getRecipeFromCache(level, IndustrialBlastingRecipe.class, firstItemInput.copy(), secondItemInput.copy());
                lastFirstItem = firstItemInput.getItem();
                lastSecondItem = secondItemInput.getItem();
                counter = 0;
            }

            if (recipe != null) {
                // Tank fluid amount check + capacity and recipe checks
                if (itemOutput.getCount() < recipe.getResult().getMaxStackSize()
                    && heatTank.getTank().getFluidAmount() >= Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()
                    && heatTank.getTank().getFluid().getRawFluid().getFluidType().getTemperature() >= recipe.getMinimumHeat()
                    && (itemOutput.isEmpty() || recipe.getResult().getItem().equals(itemOutput.getItem()))
                    && recipe.getFirstInputAsList().contains(firstItemInput.getItem())
                    && firstItemInput.getCount() >= recipe.getIngredientCount()
                    && recipe.ingredientListIncludingSeconds.get().contains(secondItemInput.getItem())
                    && secondItemInput.getCount() >= recipe.getSecondInputAmount()
                ) {
                    // Check for power
                    if (canConsumeEnergy()) {
                        if (counter == 1) {

                            // Drain Input
                            heatTank.getTank().drain(Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get(), IFluidHandler.FluidAction.EXECUTE);

                            inventory.extractItem(2, recipe.getIngredientCount(), false);
                            inventory.extractItem(3, recipe.getSecondInputAmount(), false);

                            // Place the new output stack on top of the old one
                            if (itemOutput.getItem() != recipe.getResult().getItem()) {
                                if (itemOutput.getItem() == Items.AIR) { // To prevent the slot from being jammed by air
                                    itemOutput.setCount(1);
                                }
                            }
                            inventory.insertItem(4, recipe.getResult().copy(), false); // CRASH the game if this is not empty!

                            counter--;
                            consumeEnergy();
                            this.setChanged();
                        } else if (counter > 0) {
                            counter--;
                            consumeEnergy();
                            if(++sound_tick == 19) {
                                sound_tick = 0;
                                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                                    level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                                }
                            }
                        } else {
                            counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                            length = counter;
                        }
                    } else { // Energy Check
                        decrementSuperCounterOnNoPower();
                    }
                } else { // Set counter to zero
                    counter = 0;
                }
            }
        }

    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    return stack.getItem() instanceof BucketItem || stack.getItem() == Items.BUCKET;
                } else if (slot == 2) {
                    return RecipeUtil.isFirstIngredientForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 3) {
                    return RecipeUtil.isSecondIngredientForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 4) {
                    return RecipeUtil.isAnOutputForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 5) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new BlastFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressCounterPercent() {
        if (length != 0) {
            return (int) (100 - (((float) counter / (float) length) * 100));
        } else {
            return 0;
        }
    }

    public int ticksLeft() {
        return counter;
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return heatTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public boolean getMultiblockValidity() {
        return validity;
    }

    public RelationalTank getHeatTank() {
        return this.heatTank;
    }

    public int getTemperatureKelvin() {
        return this.heatTank.getTank().getFluid().getRawFluid().getFluidType().getTemperature();
    }

    public int getTemperatureCelsius() {
        return getTemperatureKelvin() - 273;
    }

    public int getTemperatureFahrenheit() {
        return (int) ((getTemperatureKelvin() - 273) * 1.8) + 32;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId) {
        if (slotId == heatTankItemTopManager.getSlotNum()) heatTankItemTopManager.setStatus(status);
        else if (slotId == heatTankItemBottomManager.getSlotNum()) heatTankItemBottomManager.setStatus(status);
        else if (slotId == firstInputSlotManager.getSlotNum()) firstInputSlotManager.setStatus(status);
        else if (slotId == secondInputSlotManager.getSlotNum()) secondInputSlotManager.setStatus(status);
        else if (slotId == outputSlotManager.getSlotNum()) outputSlotManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId) {
        if (slotId == heatTankItemTopManager.getSlotNum()) heatTankItemTopManager.setDirection(direction);
        else if (slotId == heatTankItemBottomManager.getSlotNum()) heatTankItemBottomManager.setDirection(direction);
        else if (slotId == firstInputSlotManager.getSlotNum()) firstInputSlotManager.setDirection(direction);
        else if (slotId == secondInputSlotManager.getSlotNum()) secondInputSlotManager.setDirection(direction);
        else if (slotId == outputSlotManager.getSlotNum()) outputSlotManager.setDirection(direction);
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.BLAST_FURNACE_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.BLAST_FURNACE_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.BLAST_FURNACE_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 5;
    }
}