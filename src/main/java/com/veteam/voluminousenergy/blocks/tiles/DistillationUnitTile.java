package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class DistillationUnitTile extends VEFluidTileEntity {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> iTopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,0,1));
    private LazyOptional<IItemHandlerModifiable> iBottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));
    private LazyOptional<IItemHandlerModifiable> o0TopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,2,3));
    private LazyOptional<IItemHandlerModifiable> o0BottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,3,4));
    private LazyOptional<IItemHandlerModifiable> o1TopHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,4,5));
    private LazyOptional<IItemHandlerModifiable> o1BottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 5,6));
    private LazyOptional<IItemHandlerModifiable> o2Handler = LazyOptional.of(() -> new RangedWrapper(this.inventory,6,7));

    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
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

    public final ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public DistillationUnitTile() {
        super(VEBlocks.DISTILLATION_UNIT_TILE);
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
            ItemStack inputFluidStack = new ItemStack(inputTank.getTank().getFluid().getRawFluid().getFilledBucket(),1);
            VEFluidRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(inputFluidStack), world).orElse(null);
            if (recipe != null) {
                if (outputTank0 != null && outputTank1 != null) {

                    // Tank fluid amount check + tank cap checks
                    if (thirdOutput.getCount() < recipe.getResults().get(2).getMaxStackSize() && inputTank.getTank().getFluidAmount()
                            >= recipe.getInputAmount() && outputTank0.getTank().getFluidAmount()
                            + recipe.getOutputAmount() <= tankCapacity && outputTank1.getTank().getFluidAmount()
                            + recipe.getAmounts().get(2) <= tankCapacity){
                        // Check for power
                        if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0) {
                            if (counter == 1){

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // First Output Tank
                                if (outputTank0.getTank().getFluid().getRawFluid() != recipe.getFluids().get(1).getRawFluid()){
                                    outputTank0.getTank().setFluid(recipe.getFluids().get(1));
                                } else {
                                    outputTank0.getTank().fill(recipe.getFluids().get(1), IFluidHandler.FluidAction.EXECUTE);
                                }

                                // Second Output Tank
                                if (outputTank1.getTank().getFluid().getRawFluid() != recipe.getFluids().get(2).getRawFluid()){
                                    outputTank1.getTank().setFluid(recipe.getFluids().get(2));
                                } else {
                                    outputTank1.getTank().fill(recipe.getFluids().get(2), IFluidHandler.FluidAction.EXECUTE);
                                }

                                if (thirdOutput.getItem() != recipe.getResults().get(2).getItem()) {
                                    if (thirdOutput.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                                        thirdOutput.setCount(1);
                                    }
                                    ItemStack recipeStack = new ItemStack(recipe.getResults().get(2).getItem(),recipe.getOutputAmount());
                                    recipeStack.setCount(recipe.getAmounts().get(3));
                                    inventory.insertItem(6,recipeStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    ItemStack recipeStack = new ItemStack(recipe.getResults().get(2).getItem(),recipe.getOutputAmount());
                                    recipeStack.setCount(recipe.getAmounts().get(3)); // Simply change the stack to equal the output amount
                                    inventory.insertItem(6,recipeStack,false); // Place the new output stack on top of the old one
                                }

                                counter--;
                                energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.DISTILLATION_UNIT_POWER_USAGE.get()));
                                this.markDirty();
                            } else if (counter > 0){
                                counter--;
                                energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.DISTILLATION_UNIT_POWER_USAGE.get()));
                            } else {
                                counter = recipe.getProcessTime();
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

    /*
        Read and Write on World save
     */

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));
        // Tanks
        CompoundNBT inputTank = tag.getCompound("inputTank");
        CompoundNBT outputTank0 = tag.getCompound("outputTank0");
        CompoundNBT outputTank1 = tag.getCompound("outputTank1");

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

        super.read(state,tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("energy", compound);
        });

        // Tanks
        CompoundNBT inputNBT = new CompoundNBT();
        CompoundNBT outputNBT0 = new CompoundNBT();
        CompoundNBT outputNBT1 = new CompoundNBT();

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

        return super.write(tag);
    }

    /*
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }
     */

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
        this.read(this.getBlockState(), pkt.getNbtCompound());
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
        return new ItemStackHandler(7) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    VEFluidRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE,new Inventory(stack),world).orElse(null);
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if (slot == 2 || slot == 3 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;

                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        VEFluidRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && stack.getItem() instanceof BucketItem && recipe.getOutputFluid().getRawFluid() == ((BucketItem) stack.getItem()).getFluid() ){
                            recipeHit.set(true);
                        }
                    });
                    return recipeHit.get();
                } else if (slot == 4 || slot == 5 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;
                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        VEFluidRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && stack.getItem() instanceof BucketItem && recipe.getFluids().get(2).getRawFluid() == ((BucketItem) stack.getItem()).getFluid() ){
                            recipeHit.set(true);
                        }
                    });
                    return recipeHit.get();
                } else if (slot == 6){
                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        VEFluidRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && recipe.getResults().get(2).getItem() == stack.getItem()){
                            recipeHit.set(true);
                        }
                    });
                    return recipeHit.get();
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

    private IEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.DISTILLATION_UNIT_MAX_POWER.get(), Config.DISTILLATION_UNIT_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return handler.cast();
            if(iTopManager.getStatus() && iTopManager.getDirection().getIndex() == side.getIndex())
                return iTopHandler.cast();
            else if(iBottomManager.getStatus() && iBottomManager.getDirection().getIndex() == side.getIndex())
                return iBottomHandler.cast();
            else if(o0TopManager.getStatus() && o0TopManager.getDirection().getIndex() == side.getIndex())
                return o0TopHandler.cast();
            else if(o0BottomManager.getStatus() && o0BottomManager.getDirection().getIndex() == side.getIndex())
                return o0BottomHandler.cast();
            else if(o1TopManager.getStatus() && o1TopManager.getDirection().getIndex() == side.getIndex())
                return o1TopHandler.cast();
            else if(o1BottomManager.getStatus() && o1BottomManager.getDirection().getIndex() == side.getIndex())
                return o1BottomHandler.cast();
            else if(o2Manager.getStatus() && o2Manager.getDirection().getIndex() == side.getIndex())
                return o2Handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if(inputTank.getSideStatus() && inputTank.getSideDirection().getIndex() == side.getIndex())
                return inputFluidHandler.cast();
            if(outputTank0.getSideStatus() && outputTank0.getSideDirection().getIndex() == side.getIndex())
                return output0FluidHandler.cast();
            if(outputTank1.getSideStatus() && outputTank1.getSideDirection().getIndex() == side.getIndex())
                return output1FluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new DistillationUnitContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (100 - ((counter * 100) / length))) / 100;
        }
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
        for (final BlockPos blockPos :  BlockPos.getAllInBoxMutable(pos.add(sX,sY,sZ),pos.add(lX,lY,lZ))){
            final BlockState blockState = world.getBlockState(blockPos);

            if (blockState.getBlock() != VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK.getBlock()){ // Fails multiblock condition
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
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
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
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(iTopManager.getDirection().getIndex(),iTopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(iBottomManager.getDirection().getIndex(),iBottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o0TopManager.getDirection().getIndex(),o0TopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o0BottomManager.getDirection().getIndex(),o0BottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o1TopManager.getDirection().getIndex(),o1TopManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o1BottomManager.getDirection().getIndex(),o1BottomManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(o2Manager.getDirection().getIndex(),o2Manager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(inputTank.getSideDirection().getIndex(), inputTank.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(outputTank0.getSideDirection().getIndex(), outputTank0.getId()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(outputTank1.getSideDirection().getIndex(), outputTank1.getId()));
                    }
                });
            });
        } else if (!playerUuid.isEmpty()){ // Legacy solution
            double x = this.getPos().getX();
            double y = this.getPos().getY();
            double z = this.getPos().getZ();
            final double radius = 16;
            RegistryKey<World> worldRegistryKey = this.getWorld().getDimensionKey();
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(iTopManager.getDirection().getIndex(),iTopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(iBottomManager.getDirection().getIndex(),iBottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o0TopManager.getDirection().getIndex(),o0TopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o0BottomManager.getDirection().getIndex(),o0BottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o1TopManager.getDirection().getIndex(),o1TopManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o1BottomManager.getDirection().getIndex(),o1BottomManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(o2Manager.getDirection().getIndex(),o2Manager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(inputTank.getSideDirection().getIndex(), inputTank.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(outputTank0.getSideDirection().getIndex(), outputTank0.getId()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(outputTank1.getSideDirection().getIndex(), outputTank1.getId()));

        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || world == null) return;
        if(world.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            world.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.openContainer != null){
                    if(!(player.openContainer instanceof DistillationUnitContainer)){
                        toRemove.add(player.getUniqueID());
                    }
                } else if (player.openContainer == null){
                    toRemove.add(player.getUniqueID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}