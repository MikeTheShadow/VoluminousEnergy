package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.StirlingGeneratorContainer;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StirlingGeneratorTile extends VoluminousTileEntity implements IVEPoweredTileEntity,IVECountable {
    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT,"slot_manager");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(slotManager);
    }};

    private int energyRate;
    private final ItemStackHandler inventory = this.createHandler();

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

            StirlingGeneratorRecipe recipe = RecipeUtil.getStirlingGeneratorRecipe(level, input.copy());

            if (counter > 0){
                if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) + energyRate <= Config.STIRLING_GENERATOR_MAX_POWER.get()){
                    counter--;
                    energy.ifPresent(e -> e.addEnergy(energyRate)); //Amount of energy to add per tick
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
        energyRate = tag.getInt("energy_rate");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("energy_rate", energyRate);
        super.saveAdditional(tag);
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
                StirlingGeneratorRecipe recipe = RecipeUtil.getStirlingGeneratorRecipe(level, stack);
                return slot == 0 && recipe != null && recipe.getIngredient().test(referenceStack);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                StirlingGeneratorRecipe recipe = RecipeUtil.getStirlingGeneratorRecipe(level, stack);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.getIngredient().getItems()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                }
                return stack;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new StirlingGeneratorContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
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
    public int getMaxPower() {
        return Config.STIRLING_GENERATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return 0;
    }

    @Override
    public int getTransferRate() {
        return Config.STIRLING_GENERATOR_SEND.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}
