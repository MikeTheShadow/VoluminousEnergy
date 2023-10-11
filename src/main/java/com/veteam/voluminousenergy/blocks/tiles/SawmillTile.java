package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.SawmillContainer;
import com.veteam.voluminousenergy.recipe.VEFluidSawmillRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.TankType;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
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

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0,0, Direction.UP, true, SlotType.INPUT));
        add(new VESlotManager(1,0, Direction.DOWN, true,SlotType.OUTPUT));
        add(new VESlotManager(2,1, Direction.NORTH, true,SlotType.OUTPUT));
        add(new VESlotManager(3, Direction.SOUTH,true,SlotType.FLUID_INPUT,4,0));
        add(new VESlotManager(4, Direction.EAST,true,SlotType.FLUID_OUTPUT));
    }};
    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,0, TankType.OUTPUT, "outputTank:output_tank_gui");
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
        super(VEBlocks.SAWMILL_TILE.get(), pos, state, com.veteam.voluminousenergy.recipe.VEFluidSawmillRecipe.RECIPE_TYPE);
        outputTank.setAllowAny(true);
    }

    VEFluidSawmillRecipe recipe;

    @Override
    public void tick() {
        updateClients();
        validateRecipe();
        processFluidIO();

        ItemStack logInput = inventory.getStackInSlot(0).copy(); // Log input
        //ItemStack plankOutput = inventory.getStackInSlot(1).copy(); // Plank Output
        //ItemStack secondOutput = inventory.getStackInSlot(2).copy(); // Second output

        // Resolve recipes:
        if (!logInput.isEmpty()){
            ItemStack plankOutputStack;
            ItemStack secondOutputStack;

            if ((recipe == null && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get())
                    || (recipe != null && recipe.isLogRecipe() && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get())){ // Recipe is null, use alternative method if allowed, or dummy recipe
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

            } else if (recipe != null && !recipe.isLogRecipe()){ // Using Recipe
                plankOutputStack = recipe.getResult(0).copy();
                secondOutputStack = recipe.getResult(1).copy();
                FluidStack outputFluid = recipe.getOutputFluid(0).copy();

                if ((outputTank.getTank().getFluidAmount() + outputFluid.getAmount()) <= TANK_CAPACITY
                        && (inventory.getStackInSlot(1).isEmpty() || inventory.getStackInSlot(1).getItem() == plankOutputStack.getItem())
                        && (inventory.getStackInSlot(2).isEmpty() || inventory.getStackInSlot(2).getItem() == secondOutputStack.getItem())
                        && (inventory.getStackInSlot(1).getCount() + recipe.getResult(0).getCount()) <= 64
                        && (inventory.getStackInSlot(2).getCount() + recipe.getResult(1).getCount()) <= 64){
                    if (outputTank.getTank().getFluid().isFluidEqual(outputFluid.copy()) || outputTank.getTank().getFluid().isEmpty()){
                        coreTickProcessing(recipe, logInput, plankOutputStack, secondOutputStack, outputFluid);
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

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;
        ItemStack logInput = inventory.getStackInSlot(0).copy();
        recipe = RecipeUtil.getSawmillingRecipeFromLog(level, logInput.copy());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                markRecipeDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0 || slot == 1 || slot == 2){
                    return true;
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
                if(!isItemValid(slot,stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private void coreTickProcessing(@Nullable VEFluidSawmillRecipe VEFluidSawmillRecipe,
                                    ItemStack logInput,
                                    ItemStack resolvedPlankOutput,
                                    ItemStack secondItemOutput,
                                    FluidStack fluidOutput){
        if (canConsumeEnergy()){
            if (counter == 1){
                // Core processing occurs here

                inventory.extractItem(0, (VEFluidSawmillRecipe != null ? VEFluidSawmillRecipe.getIngredient(0).getItems()[0].getCount() : Config.SAWMILL_LOG_CONSUMPTION_RATE.get()), false); // Extract log

                // Plank output
                ItemStack currentPlankStack = inventory.getStackInSlot(1);
                if (currentPlankStack.getItem() != resolvedPlankOutput.getItem() || currentPlankStack.getItem() == Items.AIR){
                    if (currentPlankStack.getItem() == Items.AIR){
                        currentPlankStack.setCount(1);
                    }
                    inventory.insertItem(1, resolvedPlankOutput.copy(), false);
                } else { // Assuming the recipe output item is already in the output slot
                   // currentPlankStack.setCount(.getCount()); // Simply change the item to equal the output amount
                    inventory.insertItem(1, resolvedPlankOutput.copy(),false); // Place the new output item on top of the old one
                }

                // Second output
                ItemStack currentSecondOutput = inventory.getStackInSlot(2);
                if (currentSecondOutput.getItem() != secondItemOutput.getItem() || currentSecondOutput.getItem() == Items.AIR){
                    if (currentSecondOutput.getItem() == Items.AIR){
                        currentSecondOutput.setCount(1);
                    }
                    inventory.insertItem(2, secondItemOutput.copy(), false);
                } else { // Assuming the recipe output item is already in the output slot
                    //currentSecondOutput.setCount(secondItemOutput.getCount()); // Simply change the item to equal the output amount
                    inventory.insertItem(2, secondItemOutput.copy(),false); // Place the new output item on top of the old one
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
                counter = VEFluidSawmillRecipe != null
                                ? this.calculateCounter(VEFluidSawmillRecipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy())    // Sawmill recipe not null
                                : this.calculateCounter(Config.SAWMILL_PROCESSING_TIME.get(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());// Use default values when null
                length = counter;
            }
        }
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

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return VEFluidSawmillRecipe.RECIPE_TYPE;
    }
}
