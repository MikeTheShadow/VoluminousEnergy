package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRNGRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.util.Mth.abs;

public abstract class VEFluidTileEntity extends VETileEntity implements IFluidTileEntity {

    public static final int TANK_CAPACITY = 4000;
    private boolean fluidInputDirty;

    public VEFluidTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends Recipe<?>> recipeType) {
        super(type, pos, state, recipeType);
    }

    @Override
    public ItemStackHandler createHandler(int slots) {

        VEFluidTileEntity tileEntity = this;
        int upgradeSlotLocation = -1;
        if (tileEntity instanceof IVEPoweredTileEntity poweredTileEntity) {
            upgradeSlotLocation = poweredTileEntity.getUpgradeSlotId();
        }

        int finalUpgradeSlotLocation = upgradeSlotLocation;
        return new ItemStackHandler(slots) {

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                List<VESlotManager> managers = getSlotManagers();

                if(slot == finalUpgradeSlotLocation) tileEntity.markRecipeDirty();
                else if (slot < managers.size()) {
                    SlotType slotType = getSlotManagers().get(slot).getSlotType();
                    if (slotType == SlotType.INPUT) {
                        tileEntity.markRecipeDirty();
                    } else if (slotType.isFluidBucketIORelated()) {
                        tileEntity.markFluidInputDirty();
                    }
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == finalUpgradeSlotLocation) return TagUtil.isTaggedMachineUpgradeItem(stack);
                VESlotManager manager = tileEntity.getSlotManagers().get(slot);
                if(manager.getAllowedItems().contains(stack.getItem())) return true;
                if (manager.getSlotType() == SlotType.FLUID_INPUT && stack.getItem() instanceof BucketItem bucketItem) {
                    if (bucketItem.getFluid() == Fluids.EMPTY) return true;
                    RelationalTank tank = tileEntity.getRelationalTanks().get(manager.getTankId());
                    if(tank.getTankType() == TankType.OUTPUT) {
                        return bucketItem.getFluid().isSame(Fluids.EMPTY);
                    }
                    for (Recipe<?> recipe : tileEntity.getPotentialRecipes()) {
                        VEFluidRecipe veFluidRecipe = (VEFluidRecipe) recipe;
                        if (veFluidRecipe.getFluidIngredient(tank.getRecipePos()).test(new FluidStack(bucketItem.getFluid(), 1))) {
                            return true;
                        }
                    }
                } else if (manager.getSlotType() == SlotType.INPUT) {
                    for (Recipe<?> recipe : tileEntity.getPotentialRecipes()) {
                        VEFluidRecipe veFluidRecipe = (VEFluidRecipe) recipe;
                        if (veFluidRecipe.getIngredient(manager.getRecipePos()).test(stack)) {
                            return true;
                        }
                    }
                } else return manager.getSlotType() == SlotType.FLUID_OUTPUT;
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    //use for inputting a fluid
    public void inputFluid(RelationalTank tank, int slot1, int slot2) {
        ItemStack input = tank.getInput().copy();
        ItemStack output = tank.getOutput().copy();
        FluidTank inputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (input.getItem() instanceof BucketItem && input.getItem() != Items.BUCKET) {
            if ((output.getItem() == Items.BUCKET && output.getCount() < 16) || checkOutputSlotForEmptyOrBucket(output)) {
                Fluid fluid = ((BucketItem) input.getItem()).getFluid();
                if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= inputTank.getTankCapacity(0)) {
                    inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                    handler.extractItem(slot1, 1, false);
                    handler.insertItem(slot2, new ItemStack(Items.BUCKET, 1), false);
                    this.markRecipeDirty();
                }
            }
        }
    }


    //use for when the input and output slot are different
    public void outputFluid(RelationalTank tank, int slot1, int slot2) {
        ItemStack inputSlot = tank.getInput();
        ItemStack outputSlot = tank.getOutput();
        FluidTank outputTank = tank.getTank();
        ItemStackHandler handler = getInventoryHandler();
        if (inputSlot.getItem() == Items.BUCKET && outputTank.getFluidAmount() >= 1000 && inputSlot.getCount() > 0 && outputSlot.copy() == ItemStack.EMPTY) {
            ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getBucket(), 1);
            outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            handler.extractItem(slot1, 1, false);
            handler.insertItem(slot2, bucketStack, false);
            this.markRecipeDirty();
        }
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
    void processRecipe() {
        if (selectedRecipe == null) return;
        VEFluidRecipe recipe = (VEFluidRecipe) selectedRecipe;

        if (canConsumeEnergy()) {

            if (counter == 1) {
                // Validate output
                for (RelationalTank relationalTank : getRelationalTanks()) {
                    if (relationalTank.getTankType() == TankType.OUTPUT) {
                        FluidStack recipeFluid = recipe.getOutputFluid(relationalTank.getRecipePos());
                        FluidTank tank = relationalTank.getTank();
                        FluidStack currentFluid = tank.getFluid();
                        if(currentFluid.isEmpty()) continue;
                        // If the output fluid amount won't fit, then you must acquit
                        if (!recipeFluid.isFluidEqual(currentFluid)
                                || tank.getFluidAmount() + recipeFluid.getAmount() > tank.getCapacity()) {
                            return;
                        }
                    }
                }

                ItemStackHandler handler = getInventoryHandler();

                if (handler != null) {
                    // Validate output
                    for (VESlotManager slotManager : getSlotManagers()) {
                        if(slotManager.getSlotType() != SlotType.OUTPUT) continue;
                        ItemStack recipeStack = recipe.getResult(slotManager.getRecipePos());
                        ItemStack currentItem = slotManager.getItem(handler);
                        if(currentItem.isEmpty()) continue;
                        // If the output item amount won't fit, then you must acquit
                        if(!recipeStack.is(currentItem.getItem())
                                || recipeStack.getCount() + currentItem.getCount() > currentItem.getMaxStackSize()) {
                            return;
                        }
                    }

                    VEFluidRNGRecipe irngRecipe = null;
                    if(recipe instanceof VEFluidRNGRecipe rec) {
                        irngRecipe = rec;
                    }
                    Random r = new Random();

                    // process recipe
                    for(VESlotManager slotManager : getSlotManagers()) {
                        if(slotManager.getSlotType() == SlotType.OUTPUT) {
                            ItemStack output = recipe.getResult(slotManager.getRecipePos());
                            ItemStack currentStack = slotManager.getItem(handler);
                            // rng calculations
                            if(irngRecipe != null) {
                                float randomness = irngRecipe.getOutputChance(slotManager.getRecipePos());
                                if(randomness != 1) {
                                    float random = abs(0 + r.nextFloat() * (-1));
                                    if(random > randomness) continue;
                                }

                            }
                            if(currentStack.isEmpty()) slotManager.setItem(output,handler);
                            else currentStack.setCount(currentStack.getCount() + output.getCount());
                        } else if(slotManager.getSlotType() == SlotType.INPUT) {
                            Ingredient ingredient = recipe.getIngredient(slotManager.getRecipePos());
                            ItemStack currentStack = slotManager.getItem(handler);
                            currentStack.setCount(currentStack.getCount() - ingredient.getItems()[0].getCount());
                        }
                    }
                }

                // process recipe
                for (RelationalTank relationalTank : getRelationalTanks()) {
                    if (relationalTank.getTankType() == TankType.OUTPUT) {
                        relationalTank.fillOutput(recipe,relationalTank.getRecipePos());
                    } else if(relationalTank.getTankType() == TankType.INPUT) {
                        relationalTank.drainInput(recipe,relationalTank.getRecipePos());
                    }
                }
                doExtraRecipeProcessing();

                this.markRecipeDirty();
                this.markFluidInputDirty();
                this.setChanged();
            } else if (counter > 0) {
                if (++sound_tick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
                    sound_tick = 0;
                    level.playSound(null, this.getBlockPos(), VESounds.AQUEOULIZER, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                counter = length;
            }
            counter--;
            consumeEnergy();
        }
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
            if (id == tank.getSlotNum()) {
                this.capabilityMap.moveFluidSlotManagerPos(tank,IntToDirection.IntegerToDirection(direction));
            }
        }
    }

    public abstract @Nonnull List<RelationalTank> getRelationalTanks();

    public static boolean checkOutputSlotForEmptyOrBucket(ItemStack slotStack) {
        return slotStack.copy() == ItemStack.EMPTY || ((slotStack.copy().getItem() == Items.BUCKET) && slotStack.copy().getCount() < 16);
    }

    public void markFluidInputDirty() {
        this.fluidInputDirty = true;
    }

    protected void processFluidIO() {
        if (!fluidInputDirty) return;
        fluidInputDirty = false;
        for (VESlotManager manager : this.getSlotManagers()) {
            ItemStackHandler inventory = this.getInventoryHandler();
            if (manager.getSlotType() == SlotType.FLUID_INPUT) {

                RelationalTank tank = this.getRelationalTanks().get(manager.getTankId());
                tank.setInput(inventory.getStackInSlot(manager.getSlotNum()));
                tank.setOutput(inventory.getStackInSlot(manager.getOutputSlotId()));
                if(tank.getTankType() == TankType.INPUT || tank.getTankType() == TankType.BOTH) inputFluid(tank, manager.getSlotNum(), manager.getOutputSlotId());
                outputFluid(tank, manager.getSlotNum(), manager.getOutputSlotId());
            }
        }
    }

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;

        this.potentialRecipes = RecipeCache.getFluidRecipesFromCache(level, this.getRecipeType(), getSlotManagers(), getRelationalTanks(), this, true);
        if (this.potentialRecipes.size() == 1) {
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

            if (newRecipe == null) {
                counter = 0;
                length = 0;
                this.selectedRecipe = null;
                return;
            }

            int newLength;

            if (this instanceof IVEPoweredTileEntity poweredTileEntity && handler != null) {
                newLength = this.calculateCounter(newRecipe.getProcessTime(),
                        handler.getStackInSlot(poweredTileEntity.getUpgradeSlotId()).copy());
            } else {
                newLength = this.calculateCounter(newRecipe.getProcessTime(), ItemStack.EMPTY);
            }


            double ratio = (double) length / (double) newLength;
            length = newLength;
            counter = (int) (counter / ratio);

            if (this.selectedRecipe != newRecipe) {
                this.selectedRecipe = newRecipe;
                counter = newLength;
            }
        } else {
            counter = 0;
            length = 0;
            this.selectedRecipe = null;
        }
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num >= getRelationalTanks().size() || num < 0) {
            return FluidStack.EMPTY;
        }
        return getRelationalTanks().get(num).getTank().getFluid();
    }
}
