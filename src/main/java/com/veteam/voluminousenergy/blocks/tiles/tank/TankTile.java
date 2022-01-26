package com.veteam.voluminousenergy.blocks.tiles.tank;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TankTile extends VEFluidTileEntity { // TODO: 2 items slots, 1 tank

    private final RelationalTank tank  = new RelationalTank(new FluidTank(0),0,null,null, TankType.BOTH);

    public VESlotManager bucketTopSlotManager = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager bucketBottomSlotManager = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(bucketBottomSlotManager);
        add(bucketTopSlotManager);
    }};

    List<RelationalTank> fluidManagers = new ArrayList<>(){{
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

    @Nullable
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return null;
    }

    public TankTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int capacity) {
        super(blockEntityType, pos, state);
        capacity = capacity * 1000;
        tank.getTank().setCapacity(capacity);
        tank.setAllowAny(true);
        tank.setIgnoreDirection(true);
    }

    public TankTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack bucketTop = inventory.getStackInSlot(0).copy(); // Bucket Top slot
        ItemStack bucketBottom = inventory.getStackInSlot(1).copy(); // Bucket Bottom slot

        tank.setInput(bucketTop.copy());
        tank.setOutput(bucketBottom.copy());

        if(this.inputFluid(tank,0,1)) return;
        if(this.outputFluid(tank,0,1)) return;

    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (stack.getItem() instanceof BucketItem) return true;
                return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public RelationalTank getTank(){
        return this.tank;
    }

        /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        this.inventory.deserializeNBT(inv);

        //  Slots
        bucketTopSlotManager.read(tag, "bucket_top_slot");
        bucketBottomSlotManager.read(tag, "bucket_bottom_slot");

        // Tanks
        CompoundTag tankNBT = tag.getCompound("tank");
        this.tank.getTank().readFromNBT(tankNBT);
        this.tank.readGuiProperties(tag,"tank_gui");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", this.inventory.serializeNBT());

        // Slots
        bucketTopSlotManager.write(tag, "bucket_top_slot");
        bucketBottomSlotManager.write(tag, "bucket_bottom_slot");

        // Tanks
        CompoundTag tankNBT = new CompoundTag();
        this.tank.getTank().writeToNBT(tankNBT);
        tag.put("tank", tankNBT);
        this.tank.writeGuiProperties(tag, "tank_gui");
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
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
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    public int getTankCapacity(){
        return this.tank.getTank().getCapacity();
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

}
