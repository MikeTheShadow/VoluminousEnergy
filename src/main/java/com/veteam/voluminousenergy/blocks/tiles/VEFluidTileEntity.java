package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.VERecipeFluid;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.veteam.voluminousenergy.VoluminousEnergy.LOGGER;

public abstract class VEFluidTileEntity extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int TANK_CAPACITY = 4000;

    public VEFluidTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    //use for inputting a fluid
    public boolean inputFluid(RelationalTank tank,int slot1,int slot2) {
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
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getFilledBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot1,1,false);
            handler.insertItem(slot2, bucketStack, false);
            return true;
        }
        return false;
    }
    //Use only when the input and ouput slot are the same slot
    public boolean outputFluidStatic(RelationalTank tank,int slot) {

        ItemStack inputSlot = tank.getOutput();
        FluidTank outputTank = tank.getTank();
        ItemStackHandler handler = getItemStackHandler();
        if (inputSlot.copy().getItem() == Items.BUCKET && inputSlot.copy().getCount() == 1 && outputTank.getFluidAmount() >= 1000) {
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getFilledBucket(), 1);
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


    public IFluidHandler createFluidHandler(VERecipeFluid veRecipe, RelationalTank... relationalTanks) {

        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 3;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? FluidStack.EMPTY : t.getTank().getFluid();
                    }
                }
                LOGGER.debug("Invalid tankId in Centrifugal Agitator Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? 0 : t.getTank().getCapacity();
                    }
                }
                LOGGER.debug("Invalid tankId in Centrifugal Agitator Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getTankType() == TankType.INPUT) {
                        ItemStack bucketStack = new ItemStack(stack.getRawFluid().getFilledBucket());
                        VERecipeFluid recipe = world.getRecipeManager().getRecipe(veRecipe.getType(), new Inventory(bucketStack), world).orElse(null);
                        return recipe != null && t.getTank() != null && t.getTank().isFluidValid(stack);
                    } else {
                        AtomicBoolean recipeHit = new AtomicBoolean(false);
                        veRecipe.getIngredientList().forEach(i -> {
                            VERecipeFluid recipe = world.getRecipeManager().getRecipe(veRecipe.getType(), new Inventory(new ItemStack(i)), world).orElse(null);
                            if (recipe != null && recipe.getOutputFluids().get(t.getOutputID()).getFluid().isEquivalentTo(stack.getFluid())){ // In theory should never be null
                                recipeHit.set(true);
                            }
                        });
                        return recipeHit.get() && t.getTank() != null && t.getTank().isFluidValid(stack);
                    }
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
                        return t.getTank().drain(maxDrain,action);
                    }
                }
                return FluidStack.EMPTY;
            }
        };
    }

}
