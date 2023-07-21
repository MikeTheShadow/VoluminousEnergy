package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.HydroponicIncubatorContainer;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import com.veteam.voluminousenergy.util.recipe.RecipeFluid;
import com.veteam.voluminousenergy.util.recipe.RecipeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class HydroponicIncubatorTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {


    // Slot Managers
    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, SlotType.INPUT);
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, SlotType.OUTPUT);
    public VESlotManager input2sm = new VESlotManager(2, Direction.NORTH, true, SlotType.INPUT);
    public VESlotManager output0sm = new VESlotManager(3, Direction.NORTH, true, SlotType.OUTPUT);
    public VESlotManager output1sm = new VESlotManager(4, Direction.NORTH, true, SlotType.OUTPUT);
    public VESlotManager output2sm = new VESlotManager(5, Direction.NORTH, true, SlotType.OUTPUT);
    public VESlotManager output3sm = new VESlotManager(6, Direction.NORTH, true, SlotType.OUTPUT);

    private final ItemStackHandler inventory = createHandler();

    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR, 0));

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(input0sm);
            add(input1sm);
            add(input2sm);
            add(output0sm);
            add(output1sm);
            add(output2sm);
            add(output3sm);
        }
    };

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "inputTank:input_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(inputTank);
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

    public HydroponicIncubatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.HYDROPONIC_INCUBATOR_TILE.get(), pos, state,HydroponicIncubatorRecipe.RECIPE_TYPE);
        inputTank.setAllowAny(true);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy(); // Bucket Top
        ItemStack input1 = inventory.getStackInSlot(1).copy(); // Bucket Bottom
        ItemStack inputItem = inventory.getStackInSlot(2).copy(); // Plant item to "grow"
        ItemStack output0 = inventory.getStackInSlot(3).copy(); // Primary output
        ItemStack output1 = inventory.getStackInSlot(4).copy(); // First rng
        ItemStack output2 = inventory.getStackInSlot(5).copy(); // Second RNG
        ItemStack output3 = inventory.getStackInSlot(6).copy(); // Third RNG

        inputTank.setInput(input.copy());
        inputTank.setOutput(input1.copy());

        if (this.inputFluid(inputTank, 0, 1)) return;
        if (this.outputFluid(inputTank, 0, 1)) return;

        inputItemStack.set(inputItem.copy()); // This reference is for preventing insertions on output slots, while still being able to verify them

        // Manually find the recipe since we have 2 conditions rather than the 1 input the vanilla getRecipe supports
        if (selectedRecipe != null) {
            HydroponicIncubatorRecipe recipe = (HydroponicIncubatorRecipe) selectedRecipe;
            // Check for power
            if (canConsumeEnergy()) {
                if (counter == 1) {

                    // Drain Input Tank
                    inputTank.drainInput(recipe,0);

//                            inventory.extractItem(3, recipe.ingredientCount,false);

                    // Primary output slot
                    ItemStack newOutputStack = recipe.getOutputItem(0).copy();

                    if (output0.getItem() != newOutputStack.getItem() || output0.getItem() == Items.AIR) {
                        if (output0.getItem() == Items.AIR) { // Fix air >1 jamming slots
                            output0.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputItem(0).getCount());
                        inventory.insertItem(3, newOutputStack.copy(), false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output0.setCount(recipe.getOutputItem(0).getCount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(3, output0.copy(), false); // Place the new output stack on top of the old one
                    }

                    // Manipulating first RNG output slot
                    if (recipe.getChance0() != 0) { // If the chance is ZERO, this functionality won't be used
                        ItemStack newRngStack = recipe.getOutputItem(1).copy();

                        // Generate Random floats
                        Random r = new Random();
                        float random = Mth.abs(0 + r.nextFloat() * (-1));
                        //LOGGER.debug("Random: " + random);
                        // ONLY manipulate the slot if the random float is under or is identical to the chance float
                        if (random <= recipe.getChance0()) {
                            //LOGGER.debug("Chance HIT!");
                            if (output1.getItem() != recipe.getOutputItem(1).getItem()) {
                                if (output1.getItem() == Items.AIR) {
                                    output1.setCount(1);
                                }
                                newRngStack.setCount(recipe.getOutputItem(1).getCount());
                                inventory.insertItem(4, newRngStack.copy(), false); // CRASH the game if this is not empty!
                            } else { // Assuming the recipe output item is already in the output slot
                                output1.setCount(recipe.getOutputItem(1).getCount()); // Simply change the stack to equal the output amount
                                inventory.insertItem(4, output1.copy(), false); // Place the new output stack on top of the old one
                            }
                        }
                    }

                    // Manipulating second RNG output slot
                    if (recipe.getChance1() != 0) { // If the chance is ZERO, this functionality won't be used
                        ItemStack newRngStack = recipe.getOutputItem(2).copy();

                        // Generate Random floats
                        Random r = new Random();
                        float random = Mth.abs(0 + r.nextFloat() * (0 - 1));
                        //LOGGER.debug("Random: " + random);
                        // ONLY manipulate the slot if the random float is under or is identical to the chance float
                        if (random <= recipe.getChance1()) {
                            //LOGGER.debug("Chance HIT!");
                            if (output2.getItem() != recipe.getOutputItem(2).getItem()) {
                                if (output2.getItem() == Items.AIR) {
                                    output2.setCount(1);
                                }
                                newRngStack.setCount(recipe.getOutputItem(2).getCount());
                                inventory.insertItem(5, newRngStack.copy(), false); // CRASH the game if this is not empty!
                            } else { // Assuming the recipe output item is already in the output slot
                                output2.setCount(recipe.getOutputItem(2).getCount()); // Simply change the stack to equal the output amount
                                inventory.insertItem(5, output2.copy(), false); // Place the new output stack on top of the old one
                            }
                        }
                    }

                    // Manipulating third RNG output slot
                    if (recipe.getChance2() != 0) { // If the chance is ZERO, this functionality won't be used
                        ItemStack newRngStack = recipe.getOutputItem(3).copy();

                        // Generate Random floats
                        Random r = new Random();
                        float random = Mth.abs(0 + r.nextFloat() * (0 - 1));
                        //LOGGER.debug("Random: " + random);
                        // ONLY manipulate the slot if the random float is under or is identical to the chance float
                        if (random <= recipe.getChance2()) {
                            //LOGGER.debug("Chance HIT!");
                            if (output3.getItem() != recipe.getOutputItem(3).getItem()) {
                                if (output3.getItem() == Items.AIR) {
                                    output3.setCount(1);
                                }
                                newRngStack.setCount(recipe.getOutputItem(3).getCount());
                                inventory.insertItem(6, newRngStack.copy(), false); // CRASH the game if this is not empty!
                            } else { // Assuming the recipe output item is already in the output slot
                                output3.setCount(recipe.getOutputItem(3).getCount()); // Simply change the stack to equal the output amount
                                inventory.insertItem(6, output3.copy(), false); // Place the new output stack on top of the old one
                            }
                        }
                    }

                    counter--;
                    consumeEnergy();
                    this.setChanged();
                } else if (counter > 0) {
                    counter--;
                    consumeEnergy();
                    if (++sound_tick == 19) {
                        sound_tick = 0;
                        if (Config.PLAY_MACHINE_SOUNDS.get()) {
                            level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                } else {
                    counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                    length = counter;
                }
            } else { // Energy Check
            }
        }
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(8) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    return stack.getItem() instanceof BucketItem;
                } else if (slot < 7) {
                    return true; // TODO FIX ME
                }

                if (slot == 7) return TagUtil.isTaggedMachineUpgradeItem(stack); // this is the upgrade slot
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                if (slot == 2) {
                    if (inventory.getStackInSlot(2).getCount() > 0) return stack;

                    if (stack.getCount() > 1) {
                        ItemStack singleItemStack = stack.copy();
                        singleItemStack.setCount(1);
                        stack.setCount(stack.getCount() - 1);
                        super.insertItem(slot, singleItemStack, false);
                        return stack;
                    }
                } else if (slot > 2 && slot < 7) {
                    if (!isItemValid(slot, stack)) return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new HydroponicIncubatorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return inputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank() {
        return this.inputTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return this.fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.HYDROPONIC_INCUBATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.HYDROPONIC_INCUBATOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.HYDROPONIC_INCUBATOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 7;
    }
}
