package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.upgrades.MysteriousMultiplier;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.AbstractRecipeProcessor;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VEPortTransfer;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import com.veteam.voluminousenergy.util.records.CounterLength;
import com.veteam.voluminousenergy.util.tiles.CapabilityMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class VETileEntity extends BlockEntity implements MenuProvider {

    VEItemStackHandler inventory;
    private final RecipeType<? extends Recipe<?>> recipeType;

    final List<VERelationalTank> tanks = new ArrayList<>();
    final List<VESlotManager> managers = new ArrayList<>();
    private final VEFaceIO faceIO = new VEFaceIO();
    AbstractRecipeProcessor recipeProcessor;
    boolean sendsOutPower;

    public static final int DEFAULT_TANK_CAPACITY = 4000;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public Object2IntOpenHashMap<ResourceLocation> getRecipesUsed() {
        return recipesUsed;
    }

    public VETileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            RecipeType<? extends Recipe<?>> recipeType) {
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

    public @Nonnull List<VERelationalTank> getRelationalTanks() {
        return tanks;
    }

    public boolean interactWithTank(Player player, AbstractContainerMenu menu, int tankId, boolean putFluid) {
        if (level == null || level.isClientSide || menu != player.containerMenu
                || tankId < 0 || tankId >= tanks.size()) {
            return false;
        }

        ItemStack carriedStack = menu.getCarried();
        IItemHandler playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (carriedStack.isEmpty()
                || carriedStack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM) == null
                || playerInventory == null) {
            return false;
        }

        VERelationalTank relationalTank = tanks.get(tankId);
        if (putFluid && relationalTank.getTankType() == TankType.OUTPUT) {
            return false;
        }

        IFluidHandler selectedTank = getSelectedTankHandler(tankId);
        FluidActionResult result = putFluid
                ? FluidUtil.tryEmptyContainerAndStow(
                        carriedStack, selectedTank, playerInventory, Integer.MAX_VALUE, player, true)
                : FluidUtil.tryFillContainerAndStow(
                        carriedStack, selectedTank, playerInventory, Integer.MAX_VALUE, player, true);
        if (!result.isSuccess()) {
            return false;
        }

        menu.setCarried(result.getResult());
        if (recipeProcessor instanceof BasicProcessor processor) {
            processor.markRecipeDirty();
        }
        setChanged();
        updateClients();
        menu.broadcastChanges();
        return true;
    }

    public boolean isFluidValidForTank(VERelationalTank tank, FluidStack stack) {
        if (tank.isAllowAny()) {
            return true;
        }
        if (tank.getValidator() != null) {
            return tank.getValidator().validateFluid(stack, this);
        }
        if (recipeProcessor instanceof BasicProcessor processor) {
            for (VERecipe recipe : processor.getPotentialRecipes()) {
                if (recipe.getFluidIngredient(tank.getRecipePos()).test(stack)) {
                    return true;
                }
            }
        }
        return tank.getTank().isFluidValid(stack);
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num >= getRelationalTanks().size() || num < 0) {
            return FluidStack.EMPTY;
        }
        return getRelationalTanks().get(num).getTank().getFluid();
    }

    // END OF FLUID STUFF

    /**
     * Must include a call to updateClients();
     * This message can be removed if updateClients(); is found to be useless
     */
    public void tick() {
        updateClients();
        if(this.recipeProcessor != null)
            recipeProcessor.tick(this);

        VEPortTransfer.tick(this);

        if (this.sendsOutPower)
            sendOutPower();
    }

    public void setLit(boolean lit) {
        BlockState state = this.getBlockState();
        if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT) != lit) {
            this.level.setBlock(this.worldPosition, state.setValue(BlockStateProperties.LIT, lit), 3);
        }
    }

    /**
     * Call this method whenever something in the tile entity has been updated.
     * If the server and client are seeing something different this is why
     * Note calling this only updates the clients and doesn't save anything to file
     * if you wish to save to file call setChanged();
     */
    public void updateClients() {
        if (level == null)
            return;
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    public IFluidHandler getSelectedTankHandler(int tankId) {
        return new SelectedTankFluidHandler(this, getRelationalTank(tankId));
    }

    public List<VEIOPort> getIOPorts() {
        List<VEIOPort> ports = new ArrayList<>(managers);
        ports.addAll(tanks);
        return ports;
    }

    public VEFaceIO getFaceIO() {
        return faceIO;
    }

    public @org.jetbrains.annotations.Nullable VEIOPort getIOPort(boolean fluid, int portId) {
        if (portId < 0 || portId >= (fluid ? tanks.size() : managers.size())) {
            return null;
        }
        return fluid ? tanks.get(portId) : managers.get(portId);
    }

    public void refreshPortFaces() {
        capabilityMap = null;
        if (level != null && !level.isClientSide) {
            level.invalidateCapabilities(worldPosition);
        }
    }

    private static class SelectedTankFluidHandler implements IFluidHandler {

        private final VETileEntity tile;
        private final VERelationalTank relationalTank;

        private SelectedTankFluidHandler(VETileEntity tile, VERelationalTank relationalTank) {
            this.tile = tile;
            this.relationalTank = relationalTank;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return tank == 0 ? relationalTank.getTank().getFluid() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? relationalTank.getTank().getCapacity() : 0;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0
                    && relationalTank.getTankType() != TankType.OUTPUT
                    && tile.isFluidValidForTank(relationalTank, stack);
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if (relationalTank.getTankType() == TankType.OUTPUT || !isFluidValid(0, resource)) {
                return 0;
            }
            return relationalTank.getTank().fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return relationalTank.getTank().drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return relationalTank.getTank().drain(maxDrain, action);
        }
    }

    public int getEnergyCostMultiplier() {
        return this.consumptionMultiplier(this.energy.getConsumption(), this.energy.getUpgradeSlotId());
    }

    protected int consumptionMultiplier(int consumption, int slot) {
        ItemStack upgradeStack = slot == -1 ? ItemStack.EMPTY : getInventory().getStackInSlot(slot);

        float upgradeMulti = upgradeStack.getOrDefault(VEDataComponents.MULTIPLIER_DATA, 0.0f);

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
        } else if (upgradeMulti != 0) {
            MysteriousMultiplier.QualityTier qualityTier = MysteriousMultiplier.getQualityTier(upgradeMulti);
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

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registry) {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag, registry);
        return compoundTag;
    }

    @Override
    public void setComponents(DataComponentMap pComponents) {
        super.setComponents(pComponents);
    }

    @Override
    public DataComponentMap components() {
        return super.components();
    }

    /**
     * Loads inventory, energy, slot managers, counter, and length.
     *
     * @param tag CompoundTag
     */
    @Override
    public void loadAdditional(CompoundTag tag, @NotNull HolderLookup.Provider registry) {
        faceIO.load(tag.getCompound("face_io"));
        CompoundTag inv = tag.getCompound("inv");
        VEItemStackHandler handler = getInventory();

        if (handler != null)
            handler.deserializeNBT(registry, inv);
        if (energy != null)
            energy.deserializeNBT(tag);

        for (VESlotManager manager : getSlotManagers()) {
            manager.read(tag);
        }

        for (VERelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = tag.getCompound(relationalTank.getTankName());
            relationalTank.getTank().readFromNBT(registry, compoundTag);
            relationalTank.readGuiProperties(tag);
        }

        if (tag.contains("sends_out_power")) {
            this.sendsOutPower = tag.getBoolean("sends_out_power");
        }

        if (tag.contains("recipes_used", 10)) {
            CompoundTag recipesTag = tag.getCompound("recipes_used");
            for (String s : recipesTag.getAllKeys()) {
                this.recipesUsed.put(ResourceLocation.parse(s), recipesTag.getInt(s));
            }
        }

        super.loadAdditional(tag, registry);
    }

    /**
     * Saves inventory, energy, slot managers, counter, and length.
     * To save the tile call setChanged();
     *
     * @param tag CompoundTag
     */
    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registry) {
        tag.put("face_io", faceIO.save());
        ItemStackHandler handler = getInventory();
        if (handler != null) {
            CompoundTag compound = handler.serializeNBT(registry);
            tag.put("inv", compound);
        }

        if (energy != null)
            energy.serializeNBT(tag);

        for (VESlotManager manager : getSlotManagers()) {
            manager.write(tag);
        }

        for (VERelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = new CompoundTag();
            relationalTank.getTank().writeToNBT(registry, compoundTag);
            tag.put(relationalTank.getTankName(), compoundTag);
            relationalTank.writeGuiProperties(tag);
        }

        tag.putBoolean("sends_out_power", sendsOutPower);

        if (!this.recipesUsed.isEmpty()) {
            CompoundTag recipesTag = new CompoundTag();
            this.recipesUsed.forEach((id, count) -> recipesTag.putInt(id.toString(), count));
            tag.put("recipes_used", recipesTag);
        }

        super.saveAdditional(tag, registry);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (this.level != null) {
            CompoundTag customDataTag = this.saveCustomOnly(this.level.registryAccess());

            if (!customDataTag.isEmpty()) {
                components.set(DataComponents.CUSTOM_DATA, CustomData.of(customDataTag));
            }
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        CustomData customData = componentInput.get(DataComponents.CUSTOM_DATA);

        if (customData != null) {
            CompoundTag dataTag = customData.getUnsafe();
            loadAdditional(dataTag, this.level.registryAccess());
        }
    }

    public void recordRecipeUsed(VERecipe recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.id();

            if (resourcelocation != null) {
                this.recipesUsed.addTo(resourcelocation, 1);
            }
        }
    }

    public void recordRecipeUsed(RecipeHolder<?> recipe) {
        if (recipe != null) {
            this.recipesUsed.addTo(recipe.id(), 1);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        if (tileEntity instanceof VETileEntity tile && tile.energy != null) {
            return tile.energy.receiveEnergy(maxReceive, false);
        }
        return 0;
    }

    void sendOutPower() {
        for (Direction dir : Direction.values()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
            Direction opposite = dir.getOpposite();
            if (tileEntity != null) {
                // If less energy stored then max transfer send the all the energy stored rather
                // than the max transfer amount
                int smallest = Math.min(Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
                int received = receiveEnergy(tileEntity, opposite, smallest);
                energy.consumeEnergy(received);
                if (energy.getEnergyStored() <= 0) {
                    break;
                }
            }
        }
    }

    CapabilityMap capabilityMap = null;

    public CapabilityMap getCapabilityMap() {
        if (this.capabilityMap == null) {
            this.capabilityMap = new CapabilityMap(inventory, getSlotManagers(), getRelationalTanks(), energy, this);
        }
        return this.capabilityMap;
    }

    /**
     * Call this to consume energy.
     * Note that tiles now require an upgrade slot and thus an inventory to properly
     * function here.
     * If you need to consume energy WITHOUT an upgrade slot make a new method that
     * does not have this.
     * Throws an error if missing the power consumeEnergy IMPL
     */
    public boolean consumeEnergy() {
        if (this.energy == null || !this.canConsumeEnergy())
            return false;
        energy.consumeEnergy(this.consumptionMultiplier(energy.getConsumption(), energy.getUpgradeSlotId()));
        return true;
    }

    /**
     * @return True if the object has enough energy to be able to continue. Or the
     *         entity doesn't run on energy
     */
    public boolean canConsumeEnergy() {
        if (energy != null) {
            if (energy.getMaxEnergyStored() == 0)
                return true; // For tiles that do not consume power
            return energy.getEnergyStored() > this.consumptionMultiplier(energy.getConsumption(),
                    energy.getUpgradeSlotId());
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
    public abstract AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory,
            @NotNull Player player);

    /**
     * We do a null check on inventory so this can be null. Might change though
     * REMEMBER YOU NEED TO BUILD YOUR OWN INVENTORY HANDLER
     * USE EITHER A NEWLY CREATED ONE OR ONE OF THE createHandler's defined here
     *
     * @return a ItemStackHandler or null if the object lacks an inventory
     */
    public @Nullable VEItemStackHandler getInventory() {
        return this.inventory;
    }

    /**
     * Important note. If the entity has no slot managers return a new ArrayList
     * otherwise this will crash
     *
     * @return A not null List<VESlotManager> list
     */
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return this.managers;
    }

    /*
     * When a data packet is received load it.
     */
    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt,
            @NotNull HolderLookup.Provider lookupProvider) {
        if (energy != null && pkt.getTag().contains("energy"))
            energy.setEnergy(pkt.getTag().getInt("energy"));
        this.loadAdditional(pkt.getTag(), lookupProvider);
        super.onDataPacket(net, pkt, lookupProvider);
    }

    public int progressBurnCounterPX(int px) {

        CounterLength counterLength = this.getData(VEAttachments.COUNTER_LENGTH);

        int counter = counterLength.counter();
        int length = counterLength.length();
        if (counter != 0 && length != 0)
            return (px * (((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressBurnCounterPX(int px, int counter, int length) {
        if (counter != 0 && length != 0)
            return (px * (((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressProcessingCounterPX(int px) {
        CounterLength counterLength = this.getData(VEAttachments.COUNTER_LENGTH);
        int counter = counterLength.counter();
        int length = counterLength.length();
        if (counter != 0 && length != 0)
            return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressCounterPercent() {
        CounterLength counterLength = this.getData(VEAttachments.COUNTER_LENGTH);
        int counter = counterLength.counter();
        int length = counterLength.length();
        if (length != 0) {
            return (int) (100 - (((float) counter / (float) length) * 100));
        } else {
            return 0;
        }
    }

    public int progressCounterPercent(int counter, int length) {
        if (length != 0) {
            return (int) (100 - (((float) counter / (float) length) * 100));
        } else {
            return 0;
        }
    }

    public int ticksLeft() {
        return this.getData(VEAttachments.COUNTER_LENGTH).counter();
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

    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return this.recipeType;
    }

    void addTanks(List<VERelationalTank> tanks) {
        this.tanks.addAll(tanks);
    }

    void addSlots(List<VESlotManager> managers) {
        this.managers.addAll(managers);
    }

    public List<VERelationalTank> getTanks() {
        return tanks;
    }

    public VERelationalTank getRelationalTank(int id) {
        return tanks.get(id);
    }

    public int getTankCapacity(int id) {
        return this.tanks.get(id).getTank().getCapacity();
    }

    public List<VESlotManager> getManagers() {
        return managers;
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
        this.setChanged();
    }

    public AbstractRecipeProcessor getRecipeProcessor() {
        return recipeProcessor;
    }
}
