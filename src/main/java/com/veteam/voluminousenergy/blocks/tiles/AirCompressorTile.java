package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.fluids.CompressedAir;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class AirCompressorTile extends VEFluidTileEntity implements MenuProvider {
    public VESlotManager outputSlotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.output_slot", SlotType.OUTPUT);

    // Handlers
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    // Sided item handlers
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> outputItemHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 1));

    private final int TANK_CAPACITY = 4000;
    private byte counter;

    //private FluidTank airTank = new FluidTank(tankCapacity);
    private RelationalTank airTank = new RelationalTank( new FluidTank(TANK_CAPACITY),0,null,null, TankType.OUTPUT);

    public AirCompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AIR_COMPRESSOR_TILE, pos, state);
    }

    @Deprecated
    public AirCompressorTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.AIR_COMPRESSOR_TILE, pos, state);
    }

    @Override
    public void tick(){
        updateClients();

        ItemStack slotStack = inventory.getStackInSlot(0).copy();

        // Check item in the slot to see if it's a bucket. If it is--and there is fluid for it--fill it.
        if (slotStack.copy().getItem() != null || slotStack.copy() != ItemStack.EMPTY) {
            if (slotStack.getItem() == Items.BUCKET && airTank.getTank().getFluidAmount() >= 1000 && slotStack.getCount() == 1) {
                ItemStack bucketStack = new ItemStack(airTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                airTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                inventory.extractItem(0, 1, false);
                inventory.insertItem(0, bucketStack, false);
            }
        }

        if (airTank != null && (airTank.getTank().getFluidAmount() + 250) <= TANK_CAPACITY && counter == 0 && canConsumeEnergy()){
            // Check blocks around the Air Compressor to see if it's air
            int x = this.worldPosition.getX();
            int y = this.worldPosition.getY();
            int z = this.worldPosition.getZ();

            // Sanity check
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y,z)).getBlock()){
                addAirToTank();
            }

            // Check X offsets
            if (Blocks.AIR == level.getBlockState(new BlockPos(x+1,y,z)).getBlock()){
                //LOGGER.debug("HIT! x+1: " + (x+1) + " y: " + y + " z: " + z + " is AIR!");
                addAirToTank();
            }
            if (Blocks.AIR == level.getBlockState(new BlockPos(x-1,y,z)).getBlock()){
                //LOGGER.debug("HIT! x-1: " + (x-1) + " y: " + y + " z: " + z + " is AIR!");
                addAirToTank();
            }

            // Check Y offsets
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y+1,z)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y+1: " + (y+1) + " z: " + z + " is AIR!");
                addAirToTank();
            }
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y-1,z)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y-1: " + (y-1) + " z: " + z + " is AIR!");
                addAirToTank();
            }

            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y,z+1)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y: " + y + " z+1: " + (z+1) + " is AIR!");
                addAirToTank();
            }
            if (Blocks.AIR == level.getBlockState(new BlockPos(x,y,z-1)).getBlock()){
                //LOGGER.debug("HIT! x: " + x + " y: " + y + " z-1: " + (z-1) + " is AIR!");
                addAirToTank();
            }

        } else if (airTank.getTank() != null && (airTank.getTank().getFluidAmount() + 250) <= TANK_CAPACITY && canConsumeEnergy()){
            consumeEnergy();
        }

        if(counter == 0) counter = (byte)this.calculateCounter(20,this.inventory.getStackInSlot(1).copy());
        else counter--;
    }

    public void addAirToTank() {
        if ((airTank.getTank().getFluidAmount() + 250) <= TANK_CAPACITY) {
            airTank.getTank().fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), 250), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.AIR_COMPRESSOR_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(1).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.AIR_COMPRESSOR_POWER_USAGE.get(), this.inventory.getStackInSlot(1).copy());
    }

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        this.inventory.deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));

        fluid.ifPresent(f -> {
            CompoundTag airNBT = tag.getCompound("air_tank");
            airTank.getTank().readFromNBT(airNBT);
        });

        outputSlotManager.read(tag, "output_slot");
        airTank.readGuiProperties(tag, "air_tank_properties");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag){
        tag.put("inv", this.inventory.serializeNBT());
        energy.ifPresent(h -> h.serializeNBT(tag));

        // Tanks
        fluid.ifPresent(f -> {
            CompoundTag tankNBT = new CompoundTag();
            airTank.getTank().writeToNBT(tankNBT);
            tag.put("air_tank", tankNBT);
        });

        outputSlotManager.write(tag, "output_slot");
        airTank.writeGuiProperties(tag, "air_tank_properties");
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


    private IFluidHandler createFluid() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return airTank == null ? FluidStack.EMPTY : airTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return airTank == null ? 0 : airTank.getTank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return airTank != null && airTank.getTank().isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && airTank.getTank().isEmpty() || resource.isFluidEqual(airTank.getTank().getFluid())) {
                    return airTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(airTank.getTank().getFluid())) {
                    return airTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (airTank.getTank().getFluidAmount() > 0) {
                    return airTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }

    private ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            if (slot == 3){
                return stack.getItem() == VEItems.QUARTZ_MULTIPLIER;
            }
            return true;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == 3){
                if(stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }
    };

    private VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.AIR_COMPRESSOR_MAX_POWER.get(), Config.AIR_COMPRESSOR_TRANSFER.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getCapability(cap, side, handler, inventory, Collections.singletonList(outputSlotManager));
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null) { // TODO: Better handle Null direction
            airTank.setValidFluids(Collections.singletonList(CompressedAir.CompressedAirFluid().getFlowing()));
            return getCapability(cap,side,handler,Collections.singletonList(airTank));
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
        return new AirCompressorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getAirTankFluid(){
        return this.airTank.getTank().getFluid();
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == outputSlotManager.getSlotNum()){
            outputSlotManager.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == outputSlotManager.getSlotNum()){
            outputSlotManager.setDirection(direction);
        }
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.airTank.getId()){
            this.airTank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.airTank.getId()){
            this.airTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        }
    }

    public RelationalTank getAirTank(){
        return this.airTank;
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){

                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(outputSlotManager.getStatus(), outputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(airTank.getSideStatus(), airTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(outputSlotManager.getDirection().get3DDataValue(),outputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(airTank.getSideDirection().get3DDataValue(), airTank.getId()));
                    }
                });
            });
        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || level == null) return;
        if(level.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            level.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.containerMenu != null){
                    if(!(player.containerMenu instanceof AirCompressorContainer)){
                        toRemove.add(player.getUUID());
                    }
                } else if (player.containerMenu == null){
                    toRemove.add(player.getUUID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}
