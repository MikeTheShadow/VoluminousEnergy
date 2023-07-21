package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.FluidMixerContainer;
import com.veteam.voluminousenergy.recipe.FluidMixerRecipe;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluidMixerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    VESlotManager[] slotManagers = new VESlotManager[] {
            new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0),
            new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT),
            new VESlotManager(2, Direction.EAST, true, SlotType.FLUID_INPUT, 3, 1),
            new VESlotManager(3, Direction.WEST, true, SlotType.FLUID_OUTPUT),
            new VESlotManager(4, Direction.NORTH, true, SlotType.FLUID_INPUT, 5, 2),
            new VESlotManager(5, Direction.SOUTH, true, SlotType.FLUID_OUTPUT)
    };

    RelationalTank[] fluidManagers = new RelationalTank[] {
            new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank0:input_tank_0_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 1, 1, TankType.INPUT, "inputTank1:input_tank_1_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 2, 0, TankType.OUTPUT, "outputTank0:output_tank_0_gui")
    };

    public FluidMixerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.FLUID_MIXER_TILE.get(), pos, state, FluidMixerRecipe.RECIPE_TYPE);
        fluidManagers[0].setValidator(this, true);
        fluidManagers[1].setValidator(this, true);
        fluidManagers[2].setValidator(this, false);
    }

    public ItemStackHandler inventory = createHandler(7);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        super.tick();

        // Main Fluid Processing occurs here
        if (selectedRecipe != null) {

            VEFluidRecipe recipe = (VEFluidRecipe) selectedRecipe;

            // Tank fluid amount check + tank cap checks
            if (fluidManagers[2].getTank().getFluidAmount() + recipe.getOutputFluid(0).getAmount() <= TANK_CAPACITY) {
                // Check for power
                if (canConsumeEnergy()) {
                    if (counter == 1) {

                        // Drain Input
                        fluidManagers[0].drainInput(recipe,0);
                        fluidManagers[1].drainInput(recipe,1);

                        // First Output Tank
                        if (fluidManagers[2].getTank().getFluid().getRawFluid() != recipe.getOutputFluid(0).getRawFluid()) {
                            fluidManagers[2].getTank().setFluid(recipe.getOutputFluid(0).copy());
                        } else {
                            fluidManagers[2].getTank().fill(recipe.getOutputFluid(0).copy(), IFluidHandler.FluidAction.EXECUTE);
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
        return List.of(slotManagers);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new FluidMixerContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    // TODO abstract this to the fluid tile entity. This messes with the screen so be careful with that

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return List.of(fluidManagers);
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