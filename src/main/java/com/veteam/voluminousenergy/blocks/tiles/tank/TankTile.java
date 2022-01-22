package com.veteam.voluminousenergy.blocks.tiles.tank;

import com.veteam.voluminousenergy.blocks.containers.tank.TankContainer;
import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.veteam.voluminousenergy.VoluminousEnergy.LOGGER;

public class TankTile extends VEFluidTileEntity { // TODO: 2 items slots, 1 tank

    private int capacity;
    private RelationalTank tank;
    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(this::createInputFluidHandler);

    // ItemHandlers
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> bucketTop = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 1));
    private LazyOptional<IItemHandlerModifiable> bucketBottom = LazyOptional.of(() -> new RangedWrapper(this.inventory, 1, 2));

    public VESlotManager bucketTopSlotManager = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager bucketBottomSlotManager = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(bucketBottomSlotManager);
        add(bucketTopSlotManager);
    }};

    List<RelationalTank> fluidManagers = new ArrayList<>(){{
        add(tank);
    }};

    private ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public TankTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int capacity) {
        super(blockEntityType, pos, state);
        this.capacity = capacity * 1000;
        tank = new RelationalTank(new FluidTank(this.capacity),0,null,null, TankType.OUTPUT);
        tank.setAllowAny(true);
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

    private IFluidHandler createInputFluidHandler() {
        return this.createFluidHandler(tank);
    }

    public IFluidHandler createFluidHandler(RelationalTank... relationalTanks) { // Derived from the Super Class

        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return relationalTanks.length;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? FluidStack.EMPTY : t.getTank().getFluid();
                    }
                }
                LOGGER.warn("Invalid tankId in TankTile for getFluidInTank, id: " + tank + ", max id: " + (relationalTanks.length - 1));
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? 0 : t.getTank().getCapacity();
                    }
                }
                LOGGER.warn("Invalid tankId in TankTile for getTankCapacity, id: " + tank + ", max id: " + (relationalTanks.length - 1));
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                try {
                    for (RelationalTank t : relationalTanks){
                        return t.getTank() != null && t.getTank().isFluidValid(stack);
                    }
                } catch (Exception e){
                    LOGGER.warn("ERROR with isFluidValid in TankTile");
                }

                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {

                for(RelationalTank t : relationalTanks) {
                    if(isFluidValid(t.getId(),resource) && t.getTank().isEmpty() || resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().fill(resource, action);
                    }
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                for(RelationalTank t : relationalTanks) {
                    if(resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().drain(resource,action);
                    }
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                for(RelationalTank t : relationalTanks) {
                    if(t.getTank().getFluidAmount() > 0) {
                        if (Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                            return t.getTank().drain(maxDrain, action);
                        } else if (t.getTankType() != TankType.INPUT) {
                            return t.getTank().drain(maxDrain, action);
                        }
                    }
                }
                return FluidStack.EMPTY;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getCapability(cap, side, handler, inventory, slotManagers);
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null) { // TODO: Better handle Null direction
            return getCapability(cap, side, handler, Collections.singletonList(tank));
        } else {
            return super.getCapability(cap, side);
        }
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
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @javax.annotation.Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    @Override
    public Component getDisplayName() {return new TextComponent(getType().getRegistryName().getPath());}

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return null; //new TankContainer(i,level,this.worldPosition,playerInventory,player);
    }

    @Override
    public int getTankCapacity(){
        return this.tank.getTank().getCapacity();
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status,slotId,bucketTopSlotManager,bucketBottomSlotManager);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction,slotId,bucketTopSlotManager,bucketBottomSlotManager);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        processGUIPacketFluidStatus(status,id,tank);
    }

    public void updateTankPacketFromGui(int direction, int id){
        processGUIPacketFluidDirection(direction,id,tank);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> level.getServer().getPlayerList().getPlayers().forEach(s -> {
                if (s.getUUID().equals(u)){
                    bulkSendSMPacket(s, bucketTopSlotManager,bucketBottomSlotManager);
                    bulkSendTankPackets(s,tank);
                }
            }));
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
                    if(!(player.containerMenu instanceof TankContainer)){
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
