package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.upgrades.MysteriousMultiplier;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.RecipeProcessor;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import com.veteam.voluminousenergy.util.tiles.CapabilityMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class VETileEntity extends BlockEntity implements MenuProvider {

    VEItemStackHandler inventory;
    private final RecipeType<? extends Recipe<?>> recipeType;
    VERecipe selectedRecipe = null;
    List<VERecipe> potentialRecipes = new ArrayList<>();

    final List<VERelationalTank> tanks = new ArrayList<>();
    final List<VESlotManager> managers = new ArrayList<>();
    final HashMap<String,Integer> dataMap = new HashMap<>();
    RecipeProcessor recipeProcessor;
    boolean sendsOutPower;

    public VETileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends Recipe<?>> recipeType) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    /**
     * Nullable VEEnergyStorage.
     */
    VEEnergyStorage energy;

    public static void serverTick(Level level, BlockPos pos, BlockState state, VETileEntity voluminousTile) {
        voluminousTile.tick();
    }

    // Fluid methods to subclass?

    public static final int DEFAULT_TANK_CAPACITY = 4000;
    boolean fluidInputDirty = true;

    public void inputFluid(VERelationalTank tank, int slot1, int slot2) {
        ItemStack input = tank.getInput().copy();
        ItemStack output = tank.getOutput().copy();
        FluidTank inputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (input.getItem() instanceof BucketItem && input.getItem() != Items.BUCKET) {
            if ((output.getItem() == Items.BUCKET && output.getCount() < 16) || checkOutputSlotForEmptyOrBucket(output)) {
                Fluid fluid = ((BucketItem) input.getItem()).getFluid();
                if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= inputTank.getTankCapacity(0)) {
                    inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    handler.extractItem(slot1, 1, false);
                    handler.insertItem(slot2, new ItemStack(Items.BUCKET, 1), false);
                    this.markRecipeDirty();
                }
            }
        }
    }


    //use for when the input and output tilePos are different
    public void outputFluid(VERelationalTank tank, int slot1, int slot2) {
        ItemStack inputSlot = tank.getInput();
        ItemStack outputSlot = tank.getOutput();
        FluidTank outputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (inputSlot.getItem() == Items.BUCKET && outputTank.getFluidAmount() >= 1000 && inputSlot.getCount() > 0 && outputSlot.copy() == ItemStack.EMPTY) {
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot1, 1, false);
            handler.insertItem(slot2, bucketStack, false);
            this.markRecipeDirty();
        }
    }


    public int getTankCapacity() {
        return DEFAULT_TANK_CAPACITY;
    }

    public void updateTankPacketFromGui(boolean status, int id) {
        for (VERelationalTank tank : getRelationalTanks()) {
            if (id == tank.getSlotNum()) tank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id) {
        for (VERelationalTank tank : getRelationalTanks()) {
            if (id == tank.getSlotNum()) {
                this.capabilityMap.moveFluidSlotManagerPos(tank, IntToDirection.IntegerToDirection(direction));
            }
        }
    }

    public @Nonnull List<VERelationalTank> getRelationalTanks() {
        return tanks;
    }

    public static boolean checkOutputSlotForEmptyOrBucket(ItemStack slotStack) {
        return slotStack.copy() == ItemStack.EMPTY || ((slotStack.copy().getItem() == Items.BUCKET) && slotStack.copy().getCount() < 16);
    }

    public void markFluidInputDirty() {
        this.fluidInputDirty = true;
    }

    protected void processFluidIO() {
        if (!fluidInputDirty) return;
        fluidInputDirty = false;
        for (VESlotManager manager : this.getSlotManagers()) {
            ItemStackHandler inventory = this.getInventoryHandler();
            if (manager.getSlotType() == SlotType.FLUID_INPUT) {

                VERelationalTank tank = this.getRelationalTanks().get(manager.getTankId());
                tank.setInput(inventory.getStackInSlot(manager.getSlotNum()));
                tank.setOutput(inventory.getStackInSlot(manager.getOutputSlotId()));
                if (tank.getTankType() == TankType.INPUT || tank.getTankType() == TankType.BOTH)
                    inputFluid(tank, manager.getSlotNum(), manager.getOutputSlotId());
                outputFluid(tank, manager.getSlotNum(), manager.getOutputSlotId());
            }
        }
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num >= getRelationalTanks().size() || num < 0) {
            return FluidStack.EMPTY;
        }
        return getRelationalTanks().get(num).getTank().getFluid();
    }

    //  END OF FLUID STUFF

    int counter = 0;
    int length = 0;
    int sound_tick = 0;
    boolean isRecipeDirty = true;


    /**
     * Must include a call to updateClients();
     * This message can be removed if updateClients(); is found to be useless
     */
    public void tick() {
        processFluidIO();
        updateClients();

        if(this.recipeProcessor != null) {
            if(this.isRecipeDirty) {
                recipeProcessor.validateRecipe(this);
                this.isRecipeDirty = false;
            }
            recipeProcessor.processRecipe(this);
        }
        if(this.sendsOutPower) sendOutPower();
    }

    /**
     * Call this method whenever something in the tile entity has been updated.
     * If the server and client are seeing something different this is why
     * Note calling this only updates the clients and doesn't save anything to file
     * if you wish to save to file call setChanged();
     */
    public void updateClients() {
        if (level == null) return;
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 1);
    }

    public int calculateCounter(int processTime, ItemStack upgradeStack) {
        if (upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER.get()) {
            int count = upgradeStack.getCount();
            if (count == 4) {
                return 5;
            } else {
                return (-45 * upgradeStack.getCount()) + processTime;
            }
        } else if (!upgradeStack.isEmpty() && TagUtil.isTaggedMachineUpgradeItem(upgradeStack)) {
            CompoundTag compound = upgradeStack.getTag();
            return compound != null ? (int) ((float) (processTime * compound.getFloat("multiplier"))) : processTime;
        }
        return processTime;
    }

    protected int consumptionMultiplier(int consumption, int slot) {
        ItemStack upgradeStack = slot == -1 ? ItemStack.EMPTY : getInventory().getStackInSlot(slot);
        if (upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER.get()) {
            int count = upgradeStack.getCount();
            if (count == 4) {
                return consumption * 16;
            } else if (count == 3) {
                return consumption * 8;
            } else if (count == 2) {
                return consumption * 4;
            } else if (count == 1) {
                return consumption * 2;
            }
        } else if (!upgradeStack.isEmpty() && TagUtil.isTaggedMachineUpgradeItem(upgradeStack)) {
            CompoundTag compound = upgradeStack.getTag();
            float multiplier = compound != null ? compound.getFloat("multiplier") : 1;
            MysteriousMultiplier.QualityTier qualityTier = MysteriousMultiplier.getQualityTier(multiplier);

            return (int) switch (qualityTier) {
                case NULL -> consumption;
                case BASIC -> consumption * 1.15;
                case GRAND -> consumption * 1.25;
                case RARE -> consumption * 1.5;
                case ARCANE -> consumption * 2;
                case HEROIC -> consumption * 4;
                case UNIQUE -> consumption * 6;
                case CELESTIAL -> consumption * 8;
                case DIVINE -> consumption * 10;
                case EPIC -> consumption * 12;
                case LEGENDARY -> consumption * 14;
                case MYTHIC -> consumption * 16;
            };

        }
        return consumption;
    }


    /**
     * Quickly get the energy stored in the tile
     *
     * @return int representing the stored energy of the tile entity
     */
    protected int getEnergyStored() {
        return this.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    /**
     * Loads inventory, energy, tilePos managers, counter, and length.
     *
     * @param tag CompoundTag
     */
    @Override
    public void load(CompoundTag tag) {
        CompoundTag inv = tag.getCompound("inv");

        ItemStackHandler handler = getInventoryHandler();

        if (handler != null) {
            handler.deserializeNBT(inv);
        }
        if(energy != null) energy.deserializeNBT(tag);

        for(var entry : dataMap.entrySet()) {
            dataMap.put(entry.getKey(),tag.getInt(entry.getKey()));
        }

        for (VESlotManager manager : getSlotManagers()) {
            manager.read(tag);
        }

        for (VERelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = tag.getCompound(relationalTank.getTankName());
            relationalTank.getTank().readFromNBT(compoundTag);
            relationalTank.readGuiProperties(tag);
        }

        super.load(tag);
    }

    /**
     * Saves inventory, energy, tilePos managers, counter, and length.
     * To save the tile call setChanged();
     *
     * @param tag CompoundTag
     */
    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        ItemStackHandler handler = getInventoryHandler();
        if (handler != null) {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) handler).serializeNBT();
            tag.put("inv", compound);
        }

        if (energy != null) energy.serializeNBT(tag);

        for (VESlotManager manager : getSlotManagers()) {
            manager.write(tag);
        }

        for(var entry : dataMap.entrySet()) {
            tag.putInt(entry.getKey(),entry.getValue());
        }

        for (VERelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = new CompoundTag();
            relationalTank.getTank().writeToNBT(compoundTag);
            tag.put(relationalTank.getTankName(), compoundTag);
            relationalTank.writeGuiProperties(tag);
        }

        super.saveAdditional(tag);
    }

    @Override
    public void setChanged() {
        updateClients();
        super.setChanged();
    }

    @Deprecated
    public ItemStackHandler createHandler(int size, VETileEntity tileEntity) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (tileEntity.energy != null && tileEntity.energy.getUpgradeSlotId() == slot) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack);
                }
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (tileEntity.energy != null && slot == tileEntity.energy.getUpgradeSlotId()) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack) ? super.insertItem(slot, stack, simulate) : stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        if(tileEntity instanceof VETileEntity tile && tile.energy != null) {
            return tile.energy.receiveEnergy(maxReceive,false);
        }
        return 0;
    }

    void sendOutPower() {
        for (Direction dir : Direction.values()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
            Direction opposite = dir.getOpposite();
            if (tileEntity != null) {
                // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                int smallest = Math.min(Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
                int received = receiveEnergy(tileEntity, opposite, smallest);
                energy.consumeEnergy(received);
                if (energy.getEnergyStored() <= 0) {
                    break;
                }
            }
        }
    }

    /**
     * @param status boolean status of the tilePos
     * @param slotId int id of the tilePos
     */
    public void updatePacketFromGui(boolean status, int slotId) {
        for (VESlotManager slot : getSlotManagers()) {
            if (slotId == slot.getSlotNum()) {
                slot.setStatus(status);
                return;
            }
        }
    }

    public void updatePacketFromGui(int direction, int slotId) {
        for (VESlotManager slot : getSlotManagers()) {
            if (slotId == slot.getSlotNum()) {
                this.capabilityMap.moveSlotManagerPos(slot, direction);
                return;
            }
        }
    }

    CapabilityMap capabilityMap = null;

    /**
     * This handles items,energy and fluids. Handling fluids could be moved to VETileEntity
     *
     * @param cap  Base capability
     * @param side Base Direction
     * @param <T>  T the type of capability
     * @return A LazyOptional of Optional of type T matching the capability passed into this
     */
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        ItemStackHandler inventory = getInventoryHandler();
        List<VESlotManager> itemManagers = getSlotManagers();
        if (capabilityMap == null) {
            capabilityMap = new CapabilityMap(inventory, itemManagers, getRelationalTanks(), energy, this);
        }
        return this.capabilityMap.getCapability(cap, side, this);
    }

    /**
     * Call this to consume energy
     * Note that tiles now require an upgrade tilePos and thus an inventory to properly function here
     * If you need to consume energy WITHOUT an upgrade tilePos make a new method that does not have this.
     * Throws an error if missing the power consumeEnergy IMPL
     */
    public void consumeEnergy() {
        if(this.energy == null) return;
        energy.consumeEnergy(this.consumptionMultiplier(energy.getConsumption(),energy.getUpgradeSlotId()));
    }

    /**
     * @return True if the object has enough energy to be able to continue. Or the entity doesn't run on energy
     */
    public boolean canConsumeEnergy() {
        if (energy != null) {
            if (energy.getMaxEnergyStored() == 0) return true; // For tiles that do not consume power
            return energy.getEnergyStored()
                    > this.consumptionMultiplier(energy.getConsumption(), energy.getUpgradeSlotId());
        }
        return true;
    }

    /**
     * Gets the display name and throws an NotImpl exception if missing registry
     *
     * @return Name component
     */
    @Override
    public @Nonnull Component getDisplayName() {
        ResourceLocation name = RegistryLookups.getBlockEntityTypeKey(this);
        if (name == null)
            throw new NotImplementedException("Missing registry name for class: " + this.getClass().getName());
        return Component.nullToEmpty(name.getPath());
    }

    /**
     * Make this null if the item does not have a menu
     *
     * @param id              the ID
     * @param playerInventory inventory of the player
     * @param player          the player themselves
     * @return a new AbstractContainerMenu corresponding to this
     */
    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player);

    /**
     * We do a null check on inventory so this can be null. Might change though
     * REMEMBER YOU NEED TO BUILD YOUR OWN INVENTORY HANDLER
     * USE EITHER A NEWLY CREATED ONE OR ONE OF THE createHandler's defined here
     *
     * @return a ItemStackHandler or null if the object lacks an inventory
     */
    public @Nullable ItemStackHandler getInventoryHandler() {
        return this.inventory;
    }

    /**
     * Important note. If the entity has no tilePos managers return a new ArrayList otherwise this will crash
     *
     * @return A not null List<VESlotManager> list
     */
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return this.managers;
    }

    public @Nonnull ItemStack getStackInSlot(int slot) {
        return this.getSlotManagers().get(slot).getItem(this.inventory);
    }

    /**
     * When a data packet is received load it.
     *
     * @param net Connection
     * @param pkt ClientboundBlockEntityDataPacket
     */
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (energy != null && pkt.getTag() != null && pkt.getTag().contains("energy"))
            energy.setEnergy(pkt.getTag().getInt("energy"));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    public int progressBurnCounterPX(int px) {
        int counter = dataMap.get("counter");
        int length = dataMap.get("length");
        if (counter != 0 && length != 0) return (px * (((counter * 100) / length))) / 100;;
        return 0;
    }

    public int progressProcessingCounterPX(int px) {
        int counter = dataMap.get("counter");
        int length = dataMap.get("length");
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressCounterPercent() {
        int counter = dataMap.get("counter");
        int length = dataMap.get("length");
        if (length != 0) {
            return (int) (100 - (((float) counter / (float) length) * 100));
        } else {
            return 0;
        }
    }

    public int ticksLeft() {
        return dataMap.get("counter");
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public VEEnergyStorage getEnergy() {
        return energy;
    }

    public void markRecipeDirty() {
        this.isRecipeDirty = true;
    }

    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return this.recipeType;
    }

    public List<VERecipe> getPotentialRecipes() {
        return potentialRecipes;
    }

    void addTanks(List<VERelationalTank> tanks) {
        this.tanks.addAll(tanks);
    }

    void addSlots(List<VESlotManager> managers) {
        this.managers.addAll(managers);
    }

    public VEItemStackHandler getInventory() {
        return inventory;
    }

    @Nullable
    public VERecipe getSelectedRecipe() {
        return selectedRecipe;
    }

    public List<VERelationalTank> getTanks() {
        return tanks;
    }

    public VERelationalTank getTank(int id) {
        return tanks.get(id);
    }

    public int getTankCapacity(int id) {
        return this.tanks.get(id).getTank().getCapacity();
    }

    public List<VESlotManager> getManagers() {
        return managers;
    }

    public boolean isFluidInputDirty() {
        return fluidInputDirty;
    }

    public boolean isRecipeDirty() {
        return isRecipeDirty;
    }

    public int getData(String key) {
        return this.dataMap.getOrDefault(key,-1);
    }

    public void setData(String key, int value) {
        this.dataMap.put(key,value);
    }

    public void setRecipeDirty(boolean dirty) {
        this.isRecipeDirty = dirty;
    }

    public void setPotentialRecipes(List<VERecipe> recipes) {
        this.potentialRecipes = recipes;
    }

    @Deprecated
    void processRecipe() {

    }

    @Deprecated
    void doExtraRecipeProcessing() {

    }

    @Deprecated
    public void validateRecipe() {
    }

    public boolean sendsOutPower() {
        return sendsOutPower;
    }

    public void setSendsOutPower(boolean sendsOutPower) {
        this.sendsOutPower = sendsOutPower;
    }

    public void setSelectedRecipe(VERecipe recipe) {
        this.selectedRecipe = recipe;
    }
}
