package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.FluidElectrolyzerContainer;
import com.veteam.voluminousenergy.recipe.FluidElectrolyzerRecipe;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluidElectrolyzerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager[] slotManagers = new VESlotManager[] {
            new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0),
            new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT),
            new VESlotManager(2, Direction.EAST, true, SlotType.FLUID_INPUT, 3, 1),
            new VESlotManager(3, Direction.WEST, true, SlotType.FLUID_OUTPUT),
            new VESlotManager(4, Direction.NORTH, true, SlotType.FLUID_INPUT, 5, 2),
            new VESlotManager(5, Direction.SOUTH, true, SlotType.FLUID_OUTPUT)
    };

    public RelationalTank[] fluidManagers = new RelationalTank[] {
            new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank:input_tank_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 1, 0, TankType.OUTPUT,  "outputTank0:output_tank_0_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 2, 1, TankType.OUTPUT,  "outputTank1:output_tank_1_gui")
    };

    public FluidElectrolyzerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.FLUID_ELECTROLYZER_TILE.get(), pos, state, FluidElectrolyzerRecipe.RECIPE_TYPE);
        fluidManagers[0].setValidator(this, true);
        fluidManagers[1].setValidator(this, false);
        fluidManagers[2].setValidator(this, false);
    }

    public ItemStackHandler inventory = createHandler(7);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    RecipeFluid lastFluid = new RecipeFluid();
    VEFluidRecipe recipe;

    @Override
    public void tick() {
        updateClients();
        super.tick();
        
        if (selectedRecipe != null) {

            VEFluidRecipe recipe = (VEFluidRecipe) selectedRecipe;

            // Tank fluid amount check + tank cap checks
            if (fluidManagers[1].canInsertOutputFluid(recipe, 0)
                    && fluidManagers[2].canInsertOutputFluid(recipe, 1)) {
                // Check for power
                if (canConsumeEnergy()) {
                    if (counter == 1) {
                        fluidManagers[0].drainInput(recipe, 0);

                        if (fluidManagers[1].canInsertOutputFluid(recipe, 0) && fluidManagers[2].canInsertOutputFluid(recipe, 1)) {
                            fluidManagers[1].fillOutput(recipe, 0);
                            fluidManagers[2].fillOutput(recipe, 1);
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
        return 6;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return List.of(slotManagers);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new FluidElectrolyzerContainer(i, level, worldPosition, playerInventory, playerEntity);
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
        if (num >= fluidManagers.length || num < 0) {
            return FluidStack.EMPTY;
        }
        return fluidManagers[num].getTank().getFluid();
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return List.of(fluidManagers);
    }

    public RelationalTank getInputTank() {
        return this.fluidManagers[0];
    }

    public RelationalTank getOutputTank0() {
        return this.fluidManagers[1];
    }

    public RelationalTank getOutputTank1() {
        return this.fluidManagers[2];
    }

    @Override
    public int getMaxPower() {
        return Config.FLUID_ELECTROLYZER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.FLUID_ELECTROLYZER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.FLUID_ELECTROLYZER_TRANSFER.get();
    }
}
