package com.veteam.voluminousenergy.util.tiles;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.MultiFluidSlotWrapper;
import com.veteam.voluminousenergy.util.MultiSlotWrapper;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CapabilityMap {

    private final HashMap<Direction, MultiSlotWrapper> itemMap = new HashMap<>();
    private final HashMap<Direction, MultiFluidSlotWrapper> fluidMap = new HashMap<>();
    @Nullable
    private final MultiSlotWrapper unsidedInventory;
    @Nullable
    private final VEEnergyStorage energyStorage;

    public CapabilityMap(@Nullable ItemStackHandler inventory, List<VESlotManager> managerList, List<VERelationalTank> tanks,@Nullable VEEnergyStorage energy, @Nullable VETileEntity tileEntity) {
        this.unsidedInventory = inventory == null ? null : new MultiSlotWrapper(inventory, managerList, null);
        this.energyStorage = energy;
        for (Direction direction : Direction.values()) {
            if (inventory != null) {
                itemMap.put(direction, new MultiSlotWrapper(inventory, new ArrayList<>(), direction));
            }
            if (tileEntity != null) {
                fluidMap.put(direction, new MultiFluidSlotWrapper(new ArrayList<>(), tileEntity, direction));
            }
        }

        for (VESlotManager manager : managerList) {
            if (!manager.isAssigned()) {
                continue;
            }
            MultiSlotWrapper wrapper = itemMap.get(manager.getDirection());
            wrapper.addSlotManager(manager);
        }

        for (VERelationalTank tank : tanks) {
            if (!tank.isAssigned()) {
                continue;
            }
            MultiFluidSlotWrapper wrapper = fluidMap.get(tank.getSideDirection());
            wrapper.addRelationalTank(tank);
        }
    }


    @Nullable
    public IItemHandler getItemStackHandler(@Nullable Direction side, BlockEntity tileEntity) {
        if (side == null) {
            return this.unsidedInventory;
        }

        Direction normalizedSide = normalizeDirection(side, tileEntity);
        return this.itemMap.get(normalizedSide);
    }

    @Nullable
    public IEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side, BlockEntity tileEntity) {
        if(side == null) return null;
        Direction normalizedSide = normalizeDirection(side, tileEntity);
        return this.fluidMap.get(normalizedSide);
    }

    public static Direction toWorldDirection(Direction direction, BlockEntity tileEntity) {
        Direction facing = tileEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return toWorldDirection(direction, facing);
    }

    public static Direction toWorldDirection(Direction direction, Direction facing) {
        return switch (direction) {
            case SOUTH -> facing;
            case NORTH -> facing.getOpposite();
            case EAST -> facing.getClockWise();
            case WEST -> facing.getCounterClockWise();
            case UP, DOWN -> direction;
        };
    }

    private static Direction normalizeDirection(Direction direction, BlockEntity tileEntity) {
        return toLocalDirection(direction, tileEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    public static Direction toLocalDirection(Direction direction, Direction facing) {
        for (Direction localDirection : Direction.values()) {
            if (toWorldDirection(localDirection, facing) == direction) {
                return localDirection;
            }
        }
        throw new IllegalArgumentException("Unknown world direction: " + direction);
    }
}
