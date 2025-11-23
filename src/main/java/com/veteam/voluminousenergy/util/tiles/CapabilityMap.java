package com.veteam.voluminousenergy.util.tiles;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
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
    private final ItemStackHandler inventory;
    @Nullable
    private final VEEnergyStorage energyStorage;

    public CapabilityMap(@Nullable ItemStackHandler inventory, List<VESlotManager> managerList, List<VERelationalTank> tanks,@Nullable VEEnergyStorage energy, @Nullable VETileEntity tileEntity) {
        this.inventory = inventory;
        this.energyStorage = energy;
        for (Direction direction : Direction.values()) {
            if (inventory != null) itemMap.put(direction, new MultiSlotWrapper(inventory, new ArrayList<>()));
            if (tileEntity != null) fluidMap.put(direction, new MultiFluidSlotWrapper(new ArrayList<>(), tileEntity));
        }

        for (VESlotManager manager : managerList) {
            MultiSlotWrapper wrapper = itemMap.get(manager.getDirection());
            wrapper.addSlotManager(manager);
        }

        for (VERelationalTank tank : tanks) {
            MultiFluidSlotWrapper wrapper = fluidMap.get(tank.getSideDirection());
            wrapper.addRelationalTank(tank);
        }
    }


    public void moveSlotManagerPos(VESlotManager manager, int direction) {
        Direction oldDir = manager.getDirection();
        itemMap.get(oldDir).removeSlotManager(manager);
        Direction newDir = IntToDirection.IntegerToDirection(direction);
        manager.setDirection(newDir);
        itemMap.get(manager.getDirection()).addSlotManager(manager);
    }

    public void moveFluidSlotManagerPos(VERelationalTank tank, Direction direction) {
        Direction oldDirection = tank.getSideDirection();
        fluidMap.get(oldDirection).removeRelationalTank(tank);
        tank.setSideDirection(direction);
        fluidMap.get(direction).addRelationalTank(tank);
    }

    @Nullable
    public IItemHandler getItemStackHandler(@Nullable Direction side, BlockEntity tileEntity) {
        if (side == null) return this.inventory;

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

    private static Direction normalizeDirection(Direction direction, BlockEntity tileEntity) {
        Direction currentDirection = tileEntity.getBlockState().getValue(BlockStateProperties.FACING);
        int directionInt = direction.get3DDataValue();
        if (directionInt == 0 || directionInt == 1) return direction;
        Direction rotated = currentDirection;
        for (int i = 0; i < 4; i++) {
            rotated = rotated.getClockWise();
            direction = direction.getClockWise();
            if (rotated.get3DDataValue() == 2) break;
        }
        return direction.getClockWise().getClockWise();
    }
}
