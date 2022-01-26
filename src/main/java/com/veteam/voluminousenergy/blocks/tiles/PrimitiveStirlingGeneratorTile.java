package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PrimitiveStirlingGeneratorTile extends VoluminousTileEntity {

    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT,"slot_manager");

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

    private @Nonnull VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get());
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
        return new PrimitiveStirlingGeneratorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Nonnull
    @Override
    public ItemStackHandler getInventoryHandler() {
        return this.inventory;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return this.slotManagers;
    }

    @Nonnull
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return this.energy;
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
}