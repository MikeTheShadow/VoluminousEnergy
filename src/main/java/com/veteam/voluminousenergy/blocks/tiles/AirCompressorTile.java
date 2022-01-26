package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.fluids.CompressedAir;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class AirCompressorTile extends VEFluidTileEntity {
    public VESlotManager outputSlotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.output_slot", SlotType.OUTPUT,"output_slot");

    // Handlers
    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private final LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private final int TANK_CAPACITY = 4000;
    private byte counter;

    //private FluidTank airTank = new FluidTank(tankCapacity);
    private final RelationalTank airTank = new RelationalTank( new FluidTank(TANK_CAPACITY),0,null,null, TankType.OUTPUT,"air_tank:air_tank_properties");

    public AirCompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AIR_COMPRESSOR_TILE, pos, state);
        airTank.setValidFluids(Collections.singletonList(CompressedAir.CompressedAirFluid().getFlowing()));
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


    private @Nonnull IFluidHandler createFluid() {
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

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
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

    public @Nonnull VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.AIR_COMPRESSOR_MAX_POWER.get(), Config.AIR_COMPRESSOR_TRANSFER.get());
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
    public @Nonnull List<RelationalTank> getRelationalTanks() {
        return Collections.singletonList(airTank);
    }

    public RelationalTank getAirTank(){
        return this.airTank;
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return this.inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return Collections.singletonList(outputSlotManager);
    }

    @Nullable
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return this.energy;
    }
}
