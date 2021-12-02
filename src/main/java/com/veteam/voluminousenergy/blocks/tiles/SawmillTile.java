package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.SawmillContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.SawmillingRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class SawmillTile extends VEFluidTileEntity {

    // Handlers
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(this::createOutputFluidHandler);
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> inputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 1)); // Log Input
    private LazyOptional<IItemHandlerModifiable> plankOutputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 1, 2)); // Plank Output
    private LazyOptional<IItemHandlerModifiable> secondOutputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 2, 3)); // Sawdust/Second output
    private LazyOptional<IItemHandlerModifiable> topBucketSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 3,4)); // Top bucket slot
    private LazyOptional<IItemHandlerModifiable> bottomBucketSlotHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 4,5)); // Bottom bucket slot
    //private LazyOptional<IItemHandlerModifiable> quartzMultiplierHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 5,6)); // Quartz Multiplier slot

    // Slot Managers
    public VESlotManager inputSm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot");
    public VESlotManager plankSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot");
    public VESlotManager secondOutputSm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot");
    public VESlotManager bucketTopSm = new VESlotManager(3, Direction.SOUTH,true,"slot.voluminousenergy.input_slot");
    public VESlotManager bucketBottomSm = new VESlotManager(4, Direction.EAST,true,"slot.voluminousenergy.output_slot");

    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.OUTPUT,0);
    private final FluidStack configuredFluidForNoRecipe = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(Config.SAWMILL_FLUID_LOCATION.get()))), Config.SAWMILL_FLUID_AMOUNT.get());

    private int counter;
    private int length;

    private ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public SawmillTile(BlockPos pos, BlockState state) {
        super(VEBlocks.SAWMILL_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack logInput = inventory.getStackInSlot(0).copy(); // Log input
        //ItemStack plankOutput = inventory.getStackInSlot(1).copy(); // Plank Output
        //ItemStack secondOutput = inventory.getStackInSlot(2).copy(); // Second output
        ItemStack topBucketInput = inventory.getStackInSlot(3).copy(); // Top bucket input slot
        ItemStack bottomBucketOutput = inventory.getStackInSlot(4).copy(); // Bottom bucket output slot

        outputTank.setInput(topBucketInput.copy());
        outputTank.setOutput(bottomBucketOutput.copy());

        if(this.inputFluid(outputTank,3,4)) return;
        if(this.outputFluid(outputTank,3,4)) return;

        // Resolve recipes:
        if (!logInput.isEmpty()){
            SawmillingRecipe sawmillingRecipe = RecipeUtil.getSawmillingRecipeFromLog(level, logInput.copy());
            ItemStack plankOutputStack;
            ItemStack secondOutputStack;

            if (sawmillingRecipe == null && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()){ // Recipe is null, use alternative method if allowed
                plankOutputStack = RecipeUtil.getPlankFromLogParallel(level, logInput.copy()); //RecipeUtil.getPlankFromLogParallel(level, logInput.copy());
                secondOutputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Config.SAWMILL_SECOND_OUTPUT_RESOURCE_LOCATION.get())), Config.SAWMILL_SECOND_OUTPUT_COUNT.get());

                if (plankOutputStack != null){ // Valid Item!
                    if (this.configuredFluidForNoRecipe != null
                        && (outputTank.getTank().getFluidAmount() + Config.SAWMILL_FLUID_AMOUNT.get()) <= TANK_CAPACITY
                        && (inventory.getStackInSlot(1).isEmpty() || inventory.getStackInSlot(1).getItem() == plankOutputStack.getItem())
                        && (inventory.getStackInSlot(2).isEmpty() || inventory.getStackInSlot(2).getItem() == secondOutputStack.getItem())
                        && (inventory.getStackInSlot(1).getCount() + Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get()) <= 64
                        && (inventory.getStackInSlot(2).getCount() + Config.SAWMILL_SECOND_OUTPUT_COUNT.get()) <= 64){

                        if (outputTank.getTank().getFluid().isFluidEqual(this.configuredFluidForNoRecipe.copy()) || outputTank.getTank().getFluid().isEmpty()){
                            plankOutputStack.setCount(Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get());
                            coreTickProcessing(null,
                                    logInput,
                                    plankOutputStack.copy(),
                                    secondOutputStack.copy(),
                                    this.configuredFluidForNoRecipe.copy()
                            );
                        } else {
                            counter = 0;
                        }
                    } else {
                        counter = 0;
                    }
                } else {
                    counter = 0;
                }

            } else { // Using Recipe
                plankOutputStack = sawmillingRecipe.result.copy();
                secondOutputStack = sawmillingRecipe.secondResult.copy();
                FluidStack outputFluid = sawmillingRecipe.getOutputFluid().copy();

                if ((outputTank.getTank().getFluidAmount() + outputFluid.getAmount()) <= TANK_CAPACITY
                        && (inventory.getStackInSlot(1).isEmpty() || inventory.getStackInSlot(1).getItem() == plankOutputStack.getItem())
                        && (inventory.getStackInSlot(2).isEmpty() || inventory.getStackInSlot(2).getItem() == secondOutputStack.getItem())
                        && (inventory.getStackInSlot(1).getCount() + sawmillingRecipe.result.getCount()) <= 64
                        && (inventory.getStackInSlot(2).getCount() + sawmillingRecipe.secondResult.getCount()) <= 64){
                    if (outputTank.getTank().getFluid().isFluidEqual(outputFluid.copy()) || outputTank.getTank().getFluid().isEmpty()){
                        coreTickProcessing(sawmillingRecipe, logInput, plankOutputStack, secondOutputStack, outputFluid);
                    } else {
                        counter = 0;
                    }
                } else {
                    counter = 0;
                }
            }
        } else {
            counter = 0;
        }
    }

    private void coreTickProcessing(@Nullable SawmillingRecipe sawmillingRecipe,
                                    ItemStack logInput,
                                    ItemStack resolvedPlankOutput,
                                    ItemStack secondItemOutput,
                                    FluidStack fluidOutput){
        if (canConsumeEnergy()){
            if (counter == 1){
                // Core processing occurs here

                inventory.extractItem(0, (sawmillingRecipe != null ? sawmillingRecipe.ingredientCount : Config.SAWMILL_LOG_CONSUMPTION_RATE.get()), false); // Extract log

                // Plank output
                ItemStack currentPlankStack = inventory.getStackInSlot(1);
                if (currentPlankStack.getItem() != resolvedPlankOutput.getItem() || currentPlankStack.getItem() == Items.AIR){
                    if (currentPlankStack.getItem() == Items.AIR){
                        currentPlankStack.setCount(1);
                    }
                    inventory.insertItem(1, resolvedPlankOutput.copy(), false);
                } else { // Assuming the recipe output item is already in the output slot
                   // currentPlankStack.setCount(.getCount()); // Simply change the stack to equal the output amount
                    inventory.insertItem(1, resolvedPlankOutput.copy(),false); // Place the new output stack on top of the old one
                }

                // Second output
                ItemStack currentSecondOutput = inventory.getStackInSlot(2);
                if (currentSecondOutput.getItem() != secondItemOutput.getItem() || currentSecondOutput.getItem() == Items.AIR){
                    if (currentSecondOutput.getItem() == Items.AIR){
                        currentSecondOutput.setCount(1);
                    }
                    inventory.insertItem(2, secondItemOutput.copy(), false);
                } else { // Assuming the recipe output item is already in the output slot
                    //currentSecondOutput.setCount(secondItemOutput.getCount()); // Simply change the stack to equal the output amount
                    inventory.insertItem(2, secondItemOutput.copy(),false); // Place the new output stack on top of the old one
                }

                // Output Tank
                if (outputTank.getTank().getFluid().getRawFluid() != fluidOutput.getRawFluid()){
                    outputTank.getTank().setFluid(fluidOutput.copy());
                } else {
                    outputTank.getTank().fill(fluidOutput.copy(), IFluidHandler.FluidAction.EXECUTE);
                }

                counter--;
                consumeEnergy();
                this.setChanged();
            } else if (counter > 0){
                counter--;
                consumeEnergy();
            } else {
                counter = sawmillingRecipe != null
                                ? this.calculateCounter(sawmillingRecipe.getProcessTime(), inventory.getStackInSlot(5).copy())    // Sawmill recipe not null
                                : this.calculateCounter(Config.SAWMILL_PROCESSING_TIME.get(), inventory.getStackInSlot(5).copy());// Use default values when null
                length = counter;
            }
        } else {
            counter = 0;
        }
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.SAWMILL_POWER_USAGE.get(),
                                this.inventory.getStackInSlot(5).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.SAWMILL_POWER_USAGE.get(), this.inventory.getStackInSlot(5).copy());
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
        CompoundTag outputTank = tag.getCompound("outputTank");

        this.outputTank.getTank().readFromNBT(outputTank);

        this.outputTank.readGuiProperties(tag, "output_tank_gui");

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
        CompoundTag outputNBT = new CompoundTag();

        this.outputTank.getTank().writeToNBT(outputNBT);

        tag.put("outputTank", outputNBT);

        this.outputTank.writeGuiProperties(tag, "output_tank_gui");

        return super.save(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
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

    private IFluidHandler createOutputFluidHandler(){
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return outputTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return outputTank.getTank().getTankCapacity(0);
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (!inventory.getStackInSlot(0).isEmpty()){
                    ItemStack logStack = inventory.getStackInSlot(0).copy();
                    SawmillingRecipe sawmillingRecipe = RecipeUtil.getSawmillingRecipeFromLog(level, logStack);

                    if (sawmillingRecipe == null){
                        if (!configuredFluidForNoRecipe.isFluidEqual(stack) || !Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) return false;
                        ItemStack plankStack = RecipeUtil.getPlankFromLogParallel(level, logStack);
                        return plankStack != null && plankStack.isEmpty();
                    } else { // Sawmilling recipe is not null
                        return sawmillingRecipe.getOutputFluid().isFluidEqual(stack);
                    }
                } else if (!inventory.getStackInSlot(1).isEmpty()){
                    ItemStack plankStack = inventory.getStackInSlot(1).copy();
                    SawmillingRecipe sawmillingRecipe = RecipeUtil.getSawmillingRecipeFromPlank(level, plankStack);

                    if (sawmillingRecipe == null){
                        if (!configuredFluidForNoRecipe.isFluidEqual(stack) || !Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) return false;
                        ArrayList<ItemStack> logList = RecipeUtil.getLogFromPlankParallel(level, plankStack);
                        return logList == null ? false : !(logList.isEmpty());
                    } else { // Sawmilling recipe is not null
                        return sawmillingRecipe.getOutputFluid().isFluidEqual(stack);
                    }
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if(isFluidValid(outputTank.getId(),resource) && outputTank.getTank().isEmpty()
                        || resource.isFluidEqual(outputTank.getTank().getFluid())) {
                    return outputTank.getTank().fill(resource, action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                if(resource.isFluidEqual(outputTank.getTank().getFluid())) {
                    return outputTank.getTank().drain(resource,action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if(outputTank.getTank().getFluidAmount() > 0) {
                    if (Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                        return outputTank.getTank().drain(maxDrain, action);
                    } else if (outputTank.getTankType() != TankType.INPUT) {
                        return outputTank.getTank().drain(maxDrain, action);
                    }
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
                if(slot == 0){ // Log inputted into the input slot
                    ItemStack altPlankStack = RecipeUtil.getPlankFromLogParallel(level, stack.copy());
                    return (altPlankStack != null);
                } else if (slot == 1){
                    ArrayList<ItemStack> plankList = RecipeUtil.getLogFromPlankParallel(level, stack.copy());
                    return plankList != null && !plankList.isEmpty();
                } else if (slot == 2){
                    return true; // TODO: better than this
                } else if (slot == 3 || slot == 4) {
                    return stack.getItem() instanceof BucketItem;
                } else if (slot == 5){
                    return stack.getItem().equals(VEItems.QUARTZ_MULTIPLIER); // this is the upgrade slot
                }
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.SAWMILL_MAX_POWER.get(), Config.SAWMILL_TRANSFER.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) return handler.cast();

            if (inputSm.getStatus() && inputSm.getDirection().get3DDataValue() == side.get3DDataValue())
                return inputHandler.cast();
            else if (plankSm.getStatus() && plankSm.getDirection().get3DDataValue() == side.get3DDataValue())
                return plankOutputHandler.cast();
            else if (secondOutputSm.getStatus() && secondOutputSm.getDirection().get3DDataValue() == side.get3DDataValue())
                return secondOutputHandler.cast();
            else if (bucketTopSm.getStatus() && bucketTopSm.getDirection().get3DDataValue() == side.get3DDataValue())
                return topBucketSlotHandler.cast();
            else if (bucketBottomSm.getStatus() && bucketBottomSm.getDirection().get3DDataValue() == side.get3DDataValue())
                return bottomBucketSlotHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if(outputTank.getSideStatus() && outputTank.getSideDirection().get3DDataValue() == side.get3DDataValue())
                return outputFluidHandler.cast();
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
        return new SawmillContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return outputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getOutputTank(){
        return this.outputTank;
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == inputSm.getSlotNum()){
            inputSm.setStatus(status);
        } else if (slotId == plankSm.getSlotNum()){
            plankSm.setStatus(status);
        } else if(slotId == secondOutputSm.getSlotNum()){
            secondOutputSm.setStatus(status);
        } else if(slotId == bucketTopSm.getSlotNum()){
            bucketTopSm.setStatus(status);
        }else if(slotId == bucketBottomSm.getSlotNum()){
            bucketBottomSm.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == inputSm.getSlotNum()){
            inputSm.setDirection(direction);
        } else if (slotId == plankSm.getSlotNum()){
            plankSm.setDirection(direction);
        } else if(slotId == secondOutputSm.getSlotNum()){
            secondOutputSm.setDirection(direction);
        } else if(slotId == bucketTopSm.getSlotNum()){
            bucketTopSm.setDirection(direction);
        } else if(slotId == bucketBottomSm.getSlotNum()){
            bucketBottomSm.setDirection(direction);
        }
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.outputTank.getId()){
            this.outputTank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.outputTank.getId()){
            this.outputTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
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
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(inputSm.getStatus(), inputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(plankSm.getStatus(), plankSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(secondOutputSm.getStatus(), secondOutputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bucketTopSm.getStatus(), bucketTopSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bucketBottomSm.getStatus(), bucketBottomSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(outputTank.getSideStatus(), outputTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(inputSm.getDirection().get3DDataValue(), inputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(plankSm.getDirection().get3DDataValue(), plankSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(secondOutputSm.getDirection().get3DDataValue(), secondOutputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bucketTopSm.getDirection().get3DDataValue(), bucketTopSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bucketBottomSm.getDirection().get3DDataValue(), bucketBottomSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(outputTank.getSideDirection().get3DDataValue(), outputTank.getId()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(inputSm.getStatus(), inputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(plankSm.getStatus(), plankSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(secondOutputSm.getStatus(), secondOutputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(bucketTopSm.getStatus(), bucketTopSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(bucketBottomSm.getStatus(), bucketBottomSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(outputTank.getSideStatus(), outputTank.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(inputSm.getDirection().get3DDataValue(), inputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(plankSm.getDirection().get3DDataValue(), plankSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(secondOutputSm.getDirection().get3DDataValue(), secondOutputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(bucketTopSm.getDirection().get3DDataValue(), bucketTopSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(bucketBottomSm.getDirection().get3DDataValue(), bucketBottomSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(outputTank.getSideDirection().get3DDataValue(), outputTank.getId()));
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
                    if(!(player.containerMenu instanceof SawmillContainer)){
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
