package com.veteam.voluminousenergy.blocks.tiles.tank;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TankTile extends VEFluidTileEntity {

    private final RelationalTank tank  = new RelationalTank(new FluidTank(0),0,null,null, TankType.BOTH,"tank:tank_gui");

    public VESlotManager bucketTopSlotManager = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"bucket_top_slot");
    public VESlotManager bucketBottomSlotManager = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"bucket_bottom_slot");

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
                return stack.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();
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
    public int getTankCapacity(){
        return this.tank.getTank().getCapacity();
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

}
