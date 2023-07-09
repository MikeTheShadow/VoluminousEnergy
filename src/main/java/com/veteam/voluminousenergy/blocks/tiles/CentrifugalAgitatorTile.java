package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalAgitatorContainer;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
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

public class CentrifugalAgitatorTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager[] slotManagers = new VESlotManager[]{
            new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0),
            new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT),
            new VESlotManager(2, Direction.NORTH, true, SlotType.FLUID_HYBRID, 2, 1),
            new VESlotManager(3, Direction.SOUTH, true, SlotType.FLUID_HYBRID, 3, 2)
    };

    public RelationalTank[] fluidManagers = new RelationalTank[]{
            new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank:input_tank_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 1, 0, TankType.OUTPUT,  "outputTank0:output_tank_0_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 2, 1, TankType.OUTPUT, "outputTank1:output_tank_1_gui")
    };

    public CentrifugalAgitatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CENTRIFUGAL_AGITATOR_TILE.get(), pos, state, CentrifugalAgitatorRecipe.RECIPE_TYPE);
        fluidManagers[0].setValidator(this, true);
        fluidManagers[1].setValidator(this, false);
        fluidManagers[2].setValidator(this, false);
    }

    public ItemStackHandler inventory = createHandler(5);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        super.tick();
        // Main Fluid Processing occurs here

        if (selectedRecipe == null) return;
        VEFluidRecipe recipe = (VEFluidRecipe) selectedRecipe;

        if (fluidManagers[1].canInsertOutputFluid(recipe, 0) && fluidManagers[2].canInsertOutputFluid(recipe, 1)) {
            // Check for power
            if (canConsumeEnergy()) {
                if (counter == 1) {
                    // Drain Input
                    fluidManagers[0].drainInput(recipe,0);

                    //AFTER
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

    @Override
    public int getUpgradeSlotId() {
        return 4;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return List.of(slotManagers);
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
            return fluidManagers[0].getTank().getFluid();
        } else if (num == 1) {
            return fluidManagers[1].getTank().getFluid();
        } else if (num == 2) {
            return fluidManagers[2].getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return List.of(fluidManagers);
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