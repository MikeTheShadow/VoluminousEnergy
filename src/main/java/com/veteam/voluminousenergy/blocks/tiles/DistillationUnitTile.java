package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
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
import net.minecraft.world.SimpleContainer;
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

public class DistillationUnitTile extends VEFluidTileEntity {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> iTopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,0,1));
    private LazyOptional<IItemHandlerModifiable> iBottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));
    private LazyOptional<IItemHandlerModifiable> o0TopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,2,3));
    private LazyOptional<IItemHandlerModifiable> o0BottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,3,4));
    private LazyOptional<IItemHandlerModifiable> o1TopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,4,5));
    private LazyOptional<IItemHandlerModifiable> o1BottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 5,6));
    private LazyOptional<IItemHandlerModifiable> o2Handler = LazyOptional.of(() -> new RangedWrapper(this.inventory,6,7));

    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> inputFluidHandler = LazyOptional.of(this::createInputFluidHandler);
    private LazyOptional<IFluidHandler> output0FluidHandler = LazyOptional.of(this::createOutput0FluidHandler);
    private LazyOptional<IFluidHandler> output1FluidHandler = LazyOptional.of(this::createOutput1FluidHandler);

    public VESlotManager iTopManager = new VESlotManager(0,Direction.UP,false,"slot.voluminousenergy.input_slot");
    public VESlotManager iBottomManager = new VESlotManager(1,Direction.DOWN,false,"slot.voluminousenergy.output_slot");
    public VESlotManager o0TopManager = new VESlotManager(2,Direction.UP,false,"slot.voluminousenergy.input_slot");
    public VESlotManager o0BottomManager = new VESlotManager(3,Direction.DOWN,false,"slot.voluminousenergy.output_slot");
    public VESlotManager o1TopManager = new VESlotManager(4,Direction.UP,false,"slot.voluminousenergy.input_slot");
    public VESlotManager o1BottomManager = new VESlotManager(5,Direction.DOWN,false,"slot.voluminousenergy.output_slot");
    public VESlotManager o2Manager = new VESlotManager(6,Direction.DOWN,false,"slot.voluminousenergy.output_slot");

    private int tankCapacity = 4000;

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);
    RelationalTank outputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY),1,null,null, TankType.OUTPUT,0);
    RelationalTank outputTank1 = new RelationalTank(new FluidTank(TANK_CAPACITY), 2, null, null, TankType.OUTPUT, 1);

    private int counter;
    private int length;
    private byte tick = 19;
    private boolean validity = false;

    public ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public DistillationUnitTile(BlockPos pos, BlockState state) {
        super(VEBlocks.DISTILLATION_UNIT_TILE, pos, state);
    }

    @Deprecated
    public DistillationUnitTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.DISTILLATION_UNIT_TILE, pos, state);
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
        ItemStack inputTop = inventory.getStackInSlot(0).copy();
        ItemStack inputBottom = inventory.getStackInSlot(1).copy();
        ItemStack firstOutputTop = inventory.getStackInSlot(2).copy();
        ItemStack firstOutputBottom = inventory.getStackInSlot(3).copy();
        ItemStack secondOutputTop = inventory.getStackInSlot(4).copy();
        ItemStack secondOutputBottom = inventory.getStackInSlot(5).copy();
        ItemStack thirdOutput = inventory.getStackInSlot(6).copy();

        inputTank.setIOItemstack(inputTop.copy(),inputBottom.copy());
        outputTank0.setIOItemstack(firstOutputTop.copy(),firstOutputBottom.copy());
        outputTank1.setIOItemstack(secondOutputTop.copy(),secondOutputBottom.copy());

        if(inputFluid(inputTank,0,1)) return;
        if(this.outputFluid(inputTank,0,1)) return;

        if(this.inputFluid(outputTank0,2,3)) return;
        if(this.outputFluid(outputTank0,2,3)) return;

        if(this.inputFluid(outputTank1,4,5)) return;
        if(this.outputFluid(outputTank1,4,5)) return;

        // Main Fluid Processing occurs here:
        if (inputTank != null || !inputTank.getTank().isEmpty()) {
            VEFluidRecipe recipe = RecipeUtil.getDistillationRecipe(level, inputTank.getTank().getFluid());

            if (recipe != null) {
                if (outputTank0 != null && outputTank1 != null) {

                    // Tank fluid amount check + tank cap checks
                    if (thirdOutput.getCount() < ((DistillationRecipe)recipe).getThirdResult().getMaxStackSize() && inputTank.getTank().getFluidAmount()
                            >= recipe.getInputAmount() && outputTank0.getTank().getFluidAmount()
                            + recipe.getOutputAmount() <= tankCapacity && outputTank1.getTank().getFluidAmount()
                            + ((DistillationRecipe)recipe).getSecondFluid().getAmount() <= tankCapacity){
                        // Check for power
                        if (canConsumeEnergy()) {
                            if (counter == 1){

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // First Output Tank
                                if (outputTank0.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                    outputTank0.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank0.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                // Second Output Tank
                                if (outputTank1.getTank().getFluid().getRawFluid() != ((DistillationRecipe)recipe).getSecondResult().getRawFluid()){
                                    outputTank1.getTank().setFluid(((DistillationRecipe)recipe).getSecondFluid().copy());
                                } else {
                                    outputTank1.getTank().fill(((DistillationRecipe)recipe).getSecondFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                if (thirdOutput.getItem() != ((DistillationRecipe)recipe).getThirdResult().getItem()) {
                                    if (thirdOutput.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                                        thirdOutput.setCount(1);
                                    }
                                    inventory.insertItem(6,((DistillationRecipe)recipe).getThirdResult().copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    inventory.insertItem(6,((DistillationRecipe)recipe).getThirdResult().copy(),false); // Place the new output stack on top of the old one
                                }

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0){
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(7).copy());
                                length = counter;
                            }
                        } // Energy Check
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                }
            }
        }

        // End of item handler

    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> ((VEEnergyStorage)e)
                .consumeEnergy(this.consumptionMultiplier(Config.DISTILLATION_UNIT_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(7).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.DISTILLATION_UNIT_POWER_USAGE.get(), this.inventory.getStackInSlot(7).copy());
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
        CompoundTag inputTank = tag.getCompound("inputTank");
        CompoundTag outputTank0 = tag.getCompound("outputTank0");
        CompoundTag outputTank1 = tag.getCompound("outputTank1");

        this.inputTank.getTank().readFromNBT(inputTank);
        this.outputTank0.getTank().readFromNBT(outputTank0);
        this.outputTank1.getTank().readFromNBT(outputTank1);

        this.inputTank.readGuiProperties(tag, "input_tank_gui");
        this.outputTank0.readGuiProperties(tag, "output_tank_0_gui");
        this.outputTank1.readGuiProperties(tag, "output_tank_1_gui");

        this.iTopManager.read(tag, "i_top_manager");
        this.iBottomManager.read(tag, "i_bottom_manager");
        this.o0TopManager.read(tag, "o_0_top_manager");
        this.o0BottomManager.read(tag, "o_0_bottom_manager");
        this.o1TopManager.read(tag, "o_1_top_manager");
        this.o1BottomManager.read(tag, "o_1_bottom_manager");
        this.o2Manager.read(tag, "o_2_manager");

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
        CompoundTag inputNBT = new CompoundTag();
        CompoundTag outputNBT0 = new CompoundTag();
        CompoundTag outputNBT1 = new CompoundTag();

        this.inputTank.getTank().writeToNBT(inputNBT);
        this.outputTank0.getTank().writeToNBT(outputNBT0);
        this.outputTank1.getTank().writeToNBT(outputNBT1);

        tag.put("inputTank", inputNBT);
        tag.put("outputTank0", outputNBT0);
        tag.put("outputTank1", outputNBT1);

        this.inputTank.writeGuiProperties(tag, "input_tank_gui");
        this.outputTank0.writeGuiProperties(tag, "output_tank_0_gui");
        this.outputTank1.writeGuiProperties(tag, "output_tank_1_gui");

        this.iTopManager.write(tag, "i_top_manager");
        this.iBottomManager.write(tag, "i_bottom_manager");
        this.o0TopManager.write(tag, "o_0_top_manager");
        this.o0BottomManager.write(tag, "o_0_bottom_manager");
        this.o1TopManager.write(tag, "o_1_top_manager");
        this.o1BottomManager.write(tag, "o_1_bottom_manager");
        this.o2Manager.write(tag, "o_2_manager");

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
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createInputFluidHandler() {
        return this.createFluidHandler(new DistillationRecipe(), inputTank);
    }

    private IFluidHandler createOutput0FluidHandler(){
        return this.createFluidHandler(new DistillationRecipe(), outputTank0);
    }

    private IFluidHandler createOutput1FluidHandler(){
        return this.createFluidHandler(new DistillationRecipe(), outputTank1);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(8) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(DistillationRecipe.RECIPE_TYPE,new SimpleContainer(stack),level).orElse(null);
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if (slot == 2 || slot == 3 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;

                    return RecipeUtil.getDistillationRecipeFromResult(level, new FluidStack(((BucketItem) stack.getItem()).getFluid(), 1000)) != null;
                } else if (slot == 4 || slot == 5 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;

                    return RecipeUtil.getDistillationRecipeFromSecondResult(level, new FluidStack(((BucketItem) stack.getItem()).getFluid(), 1000)) != null;
                } else if (slot == 6){
                    return RecipeUtil.getDistillationRecipeFromThirdResult(level, stack) != null;
                } else if (slot == 7){
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
        return new VEEnergyStorage(Config.DISTILLATION_UNIT_MAX_POWER.get(), Config.DISTILLATION_UNIT_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return handler.cast();
            if(iTopManager.getStatus() && iTopManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return iTopHandler.cast();
            else if(iBottomManager.getStatus() && iBottomManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return iBottomHandler.cast();
            else if(o0TopManager.getStatus() && o0TopManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return o0TopHandler.cast();
            else if(o0BottomManager.getStatus() && o0BottomManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return o0BottomHandler.cast();
            else if(o1TopManager.getStatus() && o1TopManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return o1TopHandler.cast();
            else if(o1BottomManager.getStatus() && o1BottomManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return o1BottomHandler.cast();
            else if(o2Manager.getStatus() && o2Manager.getDirection().get3DDataValue() == side.get3DDataValue())
                return o2Handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null){ // TODO: Better Handle Null direction
            if(inputTank.getSideStatus() && inputTank.getSideDirection().get3DDataValue() == side.get3DDataValue())
                return inputFluidHandler.cast();
            if(outputTank0.getSideStatus() && outputTank0.getSideDirection().get3DDataValue() == side.get3DDataValue())
                return output0FluidHandler.cast();
            if(outputTank1.getSideStatus() && outputTank1.getSideDirection().get3DDataValue() == side.get3DDataValue())
                return output1FluidHandler.cast();
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
        return new DistillationUnitContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return inputTank.getTank().getFluid();
        } else if (num == 1){
            return outputTank0.getTank().getFluid();
        } else if (num == 2){
            return outputTank1.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return tankCapacity;
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

            if (blockState.getBlock() != VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK){ // Fails multiblock condition
                return false;
            }
        }
        return true;
    }

    public boolean getMultiblockValidity(){
        return validity;
    }

    public RelationalTank getInputTank(){
        return this.inputTank;
    }

    public RelationalTank getOutputTank0(){
        return this.outputTank0;
    }

    public RelationalTank getOutputTank1(){
        return this.outputTank1;
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == iTopManager.getSlotNum()) iTopManager.setStatus(status);
        else if (slotId == iBottomManager.getSlotNum()) iTopManager.setStatus(status);
        else if(slotId == o0TopManager.getSlotNum()) o0TopManager.setStatus(status);
        else if(slotId == o0BottomManager.getSlotNum()) o0BottomManager.setStatus(status);
        else if(slotId == o1TopManager.getSlotNum()) o1TopManager.setStatus(status);
        else if(slotId == o1BottomManager.getSlotNum()) o1BottomManager.setStatus(status);
        else if(slotId == o2Manager.getSlotNum()) o2Manager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == iTopManager.getSlotNum()) iTopManager.setDirection(direction);
        else if (slotId == iBottomManager.getSlotNum()) iBottomManager.setDirection(direction);
        else if(slotId == o0TopManager.getSlotNum()) o0TopManager.setDirection(direction);
        else if(slotId == o0BottomManager.getSlotNum()) o0BottomManager.setDirection(direction);
        else if(slotId == o1TopManager.getSlotNum()) o1TopManager.setDirection(direction);
        else if(slotId == o1BottomManager.getSlotNum()) o1BottomManager.setDirection(direction);
        else if(slotId == o2Manager.getSlotNum()) o2Manager.setDirection(direction);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.inputTank.getId()) this.inputTank.setSideStatus(status);
        else if(id == this.outputTank0.getId()) this.outputTank0.setSideStatus(status);
        else if(id == this.outputTank1.getId()) this.outputTank1.setSideStatus(status);
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.inputTank.getId())
            this.inputTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        else if(id == this.outputTank0.getId())
            this.outputTank0.setSideDirection(IntToDirection.IntegerToDirection(direction));
        else if(id == this.outputTank1.getId())
            this.outputTank1.setSideDirection(IntToDirection.IntegerToDirection(direction));
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(iTopManager.getStatus(), iTopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(iBottomManager.getStatus(), iBottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(o0TopManager.getStatus(), o0TopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(o0BottomManager.getStatus(), o0BottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(o1TopManager.getStatus(), o1TopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(o1BottomManager.getStatus(), o1BottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(o2Manager.getStatus(), o2Manager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(inputTank.getSideStatus(), inputTank.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(outputTank0.getSideStatus(), outputTank0.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(outputTank1.getSideStatus(), outputTank1.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(iTopManager.getDirection().get3DDataValue(),iTopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(iBottomManager.getDirection().get3DDataValue(),iBottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o0TopManager.getDirection().get3DDataValue(),o0TopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o0BottomManager.getDirection().get3DDataValue(),o0BottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o1TopManager.getDirection().get3DDataValue(),o1TopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o1BottomManager.getDirection().get3DDataValue(),o1BottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o2Manager.getDirection().get3DDataValue(),o2Manager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(inputTank.getSideDirection().get3DDataValue(), inputTank.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(outputTank0.getSideDirection().get3DDataValue(), outputTank0.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(outputTank1.getSideDirection().get3DDataValue(), outputTank1.getId()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(iTopManager.getStatus(), iTopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(iBottomManager.getStatus(), iBottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(o0TopManager.getStatus(), o0TopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(o0BottomManager.getStatus(), o0BottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(o1TopManager.getStatus(), o1TopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(o1BottomManager.getStatus(), o1BottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(o2Manager.getStatus(), o2Manager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(inputTank.getSideStatus(), inputTank.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(outputTank0.getSideStatus(), outputTank0.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(outputTank1.getSideStatus(), outputTank1.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(iTopManager.getDirection().get3DDataValue(),iTopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(iBottomManager.getDirection().get3DDataValue(),iBottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o0TopManager.getDirection().get3DDataValue(),o0TopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o0BottomManager.getDirection().get3DDataValue(),o0BottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o1TopManager.getDirection().get3DDataValue(),o1TopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o1BottomManager.getDirection().get3DDataValue(),o1BottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o2Manager.getDirection().get3DDataValue(),o2Manager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(inputTank.getSideDirection().get3DDataValue(), inputTank.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(outputTank0.getSideDirection().get3DDataValue(), outputTank0.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(outputTank1.getSideDirection().get3DDataValue(), outputTank1.getId()));
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
                    if(!(player.containerMenu instanceof DistillationUnitContainer)){
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