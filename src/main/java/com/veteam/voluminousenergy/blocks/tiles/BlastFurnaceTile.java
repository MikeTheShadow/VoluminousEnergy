package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.BlastFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlastFurnaceTile extends VEMultiBlockTileEntity {
    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager heatTankItemTopManager = new VESlotManager(0, Direction.UP,false,"slot.voluminousenergy.input_slot", SlotType.INPUT,"heat_top_manager");
    public VESlotManager heatTankItemBottomManager = new VESlotManager(1,Direction.DOWN,false,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"heat_bottom_manager");
    public VESlotManager firstInputSlotManager = new VESlotManager(2, Direction.EAST, false, "slot.voluminousenergy.input_slot",SlotType.INPUT,"first_input_manager");
    public VESlotManager secondInputSlotManager = new VESlotManager(3, Direction.WEST, false, "slot.voluminousenergy.input_slot",SlotType.INPUT,"second_input_manager");
    public VESlotManager outputSlotManager = new VESlotManager(4, Direction.NORTH, false, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"output_manager");

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(heatTankItemTopManager);
            add(heatTankItemBottomManager);
            add(firstInputSlotManager);
            add(secondInputSlotManager);
            add(outputSlotManager);
        }
    };

    RelationalTank heatTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT,"heatTank:heat_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(heatTank);
            heatTank.setAllowAny(true);
        }
    };

    private byte tick = 19;

    public ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return this.slotManagers;
    }

    @Nonnull
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return this.energy;
    }

    public BlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.BLAST_FURNACE_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();
        tick++;
        if (tick == 20){
            tick = 0;
            validity = isMultiBlockValid(VEBlocks.TITANIUM_MACHINE_CASING_BLOCK);
        }
        if (!(validity)) {
            return;
        }

        // Main idea: Heat --> Needs to be "high enough" to work, 2 Item Inputs, 1 item output.
        ItemStack heatTankItemInputTop = inventory.getStackInSlot(0).copy();
        ItemStack heatTankItemInputBottom = inventory.getStackInSlot(1).copy();
        ItemStack firstItemInput = inventory.getStackInSlot(2).copy();
        ItemStack secondItemInput = inventory.getStackInSlot(3).copy();
        ItemStack itemOutput = inventory.getStackInSlot(4).copy();

        heatTank.setIOItemstack(heatTankItemInputTop.copy(),heatTankItemInputBottom.copy());

        if(inputFluid(heatTank,0,1)) return;
        if(this.outputFluid(heatTank,0,1)) return;

        // Main Processing occurs here:
        if (heatTank != null || !heatTank.getTank().isEmpty()) {
            IndustrialBlastingRecipe recipe = RecipeUtil.getIndustrialBlastingRecipe(level, firstItemInput.copy(), secondItemInput.copy());

            if (recipe != null) {
                // Tank fluid amount check + capacity and recipe checks
                if (itemOutput.getCount() < recipe.getResult().getMaxStackSize() &&
                        heatTank.getTank().getFluidAmount() >= Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get() &&
                        heatTank.getTank().getFluid().getRawFluid().getAttributes().getTemperature() >= recipe.getMinimumHeat() &&
                        recipe.getFirstInputAsList().contains(firstItemInput.getItem()) &&
                        firstItemInput.getCount() >= recipe.getIngredientCount() &&
                        recipe.ingredientListIncludingSeconds.contains(secondItemInput.getItem()) &&
                        secondItemInput.getCount() >= recipe.getSecondInputAmount()){
                    // Check for power
                    if (canConsumeEnergy()) {
                        if (counter == 1){

                            // Drain Input
                            heatTank.getTank().drain(Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get(), IFluidHandler.FluidAction.EXECUTE);

                            inventory.extractItem(2,recipe.getIngredientCount(),false);
                            inventory.extractItem(3, recipe.getSecondInputAmount(),false);

                            // Place the new output stack on top of the old one
                            if (itemOutput.getItem() != recipe.getResult().getItem()) {
                                if (itemOutput.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                                    itemOutput.setCount(1);
                                }
                            }
                            inventory.insertItem(4, recipe.getResult().copy(),false); // CRASH the game if this is not empty!

                            counter--;
                            consumeEnergy();
                            this.setChanged();
                        } else if (counter > 0){
                            counter--;
                            consumeEnergy();
                        } else {
                            counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(5).copy());
                            length = counter;
                        }
                    } // Energy Check
                } else { // Set counter to zero
                    counter = 0;
                }
            }
        }

    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.BLAST_FURNACE_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(5).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.BLAST_FURNACE_POWER_USAGE.get(), this.inventory.getStackInSlot(5).copy());
    }

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag) {
        counter = tag.getInt("counter");
        length = tag.getInt("length");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("counter", counter);
        tag.putInt("length", length);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createInputFluidHandler() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return heatTank == null ? FluidStack.EMPTY : heatTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return heatTank == null ? 0 : heatTank.getTank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return heatTank != null && heatTank.getTank().isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && heatTank.getTank().isEmpty() || resource.isFluidEqual(heatTank.getTank().getFluid())) {
                    return heatTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(heatTank.getTank().getFluid())) {
                    return heatTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (heatTank.getTank().getFluidAmount() > 0) {
                    return heatTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    return stack.getItem() instanceof BucketItem || stack.getItem() == Items.BUCKET;
                } else if (slot == 2) {
                    return RecipeUtil.isFirstIngredientForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 3) {
                    return RecipeUtil.isSecondIngredientForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 4) {
                    return RecipeUtil.isAnOutputForIndustrialBlastingRecipe(level, stack.copy());
                } else if (slot == 5){
                    return stack.getItem().equals(VEItems.QUARTZ_MULTIPLIER);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private @Nonnull VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.BLAST_FURNACE_MAX_POWER.get(), Config.BLAST_FURNACE_TRANSFER.get()); // Max Power Storage, Max transfer
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new BlastFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return heatTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public boolean getMultiblockValidity(){
        return validity;
    }

    public RelationalTank getHeatTank(){
        return this.heatTank;
    }

    public int getTemperatureKelvin(){
        return this.heatTank.getTank().getFluid().getRawFluid().getAttributes().getTemperature();
    }

    public int getTemperatureCelsius(){
        return getTemperatureKelvin()-273;
    }

    public int getTemperatureFahrenheit(){
        return (int) ((getTemperatureKelvin()-273) * 1.8)+32;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == heatTankItemTopManager.getSlotNum()) heatTankItemTopManager.setStatus(status);
        else if (slotId == heatTankItemBottomManager.getSlotNum()) heatTankItemBottomManager.setStatus(status);
        else if(slotId == firstInputSlotManager.getSlotNum()) firstInputSlotManager.setStatus(status);
        else if (slotId == secondInputSlotManager.getSlotNum()) secondInputSlotManager.setStatus(status);
        else if(slotId == outputSlotManager.getSlotNum()) outputSlotManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == heatTankItemTopManager.getSlotNum()) heatTankItemTopManager.setDirection(direction);
        else if (slotId == heatTankItemBottomManager.getSlotNum()) heatTankItemBottomManager.setDirection(direction);
        else if(slotId == firstInputSlotManager.getSlotNum()) firstInputSlotManager.setDirection(direction);
        else if (slotId == secondInputSlotManager.getSlotNum()) secondInputSlotManager.setDirection(direction);
        else if(slotId == outputSlotManager.getSlotNum()) outputSlotManager.setDirection(direction);
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return Collections.singletonList(heatTank);
    }
}