package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrimitiveStirlingGeneratorTile extends VoluminousTileEntity implements MenuProvider {

    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(slotManager);
    }};

    private int counter;
    private int length;
    private final ItemStackHandler inventory = this.createHandler();

    public PrimitiveStirlingGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE, pos, state);
    }

    @Deprecated
    public PrimitiveStirlingGeneratorTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();
        if (counter > 0){
            counter--;
            if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) < Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get()){
                energy.ifPresent(e -> e.addEnergy(Config.PRIMITIVE_STIRLING_GENERATOR_GENERATE.get())); //Amount of energy to add per tick
            }
            setChanged();
        } else {
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                StirlingGeneratorRecipe recipe = level.getRecipeManager().getRecipeFor(StirlingGeneratorRecipe.RECIPE_TYPE, new SimpleContainer(stack), level).orElse(null);

                if (recipe != null) { // TODO: Switch to Voluminous Energy's in house RecipeUtils for recipe querying
                    h.extractItem(0, 1, false);
                    counter = recipe.getProcessTime();
                    length = counter;
                    setChanged();
                }
            });
        }

        sendOutPower();
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
                    int smallest = Math.min(Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
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
    public void load(CompoundTag tag) {
        CompoundTag invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(invTag));
        energy.ifPresent(h -> h.deserializeNBT(tag));
        counter = tag.getInt("counter");
        length = tag.getInt("length");
        slotManager.read(tag, "slot_manager");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        handler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>)h).serializeNBT();
            tag.put("inv",compound);
        });
        energy.ifPresent(h -> h.serializeNBT(tag));
        tag.putInt("counter", counter);
        tag.putInt("length", length);
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
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt){
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot){
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (stack.getItem() == Items.COAL || stack.getItem() == Items.COAL_BLOCK || stack.getItem() == Items.CHARCOAL || stack.getItem() == VEItems.COALCOKE || stack.getItem() == VEItems.PETCOKE){
                    return true;
                } else {
                    return false;
                }
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                if (stack.getItem() == Items.COAL){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == Items.COAL_BLOCK){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == VEItems.COALCOKE){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == VEItems.PETCOKE){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == Items.CHARCOAL){
                    return super.insertItem(slot, stack, simulate);
                } else {
                    return stack;
                }
            }
        };
    }

    private @NotNull VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get());
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
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
        return new PrimitiveStirlingGeneratorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }


    public int progressCounterPX(int px) {
        if (counter == 0){
            return 0;
        } else {
            return (px*(((counter*100)/length)))/100;
        }
    }

    public int progressCounterPercent(){
        if (counter != 0 && length != 0) return (int)(100-(((float)counter/(float)length)*100));
        return 0;
    }


    public int ticksLeft(){
        return counter;
    }

    public int getEnergyRate(){
        return 40;
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
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(slotManager.getStatus(), slotManager.getSlotNum()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(slotManager.getDirection().get3DDataValue(),slotManager.getSlotNum()));
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
                    if(!(player.containerMenu instanceof PrimitiveStirlingGeneratorContainer)){
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