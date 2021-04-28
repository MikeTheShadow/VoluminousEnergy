package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ElectricFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.MathHelper;
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

public class ElectricFurnaceTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> inputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,0,1));
    private LazyOptional<IItemHandlerModifiable> outputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager inputSlotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot");
    public VESlotManager outputSlotManager = new VESlotManager(1, Direction.DOWN,true,"slot.voluminousenergy.output_slot");

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private AtomicReference<ItemStack> referenceStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private static final Logger LOGGER = LogManager.getLogger();

    public ElectricFurnaceTile(){
        super(VEBlocks.ELECTRIC_FURNACE_TILE);
    }

    public final ItemStackHandler inventory = createHandler();

    @Override
    public void tick(){
        updateClients();

        ItemStack furnaceInput = inventory.getStackInSlot(0).copy();
        ItemStack furnaceOutput = inventory.getStackInSlot(1).copy();

        inputItemStack.set(furnaceInput.copy()); // Atomic Reference, use this to query recipes FOR OUTPUT SLOT

        // Main Processing occurs here
        if (canConsumeEnergy()){
            FurnaceRecipe furnaceRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(furnaceInput.copy()), level).orElse(null);
            BlastingRecipe blastingRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.BLASTING, new Inventory(furnaceInput.copy()), level).orElse(null);

            if ((furnaceRecipe != null || blastingRecipe != null) && countChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy()) && itemChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy())){
                if (counter == 1) {
                    // Extract item
                    inventory.extractItem(0,1,false);

                    // Set output based on recipe
                    ItemStack newOutputStack;
                    if (furnaceRecipe != null) {
                        newOutputStack = furnaceRecipe.getResultItem().copy();
                    } else {
                        newOutputStack = blastingRecipe.getResultItem().copy();
                    }
                    //LOGGER.debug("NewOutputStack: " + newOutputStack);

                    // Output Item
                    if (furnaceOutput.getItem() != newOutputStack.getItem() || furnaceOutput.getItem() == Items.AIR) {
                        //LOGGER.debug("The output is not equal to the new output Stack");
                        if(furnaceOutput.getItem() == Items.AIR){ // Fix air >1 jamming slots
                            furnaceOutput.setCount(1);
                        }
                        if (furnaceRecipe != null){
                            newOutputStack.setCount(furnaceRecipe.getResultItem().getCount());
                        } else {
                            newOutputStack.setCount(blastingRecipe.getResultItem().getCount());
                        }
                        //LOGGER.debug("About to insert in pt1: " + newOutputStack);
                        inventory.insertItem(1, newOutputStack.copy(),false); // CRASH the game if this is not empty!

                    } else { // Assuming the recipe output item is already in the output slot
                        // Simply change the stack to equal the output amount
                        if (furnaceRecipe != null){
                            furnaceOutput.setCount(furnaceRecipe.getResultItem().getCount());
                        } else {
                            furnaceOutput.setCount(blastingRecipe.getResultItem().getCount());
                        }
                        //LOGGER.debug("About to insert in pt2: " + furnaceOutput);
                        inventory.insertItem(1, furnaceOutput.copy(),false); // Place the new output stack on top of the old one
                    }

                    consumeEnergy();
                    counter--;
                    this.setChanged();
                } else if (counter > 0) {
                    consumeEnergy();
                    counter--;
                } else {
                    counter = this.calculateCounter(200,inventory.getStackInSlot(2));
                    length = counter;
                    this.referenceStack.set(furnaceInput.copy());
                }

            } else counter = 0;
        } else counter = 0;
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> ((VEEnergyStorage)e)
                .consumeEnergy(this.consumptionMultiplier(Config.ELECTRIC_FURNACE_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(2).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.ELECTRIC_FURNACE_POWER_USAGE.get(), this.inventory.getStackInSlot(2).copy());
    }

    @Override
    public void load(BlockState state, CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));

        inputSlotManager.read(tag, "input_slot_manager");
        outputSlotManager.read(tag, "output_slot_manager");
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("energy",compound);
        });

        inputSlotManager.write(tag, "input_slot_manager");
        outputSlotManager.write(tag, "output_slot_manager");
        return super.save(tag);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt){
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getTag().getInt("energy")));
        this.load(this.getBlockState(), pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0) {
                    return level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), level).orElse(null) != null
                            || level.getRecipeManager().getRecipeFor(IRecipeType.BLASTING, new Inventory(stack), level).orElse(null) != null;
                } else if (slot == 1) {
                    FurnaceRecipe furnaceRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(inputItemStack.get()), level).orElse(null);
                    BlastingRecipe blastingRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.BLASTING, new Inventory(inputItemStack.get()), level).orElse(null);

                    // If both recipes are null, then don't bother
                    if (blastingRecipe == null && furnaceRecipe == null) return false;

                    if (furnaceRecipe != null) {
                        return stack.getItem() == furnaceRecipe.getResultItem().getItem();
                    }

                    return stack.getItem() == blastingRecipe.getResultItem().getItem();
                } else if (slot == 2){
                    return stack.getItem() == VEItems.QUARTZ_MULTIPLIER;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                if (slot == 0){
                    ItemStack referenceStack = stack.copy();
                    referenceStack.setCount(64);
                    FurnaceRecipe recipe = level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(referenceStack), level).orElse(null);
                    BlastingRecipe blastingRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.BLASTING, new Inventory(referenceStack),level).orElse(null);

                    if (recipe != null || blastingRecipe != null){
                        return super.insertItem(slot, stack, simulate);
                    }

                } else if (slot == 1){
                    return super.insertItem(slot, stack, simulate);
                } else if (slot == 2 && stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }

            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate){
                if (level != null){
                    FurnaceRecipe furnaceRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(referenceStack.get()), level).orElse(null);
                    BlastingRecipe blastingRecipe = level.getRecipeManager().getRecipeFor(IRecipeType.BLASTING, new Inventory(referenceStack.get()), level).orElse(null);
                    if(blastingRecipe != null) {
                        if (inventory.getStackInSlot(slot).getItem() == blastingRecipe.getResultItem().getItem()) {
                            if(blastingRecipe.getExperience() > 0){
                                generateXP(amount, blastingRecipe.getExperience());
                            }
                        }
                    } else if (furnaceRecipe != null) {
                        if (inventory.getStackInSlot(slot).getItem() == furnaceRecipe.getResultItem().getItem()) {
                            if (furnaceRecipe.getExperience() > 0) {
                                generateXP(amount, furnaceRecipe.getExperience());
                            }
                        }
                    }
                }
                return super.extractItem(slot,amount,simulate);
            }
        };
    }

    private void generateXP(int craftedAmount, float experience){
        if(level == null) return;
        int i = MathHelper.floor((float)craftedAmount * experience);
        float f = MathHelper.frac((float)craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double)f) ++i;

        while(i > 0) {
            int j = ExperienceOrbEntity.getExperienceValue(i);
            i -= j;
            level.addFreshEntity(new ExperienceOrbEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), j));
        }
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.ELECTRIC_FURNACE_MAX_POWER.get(), Config.ELECTRIC_FURNACE_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return handler.cast();
            if(inputSlotManager.getStatus() && inputSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return inputHandler.cast();
            else if(outputSlotManager.getStatus() && outputSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return outputHandler.cast();
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
        return new ElectricFurnaceContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
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
            return (itemStack.getCount() + furnaceRecipe.getResultItem().getCount()) <= 64;
        } else if (blastingRecipe != null){
            return (itemStack.getCount() + blastingRecipe.getResultItem().getCount()) <= 64;
        }
        return false;
    }

    public boolean itemChecker(FurnaceRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack){
        if(furnaceRecipe != null){
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return furnaceRecipe.getResultItem().getItem() == itemStack.getItem();
        } else if (blastingRecipe != null){
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return blastingRecipe.getResultItem().getItem() == itemStack.getItem();
        }
        return false;
    }

    public int getCounter(){
        return counter;
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
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(inputSlotManager.getStatus(), inputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(outputSlotManager.getStatus(), outputSlotManager.getSlotNum()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(inputSlotManager.getDirection().get3DDataValue(),inputSlotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(outputSlotManager.getDirection().get3DDataValue(),outputSlotManager.getSlotNum()));
                    }
                });
            });
        } else if (!playerUuid.isEmpty()){ // Legacy solution
            double x = this.getBlockPos().getX();
            double y = this.getBlockPos().getY();
            double z = this.getBlockPos().getZ();
            final double radius = 16;
            RegistryKey<World> worldRegistryKey = this.getLevel().dimension();
            PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x,y,z,radius,worldRegistryKey);

            // Boolean Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(inputSlotManager.getStatus(), inputSlotManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(outputSlotManager.getStatus(), outputSlotManager.getSlotNum()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(inputSlotManager.getDirection().get3DDataValue(),inputSlotManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(outputSlotManager.getDirection().get3DDataValue(),outputSlotManager.getSlotNum()));
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
                    if(!(player.containerMenu instanceof ElectricFurnaceContainer)){
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