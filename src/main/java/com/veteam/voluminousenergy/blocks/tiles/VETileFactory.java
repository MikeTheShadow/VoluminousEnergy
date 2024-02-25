package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.RecipeProcessor;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VETileFactory {
    private List<TileTank> tanks = new ArrayList<>();
    private RegistryObject<RecipeType<VERecipe>> recipeType;
    private final RegistryObject<BlockEntityType<VETileEntity>> tileRegistry;
    private final VEContainerFactory containerFactory;
    private VEEnergyStorage storage;

    private final HashMap<String,Integer> dataMap = new HashMap<>();
    private RecipeProcessor processor;
    private boolean sendsOutPower = false;

    public VETileFactory(RegistryObject<BlockEntityType<VETileEntity>> tileRegistry, VEContainerFactory containerFactory) {
        this.tileRegistry = tileRegistry;
        this.containerFactory = containerFactory;
    }

    public VETileEntity create(BlockPos pos, BlockState state) {

        VETileEntity newTile = new VETileEntity(tileRegistry.get(), pos, state, recipeType.get()) {

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
                // This fixes a race condition issue where the client doesn't have the recipe cache built yet
                this.markFluidInputDirty();
                this.markRecipeDirty();
                return containerFactory.create(id, level, worldPosition, playerInventory, player);
            }
        };
        // Add our tanks and slots
        AtomicInteger index = new AtomicInteger();
        newTile.addSlots(containerFactory.getTileSlotsAsManagers());
        index.set(0);
        newTile.addTanks(tanks.stream().map(t -> t.asTank(index.getAndIncrement())).toList());
        // Populate the data map
        newTile.dataMap.putAll(dataMap);

        // Set energy before the tilePos count otherwise we'll run into issues with the data tilePos
        newTile.energy = storage.copy();

        //set processor
        newTile.recipeProcessor = processor;

        // send out power
        newTile.sendsOutPower = sendsOutPower;

        // build out the inventory
        int inventorySize = containerFactory.getNumberOfSlots();
        storage.setUpgradeSlotId(containerFactory.upgradeSlotId());
        newTile.inventory = new VEItemStackHandler(newTile,inventorySize,storage.getUpgradeSlotId());

        return newTile;
    }

    public VETileFactory withRecipe(RegistryObject<RecipeType<VERecipe>> recipe) {
        this.recipeType = recipe;
        return this;
    }

    public VETileFactory addEnergyStorageWithConsumption(int maxPower, int transferRate, int consumption) {
        VEEnergyStorage storage = new VEEnergyStorage(maxPower, transferRate);
        storage.setConsumption(consumption);
        this.storage = storage;
        return this;
    }

    public VETileFactory addEnergyStorage(int maxPower, int transferRate) {
        this.storage = new VEEnergyStorage(maxPower, transferRate);
        return this;
    }

    public VETileFactory addUpgradeSlot(int upgradeSlotId) {

        if (storage == null)
            throw new IllegalStateException("Attempted to add upgrade tilePos without first adding energy storage!");

        storage.setUpgradeSlotId(upgradeSlotId);
        return this;
    }

    public VETileFactory countable() {
        this.dataMap.put("counter",0);
        this.dataMap.put("length",0);
        return this;
    }

    public VETileFactory withDataFlag(String flag) {
        this.dataMap.put(flag,0);
        return this;
    }

    public VETileFactory includeSoundTick() {
        this.dataMap.put("sound_tick",0);
        return this;
    }

    public VETileFactory withTanks(TileTank... tanks) {
        this.tanks = List.of(tanks);
        return this;
    }

    public VETileFactory makesSound() {
        this.dataMap.put("sound_tick",0);
        return this;
    }

    public VETileFactory withRecipeProcessing(RecipeProcessor processor) {
        this.processor = processor;
        return this;
    }

    public VETileFactory sendsOutPower() {
        this.sendsOutPower = true;
        return this;
    }

    public record ItemInputSlot(Direction direction,int recipePos) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, recipePos, direction, true, SlotType.INPUT);
        }
    }

    public record InputSlot(Direction direction) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, true, SlotType.INPUT);
        }
    }

    public record ItemOutputSlot(Direction direction,int recipePos) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, recipePos, direction, true, SlotType.OUTPUT);
        }
    }

    public record BucketInputSlot(Direction direction,int tankId) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, true, SlotType.FLUID_INPUT, id + 1, tankId);
        }
    }

    public record BucketOutputSlot(Direction direction) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, true, SlotType.FLUID_OUTPUT);
        }
    }

    public record FluidInputTank(int recipePos, int capacity) implements TileTank {
        @Override
        public VERelationalTank asTank(int id) {
            return new VERelationalTank(new FluidTank(capacity), id, recipePos, TankType.INPUT, "input_tank_" + id + ":input_tank_gui");
        }
    }

    public record FluidOutputTank(int recipePos, int capacity) implements TileTank {
        @Override
        public VERelationalTank asTank(int id) {
            return new VERelationalTank(new FluidTank(capacity), id, recipePos, TankType.OUTPUT, "input_tank_" + id + ":output_tank_gui");
        }
    }

    public interface TileSlot {
        VESlotManager asManager(int id);
    }

    public interface TileTank {

        VERelationalTank asTank(int id);
    }
}
