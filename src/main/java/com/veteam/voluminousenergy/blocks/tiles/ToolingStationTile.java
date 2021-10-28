package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.blocks.containers.ToolingStationContainer;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.MultitoolBase;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class ToolingStationTile extends VEFluidTileEntity {
    // Handlers
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fuelFluidHandler = LazyOptional.of(this::createFuelFluidHandler);
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> fuelTopSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 1));
    private LazyOptional<IItemHandlerModifiable> fuelBottomSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 1, 2));
    private LazyOptional<IItemHandlerModifiable> mainToolSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 2,3));
    private LazyOptional<IItemHandlerModifiable> bitSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 3,4));
    private LazyOptional<IItemHandlerModifiable> multitoolBaseSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 4,5));

    // Slot Managers
    public VESlotManager fuelTopSlotSM = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot");
    public VESlotManager fuelBottomSlotSM = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot");
    public VESlotManager mainToolSlotSM = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot");
    public VESlotManager bitSlotSM = new VESlotManager(3, Direction.SOUTH,true,"slot.voluminousenergy.input_slot");
    public VESlotManager multitoolBaseSM = new VESlotManager(4, Direction.EAST, true, "slot.voluminousenergy.input_slot");

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);

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

    @Deprecated
    public ToolingStationTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.TOOLING_STATION_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack fuelInput = inventory.getStackInSlot(0).copy(); // Fuel bucket insert
        ItemStack fuelOutput = inventory.getStackInSlot(1).copy(); // Fuel bucket extract
        ItemStack mainTool = inventory.getStackInSlot(2).copy(); // Main tool slot, where the final tool would be crafted
        ItemStack toolBit = inventory.getStackInSlot(3).copy(); // this is where the bit would be put into
        ItemStack toolBase = inventory.getStackInSlot(4).copy(); // this is where the base of the tool would be put into

        fuelTank.setInput(fuelInput.copy());
        fuelTank.setOutput(fuelOutput.copy());

        if(this.inputFluid(fuelTank,0,1)) return;
        if(this.outputFluid(fuelTank,0,1)) return;

        VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(this.level,this.fuelTank.getTank().getFluid());

        if(fuelRecipe != null){
            // Logic for fuel

            // Logic for refueling the base
            if (!mainTool.isEmpty()){

            }
        }

        /* Main Fluid Processing occurs here:
        VEFluidRecipe recipe = RecipeUtil.getAqueoulizerRecipe(level, this.fuelTank.getTank().getFluid(),inputItem.copy());
        // Manually find the recipe since we have 2 conditions rather than the 1 input the vanilla getRecipe supports

        if (fuelTank != null && !fuelTank.getTank().isEmpty() && recipe != null) {
            //ItemStack inputFluidStack = new ItemStack(inputTank.getTank().getFluid().getRawFluid().getFilledBucket(),1);

            if (recipe.getRawFluids().contains(fuelTank.getTank().getFluid().getRawFluid())) {
                if (outputTank != null) {

                    // Tank fluid amount check + tank cap checks
                    if (fuelTank.getTank().getFluidAmount() >= recipe.getInputAmount() && outputTank.getTank().getFluidAmount() + recipe.getOutputAmount() <= TANK_CAPACITY){
                        // Check for power
                        if (canConsumeEnergy()){
                            if (counter == 1){

                                // Drain Input
                                fuelTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // Output Tank
                                if (outputTank.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                    outputTank.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                inventory.extractItem(3, recipe.ingredientCount,false);

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0){
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(4).copy());
                                length = counter;
                            }
                        } // Energy Check
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                } else counter = 0;
            } else counter = 0;
        } else {
            counter = 0;
        }*/
        //LOGGER.debug("Fluid: " + inputTank.getFluid().getRawFluid().getFilledBucket().getTranslationKey() + " amount: " + inputTank.getFluid().getAmount());
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
    public CompoundTag save(CompoundTag tag) {
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

        return super.save(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createFuelFluidHandler() {
        return this.createFluidHandler(new CombustionGeneratorFuelRecipe(), fuelTank); // TODO: Recipe
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(5) { // TODO: 2 for fluids + 1 for built tool + 2 for components
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot < 2) return stack.getItem() instanceof BucketItem;
                if (slot == 2) return stack.getItem() instanceof Multitool;
                if (slot == 3) return stack.getItem() instanceof BitItem;
                if (slot == 4) return stack.getItem() instanceof MultitoolBase;
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.AQUEOULIZER_MAX_POWER.get(), Config.AQUEOULIZER_TRANSFER.get()); // TODO: Config
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) return handler.cast();
            if (mainToolSlotSM.getStatus() && mainToolSlotSM.getDirection().get3DDataValue() == side.get3DDataValue())
                return mainToolSlotHandler.cast();
            else if (bitSlotSM.getStatus() && bitSlotSM.getDirection().get3DDataValue() == side.get3DDataValue())
                return bitSlotHandler.cast();
            else if (fuelTopSlotSM.getStatus() && fuelTopSlotSM.getDirection().get3DDataValue() == side.get3DDataValue())
                return fuelTopSlotHandler.cast();
            else if (fuelBottomSlotSM.getStatus() && fuelBottomSlotSM.getDirection().get3DDataValue() == side.get3DDataValue())
                return fuelBottomSlotHandler.cast();
            else if (multitoolBaseSM.getStatus() && multitoolBaseSM.getDirection().get3DDataValue() == side.get3DDataValue())
                return multitoolBaseSlotHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if(fuelTank.getSideStatus() && fuelTank.getSideDirection().get3DDataValue() == side.get3DDataValue())
                return fuelFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent(getType().getRegistryName().getPath());
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

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == fuelTopSlotSM.getSlotNum()){
            fuelTopSlotSM.setStatus(status);
        } else if (slotId == fuelBottomSlotSM.getSlotNum()){
            fuelBottomSlotSM.setStatus(status);
        } else if(slotId == mainToolSlotSM.getSlotNum()){
            mainToolSlotSM.setStatus(status);
        } else if(slotId == bitSlotSM.getSlotNum()){
            bitSlotSM.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == fuelTopSlotSM.getSlotNum()){
            fuelTopSlotSM.setDirection(direction);
        } else if (slotId == fuelBottomSlotSM.getSlotNum()){
            fuelBottomSlotSM.setDirection(direction);
        } else if(slotId == mainToolSlotSM.getSlotNum()){
            mainToolSlotSM.setDirection(direction);
        } else if(slotId == bitSlotSM.getSlotNum()){
            bitSlotSM.setDirection(direction);
        }
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
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(fuelTopSlotSM.getStatus(), fuelTopSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(fuelBottomSlotSM.getStatus(), fuelBottomSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(mainToolSlotSM.getStatus(), mainToolSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bitSlotSM.getStatus(), bitSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(multitoolBaseSM.getStatus(), multitoolBaseSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(fuelTank.getSideStatus(), fuelTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(fuelTopSlotSM.getDirection().get3DDataValue(), fuelTopSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(fuelBottomSlotSM.getDirection().get3DDataValue(), fuelBottomSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(mainToolSlotSM.getDirection().get3DDataValue(), mainToolSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bitSlotSM.getDirection().get3DDataValue(), bitSlotSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(multitoolBaseSM.getDirection().get3DDataValue(), multitoolBaseSM.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(fuelTank.getSideDirection().get3DDataValue(), fuelTank.getId()));
                    }
                });
            });
        } else if (!playerUuid.isEmpty()){ // Legacy solution
            double x = this.getBlockPos().getX();
            double y = this.getBlockPos().getY();
            double z = this.getBlockPos().getZ();
            final double radius = 16;
            ResourceKey<Level> worldRegistryKey = this.getLevel().dimension();
            PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x,y,z,radius,worldRegistryKey);

            // Boolean Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(fuelTopSlotSM.getStatus(), fuelTopSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(fuelBottomSlotSM.getStatus(), fuelBottomSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(mainToolSlotSM.getStatus(), mainToolSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(bitSlotSM.getStatus(), bitSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(multitoolBaseSM.getStatus(), multitoolBaseSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(fuelTank.getSideStatus(), fuelTank.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(fuelTopSlotSM.getDirection().get3DDataValue(), fuelTopSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(fuelBottomSlotSM.getDirection().get3DDataValue(), fuelBottomSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(mainToolSlotSM.getDirection().get3DDataValue(), mainToolSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(bitSlotSM.getDirection().get3DDataValue(), bitSlotSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(multitoolBaseSM.getDirection().get3DDataValue(), multitoolBaseSM.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(fuelTank.getSideDirection().get3DDataValue(), fuelTank.getId()));
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