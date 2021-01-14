package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.blocks.containers.GasFiredFurnaceContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.w3c.dom.ranges.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class GasFiredFurnaceTile extends VEFluidTileEntity {

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> bucketInputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,0,1));
    private LazyOptional<IItemHandlerModifiable> bucketOutputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));
    private LazyOptional<IItemHandlerModifiable> furnaceInputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,2,3));
    private LazyOptional<IItemHandlerModifiable> furnaceOutputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,3,4));
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    public VESlotManager bucketInputSm = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot");
    public VESlotManager bucketOutputSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot");
    public VESlotManager furnaceInputSm = new VESlotManager(2, Direction.EAST, true, "slot.voluminousenergy.input_slot");
    public VESlotManager furnaceOutputSm = new VESlotManager(3, Direction.WEST,true,"slot.voluminousenergy.output_slot");

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);

    private int fuelCounter;
    private int fuelLength;
    private int counter;
    private int length;

    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public GasFiredFurnaceTile() {
        super(VEBlocks.GAS_FIRED_FURNACE_TILE);
    }

    public final ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        ItemStack bucketInput = inventory.getStackInSlot(0).copy();
        ItemStack bucketOutput = inventory.getStackInSlot(1).copy();
        ItemStack furnaceInput = inventory.getStackInSlot(2).copy();
        ItemStack furnaceOutput = inventory.getStackInSlot(3).copy();

        fuelTank.setInput(bucketInput.copy());
        fuelTank.setOutput(bucketOutput.copy());

        if(this.inputFluid(fuelTank,0,1)) return;
        if(this.outputFluid(fuelTank,0,1)) return;

        inputItemStack.set(furnaceInput.copy()); // Atomic Reference, use this to query recipes FOR OUTPUT SLOT

        // Main Processing occurs here
        if (fuelTank.getTank() != null && !fuelTank.getTank().isEmpty()) {
            FurnaceRecipe furnaceRecipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(furnaceInput.copy()), world).orElse(null);
            BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(furnaceInput.copy()), world).orElse(null);

            if ((furnaceRecipe != null || blastingRecipe != null) && countChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy()) && itemChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy())){
                if (counter == 1) {
                    //LOGGER.debug("What is in the output slot? " + furnaceOutput);
                    // Extract item
                    inventory.extractItem(2,1,false);

                    // Set output based on recipe
                    ItemStack newOutputStack;
                    if (furnaceRecipe != null) {
                        newOutputStack = furnaceRecipe.getRecipeOutput().copy();
                    } else {
                        newOutputStack = blastingRecipe.getRecipeOutput().copy();
                    }
                    //LOGGER.debug("NewOutputStack: " + newOutputStack);

                    // Output Item
                    if (furnaceOutput.getItem() != newOutputStack.getItem() || furnaceOutput.getItem() == Items.AIR) {
                        //LOGGER.debug("The output is not equal to the new output Stack");
                        if(furnaceOutput.getItem() == Items.AIR){ // Fix air >1 jamming slots
                            furnaceOutput.setCount(1);
                        }
                        if (furnaceRecipe != null){
                            newOutputStack.setCount(furnaceRecipe.getRecipeOutput().getCount());
                        } else {
                            newOutputStack.setCount(blastingRecipe.getRecipeOutput().getCount());
                        }
                        //LOGGER.debug("About to insert in pt1: " + newOutputStack);
                        inventory.insertItem(3, newOutputStack.copy(),false); // CRASH the game if this is not empty!

                    } else { // Assuming the recipe output item is already in the output slot
                        // Simply change the stack to equal the output amount
                        if (furnaceRecipe != null){
                            furnaceOutput.setCount(furnaceRecipe.getRecipeOutput().getCount());
                        } else {
                            furnaceOutput.setCount(blastingRecipe.getRecipeOutput().getCount());
                        }
                        //LOGGER.debug("About to insert in pt2: " + furnaceOutput);
                        inventory.insertItem(3, furnaceOutput.copy(),false); // Place the new output stack on top of the old one
                    }

                    counter--;
                    this.markDirty();
                } else if (counter > 0) {
                    counter--;
                } else {
                    counter = 200;
                    length = counter;
                }

                // Fuel Management
                if (fuelCounter == 1){
                    fuelCounter--;
                } else if (fuelCounter > 0){
                    fuelCounter--;
                } else {
                    VEFluidRecipe recipe = world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE, new Inventory(new ItemStack (fuelTank.getTank().getFluid().getRawFluid().getFilledBucket(), 1)), world).orElse(null);
                    if (recipe != null){
                        // Drain Input
                        fuelTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
                        fuelCounter = recipe.getProcessTime()/4;
                        fuelLength = fuelCounter;
                        this.markDirty();
                    }
                }

            } else counter = 0;


        } else counter = 0;
    }

    /* Read and Write on World save */

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);

        // Tanks
        CompoundNBT tankNbt = tag.getCompound("fuel_tank");
        this.fuelTank.getTank().readFromNBT(tankNbt);

        counter = tag.getInt("counter");
        length = tag.getInt("length");
        fuelCounter = tag.getInt("fuel_counter");
        fuelLength = tag.getInt("fuel_length");

        this.fuelTank.readGuiProperties(tag, "fuel_tank_gui");
        this.bucketInputSm.read(tag, "bucket_input_gui");
        this.bucketOutputSm.read(tag, "bucket_output_gui");
        this.furnaceInputSm.read(tag, "furnace_input_gui");
        this.furnaceOutputSm.read(tag, "furnace_output_gui");

        super.read(state,tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });

        // Tanks
        CompoundNBT tankNbt = new CompoundNBT();
        this.fuelTank.getTank().writeToNBT(tankNbt);
        tag.put("fuel_tank", tankNbt);

        tag.putInt("counter", counter);
        tag.putInt("length", length);
        tag.putInt("fuel_counter", fuelCounter);
        tag.putInt("fuel_length", fuelLength);

        this.fuelTank.writeGuiProperties(tag, "fuel_tank_gui");
        this.bucketInputSm.write(tag, "bucket_input_gui");
        this.bucketOutputSm.write(tag, "bucket_output_gui");
        this.furnaceInputSm.write(tag, "furnace_input_gui");
        this.furnaceOutputSm.write(tag, "furnace_output_gui");

        return super.write(tag);
    }


    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }


    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createFluid() {
        return createFluidHandler(new CombustionGeneratorFuelRecipe(), fuelTank);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1){
                    return world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE, new Inventory(stack),world).orElse(null) != null
                            || stack.getItem() == Items.BUCKET;
                } else if (slot == 2) {
                    return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world).orElse(null) != null
                            || world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(stack), world).orElse(null) != null;
                } else if (slot == 3) {
                    FurnaceRecipe furnaceRecipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(inputItemStack.get()), world).orElse(null);
                    BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(inputItemStack.get()), world).orElse(null);

                    // If both recipes are null, then don't bother
                    if (blastingRecipe == null && furnaceRecipe == null) return false;

                    if (furnaceRecipe != null) {
                        return stack.getItem() == furnaceRecipe.getRecipeOutput().getItem();
                    }

                    return stack.getItem() == blastingRecipe.getRecipeOutput().getItem();
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!

                if (slot == 0 || slot == 1) {
                    return super.insertItem(slot, stack, simulate);
                }

                if (slot == 2){
                    ItemStack referenceStack = stack.copy();
                    referenceStack.setCount(64);
                    FurnaceRecipe recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(referenceStack), world).orElse(null);
                    BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(referenceStack),world).orElse(null);

                    if (recipe != null || blastingRecipe != null){
                        return super.insertItem(slot, stack, simulate);
                    }

                } else if (slot == 3){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if (side == null)
                return handler.cast();
            if (bucketInputSm.getStatus() && bucketInputSm.getDirection().getIndex() == side.getIndex())
                return bucketInputHandler.cast();
            else if (bucketOutputSm.getStatus() && bucketOutputSm.getDirection().getIndex() == side.getIndex())
                return bucketOutputHandler.cast();
            else if (furnaceInputSm.getStatus() && furnaceInputSm.getDirection().getIndex() == side.getIndex())
                return furnaceInputHandler.cast();
            else if (furnaceOutputSm.getStatus() && furnaceOutputSm.getDirection().getIndex() == side.getIndex())
                return furnaceOutputHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fuelTank.getSideStatus() && fuelTank.getSideDirection().getIndex() == side.getIndex()){
                return fluid.cast();
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
        return new GasFiredFurnaceContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (100 - ((counter * 100) / length))) / 100;
        }
    }

    public int progressFuelCounterPX(int px) {
        if (fuelCounter == 0){
            return 0;
        } else {
            return (px*(((fuelCounter*100)/fuelLength)))/100;
        }
    }

    @Deprecated // Use method that doesn't take in an int instead
    public FluidStack getFluidStackFromTank(int num){
        if (num == 0) {
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getFluidFromTank(){
        return fuelTank.getTank().getFluid();
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    public int getFuelCounter(){return fuelCounter;}

    public int getCounter(){return counter;}


    public int progressFuelCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)fuelCounter/(float)fuelLength)*100));
        } else {
            return 0;
        }
    }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    public boolean countChecker(FurnaceRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack){
        if(furnaceRecipe != null){
            return (itemStack.getCount() + furnaceRecipe.getRecipeOutput().getCount()) <= 64;
        } else if (blastingRecipe != null){
            return (itemStack.getCount() + blastingRecipe.getRecipeOutput().getCount()) <= 64;
        }
        return false;
    }

    public boolean itemChecker(FurnaceRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack){
        if(furnaceRecipe != null){
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return furnaceRecipe.getRecipeOutput().getItem() == itemStack.getItem();
        } else if (blastingRecipe != null){
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return blastingRecipe.getRecipeOutput().getItem() == itemStack.getItem();
        }
        return false;
    }

    public RelationalTank getFuelTank() {
        return fuelTank;
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == furnaceInputSm.getSlotNum()) furnaceInputSm.setStatus(status);
        else if (slotId == furnaceOutputSm.getSlotNum()) furnaceOutputSm.setStatus(status);
        else if(slotId == bucketInputSm.getSlotNum()) bucketInputSm.setStatus(status);
        else if(slotId == bucketOutputSm.getSlotNum()) bucketOutputSm.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == furnaceInputSm.getSlotNum()) furnaceInputSm.setDirection(direction);
        else if (slotId == furnaceOutputSm.getSlotNum()) furnaceOutputSm.setDirection(direction);
        else if(slotId == bucketInputSm.getSlotNum()) bucketInputSm.setDirection(direction);
        else if(slotId == bucketOutputSm.getSlotNum()) bucketOutputSm.setDirection(direction);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.fuelTank.getId()) this.fuelTank.setSideStatus(status);
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.fuelTank.getId()) this.fuelTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
    }

    @Override
    public void sendPacketToClient(){
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bucketInputSm.getStatus(), bucketInputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bucketOutputSm.getStatus(), bucketOutputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(furnaceInputSm.getStatus(), furnaceInputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(furnaceOutputSm.getStatus(), furnaceOutputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(fuelTank.getSideStatus(), fuelTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bucketInputSm.getDirection().getIndex(),bucketInputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bucketOutputSm.getDirection().getIndex(),bucketOutputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(furnaceInputSm.getDirection().getIndex(),furnaceInputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(furnaceOutputSm.getDirection().getIndex(),furnaceOutputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(fuelTank.getSideDirection().getIndex(), fuelTank.getId()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(bucketInputSm.getStatus(), bucketInputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(bucketOutputSm.getStatus(), bucketOutputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(furnaceInputSm.getStatus(), furnaceInputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(furnaceOutputSm.getStatus(), furnaceOutputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(fuelTank.getSideStatus(), fuelTank.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(bucketInputSm.getDirection().getIndex(),bucketInputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(bucketOutputSm.getDirection().getIndex(),bucketOutputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(furnaceInputSm.getDirection().getIndex(),furnaceInputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(furnaceOutputSm.getDirection().getIndex(),furnaceOutputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(fuelTank.getSideDirection().getIndex(), fuelTank.getId()));
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
                    if(!(player.openContainer instanceof GasFiredFurnaceContainer)){
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
