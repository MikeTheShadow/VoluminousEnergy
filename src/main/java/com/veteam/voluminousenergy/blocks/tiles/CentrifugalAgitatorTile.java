package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalAgitatorContainer;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.recipe.RecipeFluid;
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

public class CentrifugalAgitatorTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, SlotType.INPUT);
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, SlotType.OUTPUT);
    public VESlotManager output0sm = new VESlotManager(2, Direction.NORTH, true, SlotType.OUTPUT);
    public VESlotManager output1sm = new VESlotManager(3, Direction.SOUTH, true, SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(input0sm);
        add(input1sm);
        add(output0sm);
        add(output1sm);
    }};

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "inputTank:input_tank_gui");
    RelationalTank outputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY), 1, null, null, TankType.OUTPUT, 0, "outputTank0:output_tank_0_gui");
    RelationalTank outputTank1 = new RelationalTank(new FluidTank(TANK_CAPACITY), 2, null, null, TankType.OUTPUT, 1, "outputTank1:output_tank_1_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(inputTank);
        add(outputTank0);
        add(outputTank1);
    }};

    public CentrifugalAgitatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CENTRIFUGAL_AGITATOR_TILE.get(), pos, state);
        inputTank.setAllowAny(true);
        outputTank0.setAllowAny(true);
        outputTank1.setAllowAny(true);
    }

    public ItemStackHandler inventory = createHandler(5);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack input1 = inventory.getStackInSlot(1).copy();
        ItemStack output0 = inventory.getStackInSlot(2).copy();
        ItemStack output1 = inventory.getStackInSlot(3).copy();

        inputTank.setInput(input.copy());
        inputTank.setOutput(input1.copy());

        outputTank0.setOutput(output0);
        outputTank1.setOutput(output1);

        if (this.inputFluid(inputTank, 0, 1)) return;
        if (this.outputFluid(inputTank, 0, 1)) return;
        if (this.outputFluidStatic(outputTank0, 2)) return;
        if (this.outputFluidStatic(outputTank1, 3)) return;
        // Main Fluid Processing occurs here

        if (selectedRecipe != null) {

            VEFluidRecipe recipe = (VEFluidRecipe) selectedRecipe;

            if (outputTank0.canInsertOutputFluid(recipe, 0) && outputTank1.canInsertOutputFluid(recipe, 1)) {
                // Check for power
                if (canConsumeEnergy()) {
                    if (counter == 1) {
                        // Drain Input
                        inputTank.drainInput(recipe,0);

                        //AFTER
                        if (outputTank0.canInsertOutputFluid(recipe, 0) && outputTank1.canInsertOutputFluid(recipe, 1)) {
                            outputTank0.fillOutput(recipe, 0);
                            outputTank1.fillOutput(recipe, 1);
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
            }
        }
    }

    @Override
    public int getUpgradeSlotId() {
        return 4;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CentrifugalAgitatorContainer(i, level, worldPosition, playerInventory, playerEntity);
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
            return inputTank.getTank().getFluid();
        } else if (num == 1) {
            return outputTank0.getTank().getFluid();
        } else if (num == 2) {
            return outputTank1.getTank().getFluid();
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

    public RelationalTank getInputTank() {
        return this.inputTank;
    }

    public RelationalTank getOutputTank0() {
        return this.outputTank0;
    }

    public RelationalTank getOutputTank1() {
        return this.outputTank1;
    }

    @Override
    public int getMaxPower() {
        return Config.CENTRIFUGAL_AGITATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.CENTRIFUGAL_AGITATOR_TRANSFER.get();
    }
}