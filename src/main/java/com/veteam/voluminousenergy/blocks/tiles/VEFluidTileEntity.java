package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class VEFluidTileEntity extends VoluminousTileEntity implements IFluidTileEntity {

    public static final int TANK_CAPACITY = 4000;

    public VEFluidTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //use for inputting a fluid
    public boolean inputFluid(RelationalTank tank, int slot1, int slot2) {
        ItemStack input = tank.getInput();
        ItemStack output = tank.getOutput();
        FluidTank inputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (input.copy().getItem() instanceof BucketItem && input.copy().getItem() != Items.BUCKET) {
            if((output.copy().getItem() == Items.BUCKET && output.copy().getCount() < 16) || checkOutputSlotForEmptyOrBucket(output.copy())) {
                Fluid fluid = ((BucketItem) input.copy().getItem()).getFluid();
                if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= inputTank.getTankCapacity(0)) {
                    inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    handler.extractItem(slot1, 1, false);
                    handler.insertItem(slot2, new ItemStack(Items.BUCKET, 1), false);
                    return true;
                }
            }
        }
        return false;
    }

    //use for when the input and output slot are different
    public boolean outputFluid(RelationalTank tank,int slot1, int slot2) {

        ItemStack inputSlot = tank.getInput();
        ItemStack outputSlot = tank.getOutput();
        FluidTank outputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (inputSlot.getItem() == Items.BUCKET && outputTank.getFluidAmount() >= 1000 && inputSlot.getCount() > 0 && outputSlot.copy() == ItemStack.EMPTY){
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot1,1,false);
            handler.insertItem(slot2, bucketStack, false);
            return true;
        }
        return false;
    }
    //Use only when the input and output slot are the same slot
    public boolean outputFluidStatic(RelationalTank tank, int slot) {

        ItemStack inputSlot = tank.getOutput();
        FluidTank outputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (inputSlot.copy().getItem() == Items.BUCKET && inputSlot.copy().getCount() == 1 && outputTank.getFluidAmount() >= 1000) {
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot,1,false);
            handler.insertItem(slot, bucketStack, false);
            return true;
        }
        return false;
    }

    @Override
    public void load(CompoundTag tag) {

        for(RelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = tag.getCompound(relationalTank.getTankName());
            relationalTank.getTank().readFromNBT(compoundTag);
            relationalTank.readGuiProperties(tag);
        }

        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {

        //Save tanks
        for(RelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = new CompoundTag();
            relationalTank.getTank().writeToNBT(compoundTag);
            tag.put(relationalTank.getTankName(),compoundTag);
            relationalTank.writeGuiProperties(tag);
        }

        super.saveAdditional(tag);
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    public void updateTankPacketFromGui(boolean status, int id) {
        for(RelationalTank tank : getRelationalTanks()) {
            if(id == tank.getSlotNum()) tank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id) {
        for(RelationalTank tank : getRelationalTanks()) {
            if(id == tank.getSlotNum()) tank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        }
    }

    public abstract @Nonnull List<RelationalTank> getRelationalTanks();

    public static boolean checkOutputSlotForEmptyOrBucket(ItemStack slotStack){
        return slotStack.copy() == ItemStack.EMPTY || ((slotStack.copy().getItem() == Items.BUCKET) && slotStack.copy().getCount() < 16);
    }

}
