package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CombustionGeneratorTile extends VEFluidTileEntity implements IVEPoweredTileEntity,IVECountable {
    // Handlers

    // Slot Managers
    public VESlotManager oxiInSm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"oxi_in_sm");
    public VESlotManager oxiOutSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT,"oxi_out_sm");
    public VESlotManager fuelInSm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"fuel_in_sm");
    public VESlotManager fuelOutSm = new VESlotManager(3, Direction.SOUTH, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT,"fuel_out_sm");

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(oxiInSm);
            add(oxiOutSm);
            add(fuelInSm);
            add(fuelOutSm);
        }
    };

    private final int tankCapacity = 4000;

    private final RelationalTank oxidizerTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.INPUT,"oxidizerTank:oxidizer_tank_gui");
    private final RelationalTank fuelTank = new RelationalTank(new FluidTank(tankCapacity), 1, null, null, TankType.INPUT,"fuelTank:fuel_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(oxidizerTank);
            add(fuelTank);
        }
    };
    private int energyRate;

    private final ItemStackHandler inventory = createHandler();

    private static final Logger LOGGER = LogManager.getLogger();

    public CombustionGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!oxidizerTank.isValidFluidsSet()) oxidizerTank.setValidFluids(RecipeUtil.getOxidizerFluids(level));
        if (!fuelTank.isValidFluidsSet()) fuelTank.setValidFluids(RecipeUtil.getFuelCombustionInputFluidsParallel(level));

        updateClients();

        ItemStack oxidizerInput = inventory.getStackInSlot(0).copy();
        ItemStack oxidizerOutput = inventory.getStackInSlot(1).copy();
        ItemStack fuelInput = inventory.getStackInSlot(2).copy();
        ItemStack fuelOutput = inventory.getStackInSlot(3).copy();

        /*
         *  Manipulate tanks based on input from buckets in slots
         */

        // Input fluid into the oxidizer tank
        if (oxidizerInput.copy() != ItemStack.EMPTY
                && (oxidizerOutput.copy() == ItemStack.EMPTY
                    || oxidizerOutput.copy().getItem() == Items.BUCKET)) {
            if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).getFluid();
                if (oxidizerTank.isFluidValid(fluid) && (
                        oxidizerTank.getTank().isEmpty()
                                || oxidizerTank.getTank().getFluid().isFluidEqual(new FluidStack(fluid, 1000))
                                && oxidizerTank.getTank().getFluidAmount() + 1000 <= tankCapacity)) {
                    oxidizerTank.getTank().fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    inventory.extractItem(0, 1, false);

                    inventory.insertItem(1, new ItemStack(Items.BUCKET, 1), false);
                }
            }
        }
        //BucketInputOutputUtil.processBucketInput(oxidizerInput,oxidizerOutput,oxidizerTank,CombustionGeneratorOxidizerRecipe.rawFluidInputList,tankCapacity,inventory);


        // Extract fluid from the oxidizer tank
        if (oxidizerInput.copy().getItem() == Items.BUCKET
                && (oxidizerOutput.copy() == ItemStack.EMPTY
                    || oxidizerOutput.copy().getItem() == Items.BUCKET)) {
            if (oxidizerTank.getTank().getFluidAmount() >= 1000) {
                ItemStack bucketStack = new ItemStack(oxidizerTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                oxidizerTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(0, 1, false);
                inventory.insertItem(1, bucketStack, false);
            }
        }

        // Input fluid to the fuel tank
        if (fuelInput.copy() != ItemStack.EMPTY && checkOutputSlotForEmptyOrBucket(fuelOutput.copy())) {
            if (fuelInput.copy().getItem() instanceof BucketItem && fuelInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) fuelInput.copy().getItem()).getFluid();
                if (fuelTank.isFluidValid(fluid) && (
                        fuelTank.getTank().isEmpty()
                                || fuelTank.getTank().getFluid().isFluidEqual(new FluidStack(fluid, 1000))
                                && fuelTank.getTank().getFluidAmount() + 1000 <= tankCapacity)) {
                    fuelTank.getTank().fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    inventory.extractItem(2, 1, false);
                    inventory.insertItem(3, new ItemStack(Items.BUCKET, 1), false);
                }
            }
        }

        // Extract fluid from the fuel tank
        if (fuelInput.copy().getItem() == Items.BUCKET && fuelOutput.copy() == ItemStack.EMPTY) {
            if (fuelTank.getTank().getFluidAmount() >= 1000) {
                ItemStack bucketStack = new ItemStack(fuelTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                fuelTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(2, 1, false);
                inventory.insertItem(3, bucketStack, false);
            }
        }

        // Main Combustion Generator tick logic
        if (counter > 0) {
            if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) + energyRate <= Config.COMBUSTION_GENERATOR_MAX_POWER.get()) {
                counter--;
                energy.ifPresent(e -> e.addEnergy(energyRate)); //Amount of energy to add per tick
            }
            setChanged();
        } else if ((oxidizerTank != null || !oxidizerTank.getTank().isEmpty()) && (fuelTank != null || !fuelTank.getTank().isEmpty())) {
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipe(level, this.oxidizerTank.getTank().getFluid().copy());
            VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(level, this.fuelTank.getTank().getFluid().copy());

            if (oxidizerRecipe != null && fuelRecipe != null) {
                int amount = 250;
                if (oxidizerTank.getTank().getFluidAmount() >= amount && fuelTank.getTank().getFluidAmount() >= amount) {
                    oxidizerTank.getTank().drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    fuelTank.getTank().drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    if (Config.COMBUSTION_GENERATOR_BALANCED_MODE.get()) {
                        counter = (oxidizerRecipe.getProcessTime()) / 4;
                    } else {
                        counter = Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get() / 4;
                    }
                    energyRate = fuelRecipe.getProcessTime() / oxidizerRecipe.getProcessTime(); // Process time in fuel recipe is really volumetric energy
                    length = counter;
                    setChanged();
                }
            }
        }

        if (counter == 0) {
            energyRate = 0;
        }
        sendOutPower();
        // End of item handler

    }

    @Override
    public void load(CompoundTag tag){
        energyRate = tag.getInt("energy_rate");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("energy_rate", energyRate);
        super.saveAdditional(tag);
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()) {
                BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if (tileEntity != null) {
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.COMBUSTION_GENERATOR_SEND.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
                    if (energy.getEnergyStored() <= 0) {
                        break;
                    }
                }
            }
        });
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (!(stack.getItem() instanceof BucketItem)) return false;
                if (slot == 0 || slot == 1) {
                    CombustionGeneratorOxidizerRecipe recipe = RecipeUtil.getOxidizerCombustionRecipe(level, new FluidStack(((BucketItem) stack.getItem()).getFluid(), 1000));
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if (slot == 2 || slot == 3) {
                    VEFluidRecipe recipe = RecipeUtil.getFuelCombustionRecipe(level, new FluidStack(((BucketItem) stack.getItem()).getFluid(), 1000));
                    return recipe != null || stack.getItem() == Items.BUCKET;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CombustionGeneratorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (((counter * 100) / length))) / 100;
        }
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return oxidizerTank.getTank().getFluid();
        } else if (num == 1) {
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
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

    public int getEnergyRate() {
        return energyRate;
    }

    public RelationalTank getOxidizerTank() {
        return oxidizerTank;
    }

    public RelationalTank getFuelTank() {
        return fuelTank;
    }

    @Override
    public int getMaxPower() {
        return Config.COMBUSTION_GENERATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return -1;
    }

    @Override
    public int getTransferRate() {
        return Config.COMBUSTION_GENERATOR_SEND.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return -1;
    }
}
