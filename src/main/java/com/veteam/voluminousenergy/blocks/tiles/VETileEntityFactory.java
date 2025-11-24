package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.containers.iolisteners.SlotWithIOListener;
import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory;
import com.veteam.voluminousenergy.blocks.tiles.fluids.AbstractFluidValidator;
import com.veteam.voluminousenergy.blocks.tiles.inventory.AbstractItemStackValidator;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.AbstractRecipeProcessor;
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
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class VETileEntityFactory {
    private List<TileTank> tanks = new ArrayList<>();
    private Supplier<RecipeType<VERecipe>> recipeType;
    private final Supplier<BlockEntityType<VETileEntity>> tileRegistry;
    private final VEContainerFactory containerFactory;
    private VEEnergyStorage storage;
    private boolean infiniteRender = false;
    private AbstractItemStackValidator validator = null;
    private AbstractRecipeProcessor processor;
    private boolean sendsOutPower = false;

    public VETileEntityFactory(Supplier<BlockEntityType<VETileEntity>> tileRegistry, VEContainerFactory containerFactory) {
        this.tileRegistry = tileRegistry;
        this.containerFactory = containerFactory;
    }

    public VETileEntity create(BlockPos pos, BlockState state) {

        VETileEntity newTile = new VETileEntity(tileRegistry.get(), pos, state, recipeType == null ? null : recipeType.get()) {

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
                // This fixes a race condition issue where the client doesn't have the recipe cache built yet
                this.markFluidInputDirty();
                this.markRecipeDirty();
                return containerFactory.create(id, level, worldPosition, playerInventory, player);
            }


//TODO fix me for the dim laser
//            @Override
//            public AABB getRenderBoundingBox() {
//                if (infiniteRender) {
//                    return INFINITE_EXTENT_AABB;
//                } else {
//                    AABB cbb = null;
//                    try {
//                        VoxelShape collisionShape = state.getCollisionShape(this.getLevel(), pos);
//                        if (!collisionShape.isEmpty())
//                            cbb = collisionShape.bounds().move(pos);
//                    } catch (Exception e) {
//                        cbb = AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
//                    }
//                    return cbb;
//                }
//            }
        };

        // Add our tanks and slots
        AtomicInteger index = new AtomicInteger();
        newTile.addSlots(containerFactory.getTileSlotsAsManagers());
        index.set(0);
        newTile.addTanks(tanks.stream().map(t -> t.asTank(index.getAndIncrement())).toList());

        // Set energy before the slot count otherwise we'll run into issues with the data slot
        if (storage != null)
            newTile.energy = storage.copy();

        //set processor
        newTile.recipeProcessor = processor;

        // send out power
        newTile.sendsOutPower = sendsOutPower;

        // build out the inventory
        int inventorySize = containerFactory.getNumberOfSlots();
        if (storage == null) {
            newTile.inventory = new VEItemStackHandler(newTile, inventorySize, -1);
        } else {
            storage.setUpgradeSlotId(containerFactory.upgradeSlotId());
            newTile.inventory = new VEItemStackHandler(newTile, inventorySize, storage.getUpgradeSlotId());
        }

        newTile.inventory.setValidator(validator);

        return newTile;
    }

    public VETileEntityFactory withRecipe(Supplier<RecipeType<VERecipe>> recipe) {
        this.recipeType = recipe;
        return this;
    }

    public VETileEntityFactory addEnergyStorageWithConsumption(int maxPower, int transferRate, int consumption) {
        VEEnergyStorage storage = new VEEnergyStorage(maxPower, transferRate);
        storage.setConsumption(consumption);
        this.storage = storage;
        return this;
    }

    public VETileEntityFactory addEnergyStorage(int maxPower, int transferRate) {
        this.storage = new VEEnergyStorage(maxPower, transferRate);
        return this;
    }

    public VETileEntityFactory addTanks(TileTank... tanks) {
        this.tanks = List.of(tanks);
        return this;
    }

    public VETileEntityFactory withCustomRecipeProcessing(AbstractRecipeProcessor processor) {
        this.processor = processor;
        return this;
    }

    public VETileEntityFactory sendsOutPower() {
        this.sendsOutPower = true;
        return this;
    }

    public VETileEntityFactory withCustomInventoryValidator(AbstractItemStackValidator validator) {
        this.validator = validator;
        return this;
    }

    public record ItemInputSlot(Direction direction) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, true, SlotType.INPUT);
        }
    }

    public record ListenedItemInputSlot(Direction direction, SlotWithIOListener listener) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, false, SlotType.INPUT);
        }
    }

    public record InputSlot(Direction direction) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, true, SlotType.INPUT);
        }
    }

    public record ItemOutputSlot(Direction direction) implements TileSlot {
        @Override
        public VESlotManager asManager(int id) {
            return new VESlotManager(id, direction, true, SlotType.OUTPUT);
        }
    }

    public record BucketInputSlot(Direction direction, int tankId) implements TileSlot {
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

    public record FluidInputTank(int recipePos, int capacity, @Nullable AbstractFluidValidator fluidValidator) implements TileTank {

        public FluidInputTank(int recipePos, int capacity) {
            this(recipePos, capacity, null);
        }

        @Override
        public VERelationalTank asTank(int id) {
            return new VERelationalTank(new FluidTank(capacity), id, recipePos, TankType.INPUT, "input_tank_" + id + ":input_tank_gui",fluidValidator);
        }
    }

    public record FluidOutputTank(int recipePos, int capacity) implements TileTank {
        @Override
        public VERelationalTank asTank(int id) {
            return new VERelationalTank(new FluidTank(capacity), id, recipePos, TankType.OUTPUT, "input_tank_" + id + ":output_tank_gui");
        }
    }

    public record FluidInputOutputTank(int recipePos, int capacity,@Nullable AbstractFluidValidator fluidValidator) implements TileTank {

        public FluidInputOutputTank(int recipePos, int capacity) {
            this(recipePos,capacity,null);
        }

        @Override
        public VERelationalTank asTank(int id) {
            VERelationalTank tank = new VERelationalTank(new FluidTank(capacity), id, recipePos, TankType.BOTH, "both_tank_" + id + ":output_tank_gui",fluidValidator);
            tank.setAllowAny((fluidValidator == null));
            return tank;
        }
    }

    public interface TileSlot {
        VESlotManager asManager(int id);
    }

    public interface TileTank {
        VERelationalTank asTank(int id);
    }
}
