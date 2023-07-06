package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.recipe.RecipeFluid;
import com.veteam.voluminousenergy.util.recipe.RecipeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class AqueoulizerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {
    private final ItemStackHandler inventory = createHandler();

    public RelationalTank[] fluidManagers = new RelationalTank[]{
            new RelationalTank(new FluidTank(TANK_CAPACITY), 0, null, null, TankType.INPUT, "inputTank:input_tank_gui"),
            new RelationalTank(new FluidTank(TANK_CAPACITY), 1, null, null, TankType.OUTPUT, "outputTank:output_tank_gui")
    };

    public VESlotManager[] slotManagers = new VESlotManager[]{
            new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0),
            new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT),
            new VESlotManager(2, Direction.NORTH, true, SlotType.FLUID_HYBRID, 2, 1),
            new VESlotManager(3, Direction.SOUTH, true, SlotType.INPUT)
    };

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return List.of(slotManagers);
    }

    public AqueoulizerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AQUEOULIZER_TILE.get(), pos, state);
        fluidManagers[0].setAllowAny(true);
    }

    // Recipe caching
    RecipeItem lastItem = new RecipeItem();
    RecipeFluid lastFluid = new RecipeFluid();
    VEFluidRecipe recipe;

    @Override
    public void tick() {
        updateClients();

//        if (this.inputFluid(inputTank, 0, 1)) return;
//        if (this.outputFluid(inputTank, 0, 1)) return;
//        if (this.outputFluidStatic(outputTank, 2)) return;

        // Recipe check
        if (lastFluid.isDifferent(fluidManagers[0].getTank().getFluid())
                || lastItem.isDifferent(slotManagers[3].getItem(inventory))) {
            VEFluidRecipe newRecipe = RecipeCache.getFluidRecipeFromCache(level, AqueoulizerRecipe.class,
                    Collections.singletonList(fluidManagers[0].getTank().getFluid()),
                    slotManagers[3].getItem(inventory).copy());

            if (newRecipe != recipe) {
                counter = 0;
            }
            recipe = newRecipe;
        }

        if (recipe == null) return;

        if (!canConsumeEnergy()) {
            decrementSuperCounterOnNoPower();
            return;
        }

        // Tank cap checks
        if (fluidManagers[1].canInsertOutputFluid(recipe, 0) && canConsumeEnergy()) {
            if (counter == 1) {
                fluidManagers[0].drainInput(recipe, 0);
                inventory.extractItem(3, recipe.getItemIngredient(0).getItems()[0].getCount(), false);
                // Fill Output
                fluidManagers[1].fillOutput(recipe, 0);
                this.setChanged();
            } else if (counter > 0) {
                if (++sound_tick == 19 && Config.PLAY_MACHINE_SOUNDS.get()) {
                    sound_tick = 0;
                    level.playSound(null, this.getBlockPos(), VESounds.AQUEOULIZER, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                length = counter;
                return;
            }
            counter--;
            consumeEnergy();
        }
        //LOGGER.debug("Fluid: " + inputTank.getFluid().getRawFluid().getFilledBucket().getTranslationKey() + " amount: " + inputTank.getFluid().getAmount());
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    private ItemStackHandler createHandler() {

        VEFluidTileEntity tileEntity = this;
        return new ItemStackHandler(5) {

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                // TODO
                if (slot == getUpgradeSlotId())
                    return TagUtil.isTaggedMachineUpgradeItem(stack); // this is the upgrade slot
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

                if (slot >= slotManagers.length || slot < 0) return super.insertItem(slot, stack, simulate);
                if (!isItemValid(slot, stack))
                    return stack;
                ItemStack existing = this.stacks.get(slot);
                if (stack.is(existing.getItem())) return super.insertItem(slot, stack, simulate);

                VESlotManager manager = tileEntity.getSlotManagers().get(slot);


                if (manager.getSlotType() == SlotType.FLUID_INPUT && stack.getItem() instanceof BucketItem bucketItem && bucketItem.getFluid() != Fluids.EMPTY) {

                    VESlotManager outputManager = tileEntity.getSlotManagers().get(manager.getOutputSlotId());
                    RelationalTank tank = tileEntity.getRelationalTanks().get(manager.getTankId());
                    ItemStack outputItemSlot = outputManager.getItem(this);
                    if ((outputItemSlot.isEmpty() || (outputItemSlot.getMaxStackSize() < outputItemSlot.getCount()) && stack.is(outputItemSlot.getItem())) && tank.addBucket(bucketItem)) {
                        this.extractItem(slot,1,false);
                        return ItemStack.EMPTY;
                    }
                    return super.insertItem(slot, stack, simulate);
                } else if (manager.getSlotType() == SlotType.FLUID_OUTPUT
                        && stack.getItem() instanceof BucketItem bucketItem
                        && bucketItem.getFluid() == Fluids.EMPTY) {
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
        };
    }

    @Nonnull
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new AqueoulizerContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int progressCounterPercent() {
        if (length != 0) {
            return (int) (100 - (((float) counter / (float) length) * 100));
        } else {
            return 0;
        }
    }

    public int ticksLeft() {
        return counter;
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num >= fluidManagers.length) return FluidStack.EMPTY;
        return fluidManagers[num].getTank().getFluid();
    }

    @Override
    public @Nonnull List<RelationalTank> getRelationalTanks() {
        return List.of(this.fluidManagers);
    }

    @Override
    public int getMaxPower() {
        return Config.AQUEOULIZER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.AQUEOULIZER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.AQUEOULIZER_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 4;
    }
}
