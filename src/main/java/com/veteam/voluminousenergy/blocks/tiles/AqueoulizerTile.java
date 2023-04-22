package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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

import static com.veteam.voluminousenergy.VoluminousEnergy.LOGGER;

public class AqueoulizerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    // Slot Managers
    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT, "input_0_sm");
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot", SlotType.INPUT, "input_1_sm");
    public VESlotManager output0sm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.input_slot", SlotType.OUTPUT, "output_0_sm");
    // Actually an input slot omegalul
    public VESlotManager output1sm = new VESlotManager(3, Direction.SOUTH, true, "slot.voluminousenergy.input_slot", SlotType.INPUT, "output_1_sm");

    private final ItemStackHandler inventory = createHandler();

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(input0sm);
            add(input1sm);
            add(output0sm);
            add(output1sm);
        }

    };

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "inputTank:input_tank_gui");
    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 1, null, null, TankType.OUTPUT, 0, "outputTank:output_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(inputTank);
            add(outputTank);
        }
    };

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public AqueoulizerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AQUEOULIZER_TILE.get(), pos, state);
        inputTank.setAllowAny(true);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy(); // Input insert
        ItemStack input1 = inventory.getStackInSlot(1).copy(); // Input extract
        ItemStack output0 = inventory.getStackInSlot(2).copy(); // Output extract
        ItemStack inputItem = inventory.getStackInSlot(3).copy(); // Repurpose to Item input

        inputTank.setInput(input.copy());
        inputTank.setOutput(input1.copy());

        outputTank.setOutput(output0);

        if (this.inputFluid(inputTank, 0, 1)) return;
        if (this.outputFluid(inputTank, 0, 1)) return;
        if (this.outputFluidStatic(outputTank, 2)) return;

        // Main Fluid Processing occurs here:
//        VEFluidRecipe recipe = RecipeUtil.getAqueoulizerRecipe(level, this.inputTank.getTank().getFluid(),inputItem.copy());
        VEFluidRecipe recipe = RecipeCache.getFluidRecipeFromCache(AqueoulizerRecipe.class,
                Collections.singletonList(this.inputTank.getTank().getFluid()),
                inputItem.copy());
        // Manually find the recipe since we have 2 conditions rather than the 1 input the vanilla getRecipe supports

        if (inputTank != null && !inputTank.getTank().isEmpty() && recipe != null) {
            //ItemStack inputFluidStack = new ItemStack(inputTank.getTank().getFluid().getRawFluid().getFilledBucket(),1);

            if (recipe.getRawFluids().contains(inputTank.getTank().getFluid().getRawFluid())) {
                if (outputTank != null) {

                    // Tank fluid amount check + tank cap checks
                    if (inputTank.getTank().getFluidAmount() >= recipe.getInputAmount() && outputTank.getTank().getFluidAmount() + recipe.getOutputAmount() <= TANK_CAPACITY) {
                        // Check for power
                        if (canConsumeEnergy()) {
                            if (counter == 1) {

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // Output Tank
                                if (outputTank.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()) {
                                    outputTank.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                inventory.extractItem(3, recipe.ingredientCount, false);

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0) {
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                                length = counter;
                            }
                        } else { // Energy Check
                            decrementSuperCounterOnNoPower();
                        }
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                } else counter = 0;
            } else counter = 0;
        } else {
            counter = 0;
        }
        //LOGGER.debug("Fluid: " + inputTank.getFluid().getRawFluid().getFilledBucket().getTranslationKey() + " amount: " + inputTank.getFluid().getAmount());
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    private IFluidHandler createInputFluidHandler() {
        return this.createInputFluidHandler(/*new AqueoulizerRecipe(),*/ inputTank);
    }

    private IFluidHandler createOutputFluidHandler() {
        return this.createFluidHandler(new AqueoulizerRecipe(), outputTank);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    if (!(stack.getItem() instanceof BucketItem)) return false;
                }
                if (slot == 3) {
                    ArrayList<AqueoulizerRecipe> recipe = RecipeUtil.getAqueoulizerRecipesFromItemInput(level, stack);
                    return !recipe.isEmpty();
                }

                if (slot == 4) return TagUtil.isTaggedMachineUpgradeItem(stack); // this is the upgrade slot
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public IFluidHandler createInputFluidHandler(/*AqueoulizerRecipe aqueoulizerRecipe,*/ RelationalTank... relationalTanks) {

        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return relationalTanks.length;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {

                for (RelationalTank t : relationalTanks) {
                    if (t.getId() == tank) {
                        return t.getTank() == null ? FluidStack.EMPTY : t.getTank().getFluid();
                    }
                }
                LOGGER.debug("Invalid tankId in Aqueoulizer Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {

                for (RelationalTank t : relationalTanks) {
                    if (t.getId() == tank) {
                        return t.getTank() == null ? 0 : t.getTank().getCapacity();
                    }
                }
                LOGGER.debug("Invalid tankId in Aqueoulizer Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                try {
                    for (RelationalTank t : relationalTanks) {
                        if (t.getTankType() == TankType.INPUT) {
                            ArrayList<AqueoulizerRecipe> recipes = RecipeUtil.getAqueoulizerRecipesFromFluidInput(level, stack);
                            return !recipes.isEmpty() && t.getTank() != null && t.getTank().isFluidValid(stack);
                            //return aqueoulizerRecipe.rawFluidInputList.get().contains(stack.getRawFluid()) && t.getTank() != null && t.getTank().isFluidValid(stack);
                        } else {
                            return RecipeUtil.isAqueoulizerOutput(level, stack) && t.getTank() != null && t.getTank().isFluidValid(stack);
                            //return aqueoulizerRecipe.getOutputFluid().getRawFluid().isSame(stack.getRawFluid()) && t.getTank() != null && t.getTank().isFluidValid(stack);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.debug("ERROR with isFluidValid in Aqueoulizer Tile input fluid handler");
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {

                for (RelationalTank t : relationalTanks) {
                    if (isFluidValid(t.getId(), resource) && t.getTank().isEmpty() || resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().fill(resource, action);
                    }
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                for (RelationalTank t : relationalTanks) {
                    if (resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().drain(resource, action);
                    }
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                for (RelationalTank t : relationalTanks) {
                    if (t.getTank().getFluidAmount() > 0) {
                        if (Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                            return t.getTank().drain(maxDrain, action);
                        } else if (t.getTankType() != TankType.INPUT) {
                            return t.getTank().drain(maxDrain, action);
                        }
                    }
                }
                return FluidStack.EMPTY;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new AqueoulizerContainer(i, level, worldPosition, playerInventory, playerEntity);
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

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return inputTank.getTank().getFluid();
        } else if (num == 1) {
            return outputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank() {
        return this.inputTank;
    }

    public RelationalTank getOutputTank() {
        return this.outputTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return this.fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.AQUEOULIZER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.AQUEOULIZER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.AQUEOULIZER_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 4;
    }
}
