package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.veteam.voluminousenergy.VoluminousEnergy.LOGGER;

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
        ItemStackHandler handler = getItemStackHandler();
        if (input.copy().getItem() instanceof BucketItem && input.copy().getItem() != Items.BUCKET) {
            if((output.copy().getItem() == Items.BUCKET && output.copy().getCount() < 16) || output.copy() == ItemStack.EMPTY) {
                Fluid fluid = ((BucketItem) input.copy().getItem()).getFluid();
                if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= TANK_CAPACITY) {
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
        ItemStackHandler handler = getItemStackHandler();
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
        ItemStackHandler handler = getItemStackHandler();
        if (inputSlot.copy().getItem() == Items.BUCKET && inputSlot.copy().getCount() == 1 && outputTank.getFluidAmount() >= 1000) {
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot,1,false);
            handler.insertItem(slot, bucketStack, false);
            return true;
        }
        return false;
    }

    public ItemStackHandler getItemStackHandler() {
        return null;
    }


    public IFluidHandler createFluidHandler(VEFluidRecipe veRecipe, RelationalTank... relationalTanks) {

        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return relationalTanks.length;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? FluidStack.EMPTY : t.getTank().getFluid();
                    }
                }
                LOGGER.debug("Invalid tankId in VEFluidTileEntity for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? 0 : t.getTank().getCapacity();
                    }
                }
                LOGGER.debug("Invalid tankId in VEFluidTileEntity for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                try {
                    for (RelationalTank t : relationalTanks) {
                        if (t.getTankType() == TankType.INPUT) {
                            ItemStack bucketStack = new ItemStack(stack.getRawFluid().getBucket());
                            VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(veRecipe.getType(), new SimpleContainer(bucketStack), level).orElse(null);
                            return recipe != null && t.getTank() != null && t.getTank().isFluidValid(stack);
                        } else {
                            AtomicBoolean recipeHit = new AtomicBoolean(false);
                            veRecipe.getIngredientList().forEach(i -> {
                                VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(veRecipe.getType(), new SimpleContainer(new ItemStack(i)), level).orElse(null);
                                if (recipe != null && recipe.getFluids().get(t.getOutputID()).getFluid().isSame(stack.getFluid())) { // In theory should never be null
                                    recipeHit.set(true);
                                }
                            });
                            return recipeHit.get() && t.getTank() != null && t.getTank().isFluidValid(stack);
                        }
                    }
                } catch (Exception e){
                    LOGGER.debug("ERROR with isFluidValid in VEFluidTileEntity");
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {

                for(RelationalTank t : relationalTanks) {
                    if(isFluidValid(t.getId(),resource) && t.getTank().isEmpty() || resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().fill(resource, action);
                    }
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }

                for(RelationalTank t : relationalTanks) {
                    if(resource.isFluidEqual(t.getTank().getFluid())) {
                        return t.getTank().drain(resource,action);
                    }
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                for(RelationalTank t : relationalTanks) {
                    if(t.getTank().getFluidAmount() > 0) {
                        if (Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                            return t.getTank().drain(maxDrain, action);
                        } else if (t.getTankType() != TankType.INPUT) {
                            return t.getTank().drain(maxDrain, action);
                        }
                    }
                }
                return FluidStack.EMPTY;
            }
        };
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    public void bulkSendTankPackets(ServerPlayer s, RelationalTank... tanks) {
        for(RelationalTank tank : tanks) {
            VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(tank.getSideStatus(), tank.getId()));
            VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(tank.getSideDirection().get3DDataValue(), tank.getId()));
        }
   }

    public void processGUIPacketFluidStatus(boolean status, int id, RelationalTank... tanks) {
        for(RelationalTank tank : tanks) {
            if(id == tank.getId()) tank.setSideStatus(status);
        }
    }

    public void processGUIPacketFluidDirection(int direction, int id, RelationalTank... tanks) {
        for(RelationalTank tank : tanks) {
            if(id == tank.getId()) tank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        }
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side,LazyOptional<ItemStackHandler> handler , List<RelationalTank> tanks) {
        if (side == null)
            return handler.cast();
        List<RelationalTank> relationalTanks = tanks.stream().filter(manager -> manager.getSideStatus() && manager.getSideDirection().get3DDataValue() == side.get3DDataValue()).toList();
        if(relationalTanks.size() == 0) return super.getCapability(cap, side);
        MultiFluidSlotWrapper slotWrapper = new MultiFluidSlotWrapper(relationalTanks);
        return LazyOptional.of(() -> slotWrapper).cast();
    }

}
