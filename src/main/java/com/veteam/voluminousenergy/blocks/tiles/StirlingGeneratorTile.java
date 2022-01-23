package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.StirlingGeneratorContainer;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class StirlingGeneratorTile extends VoluminousTileEntity {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(slotManager);
    }};

    private int counter;
    private int length;
    private int energyRate;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private ItemStackHandler inventory = this.createHandler();

    public StirlingGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.STIRLING_GENERATOR_TILE, pos, state);
    }

    @Deprecated
    public StirlingGeneratorTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.STIRLING_GENERATOR_TILE, pos, state);
    }

    @Override
    public void tick(){
        updateClients();
        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy();

            StirlingGeneratorRecipe recipe = level.getRecipeManager().getRecipeFor(StirlingGeneratorRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
            inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

            if (counter > 0){
                if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) + energyRate <= Config.STIRLING_GENERATOR_MAX_POWER.get()){
                    counter--;
                    energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(energyRate)); //Amount of energy to add per tick
                }
                setChanged();
            } else if (!input.isEmpty()) {
                if (recipe != null  && (recipe.getEnergyPerTick() * recipe.getProcessTime()) + this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) <= Config.STIRLING_GENERATOR_MAX_POWER.get()){
                    h.extractItem(0,recipe.ingredientCount,false);
                    this.counter = recipe.getProcessTime();
                    this.energyRate = recipe.getEnergyPerTick();
                    this.length = this.counter;
                    setChanged();
                }
            }
            if (counter == 0){
                energyRate = 0;
            }
            sendOutPower();
        });
    }

    public static int recieveEnergy(BlockEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()){
                BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if(tileEntity != null){
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
                    int received = recieveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
                    if (energy.getEnergyStored() <=0){
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));

        counter = tag.getInt("counter");
        length = tag.getInt("length");
        energyRate = tag.getInt("energy_rate");

        slotManager.read(tag, "slot_manager");

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
        tag.putInt("energy_rate", energyRate);

        slotManager.write(tag, "slot_manager");
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
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                StirlingGeneratorRecipe recipe = level.getRecipeManager().getRecipeFor(StirlingGeneratorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
                //StirlingGeneratorRecipe recipe1 = level.getRecipeManager().getRecipeFor(StirlingGeneratorRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()),level).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.test(stack);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                StirlingGeneratorRecipe recipe = level.getRecipeManager().getRecipeFor(StirlingGeneratorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
                //StirlingGeneratorRecipe recipe1 = level.getRecipeManager().getRecipeFor(StirlingGeneratorRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()),level).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getItems()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                }
                return stack;
            }
        };
    }

    private VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.STIRLING_GENERATOR_MAX_POWER.get(), Config.STIRLING_GENERATOR_SEND.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getCapability(cap, side, handler, inventory, slotManagers);
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public Component getDisplayName(){
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new StirlingGeneratorContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(((counter*100)/length)))/100;
        }
    }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    public int ticksLeft(){
        return counter;
    }

    public int getEnergyRate(){
        return energyRate;
    }


    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == slotManager.getSlotNum()) slotManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == slotManager.getSlotNum()) slotManager.setDirection(direction);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        bulkSendSMPacket(s, slotManager);
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
                    if(!(player.containerMenu instanceof StirlingGeneratorContainer)){
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
