package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.FluidMixerContainer;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.FluidMixerRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.recipe.RecipeFluid;
import com.veteam.voluminousenergy.util.recipe.RecipeItem;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

public class FluidMixerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT, "input_0_sm");
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT, "input_1_sm");
    public VESlotManager input2sm = new VESlotManager(2, Direction.EAST, true, "slot.voluminousenergy.input_slot", SlotType.INPUT, "input_2_sm");
    public VESlotManager input3sm = new VESlotManager(3, Direction.WEST, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT, "input_3_sm");
    public VESlotManager output0sm = new VESlotManager(4, Direction.NORTH, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT, "output_0_sm");
    public VESlotManager output1sm = new VESlotManager(5, Direction.SOUTH, true, "slot.voluminousenergy.output_slot", SlotType.OUTPUT, "output_1_sm");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(input0sm);
        add(input1sm);
        add(input2sm);
        add(input3sm);
        add(output0sm);
        add(output1sm);
    }};

    RelationalTank inputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "inputTank0:input_tank_0_gui");
    RelationalTank inputTank1 = new RelationalTank(new FluidTank(TANK_CAPACITY), 1, null, null, TankType.INPUT, "inputTank1:input_tank_1_gui");
    RelationalTank outputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY), 2, null, null, TankType.OUTPUT, 1, "outputTank0:output_tank_0_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(inputTank0);
        add(inputTank1);
        add(outputTank0);
    }};

    public FluidMixerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.FLUID_MIXER_TILE.get(), pos, state);
        inputTank0.setAllowAny(true);
        inputTank1.setAllowAny(true);
        outputTank0.setAllowAny(true);
    }

    public ItemStackHandler inventory = createHandler(7);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    // Recipe caching
    RecipeFluid lastFluid = new RecipeFluid();
    RecipeFluid lastFluid2 = new RecipeFluid();
    FluidMixerRecipe recipe;

    @Override
    public void tick() {
        updateClients();

        ItemStack input0 = inventory.getStackInSlot(0).copy();
        ItemStack output0 = inventory.getStackInSlot(1).copy();
        ItemStack input1 = inventory.getStackInSlot(2).copy();
        ItemStack output1 = inventory.getStackInSlot(3).copy();
        ItemStack input2 = inventory.getStackInSlot(4).copy();
        ItemStack output2 = inventory.getStackInSlot(5).copy();

        inputTank0.setInput(input0.copy());
        inputTank0.setOutput(output0.copy());

        inputTank1.setInput(input1.copy());
        inputTank1.setOutput(output1.copy());

        outputTank0.setInput(input2.copy());
        outputTank0.setOutput(output2.copy());

        if (this.inputFluid(inputTank0, 0, 1)) return;
        if (this.outputFluid(inputTank0, 0, 1)) return;
        if (this.inputFluid(inputTank1, 2, 3)) return;
        if (this.outputFluid(inputTank1, 2, 3)) return;
        if (this.inputFluid(outputTank0, 4, 5)) return;
        if (this.outputFluid(outputTank0, 4, 5)) return;

        if (lastFluid.isDifferent(this.inputTank0.getTank().getFluid()) || lastFluid2.isDifferent(this.inputTank1.getTank().getFluid())) {
            FluidMixerRecipe newRecipe = (FluidMixerRecipe) RecipeCache.getFluidRecipeFromCache(level, FluidMixerRecipe.class,
                    new ArrayList<>() {{
                        add(inputTank1.getTank().getFluid());
                        add(inputTank0.getTank().getFluid());
                    }});

            if (newRecipe != recipe) {
                counter = 0;
            }
            recipe = newRecipe;
        }

        // Main Fluid Processing occurs here
        if (recipe != null) {

            // Tank fluid amount check + tank cap checks
            if (outputTank0.getTank().getFluidAmount() + recipe.getOutputFluid(0).getAmount() <= TANK_CAPACITY) {
                // Check for power
                if (canConsumeEnergy()) {
                    if (counter == 1) {

                        // Drain Input
                        inputTank0.drainInput(recipe,0);
                        inputTank1.drainInput(recipe,1);

                        // First Output Tank
                        if (outputTank0.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()) {
                            outputTank0.getTank().setFluid(recipe.getOutputFluid().copy());
                        } else {
                            outputTank0.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
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
                        counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()));
                        length = counter;
                    }
                } else { // Energy Check
                    decrementSuperCounterOnNoPower();
                }
            } else { // If fluid tank empty set counter to zero
                counter = 0;
            }
        }
    }

    @Override
    public int getUpgradeSlotId() {
        return 6;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new FluidMixerContainer(i, level, worldPosition, playerInventory, playerEntity);
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

    // TODO abstract this to the fluid tile entity. This messes with the screen so be careful with that
    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return inputTank0.getTank().getFluid();
        } else if (num == 1) {
            return inputTank1.getTank().getFluid();
        } else if (num == 2) {
            return outputTank0.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    public RelationalTank getInputTank0() {
        return this.inputTank0;
    }

    public RelationalTank getInputTank1() {
        return this.inputTank1;
    }

    public RelationalTank getOutputTank0() {
        return this.outputTank0;
    }

    @Override
    public int getMaxPower() {
        return Config.FLUID_MIXER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.FLUID_MIXER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.FLUID_MIXER_TRANSFER.get();
    }
}