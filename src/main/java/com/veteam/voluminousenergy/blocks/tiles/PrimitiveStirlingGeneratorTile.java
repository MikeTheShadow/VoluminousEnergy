package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PrimitiveStirlingGeneratorTile extends VoluminousTileEntity implements IVEPoweredTileEntity,IVECountable {

    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT,"slot_manager");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(slotManager);
    }};

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
                StirlingGeneratorRecipe recipe = RecipeUtil.getStirlingGeneratorRecipe(level, stack.copy());

                if (recipe != null) {
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

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot){
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                StirlingGeneratorRecipe recipe = RecipeUtil.getStirlingGeneratorRecipe(level, stack);
                return slot == 0 && recipe != null && recipe.getIngredient().test(referenceStack);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
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
    public int getMaxPower() {
        return Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return 0;
    }

    @Override
    public int getTransferRate() {
        return Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}