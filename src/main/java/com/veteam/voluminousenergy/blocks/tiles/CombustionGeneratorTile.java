package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
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

public class CombustionGeneratorTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0));
            add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
            add(new VESlotManager(2, Direction.NORTH, true, SlotType.FLUID_INPUT, 3, 1));
            add(new VESlotManager(3, Direction.SOUTH, true, SlotType.FLUID_OUTPUT));
        }
    };

    private final int tankCapacity = 4000;

    public static final int COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT = 250;

    private final RelationalTank fuelTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.INPUT, "fuelTank:fuel_tank_gui");
    private final RelationalTank oxidizerTank = new RelationalTank(new FluidTank(tankCapacity), 1, null, null, TankType.INPUT, "oxidizerTank:oxidizer_tank_gui");
    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(fuelTank);
            add(oxidizerTank);
        }
    };
    private int energyRate;

    private final ItemStackHandler inventory = createHandler();

    public CombustionGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE.get(), pos, state, null);
    }

    private LazyOptional<IEnergyStorage> energyCapability = null;

    VEFluidRecipe oxidizerRecipe;
    VEFluidRecipe fuelRecipe;

    @Override
    public void tick() {
        super.tick();

        // Main Combustion Generator tick logic
        if (counter > 0) {
            if (energyCapability == null) {
                energyCapability = this.getCapability(ForgeCapabilities.ENERGY);
            }
            if (energyCapability.map(IEnergyStorage::getEnergyStored).orElse(0) + energyRate <= Config.COMBUSTION_GENERATOR_MAX_POWER.get()) {
                counter--;
                energy.ifPresent(e -> e.addEnergy(energyRate)); //Amount of energy to add per tick
                if (++sound_tick == 19) {
                    sound_tick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
            setChanged();
        } else if (!oxidizerTank.getTank().isEmpty()) {
            if (oxidizerRecipe != null && fuelRecipe != null) {
                oxidizerTank.getTank().drain(COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
                fuelTank.getTank().drain(COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
                if (Config.COMBUSTION_GENERATOR_BALANCED_MODE.get()) {
                    counter = (oxidizerRecipe.getProcessTime()) / 4;
                } else {
                    counter = Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get() / 4;
                }
                energyRate = fuelRecipe.getProcessTime() / oxidizerRecipe.getProcessTime(); // Process time in fuel recipe is really volumetric energy
                length = counter;
                setChanged();
            }
        }

        if (counter == 0) {
            energyRate = 0;
        }
        sendOutPower();
    }


    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;
        oxidizerRecipe =
                RecipeCache.getFluidRecipeFromCache(level,
                        CombustionGeneratorOxidizerRecipe.RECIPE_TYPE,
                        Collections.singletonList(this.oxidizerTank.getTank().getFluid()),
                        new ArrayList<>());

        fuelRecipe = RecipeCache.getFluidRecipeFromCache(level,
                CombustionGeneratorFuelRecipe.RECIPE_TYPE,
                Collections.singletonList(this.fuelTank.getTank().getFluid()),
                new ArrayList<>());
    }

    @Override
    public void load(CompoundTag tag) {
        energyRate = tag.getInt("energy_rate");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("energy_rate", energyRate);
        super.saveAdditional(tag);
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        return tileEntity.getCapability(ForgeCapabilities.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()) {
                BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if (tileEntity != null) {
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.COMBUSTION_GENERATOR_SEND.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
                    if (energy.getEnergyStored() <= 0) {
                        break;
                    }
                }
            }
        });
    }

    private ItemStackHandler createHandler() {
        VEFluidTileEntity tileEntity = this;
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                tileEntity.markFluidInputDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

                if(stack.getItem() instanceof BucketItem bucketItem) {
                    Fluid fluid = bucketItem.getFluid();
                    if(fluid.isSame(Fluids.EMPTY)) return true;
                    FluidStack testFluid = new FluidStack(fluid,COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT);
                    if(slot == 0) {
                        List<VEFluidRecipe> recipes = RecipeCache.getFluidRecipesWithoutLevelDangerous(CombustionGeneratorFuelRecipe.RECIPE_TYPE);
                        return recipes.stream().anyMatch(r -> r.getFluidIngredient(0).test(testFluid));
                    } else if(slot == 2) {
                        List<VEFluidRecipe> recipes = RecipeCache.getFluidRecipesWithoutLevelDangerous(CombustionGeneratorOxidizerRecipe.RECIPE_TYPE);
                        return recipes.stream().anyMatch(r -> r.getFluidIngredient(0).test(testFluid));
                    }
                    return true;
                }
                VoluminousEnergy.LOGGER.info("Failed to insert item " + stack.getItem().getDescriptionId());
                return false;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CombustionGeneratorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    // TODO figure out if this can just be a call to the super method
    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (((counter * 100) / length))) / 100;
        }
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return oxidizerTank.getTank().getFluid();
        } else if (num == 1) {
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
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

    public int getEnergyRate() {
        return energyRate;
    }

    public RelationalTank getOxidizerTank() {
        return oxidizerTank;
    }

    public RelationalTank getFuelTank() {
        return fuelTank;
    }

    @Override
    public int getMaxPower() {
        return Config.COMBUSTION_GENERATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return -1;
    }

    @Override
    public int getTransferRate() {
        return Config.COMBUSTION_GENERATOR_SEND.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return -1;
    }
}
