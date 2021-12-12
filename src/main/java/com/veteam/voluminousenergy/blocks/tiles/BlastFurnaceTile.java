package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.BlastFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
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
import net.minecraft.world.item.Items;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class BlastFurnaceTile extends VEFluidTileEntity {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> iTopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,0,1));
    private LazyOptional<IItemHandlerModifiable> iBottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));
    private LazyOptional<IItemHandlerModifiable> firstItemInputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,2,3));
    private LazyOptional<IItemHandlerModifiable> secondItemInputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,3,4));
    private LazyOptional<IItemHandlerModifiable> outputItemHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,4,5));

    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> inputFluidHandler = LazyOptional.of(this::createInputFluidHandler);

    public VESlotManager heatTankItemTopManager = new VESlotManager(0, Direction.UP,false,"slot.voluminousenergy.input_slot");
    public VESlotManager heatTankItemBottomManager = new VESlotManager(1,Direction.DOWN,false,"slot.voluminousenergy.output_slot");
    public VESlotManager firstInputSlotManager = new VESlotManager(2, Direction.EAST, false, "slot.voluminousenergy.input_slot");
    public VESlotManager secondInputSlotManager = new VESlotManager(3, Direction.WEST, false, "slot.voluminousenergy.input_slot");
    public VESlotManager outputSlotManager = new VESlotManager(4, Direction.NORTH, false, "slot.voluminousenergy.output_slot");

    RelationalTank heatTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);

    private int counter;
    private int length;
    private byte tick = 19;
    private boolean validity = false;

    public ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public BlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.BLAST_FURNACE_TILE, pos, state);
    }

    @Deprecated
    public BlastFurnaceTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.BLAST_FURNACE_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();
        tick++;
        if (tick == 20){
            tick = 0;
            validity = isMultiblockValid();
        }
        if (!(validity)) {
            return;
        }

        // Main idea: Heat --> Needs to be "high enough" to work, 2 Item Inputs, 1 item output.
        ItemStack heatTankItemInputTop = inventory.getStackInSlot(0).copy();
        ItemStack heatTankItemInputBottom = inventory.getStackInSlot(1).copy();
        ItemStack firstItemInput = inventory.getStackInSlot(2).copy();
        ItemStack secondItemInput = inventory.getStackInSlot(3).copy();
        ItemStack itemOutput = inventory.getStackInSlot(4).copy();

        heatTank.setIOItemstack(heatTankItemInputTop.copy(),heatTankItemInputBottom.copy());

        if(inputFluid(heatTank,0,1)) return;
        if(this.outputFluid(heatTank,0,1)) return;

        // Main Processing occurs here:
        if (heatTank != null || !heatTank.getTank().isEmpty()) {
            IndustrialBlastingRecipe recipe = RecipeUtil.getIndustrialBlastingRecipe(level, firstItemInput.copy(), secondItemInput.copy());

            if (recipe != null) {
                // Tank fluid amount check + capacity and recipe checks
                if (itemOutput.getCount() < recipe.getResult().getMaxStackSize() &&
                        heatTank.getTank().getFluidAmount() >= Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get() &&
                        heatTank.getTank().getFluid().getRawFluid().getAttributes().getTemperature() >= recipe.getMinimumHeat() &&
                        recipe.getFirstInputAsList().contains(firstItemInput.getItem()) &&
                        firstItemInput.getCount() >= recipe.getIngredientCount() &&
                        recipe.ingredientListIncludingSeconds.contains(secondItemInput.getItem()) &&
                        secondItemInput.getCount() >= recipe.getSecondInputAmount()){
                    // Check for power
                    if (canConsumeEnergy()) {
                        if (counter == 1){

                            // Drain Input
                            heatTank.getTank().drain(Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get(), IFluidHandler.FluidAction.EXECUTE);

                            inventory.extractItem(2,recipe.getIngredientCount(),false);
                            inventory.extractItem(3, recipe.getSecondInputAmount(),false);

                            // Place the new output stack on top of the old one
                            if (itemOutput.getItem() != recipe.getResult().getItem()) {
                                if (itemOutput.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                                    itemOutput.setCount(1);
                                }
                            }
                            inventory.insertItem(4, recipe.getResult().copy(),false); // CRASH the game if this is not empty!

                            counter--;
                            consumeEnergy();
                            this.setChanged();
                        } else if (counter > 0){
                            counter--;
                            consumeEnergy();
                        } else {
                            counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(5).copy());
                            length = counter;
                        }
                    } // Energy Check
                } else { // Set counter to zero
                    counter = 0;
                }
            }
        }

    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.BLAST_FURNACE_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(5).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.BLAST_FURNACE_POWER_USAGE.get(), this.inventory.getStackInSlot(5).copy());
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
        CompoundTag heatTank = tag.getCompound("heatTank");

        this.heatTank.getTank().readFromNBT(heatTank);
        this.heatTank.readGuiProperties(tag, "heat_tank_gui");

        this.heatTankItemTopManager.read(tag, "heat_top_manager");
        this.heatTankItemBottomManager.read(tag, "heat_bottom_manager");

        this.firstInputSlotManager.read(tag, "first_input_manager");
        this.secondInputSlotManager.read(tag, "second_input_manager");
        this.outputSlotManager.read(tag, "output_manager");

        this.validity = tag.getBoolean("validity");

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
        CompoundTag heatTankNBT = new CompoundTag();

        this.heatTank.getTank().writeToNBT(heatTankNBT);

        tag.put("heatTank", heatTankNBT);

        this.heatTank.writeGuiProperties(tag, "heat_tank_gui");

        this.heatTankItemTopManager.write(tag, "heat_top_manager");
        this.heatTankItemBottomManager.write(tag, "heat_bottom_manager");

        this.firstInputSlotManager.write(tag, "first_input_manager");
        this.secondInputSlotManager.write(tag, "second_input_manager");
        this.outputSlotManager.write(tag, "output_manager");

        tag.putBoolean("validity", this.validity);
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

    private IFluidHandler createInputFluidHandler() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return heatTank == null ? FluidStack.EMPTY : heatTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return heatTank == null ? 0 : heatTank.getTank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return heatTank != null && heatTank.getTank().isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && heatTank.getTank().isEmpty() || resource.isFluidEqual(heatTank.getTank().getFluid())) {
                    return heatTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(heatTank.getTank().getFluid())) {
                    return heatTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (heatTank.getTank().getFluidAmount() > 0) {
                    return heatTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    return stack.getItem() instanceof BucketItem || stack.getItem() == Items.BUCKET;
                } else if (slot == 2) {
                    return RecipeUtil.isFirstIngredientForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 3) {
                    return RecipeUtil.isSecondIngredientForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 4) {
                    return RecipeUtil.isAnOutputForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 5){
                    return stack.getItem().equals(VEItems.QUARTZ_MULTIPLIER);
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

    private VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.BLAST_FURNACE_MAX_POWER.get(), Config.BLAST_FURNACE_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return handler.cast();
            if(heatTankItemTopManager.getStatus() && heatTankItemTopManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return iTopHandler.cast();
            else if(heatTankItemBottomManager.getStatus() && heatTankItemBottomManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return iBottomHandler.cast();
            else if (firstInputSlotManager.getStatus() && firstInputSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return firstItemInputHandler.cast();
            else if (secondInputSlotManager.getStatus() && secondInputSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return secondItemInputHandler.cast();
            else if (outputSlotManager.getStatus() && outputSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return outputItemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && heatTank.getSideStatus()){
            if (side != null){
                if (heatTank.getSideDirection().get3DDataValue() == side.get3DDataValue()) return inputFluidHandler.cast();
            } else { // TODO: Consider Config/Better NULL side handling
                return inputFluidHandler.cast();
            }
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
        return new BlastFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return heatTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public boolean isMultiblockValid (){
        // Get Direction
        String direction = getDirection();
        // Setup range to check based on direction
        byte sX, sY, sZ, lX, lY, lZ;

        if (direction == null || direction.equals("null")){
            return false;
        } else if (direction.equals("north")){
            sX = -1;
            sY = 0;
            sZ = 1;
            lX = 1;
            lY = 2;
            lZ = 3;
        } else if (direction.equals("south")){
            sX = -1;
            sY = 0;
            sZ = -1;
            lX = 1;
            lY = 2;
            lZ = -3;
        } else if (direction.equals("east")){
            sX = -1;
            sY = 0;
            sZ = 1;
            lX = -3;
            lY = 2;
            lZ = -1;
        } else if (direction.equals("west")){
            sX = 1;
            sY = 0;
            sZ = -1;
            lX = 3;
            lY = 2;
            lZ = 1;
        } else { // Invalid Direction
            return false;
        }

        // Tweak box based on direction -- This is the search range to ensure this is a valid multiblock before operation
        for (final BlockPos blockPos :  BlockPos.betweenClosed(worldPosition.offset(sX,sY,sZ),worldPosition.offset(lX,lY,lZ))){
            final BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getBlock() != VEBlocks.TITANIUM_MACHINE_CASING_BLOCK){ // Fails multiblock condition
                return false;
            }
        }
        return true;
    }

    public boolean getMultiblockValidity(){
        return validity;
    }

    public RelationalTank getHeatTank(){
        return this.heatTank;
    }

    public int getTemperatureKelvin(){
        return this.heatTank.getTank().getFluid().getRawFluid().getAttributes().getTemperature();
    }

    public int getTemperatureCelsius(){
        return getTemperatureKelvin()-273;
    }

    public int getTemperatureFahrenheit(){
        return (int) ((getTemperatureKelvin()-273) * 1.8)+32;
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == heatTankItemTopManager.getSlotNum()) heatTankItemTopManager.setStatus(status);
        else if (slotId == heatTankItemBottomManager.getSlotNum()) heatTankItemTopManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == heatTankItemTopManager.getSlotNum()) heatTankItemTopManager.setDirection(direction);
        else if (slotId == heatTankItemBottomManager.getSlotNum()) heatTankItemBottomManager.setDirection(direction);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.heatTank.getId()) this.heatTank.setSideStatus(status);
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.heatTank.getId())
            this.heatTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        // Heat Tank Boolean and Tank Packets
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(heatTankItemTopManager.getStatus(), heatTankItemTopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(heatTankItemBottomManager.getStatus(), heatTankItemBottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(heatTank.getSideStatus(), heatTank.getId()));

                        // Input and Output Slot boolean buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(firstInputSlotManager.getStatus(), firstInputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(secondInputSlotManager.getStatus(), secondInputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(outputSlotManager.getStatus(), outputSlotManager.getSlotNum()));

                        // Tank Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(heatTankItemTopManager.getDirection().get3DDataValue(), heatTankItemTopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(heatTankItemBottomManager.getDirection().get3DDataValue(), heatTankItemBottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(heatTank.getSideDirection().get3DDataValue(), heatTank.getId()));

                        // Input and Output Slot Direction buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(firstInputSlotManager.getDirection().get3DDataValue(), firstInputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(secondInputSlotManager.getDirection().get3DDataValue(), secondInputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(outputSlotManager.getDirection().get3DDataValue(), outputSlotManager.getSlotNum()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(heatTankItemTopManager.getStatus(), heatTankItemTopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(heatTankItemBottomManager.getStatus(), heatTankItemBottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(heatTank.getSideStatus(), heatTank.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(heatTankItemTopManager.getDirection().get3DDataValue(), heatTankItemTopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(heatTankItemBottomManager.getDirection().get3DDataValue(), heatTankItemBottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(heatTank.getSideDirection().get3DDataValue(), heatTank.getId()));
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
                    if(!(player.containerMenu instanceof BlastFurnaceContainer)){
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