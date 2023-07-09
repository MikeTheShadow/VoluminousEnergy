package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidTileEntity extends VETileEntity implements IFluidTileEntity {

    public static final int TANK_CAPACITY = 4000;
    private boolean fluidInputDirty;

    private final List<VEFluidRecipe> fluidRecipeList = new ArrayList<>();

    public VEFluidTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends Recipe<?>> recipeType) {
        super(type, pos, state, recipeType);
    }

    @Deprecated
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
            if ((output.copy().getItem() == Items.BUCKET && output.copy().getCount() < 16) || checkOutputSlotForEmptyOrBucket(output.copy())) {
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
    public boolean outputFluid(RelationalTank tank, int slot1, int slot2) {

        ItemStack inputSlot = tank.getInput();
        ItemStack outputSlot = tank.getOutput();
        FluidTank outputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (inputSlot.getItem() == Items.BUCKET && outputTank.getFluidAmount() >= 1000 && inputSlot.getCount() > 0 && outputSlot.copy() == ItemStack.EMPTY) {
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot1, 1, false);
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
            handler.extractItem(slot, 1, false);
            handler.insertItem(slot, bucketStack, false);
            return true;
        }
        return false;
    }

    @Override
    public void load(CompoundTag tag) {

        for (RelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = tag.getCompound(relationalTank.getTankName());
            relationalTank.getTank().readFromNBT(compoundTag);
            relationalTank.readGuiProperties(tag);
        }

        super.load(tag);
    }

    @Override
    public void tick() {
        processFluidIO();
        super.tick();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {

        //Save tanks
        for (RelationalTank relationalTank : getRelationalTanks()) {
            CompoundTag compoundTag = new CompoundTag();
            relationalTank.getTank().writeToNBT(compoundTag);
            tag.put(relationalTank.getTankName(), compoundTag);
            relationalTank.writeGuiProperties(tag);
        }

        super.saveAdditional(tag);
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    public void updateTankPacketFromGui(boolean status, int id) {
        for (RelationalTank tank : getRelationalTanks()) {
            if (id == tank.getSlotNum()) tank.setSideStatus(status);
        }
    }

    public void updateTankPacketFromGui(int direction, int id) {
        for (RelationalTank tank : getRelationalTanks()) {
            if (id == tank.getSlotNum()) tank.setSideDirection(IntToDirection.IntegerToDirection(direction));
        }
    }

    public abstract @Nonnull List<RelationalTank> getRelationalTanks();

    public static boolean checkOutputSlotForEmptyOrBucket(ItemStack slotStack) {
        return slotStack.copy() == ItemStack.EMPTY || ((slotStack.copy().getItem() == Items.BUCKET) && slotStack.copy().getCount() < 16);
    }

    public void processInputNextTick() {
        this.fluidInputDirty = true;
    }

    void processFluidIO() {
        if (!fluidInputDirty) return;
        fluidInputDirty = false;
        for (VESlotManager manager : this.getSlotManagers()) {
            ItemStackHandler inventory = this.getInventoryHandler();
            if (manager.getSlotType() == SlotType.FLUID_INPUT) {

                RelationalTank tank = this.getRelationalTanks().get(manager.getTankId());
                tank.setInput(inventory.getStackInSlot(manager.getSlotNum()));
                tank.setOutput(inventory.getStackInSlot(manager.getOutputSlotId()));
                inputFluid(tank, manager.getSlotNum(), manager.getOutputSlotId());
                outputFluid(tank, manager.getSlotNum(), manager.getOutputSlotId());

            } else if (manager.getSlotType() == SlotType.FLUID_HYBRID) {

                RelationalTank tank = this.getRelationalTanks().get(manager.getTankId());
                tank.setInput(inventory.getStackInSlot(manager.getSlotNum()));
                tank.setOutput(inventory.getStackInSlot(manager.getSlotNum()));
                outputFluidStatic(tank, manager.getSlotNum());

            }
        }
    }

    @Override
    public void validateRecipe() {
        if(!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;

        this.potentialRecipes = RecipeCache.getFluidRecipesFromCache(level,this.getRecipeType(),getSlotManagers(),getRelationalTanks(),this,true);
        if(this.potentialRecipes.size() == 1) {
            List<FluidStack> inputFluids = this.getRelationalTanks().stream()
                    .filter(tank -> tank.getTankType() == TankType.INPUT)
                    .map(tank -> tank.getTank().getFluid()).toList();

            ItemStackHandler handler = this.getInventoryHandler();
            List<ItemStack> inputItems = new ArrayList<>();
            if (handler != null) {
                inputItems = this.getSlotManagers().stream()
                        .filter(manager -> manager.getSlotType() == SlotType.INPUT)
                        .map(manager -> manager.getItem(handler)).toList();
            }
            VEFluidRecipe newRecipe = RecipeCache.getFluidRecipeFromCache(level, getRecipeType(), inputFluids, inputItems);

            if(newRecipe == null) {
                counter = 0;
                length = 0;
                this.selectedRecipe = null;
                return;
            }
            
            if (this instanceof IVEPoweredTileEntity poweredTileEntity && handler != null) {
                counter = this.calculateCounter(newRecipe.getProcessTime(),
                        handler.getStackInSlot(poweredTileEntity.getUpgradeSlotId()).copy());
            } else {
                counter = this.calculateCounter(newRecipe.getProcessTime(), ItemStack.EMPTY);
            }
            length = counter;
            if (this.selectedRecipe != newRecipe) {
                this.selectedRecipe = newRecipe;
            }
        } else {
            counter = 0;
            length = 0;
            this.selectedRecipe = null;
        }
    }
}
