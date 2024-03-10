package com.veteam.voluminousenergy.util.tiles;

import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.MultiFluidSlotWrapper;
import com.veteam.voluminousenergy.util.MultiSlotWrapper;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CapabilityMap {

    private final HashMap<Direction,MultiSlotWrapper> itemMap = new HashMap<>();
    private final HashMap<Direction,MultiFluidSlotWrapper> fluidMap = new HashMap<>();
    @Nullable
    private final ItemStackHandler inventory;
    private final LazyOptional<VEEnergyStorage> energyStorage;

    public CapabilityMap(@Nullable ItemStackHandler inventory, List<VESlotManager> managerList, LazyOptional<VEEnergyStorage> energy, @Nullable VETileEntity tileEntity) {
        this.inventory = inventory;
        this.energyStorage = energy;

        for(Direction direction : Direction.values()) {
            if(inventory != null) itemMap.put(direction,new MultiSlotWrapper(inventory,new ArrayList<>()));
            if(tileEntity instanceof VEFluidTileEntity fluidTileEntity) {
                fluidMap.put(direction,new MultiFluidSlotWrapper(new ArrayList<>(),fluidTileEntity));
            }
        }

        for(VESlotManager manager : managerList) {
            MultiSlotWrapper wrapper = itemMap.get(manager.getDirection());
            wrapper.addSlotManager(manager);
        }

        if (tileEntity instanceof VEFluidTileEntity tile) {
            for(RelationalTank tank : tile.getRelationalTanks()) {
                MultiFluidSlotWrapper wrapper = fluidMap.get(tank.getSideDirection());
                wrapper.addRelationalTank(tank);
            }
        }
    }


    public void moveSlotManagerPos(VESlotManager manager,int direction) {
        Direction oldDir = manager.getDirection();
        itemMap.get(oldDir).removeSlotManager(manager);
        Direction newDir  = IntToDirection.IntegerToDirection(direction);
        manager.setDirection(newDir);
        itemMap.get(manager.getDirection()).addSlotManager(manager);
    }

    public void moveFluidSlotManagerPos(RelationalTank tank,Direction direction) {
        Direction oldDirection = tank.getSideDirection();
        fluidMap.get(oldDirection).removeRelationalTank(tank);
        tank.setSideDirection(direction);
        fluidMap.get(direction).addRelationalTank(tank);
    }


    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side,VETileEntity tileEntity) {
        if(cap == ForgeCapabilities.ENERGY) {
            return this.energyStorage.cast();
        }
        if(side == null) {
            if(cap == ForgeCapabilities.ITEM_HANDLER) {
                return this.inventory == null ? LazyOptional.empty() : LazyOptional.of(() -> this.inventory).cast();
            }
        } else {
            Direction normalizedSide = CapabilityMap.normalizeDirection(side,tileEntity);
            if(cap == ForgeCapabilities.ITEM_HANDLER) {
                return inventory == null ? LazyOptional.empty() : LazyOptional.of(() -> this.itemMap.get(normalizedSide)).cast();
            } else if(cap == ForgeCapabilities.FLUID_HANDLER) {
                return LazyOptional.of(() -> this.fluidMap.get(normalizedSide)).cast();
            }
        }
        return LazyOptional.empty();
    }

    public static Direction normalizeDirection(Direction direction,VETileEntity tileEntity) {
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
