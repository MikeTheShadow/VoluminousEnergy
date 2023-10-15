package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DistillationUnitTile extends VEMultiBlockTileEntity implements IVEPoweredTileEntity, IVECountable {
    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, false, SlotType.FLUID_INPUT));
        add(new VESlotManager(1, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.UP, false, SlotType.FLUID_INPUT));
        add(new VESlotManager(3, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(4, Direction.UP, false, SlotType.FLUID_INPUT));
        add(new VESlotManager(5, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(6, 0, Direction.DOWN, false, SlotType.OUTPUT));
    }};

    public List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank:input_tank_gui"));
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 1, 0, TankType.OUTPUT, "outputTank0:output_tank_0_gui"));
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 2, 1, TankType.OUTPUT, "outputTank1:output_tank_1_gui"));
    }};

    private byte tick = 19;

    public ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    public DistillationUnitTile(BlockPos pos, BlockState state) {
        super(VEBlocks.DISTILLATION_UNIT_TILE.get(), pos, state, DistillationRecipe.RECIPE_TYPE);
    }

    DistillationRecipe recipe;

    @Override
    public void tick() {
        updateClients();
        validateRecipe();
        tick++;
        if (tick == 20) {
            tick = 0;
            validity = isMultiBlockValid(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK.get());
        }
        if (!(validity)) {
            return;
        }
        super.tick();

        // Main Fluid Processing occurs here:
        if (selectedRecipe != null) {
            ItemStack thirdOutput = inventory.getStackInSlot(6).copy();


            // Tank fluid amount check + tank cap checks
            if (thirdOutput.getCount() < recipe.getResult(0).getMaxStackSize()) {
                // Check for power
                if (canConsumeEnergy()) {
                    if (counter == 1) {

                        // Drain Input
                        fluidManagers.get(0).drainInput(recipe, 0);
                        fluidManagers.get(1).fillOutput(recipe, 0);

                        // Second Output Tank
                        fluidManagers.get(2).fillOutput(recipe, 1);

                        if (Mth.abs(0 + level.getRandom().nextFloat() * (-1)) < recipe.getOutputChance(0)) {
                            if (thirdOutput.getItem() != recipe.getResult(0).getItem()) {
                                if (thirdOutput.getItem() == Items.AIR) { // To prevent the slot from being jammed by air
                                    thirdOutput.setCount(1);
                                }
                                inventory.insertItem(6, recipe.getResult(0).copy(), false); // CRASH the game if this is not empty!
                            } else { // Assuming the recipe output item is already in the output slot
                                inventory.insertItem(6, recipe.getResult(0).copy(), false); // Place the new output item on top of the old one
                            }
                        }

                        counter--;
                        consumeEnergy();
                        this.setChanged();
                        this.markRecipeDirty();
                    } else if (counter > 0) {
                        counter--;
                        consumeEnergy();
                        if (++sound_tick == 19) {
                            sound_tick = 0;
                            if (Config.PLAY_MACHINE_SOUNDS.get()) {
                                level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                    } else {
                        counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(this.getUpgradeSlotId()).copy());
                        length = counter;
                    }
                } else { // Energy Check
                }
            }
        }

        // End of item handler

    }

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;
        recipe = (DistillationRecipe) RecipeCache.getFluidRecipeFromCache(level, DistillationRecipe.RECIPE_TYPE,
                Collections.singletonList(fluidManagers.get(0).getTank().getFluid()), new ArrayList<>());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(8) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                markRecipeDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
//                if (slot == 0 || slot == 1) {
//                    VEFluidRecipe recipe = level.getRecipeManager().getRecipeFor(DistillationRecipe.RECIPE_TYPE, new SimpleContainer(item), level).orElse(null);
//                    return recipe != null || item.getItem() == Items.BUCKET;
//                } else if (slot == 2 || slot == 3 && item.getItem() instanceof BucketItem) {
//                    if (item.getItem() == Items.BUCKET) return true;
//
//                    return RecipeUtil.getDistillationRecipeFromResult(level, new FluidStack(((BucketItem) item.getItem()).getFluid(), 1000)) != null;
//                } else if (slot == 4 || slot == 5 && item.getItem() instanceof BucketItem) {
//                    if (item.getItem() == Items.BUCKET) return true;
//
//                    return RecipeUtil.getDistillationRecipeFromSecondResult(level, new FluidStack(((BucketItem) item.getItem()).getFluid(), 1000)) != null;
//                } else if (slot == 6) {
//                    return RecipeUtil.getDistillationRecipeFromThirdResult(level, item) != null;
//                } else if (slot == 7) {
//                    return TagUtil.isTaggedMachineUpgradeItem(item);
//                }
                // TODO fix me
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new DistillationUnitContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    public boolean getMultiblockValidity() {
        return validity;
    }

    @Override
    public int getMaxPower() {
        return Config.DISTILLATION_UNIT_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.DISTILLATION_UNIT_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.DISTILLATION_UNIT_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 7;
    }
}