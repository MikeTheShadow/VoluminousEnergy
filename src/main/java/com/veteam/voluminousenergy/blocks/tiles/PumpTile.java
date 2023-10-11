package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PumpContainer;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PumpTile extends VEFluidTileEntity implements IVEPoweredTileEntity {
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory);
    private final LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    // Capability is unique. Can't add new impl to this
    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true, SlotType.INPUT);

    private final int tankCapacity = 4000;

    // Working data
    private boolean initDone = false;
    private int lX = 0;
    private int lY = 0;
    private int lZ = 0;

    private final RelationalTank fluidTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.OUTPUT,"tank:tank_gui");
    private Fluid pumpingFluid = Fluids.EMPTY;
    private final ItemStackHandler inventory = this.createHandler();

    public PumpTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PUMP_TILE.get(), pos, state,null);
        fluidTank.setAllowAny(true);
    }

    @Override
    public void tick(){
        updateClients();
        handler.ifPresent(h -> {
            ItemStack slotStack = h.getStackInSlot(0).copy();

            // Check item in the slot to see if it's a bucket. If it is--and there is fluid for it--fill it.
            if (slotStack.copy() != ItemStack.EMPTY) { // TODO: Consider 2 slot system like the Combustion Generator/Distillation Unit
                if (slotStack.getItem() == Items.BUCKET && fluidTank.getTank().getFluidAmount() >= 1000 && slotStack.getCount() == 1) {
                    ItemStack bucketStack = new ItemStack(fluidTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                    fluidTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(0, bucketStack, false);
                }
            }

            if (fluidTank.getTank() != null && (fluidTank.getTank().getFluidAmount() + 1000) <= tankCapacity && this.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                for(int i = 0; i < 50; i++) {
                    if(fluidPumpMethod()) break;
                }
                setChanged();
                if(++sound_tick == 19) {
                    sound_tick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, this.getBlockPos(), VESounds.AIR_COMPRESSOR, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        });
    }

    public void addFluidToTank() {
        if ((fluidTank.getTank().getFluidAmount() + 1000) <= tankCapacity) {
            energy.ifPresent(e -> e.consumeEnergy(Config.PUMP_POWER_USAGE.get()));
            fluidTank.getTank().fill(new FluidStack(this.pumpingFluid, 1000), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        /*
            We super first because we need to load those fluids before we get the fluid here
         */
        super.load(tag);
        pumpingFluid = fluidTank.getTank().getFluid().getRawFluid();

        lX = tag.getInt("lx");
        lY = tag.getInt("ly");
        lZ = tag.getInt("lz");
        initDone = tag.getBoolean("init_done");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("lx", lX);
        tag.putInt("ly", lY);
        tag.putInt("lz", lZ);
        tag.putBoolean("init_done", initDone);
        super.saveAdditional(tag);
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
                return fluidTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return fluidTank.getTank() == null ? 0 : fluidTank.getTank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return fluidTank.getTank().isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && fluidTank.getTank().isEmpty() || resource.isFluidEqual(fluidTank.getTank().getFluid())) {
                    return fluidTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(fluidTank.getTank().getFluid())) {
                    return fluidTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (fluidTank.getTank().getFluidAmount() > 0) {
                    return fluidTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energy.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER){
            return fluid.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new PumpContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return null;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return new ArrayList<>();
    }

    public FluidStack getAirTankFluid(){
        return this.fluidTank.getTank().getFluid();
    }

    public int getTankCapacity(){
        return tankCapacity;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return Collections.singletonList(fluidTank);
    }

    public boolean fluidPumpMethod() {
        if (!(initDone)) {
            lX = -22;
            lY = -1;
            lZ = -22;

            try{
                this.pumpingFluid = this.level.getBlockState(this.getBlockPos().offset(0, -1, 0)).getFluidState().getType();
                initDone = true;
            } catch (Exception e){
                return false;
            }
        }

        if (this.pumpingFluid == Fluids.EMPTY || this.pumpingFluid.isSame(Fluids.EMPTY) || this.pumpingFluid == null){ // Sanity check to prevent mass destruction
            initDone = false;
            return false;
        }

        if (lX < 22){
            lX++;
            if(this.pumpingFluid.isSame(this.level.getBlockState(this.getBlockPos().offset(lX,lY,lZ)).getFluidState().getType())){
                this.level.setBlockAndUpdate(this.getBlockPos().offset(lX,lY,lZ),Blocks.AIR.defaultBlockState()); // setBlockAndUpdate is the replacement for setBlockState in MCP mappings. This is obvious because of the flag of 3.
                addFluidToTank();
                return true;
            }

        } else if (lZ < 22){
            lZ++;
            lX = -22;
            if(this.pumpingFluid.isSame(this.level.getBlockState(this.getBlockPos().offset(lX,lY,lZ)).getFluidState().getType())){
                this.level.setBlockAndUpdate(this.getBlockPos().offset(lX,lY,lZ),Blocks.AIR.defaultBlockState());
                addFluidToTank();
                return true;
            }
        } else if (this.getBlockPos().offset(0, lY, 0).getY() > -63){
            lY--;
            lX = -22;
            lZ = -22;
            if(this.pumpingFluid.isSame(this.level.getBlockState(this.getBlockPos().offset(lX,lY,lZ)).getFluidState().getType())){
                this.level.setBlockAndUpdate(this.getBlockPos().offset(lX,lY,lZ),Blocks.AIR.defaultBlockState());
                addFluidToTank();
                return true;
            }
        }
        return false;
    }

    public RelationalTank getTank(){
        return this.fluidTank;
    }

    @Override
    public int getMaxPower() {
        return Config.PUMP_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.PUMP_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.PUMP_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}
