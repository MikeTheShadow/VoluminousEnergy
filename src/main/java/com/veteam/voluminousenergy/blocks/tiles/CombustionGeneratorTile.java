package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CombustionGeneratorTile extends VEFluidTileEntity implements MenuProvider {
    // Handlers
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory);

    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private final LazyOptional<IFluidHandler> oxidizerHandler = LazyOptional.of(this::createOxidizerHandler);
    private final LazyOptional<IFluidHandler> fuelHandler = LazyOptional.of(this::createFuelHandler);

    // Slot Managers
    public VESlotManager oxiInSm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager oxiOutSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT);
    public VESlotManager fuelInSm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager fuelOutSm = new VESlotManager(3, Direction.SOUTH, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(oxiInSm);
            add(oxiOutSm);
            add(fuelInSm);
            add(fuelOutSm);
        }
    };

    private final int tankCapacity = 4000;

    private final RelationalTank oxidizerTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.INPUT);
    private final RelationalTank fuelTank = new RelationalTank(new FluidTank(tankCapacity), 1, null, null, TankType.INPUT);

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(oxidizerTank);
            add(fuelTank);
        }
    };

    private int counter;
    private int length;
    private int energyRate;

    private final ItemStackHandler inventory = createHandler();

    private static final Logger LOGGER = LogManager.getLogger();

    public CombustionGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE, pos, state);
    }

    @Deprecated
    public CombustionGeneratorTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE, pos, state);
    }

    @Override
    public void tick() {

        updateClients();

        ItemStack oxidizerInput = inventory.getStackInSlot(0).copy();
        ItemStack oxidizerOutput = inventory.getStackInSlot(1).copy();
        ItemStack fuelInput = inventory.getStackInSlot(2).copy();
        ItemStack fuelOutput = inventory.getStackInSlot(3).copy();

        /*
         *  Manipulate tanks based on input from buckets in slots
         */

        // Input fluid into the oxidizer tank
        if (oxidizerInput.copy() != ItemStack.EMPTY && oxidizerOutput.copy() == ItemStack.EMPTY) {
            if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).getFluid();
                if (CombustionGeneratorOxidizerRecipe.rawFluidInputList.contains(fluid) && (
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
        if (oxidizerInput.copy().getItem() == Items.BUCKET && oxidizerOutput.copy() == ItemStack.EMPTY) {
            if (oxidizerTank.getTank().getFluidAmount() >= 1000) {
                ItemStack bucketStack = new ItemStack(oxidizerTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                oxidizerTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(0, 1, false);
                inventory.insertItem(1, bucketStack, false);
            }
        }

        // Input fluid to the fuel tank
        if (fuelInput.copy() != ItemStack.EMPTY && fuelOutput.copy() == ItemStack.EMPTY) {
            if (fuelInput.copy().getItem() instanceof BucketItem && fuelInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) fuelInput.copy().getItem()).getFluid();
                if (CombustionGeneratorFuelRecipe.rawFluidInputListStatic.contains(fluid) && (
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

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag) {
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));
        counter = tag.getInt("counter");
        length = tag.getInt("length");

        oxiInSm.read(tag, "oxi_in_sm");
        oxiOutSm.read(tag, "oxi_out_sm");
        fuelInSm.read(tag, "fuel_in_sm");
        fuelOutSm.read(tag, "fuel_out_sm");

        // Tanks
        CompoundTag oxidizerNBT = tag.getCompound("oxidizerTank");
        CompoundTag fuelNBT = tag.getCompound("fuelTank");
        oxidizerTank.getTank().readFromNBT(oxidizerNBT);
        fuelTank.getTank().readFromNBT(fuelNBT);

        counter = tag.getInt("counter");
        length = tag.getInt("length");
        energyRate = tag.getInt("energy_rate");

        oxidizerTank.readGuiProperties(tag, "oxidizer_tank_gui");
        fuelTank.readGuiProperties(tag, "fuel_tank_gui");

        super.load(tag);
    }

    @Nonnull
    @Override
    public void saveAdditional(CompoundTag tag) {
        handler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> h.serializeNBT(tag));
        tag.putInt("counter", counter);
        tag.putInt("length", length);

        oxiInSm.write(tag, "oxi_in_sm");
        oxiOutSm.write(tag, "oxi_out_sm");
        fuelInSm.write(tag, "fuel_in_sm");
        fuelOutSm.write(tag, "fuel_out_sm");

        // Tanks
        CompoundTag oxidizerNBT = new CompoundTag();
        oxidizerTank.getTank().writeToNBT(oxidizerNBT);
        tag.put("oxidizerTank", oxidizerNBT);

        CompoundTag fuelNBT = new CompoundTag();
        fuelTank.getTank().writeToNBT(fuelNBT);
        tag.put("fuelTank", fuelNBT);

        tag.putInt("counter", counter);
        tag.putInt("length", length);
        tag.putInt("energy_rate", energyRate);

        oxidizerTank.writeGuiProperties(tag, "oxidizer_tank_gui");
        fuelTank.writeGuiProperties(tag, "fuel_tank_gui");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private @NotNull IFluidHandler createOxidizerHandler() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0) {
                    return oxidizerTank.getTank() == null ? FluidStack.EMPTY : oxidizerTank.getTank().getFluid();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return oxidizerTank.getTank() == null ? 0 : oxidizerTank.getTank().getCapacity();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    ItemStack oxidizerStack = new ItemStack(stack.getFluid().getBucket(), 1);
                    CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipe(level, stack.copy());
                    if (oxidizerRecipe == null) {
                        return false;
                    }
                    return oxidizerTank.getTank() != null && oxidizerTank.getTank().isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && oxidizerTank.getTank().isEmpty() || resource.isFluidEqual(oxidizerTank.getTank().getFluid())) {
                    return oxidizerTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(oxidizerTank.getTank().getFluid())) {
                    return oxidizerTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (oxidizerTank.getTank().getFluidAmount() > 0) {
                    return oxidizerTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }


    private @NotNull IFluidHandler createFuelHandler() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0) {
                    return fuelTank.getTank() == null ? FluidStack.EMPTY : fuelTank.getTank().getFluid();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return fuelTank.getTank() == null ? 0 : fuelTank.getTank().getCapacity();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(level, stack.copy());
                    if (fuelRecipe == null) {
                        return false;
                    }
                    return fuelTank.getTank() != null && fuelTank.getTank().isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && fuelTank.getTank().isEmpty() || resource.isFluidEqual(fuelTank.getTank().getFluid())) {
                    return fuelTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(fuelTank.getTank().getFluid())) {
                    return fuelTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (fuelTank.getTank().getFluidAmount() > 0) {
                    return fuelTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
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

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    private @NotNull VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.COMBUSTION_GENERATOR_MAX_POWER.get(), Config.COMBUSTION_GENERATOR_SEND.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getCapability(cap, side, handler, inventory, slotManagers);
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null) { // TODO: Better handle Null direction
            oxidizerTank.setValidFluids(CombustionGeneratorOxidizerRecipe.rawFluidInputList);
            fuelTank.setValidFluids(CombustionGeneratorFuelRecipe.rawFluidInputListStatic);
            return getCapability(cap,side,handler,fluidManagers);
        } else {
            return super.getCapability(cap, side);
        }

    }

    @Override
    public Component getDisplayName() {
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CombustionGeneratorContainer(i, level, worldPosition, playerInventory, playerEntity);
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
    public void updatePacketFromGui(boolean status, int slotId) {
        processGUIPacketStatus(status, slotId, oxiInSm, oxiOutSm, fuelInSm, fuelOutSm);
    }

    public void updatePacketFromGui(int direction, int slotId) {
        processGUIPacketDirection(direction, slotId, oxiInSm, oxiOutSm, fuelInSm, fuelOutSm);
    }

    public void updateTankPacketFromGui(boolean status, int id) {
        processGUIPacketFluidStatus(status, id, oxidizerTank, fuelTank);
    }

    public void updateTankPacketFromGui(int direction, int id) {
        processGUIPacketFluidDirection(direction, id, oxidizerTank, fuelTank);
    }

    @Override
    public void sendPacketToClient() {
        if (level == null || getLevel() == null) return;
        if (getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)) {
                        bulkSendSMPacket(s, oxiInSm, oxiOutSm, fuelInSm, fuelOutSm);
                        bulkSendTankPackets(s, oxidizerTank, fuelTank);
                    }
                });
            });
        }
    }

    @Override
    protected void uuidCleanup() {
        if (playerUuid.isEmpty() || level == null) return;
        if (level.getServer() == null) return;

        if (cleanupTick == 20) {
            ArrayList<UUID> toRemove = new ArrayList<>();
            level.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (player.containerMenu != null) {
                    if (!(player.containerMenu instanceof CombustionGeneratorContainer)) {
                        toRemove.add(player.getUUID());
                    }
                } else if (player.containerMenu == null) {
                    toRemove.add(player.getUUID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}
