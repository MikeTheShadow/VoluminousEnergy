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
import net.minecraft.world.item.crafting.BlastingRecipe;
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

    public VESlotManager heatTankItemTopManager = new VESlotManager(0, Direction.UP, false, SlotType.FLUID_INPUT,1,0);
    public VESlotManager heatTankItemBottomManager = new VESlotManager(1, Direction.DOWN, false, SlotType.FLUID_OUTPUT);
    public VESlotManager firstInputSlotManager = new VESlotManager(2, Direction.EAST, false, SlotType.INPUT);
    public VESlotManager secondInputSlotManager = new VESlotManager(3, Direction.WEST, false, SlotType.INPUT);
    public VESlotManager outputSlotManager = new VESlotManager(4, Direction.NORTH, false, SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(heatTankItemTopManager);
            add(heatTankItemBottomManager);
            add(firstInputSlotManager);
            add(secondInputSlotManager);
            add(outputSlotManager);
        }
    };

    RelationalTank[] fluidManagers = new RelationalTank[] {
            new RelationalTank(new FluidTank(TANK_CAPACITY), 0, TankType.INPUT, "heatTank:heat_tank_gui")
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
        super(VEBlocks.BLAST_FURNACE_TILE.get(), pos, state, IndustrialBlastingRecipe.RECIPE_TYPE);
        fluidManagers[0].setAllowAny(true);
    }
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
        ItemStack firstItemInput = inventory.getStackInSlot(2).copy();
        ItemStack secondItemInput = inventory.getStackInSlot(3).copy();
        ItemStack itemOutput = inventory.getStackInSlot(4).copy();

        if(selectedRecipe == null) return;

        IndustrialBlastingRecipe recipe = (IndustrialBlastingRecipe) selectedRecipe;

        // Main Processing occurs here:
        if (!fluidManagers[0].getTank().isEmpty()) {
            if (selectedRecipe != null) {
                // Tank fluid amount check + capacity and recipe checks

                int firstIngredientCount = recipe.getIngredient(0).getItems()[0].getCount();
                int secondIngredientCount = recipe.getIngredient(1).getItems()[0].getCount();

                if (itemOutput.getCount() < recipe.getResult(0).getMaxStackSize()
                        && fluidManagers[0].getTank().getFluidAmount() >= Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()
                        && fluidManagers[0].getTank().getFluid().getRawFluid().getFluidType().getTemperature() >= recipe.getMinimumHeat()
                        && (itemOutput.isEmpty() || recipe.getResult(0).getItem().equals(itemOutput.getItem()))
                        && recipe.getIngredient(0).test(firstItemInput)
                        && firstItemInput.getCount() >= firstIngredientCount
                        && secondItemInput.getCount() >= secondIngredientCount
                ) {
                    // Check for power
                    if (canConsumeEnergy()) {
                        if (counter == 1) {

                            // Drain Input
                            fluidManagers[0].getTank().drain(Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get(), IFluidHandler.FluidAction.EXECUTE);

                            inventory.extractItem(2, firstIngredientCount, false);
                            inventory.extractItem(3, secondIngredientCount, false);

                            // Place the new output stack on top of the old one
                            if (itemOutput.getItem() != recipe.getResult(0).getItem()) {
                                if (itemOutput.getItem() == Items.AIR) { // To prevent the slot from being jammed by air
                                    itemOutput.setCount(1);
                                }
                            }
                            inventory.insertItem(4, recipe.getResult(0).copy(), false); // CRASH the game if this is not empty!

                            counter--;
                            consumeEnergy();
                            this.setChanged();
                            markFluidInputDirty();
                        } else if (counter > 0) {
                            counter--;
                            consumeEnergy();
                            if (++sound_tick == 19) {
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


    // TODO replace me
    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                markFluidInputDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                return true;
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

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return fluidManagers[num].getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public boolean getMultiblockValidity() {
        return validity;
    }

    public int getTemperatureKelvin() {
        return fluidManagers[0].getTank().getFluid().getRawFluid().getFluidType().getTemperature();
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
        return List.of(fluidManagers);
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