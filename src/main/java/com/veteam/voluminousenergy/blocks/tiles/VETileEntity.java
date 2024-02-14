package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.upgrades.MysteriousMultiplier;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRNGRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
import java.util.List;
import java.util.Random;

import static net.minecraft.util.Mth.abs;

public abstract class VETileEntity extends BlockEntity implements MenuProvider {
    private final RecipeType<? extends Recipe<?>> recipeType;
    Recipe<?> selectedRecipe = null;
    List<? extends Recipe<?>> potentialRecipes = new ArrayList<>();

    public VETileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends Recipe<?>> recipeType) {
        super(type, pos, state);
        this.recipeType = recipeType;
        this.isRecipeDirty = true;
    }

    /**
     * Lazy Optional of getEnergy which cannot be null but can contain a null VEEnergyStorage
     * for the ifpresent to fail
     */
    LazyOptional<VEEnergyStorage> energy = createEnergy();

    public static void serverTick(Level level, BlockPos pos, BlockState state, VETileEntity voluminousTile) {
        voluminousTile.tick();
    }

    // Fluid methods to subclass

    public void inputFluid(RelationalTank tank, int slot1, int slot2) {
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


    //use for when the input and output slot are different
    public void outputFluid(RelationalTank tank, int slot1, int slot2) {
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
        return TANK_CAPACITY;
    }

    public void updateTankPacketFromGui(boolean status, int id) {
        for (RelationalTank tank : getRelationalTanks()) {
            if (id == tank.getSlotNum()) tank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id) {
        for (RelationalTank tank : getRelationalTanks()) {
            if (id == tank.getSlotNum()) {
                this.capabilityMap.moveFluidSlotManagerPos(tank, IntToDirection.IntegerToDirection(direction));
            }
        }
    }

    @Deprecated
    public static final int TANK_CAPACITY = 4000;
    boolean fluidInputDirty;

    public @Nonnull List<RelationalTank> getRelationalTanks() {

        return new ArrayList<>();
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

                RelationalTank tank = this.getRelationalTanks().get(manager.getTankId());
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
    boolean isRecipeDirty;


    /**
     * Must include a call to updateClients();
     * This message can be removed if updateClients(); is found to be useless
     */
    public void tick() {
        processFluidIO();
        updateClients();
        validateRecipe();
        processRecipe();
    }


    void processRecipe() {
        if (selectedRecipe == null) return;
        VEFluidRecipe recipe = (VEFluidRecipe) selectedRecipe;

        if (canConsumeEnergy()) {

            if (counter == 1) {
                // Validate output
                for (RelationalTank relationalTank : getRelationalTanks()) {
                    if (relationalTank.getTankType() == TankType.OUTPUT) {
                        FluidStack recipeFluid = recipe.getOutputFluid(relationalTank.getRecipePos());
                        FluidTank tank = relationalTank.getTank();
                        FluidStack currentFluid = tank.getFluid();
                        if (currentFluid.isEmpty()) continue;
                        // If the output fluid amount won't fit, then you must acquit
                        if (!recipeFluid.isFluidEqual(currentFluid)
                                || tank.getFluidAmount() + recipeFluid.getAmount() > tank.getCapacity()) {
                            return;
                        }
                    }
                }

                ItemStackHandler handler = getInventoryHandler();

                if (handler != null) {
                    // Validate output
                    for (VESlotManager slotManager : getSlotManagers()) {
                        if (slotManager.getSlotType() != SlotType.OUTPUT) continue;
                        ItemStack recipeStack = recipe.getResult(slotManager.getRecipePos());
                        ItemStack currentItem = slotManager.getItem(handler);
                        if (currentItem.isEmpty()) continue;
                        // If the output item amount won't fit, then you must acquit
                        if (!recipeStack.is(currentItem.getItem())
                                || recipeStack.getCount() + currentItem.getCount() > currentItem.getMaxStackSize()) {
                            return;
                        }
                    }

                    VEFluidRNGRecipe irngRecipe = null;
                    if (recipe instanceof VEFluidRNGRecipe rec) {
                        irngRecipe = rec;
                    }
                    Random r = new Random();

                    // process recipe
                    for (VESlotManager slotManager : getSlotManagers()) {
                        if (slotManager.getSlotType() == SlotType.OUTPUT) {
                            ItemStack output = recipe.getResult(slotManager.getRecipePos());
                            ItemStack currentStack = slotManager.getItem(handler);
                            // rng calculations
                            if (irngRecipe != null) {
                                float randomness = irngRecipe.getOutputChance(slotManager.getRecipePos());
                                if (randomness != 1) {
                                    float random = abs(0 + r.nextFloat() * (-1));
                                    if (random > randomness) continue;
                                }

                            }
                            if (currentStack.isEmpty()) slotManager.setItem(output, handler);
                            else currentStack.setCount(currentStack.getCount() + output.getCount());
                        } else if (slotManager.getSlotType() == SlotType.INPUT) {
                            Ingredient ingredient = recipe.getIngredient(slotManager.getRecipePos());
                            ItemStack currentStack = slotManager.getItem(handler);
                            currentStack.setCount(currentStack.getCount() - ingredient.getItems()[0].getCount());
                        }
                    }
                }

                // process recipe
                for (RelationalTank relationalTank : getRelationalTanks()) {
                    if (relationalTank.getTankType() == TankType.OUTPUT) {
                        relationalTank.fillOutput(recipe, relationalTank.getRecipePos());
                    } else if (relationalTank.getTankType() == TankType.INPUT) {
                        relationalTank.drainInput(recipe, relationalTank.getRecipePos());
                    }
                }
                doExtraRecipeProcessing();

                this.markRecipeDirty();
                this.markFluidInputDirty();
                this.setChanged();
            } else if (counter > 0) {
                if (++sound_tick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
                    sound_tick = 0;
                    level.playSound(null, this.getBlockPos(), VESounds.AQUEOULIZER, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                counter = length;
            }
            counter--;
            consumeEnergy();
        }
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

    protected int calculateCounter(int processTime, ItemStack upgradeStack) {
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

    protected int consumptionMultiplier(int consumption, ItemStack upgradeStack) {
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
     * Loads inventory, energy, slot managers, counter, and length.
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

        energy.ifPresent(h -> h.deserializeNBT(tag));

        if (this instanceof IVECountable) {
            counter = tag.getInt("counter");
            length = tag.getInt("length");
        }

        for (VESlotManager manager : getSlotManagers()) {
            manager.read(tag);
        }

        for (RelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = tag.getCompound(relationalTank.getTankName());
            relationalTank.getTank().readFromNBT(compoundTag);
            relationalTank.readGuiProperties(tag);
        }

        super.load(tag);
    }

    /**
     * Saves inventory, energy, slot managers, counter, and length.
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

        if (energy != null) energy.ifPresent(h -> h.serializeNBT(tag));

        for (VESlotManager manager : getSlotManagers()) {
            manager.write(tag);
        }

        if (this instanceof IVECountable) {
            tag.putInt("counter", counter);
            tag.putInt("length", length);
        }

        for (RelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = new CompoundTag();
            relationalTank.getTank().writeToNBT(compoundTag);
            tag.put(relationalTank.getTankName(), compoundTag);
            relationalTank.writeGuiProperties(tag);
        }

        super.saveAdditional(tag);
    }

    /**
     * A default ItemStackHandler creator. Passing in an int size creates it for us
     *
     * @param slots the size of the inventory
     * @return a new inventory
     */
    public ItemStackHandler createHandler(int slots) {

        VETileEntity tileEntity = this;
        int upgradeSlotLocation = -1;
        if (tileEntity instanceof IVEPoweredTileEntity poweredTileEntity) {
            upgradeSlotLocation = poweredTileEntity.getUpgradeSlotId();
        }

        int finalUpgradeSlotLocation = upgradeSlotLocation;
        return new ItemStackHandler(slots) {

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                List<VESlotManager> managers = getSlotManagers();

                if (slot == finalUpgradeSlotLocation) tileEntity.markRecipeDirty();
                else if (slot < managers.size()) {
                    SlotType slotType = getSlotManagers().get(slot).getSlotType();
                    if (slotType == SlotType.INPUT) {
                        tileEntity.markRecipeDirty();
                    }
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == finalUpgradeSlotLocation) return TagUtil.isTaggedMachineUpgradeItem(stack);
                VESlotManager manager = tileEntity.getSlotManagers().get(slot);
                if (manager.getSlotType() == SlotType.INPUT) {
                    for (Recipe<?> recipe : tileEntity.getPotentialRecipes()) {
                        VERecipe veRecipe = (VERecipe) recipe;
                        if (veRecipe.getIngredient(manager.getRecipePos()).test(stack)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public ItemStackHandler createHandler(int size, IVEPoweredTileEntity tileEntity) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (tileEntity.getUpgradeSlotId() == slot) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack);
                }
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot == tileEntity.getUpgradeSlotId()) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack) ? super.insertItem(slot, stack, simulate) : stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    /**
     * @param status boolean status of the slot
     * @param slotId int id of the slot
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
     * @return a VEEnergyStorage object or null if this tile is not an instance of poweredTileEntity
     */
    public LazyOptional<VEEnergyStorage> createEnergy() {
        if (this instanceof IVEPoweredTileEntity IVEPoweredTileEntity) {
            VEEnergyStorage storage = new VEEnergyStorage(IVEPoweredTileEntity.getMaxPower(), IVEPoweredTileEntity.getTransferRate());
            if (this instanceof IVEPowerGenerator) {
                storage.setMaxReceive(0);
            }
            return LazyOptional.of(() -> storage);
        }
        return LazyOptional.empty();
    }

    /**
     * Call this to consume energy
     * Note that tiles now require an upgrade slot and thus an inventory to properly function here
     * If you need to consume energy WITHOUT an upgrade slot make a new method that does not have this.
     * Throws an error if missing the power consumeEnergy IMPL
     */
    public void consumeEnergy() {
        if (this instanceof IVEPoweredTileEntity IVEPoweredTileEntity) {
            energy.ifPresent(e -> e
                    .consumeEnergy(
                            this.consumptionMultiplier(IVEPoweredTileEntity.getPowerUsage(),
                                    getInventoryHandler().getStackInSlot(IVEPoweredTileEntity.getUpgradeSlotId()).copy())));
        } else {
            throw new NotImplementedException("Missing implementation of IVEPoweredTileEntity in class: " + this.getClass().getName());
        }
    }


    private LazyOptional<IEnergyStorage> capability;

    /**
     * Like consumeEnergy this requires that the object has an inventory
     *
     * @return True if the object has enough energy to be able to continue
     * Throws an error if missing the power consumeEnergy IMPL
     */
    public boolean canConsumeEnergy() {
        if (this instanceof IVEPoweredTileEntity ivePoweredTileEntity) {
            if (ivePoweredTileEntity.getMaxPower() == 0) return true; // For tiles that do not consume power
            if (capability == null) {
                capability = this.getCapability(ForgeCapabilities.ENERGY, null);
            }
            return capability.map(IEnergyStorage::getEnergyStored).orElse(0)
                    > this.consumptionMultiplier(ivePoweredTileEntity.getPowerUsage(), getInventoryHandler().getStackInSlot(ivePoweredTileEntity.getUpgradeSlotId()).copy());
        } else {
            throw new NotImplementedException("Missing implementation of IVEPoweredTileEntity in class: " + this.getClass().getName());
        }
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
     * @see #createHandler(int)
     * @see #createHandler(int, IVEPoweredTileEntity)
     */
    public abstract @Nullable
    ItemStackHandler getInventoryHandler();

    /**
     * Important note. If the entity has no slot managers return a new ArrayList otherwise this will crash
     *
     * @return A not null List<VESlotManager> list
     */
    public abstract @Nonnull List<VESlotManager> getSlotManagers();

    /**
     * When a data packet is received load it.
     *
     * @param net Connection
     * @param pkt ClientboundBlockEntityDataPacket
     */
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> {
            if (pkt.getTag() != null && pkt.getTag().contains("energy")) e.setEnergy(pkt.getTag().getInt("energy"));
        });
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressCounterPercent() {
        if (length != 0) {
            return (int) (100 - (((float) counter / (float) length) * 100));
        } else {
            return 0;
        }
    }

    public int ticksLeft() {
        return counter;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public LazyOptional<VEEnergyStorage> getEnergy() {
        return energy;
    }

    public void markRecipeDirty() {
        this.isRecipeDirty = true;
    }

    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;

        this.potentialRecipes = RecipeCache.getFluidRecipesFromCache(level, this.getRecipeType(), getSlotManagers(), getRelationalTanks(), this, true);
        if (this.potentialRecipes.size() == 1) {
            List<FluidStack> inputFluids = this.getRelationalTanks().stream()
                    .filter(tank -> tank.getTankType() == TankType.INPUT)
                    .map(tank -> tank.getTank().getFluid()).toList();

            ItemStackHandler handler = this.getInventoryHandler();
            List<ItemStack> inputItems = new ArrayList<>();
            if (handler != null) {
                inputItems = this.getSlotManagers().stream()
                        .filter(manager -> manager.getSlotType() == SlotType.INPUT)
                        .map(manager -> manager.getItem(handler)).toList();
            }
            VEFluidRecipe newRecipe = RecipeCache.getFluidRecipeFromCache(level, getRecipeType(), inputFluids, inputItems);

            if (newRecipe == null) {
                counter = 0;
                length = 0;
                this.selectedRecipe = null;
                return;
            }

            int newLength;

            if (this instanceof IVEPoweredTileEntity poweredTileEntity && handler != null) {
                newLength = this.calculateCounter(newRecipe.getProcessTime(),
                        handler.getStackInSlot(poweredTileEntity.getUpgradeSlotId()).copy());
            } else {
                newLength = this.calculateCounter(newRecipe.getProcessTime(), ItemStack.EMPTY);
            }


            double ratio = (double) length / (double) newLength;
            length = newLength;
            counter = (int) (counter / ratio);

            if (this.selectedRecipe != newRecipe) {
                this.selectedRecipe = newRecipe;
                counter = newLength;
            }
        } else {
            counter = 0;
            length = 0;
            this.selectedRecipe = null;
        }
    }

    public void doExtraRecipeProcessing() {

    }

    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return this.recipeType;
    }

    public List<? extends Recipe<?>> getPotentialRecipes() {
        return potentialRecipes;
    }
}
