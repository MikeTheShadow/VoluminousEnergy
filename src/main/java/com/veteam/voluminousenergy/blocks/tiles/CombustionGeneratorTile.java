package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class CombustionGeneratorTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    // Handlers
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> oxiInHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 1));
    private LazyOptional<IItemHandlerModifiable> oxiOutHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 1, 2));
    private LazyOptional<IItemHandlerModifiable> fuelInHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 2,3));
    private LazyOptional<IItemHandlerModifiable> fuelOutHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 3,4));

    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> oxidizerHandler = LazyOptional.of(this::createOxidizerHandler);
    private LazyOptional<IFluidHandler> fuelHandler = LazyOptional.of(this::createFuelHandler);

    // Slot Managers
    public VESlotManager oxiInSm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot");
    public VESlotManager oxiOutSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot");
    public VESlotManager fuelInSm = new VESlotManager(2,Direction.NORTH,true,"slot.voluminousenergy.input_slot");
    public VESlotManager fuelOutSm = new VESlotManager(3,Direction.SOUTH, true,"slot.voluminousenergy.output_slot");

    private int tankCapacity = 4000;

    private RelationalTank oxidizerTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.INPUT);
    private RelationalTank fuelTank = new RelationalTank(new FluidTank(tankCapacity), 1, null, null, TankType.INPUT);

    private int counter;
    private int length;
    private int energyRate;

    private final ItemStackHandler inventory = createHandler();

    private static final Logger LOGGER = LogManager.getLogger();

    public CombustionGeneratorTile() {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE);
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
        if (oxidizerInput.copy() != null || oxidizerInput.copy() != ItemStack.EMPTY && oxidizerOutput.copy() == ItemStack.EMPTY) {
            if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).getFluid();
                //FluidStack fluidStack = new FluidStack(fluid, 1000);
                if (oxidizerTank.getTank().isEmpty() || oxidizerTank.getTank().getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && oxidizerTank.getTank().getFluidAmount() + 1000 <= tankCapacity) {
                    oxidizerTank.getTank().fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    inventory.extractItem(0, 1, false);
                    inventory.insertItem(1, new ItemStack(Items.BUCKET, 1), false);
                }
            }
        }

        // Extract fluid from the oxidizer tank
        if(oxidizerInput.copy().getItem() == Items.BUCKET && oxidizerOutput.copy() == ItemStack.EMPTY) {
            if(oxidizerTank.getTank().getFluidAmount() >= 1000) {
                ItemStack bucketStack = new ItemStack(oxidizerTank.getTank().getFluid().getRawFluid().getFilledBucket(), 1);
                oxidizerTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(0, 1, false);
                inventory.insertItem(1, bucketStack, false);
            }
        }


        // Input fluid to the fuel tank
        if (fuelInput.copy() != null || fuelInput.copy() != ItemStack.EMPTY && fuelOutput.copy() == ItemStack.EMPTY) {
            if (fuelInput.copy().getItem() instanceof BucketItem && fuelInput.getCount() == 1) {
                Fluid fluid = ((BucketItem) fuelInput.copy().getItem()).getFluid();
                //FluidStack fluidStack = new FluidStack(fluid, 1000);
                if (fuelTank.getTank().isEmpty() || fuelTank.getTank().getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && fuelTank.getTank().getFluidAmount() + 1000 <= tankCapacity) {
                    fuelTank.getTank().fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    inventory.extractItem(2, 1, false);
                    inventory.insertItem(3, new ItemStack(Items.BUCKET, 1), false);
                }
            }
        }

        // Extract fluid from the fuel tank
        if(fuelInput.copy().getItem() == Items.BUCKET && fuelOutput.copy() == ItemStack.EMPTY) {
            if(fuelTank.getTank().getFluidAmount() >= 1000) {
                ItemStack bucketStack = new ItemStack(fuelTank.getTank().getFluid().getRawFluid().getFilledBucket(), 1);
                fuelTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(2, 1, false);
                inventory.insertItem(3, bucketStack, false);
            }
        }

        // Main Combustion Generator tick logic
        if (counter > 0) {
            if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) + energyRate <= Config.COMBUSTION_GENERATOR_MAX_POWER.get()){
                counter--;
                energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(energyRate)); //Amount of energy to add per tick
            }
            markDirty();
        } else if ((oxidizerTank != null || !oxidizerTank.getTank().isEmpty()) && (fuelTank != null || !fuelTank.getTank().isEmpty())){
            CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipe(world, this.oxidizerTank.getTank().getFluid().copy());
            VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(world, this.fuelTank.getTank().getFluid().copy());

            if (oxidizerRecipe != null && fuelRecipe != null){
                int amount = 250;
                if (oxidizerTank.getTank().getFluidAmount() >= amount && fuelTank.getTank().getFluidAmount() >= amount){
                    oxidizerTank.getTank().drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    fuelTank.getTank().drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    if (Config.COMBUSTION_GENERATOR_BALANCED_MODE.get()){
                        counter = (oxidizerRecipe.getProcessTime())/4;
                    } else {
                        counter = Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get()/4;
                    }
                    energyRate = fuelRecipe.getProcessTime()/oxidizerRecipe.getProcessTime(); // Process time in fuel recipe is really volumetric energy
                    length = counter;
                    markDirty();
                }
            }
        }

        if (counter == 0){
            energyRate = 0;
        }
        sendOutPower();
        // End of item handler

    }

    public static int receiveEnergy(TileEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()){
                TileEntity tileEntity = world.getTileEntity(getPos().offset(dir));
                Direction opposite = dir.getOpposite();
                if(tileEntity != null){
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.COMBUSTION_GENERATOR_SEND.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    ((VEEnergyStorage) energy).consumeEnergy(received);
                    if (energy.getEnergyStored() <=0){
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
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        CompoundNBT oxidizerNBT = tag.getCompound("oxidizerTank");
        CompoundNBT fuelNBT = tag.getCompound("fuelTank");
        oxidizerTank.getTank().readFromNBT(oxidizerNBT);
        fuelTank.getTank().readFromNBT(fuelNBT);

        counter = tag.getInt("counter");
        length = tag.getInt("length");
        energyRate = tag.getInt("energy_rate");

        oxidizerTank.readGuiProperties(tag,"oxidizer_tank_gui");
        fuelTank.readGuiProperties(tag, "fuel_tank_gui");

        super.read(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("energy", compound);
        });

        // Tanks
        CompoundNBT oxidizerNBT = new CompoundNBT();
        oxidizerTank.getTank().writeToNBT(oxidizerNBT);
        tag.put("oxidizerTank", oxidizerNBT);

        CompoundNBT fuelNBT = new CompoundNBT();
        fuelTank.getTank().writeToNBT(fuelNBT);
        tag.put("fuelTank", fuelNBT);

        tag.putInt("counter", counter);
        tag.putInt("length", length);
        tag.putInt("energy_rate", energyRate);

        oxidizerTank.writeGuiProperties(tag, "oxidizer_tank_gui");
        fuelTank.writeGuiProperties(tag, "fuel_tank_gui");

        return super.write(tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createOxidizerHandler(){
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
                    ItemStack oxidizerStack = new ItemStack(stack.getFluid().getFilledBucket(),1);
                    CombustionGeneratorOxidizerRecipe oxidizerRecipe = RecipeUtil.getOxidizerCombustionRecipe(world, stack.copy());
                    if (oxidizerRecipe == null){
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


    private IFluidHandler createFuelHandler() {
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
                    VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(world, stack.copy());
                    if (fuelRecipe == null){
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
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if(!(stack.getItem() instanceof BucketItem)) return false;
                if(slot == 0 || slot == 1) {
                    CombustionGeneratorOxidizerRecipe recipe = RecipeUtil.getOxidizerCombustionRecipe(world, new FluidStack(((BucketItem) stack.getItem()).getFluid(),1000));
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if(slot == 2 || slot == 3) {
                    VEFluidRecipe recipe = RecipeUtil.getFuelCombustionRecipe(world, new FluidStack(((BucketItem) stack.getItem()).getFluid(),1000));
                    return recipe != null || stack.getItem() == Items.BUCKET;
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

    private IEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.COMBUSTION_GENERATOR_MAX_POWER.get(), Config.COMBUSTION_GENERATOR_SEND.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return handler.cast();
            if(oxiInSm.getStatus() && oxiInSm.getDirection().getIndex() == side.getIndex())
                return oxiInHandler.cast();
            else if(oxiOutSm.getStatus() && oxiOutSm.getDirection().getIndex() == side.getIndex())
                return oxiOutHandler.cast();
            else if(fuelInSm.getStatus() && fuelInSm.getDirection().getIndex() == side.getIndex())
                return fuelInHandler.cast();
            else if(fuelOutSm.getStatus() && fuelOutSm.getDirection().getIndex() == side.getIndex())
                return fuelOutHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if(oxidizerTank.getSideStatus() && oxidizerTank.getSideDirection().getIndex() == side.getIndex())
                return oxidizerHandler.cast();
            if(fuelTank.getSideStatus() && fuelTank.getSideDirection().getIndex() == side.getIndex())
                return fuelHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new CombustionGeneratorContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px*(((counter*100)/length)))/100;
        }
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return oxidizerTank.getTank().getFluid();
        } else if (num == 1){
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return tankCapacity;
    }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    public int ticksLeft(){
        return counter;
    }

    public int getEnergyRate(){
        return energyRate;
    }

    public RelationalTank getOxidizerTank(){ return oxidizerTank;}

    public RelationalTank getFuelTank(){return fuelTank;}

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == oxiInSm.getSlotNum()){
            oxiInSm.setStatus(status);
        } else if (slotId == oxiOutSm.getSlotNum()){
            oxiOutSm.setStatus(status);
        } else if(slotId == fuelInSm.getSlotNum()){
            fuelInSm.setStatus(status);
        } else if(slotId == fuelOutSm.getSlotNum()){
            fuelOutSm.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == oxiInSm.getSlotNum()){
            oxiInSm.setDirection(direction);
        } else if (slotId == oxiOutSm.getSlotNum()){
            oxiOutSm.setDirection(direction);
        } else if(slotId == fuelInSm.getSlotNum()){
            fuelInSm.setDirection(direction);
        } else if(slotId == fuelOutSm.getSlotNum()){
            fuelOutSm.setDirection(direction);
        }
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.oxidizerTank.getId()){
            this.oxidizerTank.setSideStatus(status);
        } else if(id == this.fuelTank.getId()){
            this.fuelTank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.oxidizerTank.getId()){
            this.oxidizerTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        } else if(id == this.fuelTank.getId()){
            this.fuelTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        }
    }

    @Override
    public void sendPacketToClient(){
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(oxiInSm.getStatus(), oxiInSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(oxiOutSm.getStatus(), oxiOutSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(fuelInSm.getStatus(), fuelInSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(fuelOutSm.getStatus(), fuelOutSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(oxidizerTank.getSideStatus(), oxidizerTank.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(fuelTank.getSideStatus(), fuelTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(oxiInSm.getDirection().getIndex(),oxiInSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(oxiOutSm.getDirection().getIndex(),oxiOutSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(fuelInSm.getDirection().getIndex(),fuelInSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(fuelOutSm.getDirection().getIndex(),fuelOutSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(oxidizerTank.getSideDirection().getIndex(),oxidizerTank.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(fuelTank.getSideDirection().getIndex(),fuelTank.getId()));
                    }
                });
            });
        } else if (!playerUuid.isEmpty()){ // Legacy solution
            double x = this.getPos().getX();
            double y = this.getPos().getY();
            double z = this.getPos().getZ();
            final double radius = 16;
            RegistryKey<World> worldRegistryKey = this.getWorld().getDimensionKey();
            PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x,y,z,radius,worldRegistryKey);

            // Boolean Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(oxiInSm.getStatus(), oxiInSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(oxiOutSm.getStatus(), oxiOutSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(fuelInSm.getStatus(), fuelInSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(fuelOutSm.getStatus(), fuelOutSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(oxidizerTank.getSideStatus(), oxidizerTank.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(fuelTank.getSideStatus(), fuelTank.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(oxiInSm.getDirection().getIndex(),oxiInSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(oxiOutSm.getDirection().getIndex(),oxiOutSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(fuelInSm.getDirection().getIndex(),fuelInSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(fuelOutSm.getDirection().getIndex(),fuelOutSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(oxidizerTank.getSideDirection().getIndex(), oxidizerTank.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(fuelTank.getSideDirection().getIndex(), fuelTank.getId()));
        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || world == null) return;
        if(world.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            world.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.openContainer != null){
                    if(!(player.openContainer instanceof CombustionGeneratorContainer)){
                        toRemove.add(player.getUniqueID());
                    }
                } else if (player.openContainer == null){
                    toRemove.add(player.getUniqueID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}
