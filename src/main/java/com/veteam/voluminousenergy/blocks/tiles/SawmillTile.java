package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.SawmillContainer;
import com.veteam.voluminousenergy.recipe.SawmillingRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
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

public class SawmillTile extends VEFluidTileEntity implements IVEPoweredTileEntity,IVECountable {

    // Slot Managers
    public VESlotManager inputSm = new VESlotManager(0, Direction.UP, true, SlotType.INPUT);
    public VESlotManager plankSm = new VESlotManager(1, Direction.DOWN, true,SlotType.OUTPUT);
    public VESlotManager secondOutputSm = new VESlotManager(2, Direction.NORTH, true,SlotType.OUTPUT);
    public VESlotManager bucketTopSm = new VESlotManager(3, Direction.SOUTH,true,SlotType.INPUT);
    public VESlotManager bucketBottomSm = new VESlotManager(4, Direction.EAST,true,SlotType.OUTPUT);

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

    public SawmillTile(BlockPos pos, BlockState state) {
        super(VEBlocks.SAWMILL_TILE.get(), pos, state,SawmillingRecipe.RECIPE_TYPE);
        outputTank.setAllowAny(true);
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

            if ((sawmillingRecipe == null && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get())
                    || (sawmillingRecipe != null && sawmillingRecipe.isLogRecipe() && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get())){ // Recipe is null, use alternative method if allowed, or dummy recipe
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

            } else if (sawmillingRecipe != null && !sawmillingRecipe.isLogRecipe()){ // Using Recipe
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
                if(++sound_tick == 19) {
                    sound_tick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            } else {
                counter = sawmillingRecipe != null
                                ? this.calculateCounter(sawmillingRecipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy())    // Sawmill recipe not null
                                : this.calculateCounter(Config.SAWMILL_PROCESSING_TIME.get(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());// Use default values when null
                length = counter;
            }
        } else decrementSuperCounterOnNoPower();
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
                    SawmillingRecipe recipe = RecipeUtil.getSawmillingRecipeFromLog(level, stack);
                    if (recipe != null) return true;
                    if (Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()){
                        ItemStack altPlankStack = RecipeUtil.getPlankFromLogParallel(level, stack.copy());
                        if (altPlankStack != null) return true;
                    }
                    return false;
                } else if (slot == 1){
                    SawmillingRecipe recipe = RecipeUtil.getSawmillingRecipeFromPlank(level, stack);
                    if (recipe != null) return true;
                    if (Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()){
                        ArrayList<ItemStack> plankList = RecipeUtil.getLogFromPlankParallel(level, stack.copy());
                        return plankList != null && !plankList.isEmpty();
                    }
                    return false;
                } else if (slot == 2){
                    SawmillingRecipe recipe = RecipeUtil.getSawmillingRecipeFromSecondOutput(level, stack.copy());
                    return recipe != null;
                } else if (slot == 3 || slot == 4) {
                    return stack.getItem() instanceof BucketItem;
                } else if (slot == 5){
                    return TagUtil.isTaggedMachineUpgradeItem(stack); // this is the upgrade slot
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new SawmillContainer(i, level, worldPosition, playerInventory, playerEntity);
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

    @Override
    public int getMaxPower() {
        return Config.SAWMILL_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.SAWMILL_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.SAWMILL_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 5;
    }
}
