package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
import com.veteam.voluminousenergy.recipe.VERNGRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.util.Mth.abs;

public class HydroponicIncubatorTile extends VETileEntity {

    private final ItemStackHandler inventory = new VEItemStackHandler(this, 8);

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT,1,0));
            add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
            add(new VESlotManager(2,0, Direction.NORTH, true, SlotType.INPUT));
            add(new VESlotManager(3,0, Direction.NORTH, true, SlotType.OUTPUT));
            add(new VESlotManager(4,1, Direction.NORTH, true, SlotType.OUTPUT));
            add(new VESlotManager(5,2, Direction.NORTH, true, SlotType.OUTPUT));
            add(new VESlotManager(6,3, Direction.NORTH, true, SlotType.OUTPUT));
        }
    };

    VERelationalTank inputTank = new VERelationalTank(new FluidTank(DEFAULT_TANK_CAPACITY), 0,0, TankType.INPUT, "inputTank:input_tank_gui");

    List<VERelationalTank> fluidManagers = new ArrayList<>() {
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
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.HYDROPONIC_INCUBATOR_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return inputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    // We copy the whole method to prevent the input from being decremented
    @Override
    void processRecipe() {
        if (selectedRecipe == null) return;
        VERecipe recipe = selectedRecipe;

        if (canConsumeEnergy()) {

            if (counter == 1) {
                // Validate output
                for (VERelationalTank relationalTank : getRelationalTanks()) {
                    if (relationalTank.getTankType() == TankType.OUTPUT) {
                        FluidStack recipeFluid = recipe.getOutputFluid(relationalTank.getRecipePos());
                        FluidTank tank = relationalTank.getTank();
                        FluidStack currentFluid = tank.getFluid();
                        if(currentFluid.isEmpty()) continue;
                        // If the output fluid amount won't fit, then you must acquit
                        if (!recipeFluid.isFluidEqual(currentFluid)
                                || tank.getFluidAmount() + recipeFluid.getAmount() > tank.getCapacity()) {
                            return;
                        }
                    }
                }

                ItemStackHandler handler = getInventoryHandler();

                if (handler != null) {
                    // Validate output
                    for (VESlotManager slotManager : getSlotManagers()) {
                        if(slotManager.getSlotType() != SlotType.OUTPUT) continue;
                        ItemStack recipeStack = recipe.getResult(slotManager.getRecipePos());
                        ItemStack currentItem = slotManager.getItem(handler);
                        if(currentItem.isEmpty()) continue;
                        // If the output item amount won't fit, then you must acquit
                        if(!recipeStack.is(currentItem.getItem())
                                || recipeStack.getCount() + currentItem.getCount() > currentItem.getMaxStackSize()) {
                            return;
                        }
                    }

                    VERNGRecipe irngRecipe = null;
                    if(recipe instanceof VERNGRecipe rec) {
                        irngRecipe = rec;
                    }
                    Random r = new Random();

                    // process recipe
                    for(VESlotManager slotManager : getSlotManagers()) {
                        if(slotManager.getSlotType() == SlotType.OUTPUT) {
                            ItemStack output = recipe.getResult(slotManager.getRecipePos());
                            ItemStack currentStack = slotManager.getItem(handler);
                            // rng calculations
                            if(irngRecipe != null) {
                                float randomness = irngRecipe.getOutputChance(slotManager.getRecipePos());
                                if(randomness != 1) {
                                    float random = abs(0 + r.nextFloat() * (-1));
                                    if(random > randomness) continue;
                                }

                            }
                            if(currentStack.isEmpty()) slotManager.setItem(output,handler);
                            else currentStack.setCount(currentStack.getCount() + output.getCount());
                        }
                    }
                }

                // process recipe
                for (VERelationalTank relationalTank : getRelationalTanks()) {
                    if (relationalTank.getTankType() == TankType.OUTPUT) {
                        relationalTank.fillOutput(recipe,relationalTank.getRecipePos());
                    } else if(relationalTank.getTankType() == TankType.INPUT) {
                        relationalTank.drainInput(recipe,relationalTank.getRecipePos());
                    }
                }
                doExtraRecipeProcessing();

                this.markRecipeDirty();
                this.markFluidInputDirty();
                this.setChanged();
            } else if (counter > 0) {
                if (++sound_tick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
                    sound_tick = 0;
                    level.playSound(null, this.getBlockPos(), VESounds.AQUEOULIZER, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                counter = length;
            }
            counter--;
            consumeEnergy();
        }
    }

    @Override
    public @NotNull List<VERelationalTank> getRelationalTanks() {
        return this.fluidManagers;
    }
}
