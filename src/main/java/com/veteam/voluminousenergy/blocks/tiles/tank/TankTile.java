package com.veteam.voluminousenergy.blocks.tiles.tank;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.MultiFluidSlotWrapper;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TankTile extends VEFluidTileEntity {
    private final RelationalTank tank = new RelationalTank(new FluidTank(0), 0, TankType.BOTH, "tank:tank_gui");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0));
        add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
    }};

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(tank);
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

    List<PosPair> surroundingBlocks = new ArrayList<>();

    public TankTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int capacity) {
        super(blockEntityType, pos, state, null);
        capacity = capacity * 1000;
        tank.getTank().setCapacity(capacity);
        tank.setAllowAny(true);
        tank.setIgnoreDirection(true);
        surroundingBlocks.add(new PosPair(pos.above(), Direction.DOWN));
        surroundingBlocks.add(new PosPair(pos.below(), Direction.UP));
        surroundingBlocks.add(new PosPair(pos.east(), Direction.WEST));
        surroundingBlocks.add(new PosPair(pos.west(), Direction.EAST));
        surroundingBlocks.add(new PosPair(pos.north(), Direction.SOUTH));
        surroundingBlocks.add(new PosPair(pos.south(), Direction.NORTH));
    }

    @Override
    public void tick() {
        updateClients();
        processFluidIO();

        if (!tank.getSideStatus() || tank.getTank().getFluidAmount() == tank.getTank().getCapacity()) return;

//        Direction side = tank.getSideDirection();
//        BlockPos pos = getBlockPos().relative(side);
//        BlockEntity entity = level.getBlockEntity(pos);
//        if (entity == null) return;
//        var capability = entity.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite());
//        capability.ifPresent(fluidHandler -> {
//
//            int tanks = fluidHandler.getTanks();
//
//            for (int i = 0; i < tanks; i++) {
//                FluidStack outsideAmount = fluidHandler.getFluidInTank(i);
//                if (outsideAmount.isEmpty()) continue;
//                FluidTank fluidTank = this.tank.getTank();
//                FluidStack currentFluid = fluidTank.getFluid();
//                if (!currentFluid.isEmpty() && !outsideAmount.getFluid().isSame(currentFluid.getFluid())) continue;
//                int amountToTake = Math.min(outsideAmount.getAmount(), 1000);
//                amountToTake = Math.min(amountToTake, fluidTank.getCapacity() - fluidTank.getFluidAmount());
//                if (amountToTake == 0) return;
//                Fluid outsideFluid = outsideAmount.getFluid(); // if we don't extract this it will get modified below when extracted and potentially be set to AIR
//                int filled = fluidTank.fill(new FluidStack(outsideFluid, amountToTake), IFluidHandler.FluidAction.EXECUTE);
//                if(filled == 0) return;
//                fluidHandler.drain(amountToTake, IFluidHandler.FluidAction.EXECUTE);
//            }
//        });
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                markFluidInputDirty();
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                return (stack.getItem() instanceof BucketItem);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot,stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public RelationalTank getTank() {
        return this.tank;
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player player) {
        return null;
    }

    @Override
    public int getTankCapacity() {
        return this.tank.getTank().getCapacity();
    }

    private LazyOptional<MultiFluidSlotWrapper> multiFluidSlotWrapperLazyOptional = null;
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            if(multiFluidSlotWrapperLazyOptional == null) {
                multiFluidSlotWrapperLazyOptional = LazyOptional.of(() -> new MultiFluidSlotWrapper(getRelationalTanks(),this));
            }
            return multiFluidSlotWrapperLazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    private record PosPair(BlockPos pos, Direction direction) {

    }
}
