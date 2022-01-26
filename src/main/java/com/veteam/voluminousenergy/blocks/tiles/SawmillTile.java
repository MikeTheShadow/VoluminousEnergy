package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.SawmillContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.SawmillingRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SawmillTile extends VEFluidTileEntity {

    // Handlers
    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    // Slot Managers
    public VESlotManager inputSm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"input_slot");
    public VESlotManager plankSm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"plank_slot");
    public VESlotManager secondOutputSm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"second_output_slot");
    public VESlotManager bucketTopSm = new VESlotManager(3, Direction.SOUTH,true,"slot.voluminousenergy.input_slot",SlotType.INPUT,"bucket_top_slot");
    public VESlotManager bucketBottomSm = new VESlotManager(4, Direction.EAST,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT,"bucket_bottom_slot");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(inputSm);
        add(plankSm);
        add(secondOutputSm);
        add(bucketTopSm);
        add(bucketBottomSm);
    }};

    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.OUTPUT,0,"outputTank:output_tank_gui");
    private final FluidStack configuredFluidForNoRecipe = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(Config.SAWMILL_FLUID_LOCATION.get()))), Config.SAWMILL_FLUID_AMOUNT.get());

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(outputTank);
    }};

    private int counter;
    private int length;

    private final ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return energy;
    }

    public SawmillTile(BlockPos pos, BlockState state) {
        super(VEBlocks.SAWMILL_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack logInput = inventory.getStackInSlot(0).copy(); // Log input
        //ItemStack plankOutput = inventory.getStackInSlot(1).copy(); // Plank Output
        //ItemStack secondOutput = inventory.getStackInSlot(2).copy(); // Second output
        ItemStack topBucketInput = inventory.getStackInSlot(3).copy(); // Top bucket input slot
        ItemStack bottomBucketOutput = inventory.getStackInSlot(4).copy(); // Bottom bucket output slot

        outputTank.setInput(topBucketInput.copy());
        outputTank.setOutput(bottomBucketOutput.copy());

        if(this.inputFluid(outputTank,3,4)) return;
        if(this.outputFluid(outputTank,3,4)) return;

        // Resolve recipes:
        if (!logInput.isEmpty()){
            SawmillingRecipe sawmillingRecipe = RecipeUtil.getSawmillingRecipeFromLog(level, logInput.copy());
            ItemStack plankOutputStack;
            ItemStack secondOutputStack;

            if (sawmillingRecipe == null && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()){ // Recipe is null, use alternative method if allowed
                plankOutputStack = RecipeUtil.getPlankFromLogParallel(level, logInput.copy()); //RecipeUtil.getPlankFromLogParallel(level, logInput.copy());
                secondOutputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Config.SAWMILL_SECOND_OUTPUT_RESOURCE_LOCATION.get())), Config.SAWMILL_SECOND_OUTPUT_COUNT.get());

                if (plankOutputStack != null){ // Valid Item!
                    if (this.configuredFluidForNoRecipe != null
                        && (outputTank.getTank().getFluidAmount() + Config.SAWMILL_FLUID_AMOUNT.get()) <= TANK_CAPACITY
                        && (inventory.getStackInSlot(1).isEmpty() || inventory.getStackInSlot(1).getItem() == plankOutputStack.getItem())
                        && (inventory.getStackInSlot(2).isEmpty() || inventory.getStackInSlot(2).getItem() == secondOutputStack.getItem())
                        && (inventory.getStackInSlot(1).getCount() + Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get()) <= 64
                        && (inventory.getStackInSlot(2).getCount() + Config.SAWMILL_SECOND_OUTPUT_COUNT.get()) <= 64){

                        if (outputTank.getTank().getFluid().isFluidEqual(this.configuredFluidForNoRecipe.copy()) || outputTank.getTank().getFluid().isEmpty()){
                            plankOutputStack.setCount(Config.SAWMILL_PRIMARY_OUTPUT_COUNT.get());
                            coreTickProcessing(null,
                                    logInput,
                                    plankOutputStack.copy(),
                                    secondOutputStack.copy(),
                                    this.configuredFluidForNoRecipe.copy()
                            );
                        } else {
                            counter = 0;
                        }
                    } else {
                        counter = 0;
                    }
                } else {
                    counter = 0;
                }

            } else { // Using Recipe
                plankOutputStack = sawmillingRecipe.result.copy();
                secondOutputStack = sawmillingRecipe.secondResult.copy();
                FluidStack outputFluid = sawmillingRecipe.getOutputFluid().copy();

                if ((outputTank.getTank().getFluidAmount() + outputFluid.getAmount()) <= TANK_CAPACITY
                        && (inventory.getStackInSlot(1).isEmpty() || inventory.getStackInSlot(1).getItem() == plankOutputStack.getItem())
                        && (inventory.getStackInSlot(2).isEmpty() || inventory.getStackInSlot(2).getItem() == secondOutputStack.getItem())
                        && (inventory.getStackInSlot(1).getCount() + sawmillingRecipe.result.getCount()) <= 64
                        && (inventory.getStackInSlot(2).getCount() + sawmillingRecipe.secondResult.getCount()) <= 64){
                    if (outputTank.getTank().getFluid().isFluidEqual(outputFluid.copy()) || outputTank.getTank().getFluid().isEmpty()){
                        coreTickProcessing(sawmillingRecipe, logInput, plankOutputStack, secondOutputStack, outputFluid);
                    } else {
                        counter = 0;
                    }
                } else {
                    counter = 0;
                }
            }
        } else {
            counter = 0;
        }
    }

    private void coreTickProcessing(@Nullable SawmillingRecipe sawmillingRecipe,
                                    ItemStack logInput,
                                    ItemStack resolvedPlankOutput,
                                    ItemStack secondItemOutput,
                                    FluidStack fluidOutput){
        if (canConsumeEnergy()){
            if (counter == 1){
                // Core processing occurs here

                inventory.extractItem(0, (sawmillingRecipe != null ? sawmillingRecipe.ingredientCount : Config.SAWMILL_LOG_CONSUMPTION_RATE.get()), false); // Extract log

                // Plank output
                ItemStack currentPlankStack = inventory.getStackInSlot(1);
                if (currentPlankStack.getItem() != resolvedPlankOutput.getItem() || currentPlankStack.getItem() == Items.AIR){
                    if (currentPlankStack.getItem() == Items.AIR){
                        currentPlankStack.setCount(1);
                    }
                    inventory.insertItem(1, resolvedPlankOutput.copy(), false);
                } else { // Assuming the recipe output item is already in the output slot
                   // currentPlankStack.setCount(.getCount()); // Simply change the stack to equal the output amount
                    inventory.insertItem(1, resolvedPlankOutput.copy(),false); // Place the new output stack on top of the old one
                }

                // Second output
                ItemStack currentSecondOutput = inventory.getStackInSlot(2);
                if (currentSecondOutput.getItem() != secondItemOutput.getItem() || currentSecondOutput.getItem() == Items.AIR){
                    if (currentSecondOutput.getItem() == Items.AIR){
                        currentSecondOutput.setCount(1);
                    }
                    inventory.insertItem(2, secondItemOutput.copy(), false);
                } else { // Assuming the recipe output item is already in the output slot
                    //currentSecondOutput.setCount(secondItemOutput.getCount()); // Simply change the stack to equal the output amount
                    inventory.insertItem(2, secondItemOutput.copy(),false); // Place the new output stack on top of the old one
                }

                // Output Tank
                if (outputTank.getTank().getFluid().getRawFluid() != fluidOutput.getRawFluid()){
                    outputTank.getTank().setFluid(fluidOutput.copy());
                } else {
                    outputTank.getTank().fill(fluidOutput.copy(), IFluidHandler.FluidAction.EXECUTE);
                }

                counter--;
                consumeEnergy();
                this.setChanged();
            } else if (counter > 0){
                counter--;
                consumeEnergy();
            } else {
                counter = sawmillingRecipe != null
                                ? this.calculateCounter(sawmillingRecipe.getProcessTime(), inventory.getStackInSlot(5).copy())    // Sawmill recipe not null
                                : this.calculateCounter(Config.SAWMILL_PROCESSING_TIME.get(), inventory.getStackInSlot(5).copy());// Use default values when null
                length = counter;
            }
        } else {
            counter = 0;
        }
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.SAWMILL_POWER_USAGE.get(),
                                this.inventory.getStackInSlot(5).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.SAWMILL_POWER_USAGE.get(), this.inventory.getStackInSlot(5).copy());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createOutputFluidHandler(){
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return outputTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return outputTank.getTank().getTankCapacity(0);
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (!inventory.getStackInSlot(0).isEmpty()){
                    ItemStack logStack = inventory.getStackInSlot(0).copy();
                    SawmillingRecipe sawmillingRecipe = RecipeUtil.getSawmillingRecipeFromLog(level, logStack);

                    if (sawmillingRecipe == null){
                        if (!configuredFluidForNoRecipe.isFluidEqual(stack) || !Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) return false;
                        ItemStack plankStack = RecipeUtil.getPlankFromLogParallel(level, logStack);
                        return plankStack != null && plankStack.isEmpty();
                    } else { // Sawmilling recipe is not null
                        return sawmillingRecipe.getOutputFluid().isFluidEqual(stack);
                    }
                } else if (!inventory.getStackInSlot(1).isEmpty()){
                    ItemStack plankStack = inventory.getStackInSlot(1).copy();
                    SawmillingRecipe sawmillingRecipe = RecipeUtil.getSawmillingRecipeFromPlank(level, plankStack);

                    if (sawmillingRecipe == null){
                        if (!configuredFluidForNoRecipe.isFluidEqual(stack) || !Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) return false;
                        ArrayList<ItemStack> logList = RecipeUtil.getLogFromPlankParallel(level, plankStack);
                        return logList == null ? false : !(logList.isEmpty());
                    } else { // Sawmilling recipe is not null
                        return sawmillingRecipe.getOutputFluid().isFluidEqual(stack);
                    }
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if(isFluidValid(outputTank.getId(),resource) && outputTank.getTank().isEmpty()
                        || resource.isFluidEqual(outputTank.getTank().getFluid())) {
                    return outputTank.getTank().fill(resource, action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                if(resource.isFluidEqual(outputTank.getTank().getFluid())) {
                    return outputTank.getTank().drain(resource,action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if(outputTank.getTank().getFluidAmount() > 0) {
                    if (Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                        return outputTank.getTank().drain(maxDrain, action);
                    } else if (outputTank.getTankType() != TankType.INPUT) {
                        return outputTank.getTank().drain(maxDrain, action);
                    }
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
                if(slot == 0){ // Log inputted into the input slot
                    ItemStack altPlankStack = RecipeUtil.getPlankFromLogParallel(level, stack.copy());
                    return (altPlankStack != null);
                } else if (slot == 1){
                    ArrayList<ItemStack> plankList = RecipeUtil.getLogFromPlankParallel(level, stack.copy());
                    return plankList != null && !plankList.isEmpty();
                } else if (slot == 2){
                    return true; // TODO: better than this
                } else if (slot == 3 || slot == 4) {
                    return stack.getItem() instanceof BucketItem;
                } else if (slot == 5){
                    return stack.getItem().equals(VEItems.QUARTZ_MULTIPLIER); // this is the upgrade slot
                }
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private @Nonnull VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.SAWMILL_MAX_POWER.get(), Config.SAWMILL_TRANSFER.get());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new SawmillContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return outputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getOutputTank(){
        return this.outputTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }
}
