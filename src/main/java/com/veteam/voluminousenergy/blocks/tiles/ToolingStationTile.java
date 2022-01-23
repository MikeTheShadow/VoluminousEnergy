package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.blocks.containers.ToolingStationContainer;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.ToolingRecipe;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToolingStationTile extends VEFluidTileEntity {
    // Handlers
    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    // Slot Managers
    public VESlotManager fuelTopSlotSM = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager fuelBottomSlotSM = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager mainToolSlotSM = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager bitSlotSM = new VESlotManager(3, Direction.SOUTH,true,"slot.voluminousenergy.input_slot",SlotType.INPUT);
    public VESlotManager multitoolBaseSM = new VESlotManager(4, Direction.EAST, true, "slot.voluminousenergy.input_slot",SlotType.INPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(fuelTopSlotSM);
        add(fuelBottomSlotSM);
        add(mainToolSlotSM);
        add(bitSlotSM);
        add(multitoolBaseSM);
    }};

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
       add(fuelTank);
    }};

    private int counter;
    private int length;

    private ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public ToolingStationTile(BlockPos pos, BlockState state) {
        super(VEBlocks.TOOLING_STATION_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack fuelInput = inventory.getStackInSlot(0).copy(); // Fuel bucket insert
        ItemStack fuelOutput = inventory.getStackInSlot(1).copy(); // Fuel bucket extract
        ItemStack mainTool = inventory.getStackInSlot(2); // This will act like a POINTER, not a clone
        ItemStack toolBit = inventory.getStackInSlot(3).copy(); // this is where the bit would be put into
        ItemStack toolBase = inventory.getStackInSlot(4).copy(); // this is where the base of the tool would be put into

        fuelTank.setInput(fuelInput.copy());
        fuelTank.setOutput(fuelOutput.copy());

        if(this.inputFluid(fuelTank,0,1)) return;
        if(this.outputFluid(fuelTank,0,1)) return;

        VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(this.level,this.fuelTank.getTank().getFluid());

        if(fuelRecipe != null){
            // Logic for refueling the base
            if (!mainTool.isEmpty()){
                mainTool.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluid -> {
                    FluidStack itemFluid = fluid.getFluidInTank(0);
                    FluidStack toolingStationFluid = this.fuelTank.getTank().getFluid().copy();
                    int tankCapacity = fluid.getTankCapacity(0);

                    if(itemFluid.getAmount() < tankCapacity && (itemFluid.isFluidEqual(toolingStationFluid) || itemFluid.isEmpty())){
                        int toTransfer;

                        if (!itemFluid.isEmpty()){
                            toTransfer = Math.min(toolingStationFluid.getAmount(), itemFluid.getAmount()); // Which amount is smaller
                            toTransfer = Math.min(toTransfer, (tankCapacity - itemFluid.getAmount())); // Previous value versus the delta between the tankCapacity in the item and the current fluid amount
                        } else { // Clean slate, check only against the tank capacity
                            toTransfer = Math.min(toolingStationFluid.getAmount(), tankCapacity);
                        }

                        if (toTransfer > 0){
                            // Drain the fluid from the Tooling Station
                            this.fuelTank.getTank().drain(toTransfer, IFluidHandler.FluidAction.EXECUTE);
                            toolingStationFluid.setAmount(toTransfer); // Set the fluid that is going to go into the item
                            // Fill the item
                            fluid.fill(toolingStationFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                            // Fill the fluid in the base as well
                            inventory.getStackInSlot(4).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                                    .ifPresent(baseFluid -> baseFluid.fill(toolingStationFluid, IFluidHandler.FluidAction.EXECUTE));
                        }
                    }
                });
            }
        }

        if(mainTool.isEmpty() && inventory.getStackInSlot(2).isEmpty()){
            if(!toolBit.isEmpty() && !toolBase.isEmpty()){
                ToolingRecipe toolingRecipe = RecipeUtil.getToolingRecipeFromBitAndBase(level, toolBit.copy(), toolBase.copy());
                if (toolingRecipe != null){
                    ItemStack craftedTool = new ItemStack(toolingRecipe.result.getItem(),1);

                    // Fill the crafted Multitool with fluid from the emptyMultitool
                    craftedTool.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(
                            fluidTool -> toolBase.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluidBase ->{
                                FluidStack baseFluid = fluidBase.getFluidInTank(0).copy();
                                fluidTool.fill(baseFluid, IFluidHandler.FluidAction.EXECUTE);
                            }));

                    inventory.setStackInSlot(2, craftedTool);
                }
            }
        } else if (!mainTool.isEmpty() && toolBase.isEmpty() && toolBit.isEmpty()){
            ToolingRecipe toolingRecipe = RecipeUtil.getToolingRecipeFromResult(level, mainTool.copy());
            if (toolingRecipe != null){
                inventory.setStackInSlot(3, new ItemStack(toolingRecipe.getBits().get(0)));
                ItemStack baseStack = new ItemStack(toolingRecipe.getBases().get(0));

                // Fill the base with the same fluid as the mainTool
                baseStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(baseFluid ->
                    mainTool.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(toolFluid -> {
                        FluidStack fluidTool = toolFluid.getFluidInTank(0).copy();
                        baseFluid.fill(fluidTool, IFluidHandler.FluidAction.EXECUTE);
                }));

                inventory.setStackInSlot(4, baseStack);
                inventory.setStackInSlot(2, mainTool.copy());
            }
        }


    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.AQUEOULIZER_POWER_USAGE.get(), // TODO: Config
                                this.inventory.getStackInSlot(4).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.AQUEOULIZER_POWER_USAGE.get(), this.inventory.getStackInSlot(4).copy()); // TODO: Config
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

        // Tanks
        CompoundTag fuelTankNBT = tag.getCompound("fuel_tank");

        this.fuelTank.getTank().readFromNBT(fuelTankNBT);

        this.fuelTank.readGuiProperties(tag,"fuel_tank_gui");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        handler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> h.serializeNBT(tag));
        tag.putInt("counter", counter);
        tag.putInt("length", length);

        // Tanks
        CompoundTag fuelTankNBT = new CompoundTag();

        this.fuelTank.getTank().writeToNBT(fuelTankNBT);

        tag.put("fuel_tank", fuelTankNBT);

        this.fuelTank.writeGuiProperties(tag,"fuel_tank_gui");
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

    private IFluidHandler createFuelFluidHandler() {
        return this.createFluidHandler(new CombustionGeneratorFuelRecipe(), fuelTank);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                if(slot == 2 && this.getStackInSlot(2).isEmpty()){ // If the crafted multitool is removed, delete the components
                    if (this.getStackInSlot(3).isEmpty() || this.getStackInSlot(4).isEmpty()){

                    } else {
                        this.setStackInSlot(3, ItemStack.EMPTY);
                        this.setStackInSlot(4, ItemStack.EMPTY);
                    }
                } else if ((slot == 3 || slot == 4) && (!this.getStackInSlot(2).isEmpty())){
                    if(this.getStackInSlot(3).isEmpty() || this.getStackInSlot(4).isEmpty()){ // If one of the components of the multitool is removed, delete the multitool
                        this.setStackInSlot(2, ItemStack.EMPTY);
                    }
                }

                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot < 2) return stack.getItem() instanceof BucketItem;
                if (slot == 2) return stack.getItem() instanceof Multitool;
                if (slot == 3) return stack.getItem() instanceof BitItem;
                if (slot == 4) return (stack.getItem() == VEMultitools.EMPTY_MULTITOOL); // TODO: Remove Multitool base?
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private @NotNull VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.AQUEOULIZER_MAX_POWER.get(), Config.AQUEOULIZER_TRANSFER.get()); // TODO: Config
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap,side,handler,inventory,slotManagers,fluidManagers,energy);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new ToolingStationContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank(){
        return this.fuelTank;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status, slotId, fuelTopSlotSM, fuelBottomSlotSM, mainToolSlotSM, bitSlotSM);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction, slotId, fuelTopSlotSM, fuelBottomSlotSM, mainToolSlotSM, bitSlotSM);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.fuelTank.getId()){
            this.fuelTank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.fuelTank.getId()){
            this.fuelTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        }
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        // Slots
                        bulkSendSMPacket(s, fuelTopSlotSM, fuelBottomSlotSM, mainToolSlotSM, bitSlotSM, multitoolBaseSM);
                        bulkSendTankPackets(s, fuelTank);
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
                    if(!(player.containerMenu instanceof AqueoulizerContainer)){
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