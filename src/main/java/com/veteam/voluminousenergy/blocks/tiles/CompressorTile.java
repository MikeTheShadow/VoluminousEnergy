package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CompressorContainer;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CompressorTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> inputItemHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0,1));
    private LazyOptional<IItemHandlerModifiable> outputItemHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager inputSlotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot");
    public VESlotManager outputSlotManager = new VESlotManager(1, Direction.DOWN, true,"slot.voluminousenergy.output_slot");

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private static final Logger LOGGER = LogManager.getLogger();


    public CompressorTile(){
        super(VEBlocks.COMPRESSOR_TILE);
    }

    private ItemStackHandler inventory = createHandler();

    @Override
    public void tick(){
        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();

        CompressorRecipe recipe = world.getRecipeManager().getRecipe(CompressorRecipe.RECIPE_TYPE, new Inventory(input), world).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty()){
            if (output.getCount() + recipe.getOutputAmount() < 64 && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0) {
                if (this.counter == 1){ //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.ingredientCount,false);

                    // Get output stack and RNG stack from the recipe
                    ItemStack newOutputStack = recipe.getResult().copy();

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem()) {
                        if (output.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                            output.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputAmount());
                        inventory.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                    }

                    this.counter--;
                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.COMPRESSOR_POWER_USAGE.get()));
                    markDirty();
                } else if (this.counter > 0){ //In progress
                    this.counter--;
                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.COMPRESSOR_POWER_USAGE.get()));
                } else { // Check if we should start processing
                    if (output.isEmpty() || output.getItem() == recipe.getResult().getItem()){
                        this.counter = recipe.getProcessTime();
                        this.length = this.counter;
                    } else {
                        this.counter = 0;
                    }
                }
            } else { // This is if we reach the maximum in the slots
                this.counter = 0;
            }
        } else { // This is if the input slot is empty
            this.counter = 0;
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));

        inputSlotManager.read(tag, "input_slot");
        outputSlotManager.read(tag, "output_slot");

        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("energy",compound);
        });

        inputSlotManager.write(tag, "input_slot");
        outputSlotManager.write(tag, "output_slot");
        return super.write(tag);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt){
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                CompressorRecipe recipe = world.getRecipeManager().getRecipe(CompressorRecipe.RECIPE_TYPE, new Inventory(referenceStack), world).orElse(null);
                CompressorRecipe recipe1 = world.getRecipeManager().getRecipe(CompressorRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()),world).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.test(stack);
                } else if (slot == 1 && recipe1 != null){
                    return stack.getItem() == recipe1.result.getItem();
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                CompressorRecipe recipe = world.getRecipeManager().getRecipe(CompressorRecipe.RECIPE_TYPE, new Inventory(referenceStack), world).orElse(null);
                CompressorRecipe recipe1 = world.getRecipeManager().getRecipe(CompressorRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()),world).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if (slot == 1 && recipe1 != null){
                    if (stack.getItem() == recipe1.result.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                }
                return stack;
            }
        };
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.COMPRESSOR_MAX_POWER.get(), Config.COMPRESSOR_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return handler.cast();
            if(inputSlotManager.getStatus() && inputSlotManager.getDirection().getIndex() == side.getIndex())
                return inputItemHandler.cast();
            else if(outputSlotManager.getStatus() && outputSlotManager.getDirection().getIndex() == side.getIndex())
                return outputItemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY){
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName(){
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity)
    {
        return new CompressorContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
        }
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == inputSlotManager.getSlotNum()){
            inputSlotManager.setStatus(status);
        } else if (slotId == outputSlotManager.getSlotNum()){
            outputSlotManager.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == inputSlotManager.getSlotNum()){
            inputSlotManager.setDirection(direction);
        } else if (slotId == outputSlotManager.getSlotNum()){
            outputSlotManager.setDirection(direction);
        }
    }

    @Override
    public void sendPacketToClient(){
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(inputSlotManager.getStatus(), inputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(outputSlotManager.getStatus(), outputSlotManager.getSlotNum()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(inputSlotManager.getDirection().getIndex(),inputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(outputSlotManager.getDirection().getIndex(),outputSlotManager.getSlotNum()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(inputSlotManager.getStatus(), inputSlotManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(outputSlotManager.getStatus(), outputSlotManager.getSlotNum()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(inputSlotManager.getDirection().getIndex(),inputSlotManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(outputSlotManager.getDirection().getIndex(),outputSlotManager.getSlotNum()));
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
                    if(!(player.openContainer instanceof CompressorContainer)){
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
