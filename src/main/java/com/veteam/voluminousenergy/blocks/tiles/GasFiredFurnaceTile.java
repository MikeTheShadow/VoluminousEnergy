package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.GasFiredFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class GasFiredFurnaceTile extends VEFluidTileEntity implements IVECountable {

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0));
        add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.EAST, true, SlotType.INPUT));
        add(new VESlotManager(3, Direction.WEST, true, SlotType.OUTPUT));
    }};

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0,0, TankType.INPUT, "fuel_tank:fuel_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(fuelTank);
    }};

    private int fuelCounter;
    private int fuelLength;

    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR, 0));
    private final AtomicReference<ItemStack> referenceStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR, 0));

    public GasFiredFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.GAS_FIRED_FURNACE_TILE.get(), pos, state, null);
    }

    public ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    private SmeltingRecipe furnaceRecipe;
    private BlastingRecipe blastingRecipe;
    private CombustionGeneratorFuelRecipe fuelRecipe;

    @Override
    public void tick() {
        updateClients();
        validateRecipe();
        processFluidIO();

        ItemStack furnaceInput = inventory.getStackInSlot(2).copy();
        ItemStack furnaceOutput = inventory.getStackInSlot(3).copy();

        inputItemStack.set(furnaceInput.copy()); // Atomic Reference, use this to query recipes FOR OUTPUT SLOT

        // Main Processing occurs here
        if (!fuelTank.getTank().isEmpty()) {
            if ((furnaceRecipe != null || blastingRecipe != null) && countChecker(furnaceRecipe, blastingRecipe, furnaceOutput.copy()) && itemChecker(furnaceRecipe, blastingRecipe, furnaceOutput.copy())) {
                if (counter == 1) {
                    inventory.extractItem(2, 1, false);

                    // Set output based on recipe
                    ItemStack newOutputStack;
                    newOutputStack = Objects.requireNonNullElse(furnaceRecipe, blastingRecipe).getResultItem(level.registryAccess()).copy();
                    // Output Item
                    if (furnaceOutput.getItem() != newOutputStack.getItem() || furnaceOutput.getItem() == Items.AIR) {
                        if (furnaceOutput.getItem() == Items.AIR) {
                            furnaceOutput.setCount(1);
                        }
                        inventory.insertItem(3, newOutputStack.copy(), false);

                    } else {
                        furnaceOutput.setCount(Objects.requireNonNullElse(furnaceRecipe, blastingRecipe).getResultItem(level.registryAccess()).getCount());
                        inventory.insertItem(3, furnaceOutput.copy(), false);
                    }

                    counter--;
                    this.setChanged();
                } else if (counter > 0) {
                    counter--;
                } else {
                    counter = this.calculateCounter(200, inventory.getStackInSlot(4));
                    length = counter;
                    this.referenceStack.set(furnaceInput.copy());
                }
                // Fuel Management
                if (fuelCounter == 1) {
                    fuelCounter--;
                } else if (fuelCounter > 0) {
                    fuelCounter--;
                } else {
                    if (fuelRecipe != null) {
                        // Drain Input
                        fuelTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
                        fuelCounter = fuelRecipe.getProcessTime() / 4;
                        if (inventory.getStackInSlot(4).getCount() > 0 && inventory.getStackInSlot(4).getItem() == VEItems.QUARTZ_MULTIPLIER.get()) {
                            fuelCounter = fuelCounter / (inventory.getStackInSlot(4).getCount() ^ 2);
                        } else if (!inventory.getStackInSlot(4).isEmpty() && TagUtil.isTaggedMachineUpgradeItem(inventory.getStackInSlot(4))) {
                            ItemStack upgradeStack = inventory.getStackInSlot(4).copy();
                            if (upgradeStack.getTag() != null && !upgradeStack.getTag().isEmpty()) {
                                float multiplier = upgradeStack.getTag().getFloat("multiplier");
                                multiplier = multiplier / 0.5F > 1 ? 1 : multiplier / 0.5F;
                                fuelCounter = (int) ((float) (fuelCounter * multiplier));
                            }
                        }
                        fuelLength = fuelCounter;
                        this.setChanged();
                    }
                }

            } else counter = 0;


        } else counter = 0;
    }

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;
        ItemStack furnaceInput = slotManagers.get(2).getItem(this.inventory);
        var furnaceRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);
        var blastingRecipeNew = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(furnaceInput.copy()), level).orElse(null);

        if(furnaceRecipeNew != null) furnaceRecipe = furnaceRecipeNew.value();
        if(blastingRecipeNew != null) blastingRecipe = blastingRecipeNew.value();
        fuelRecipe = (CombustionGeneratorFuelRecipe)
                RecipeCache.getFluidRecipeFromCache(level, CombustionGeneratorFuelRecipe.RECIPE_TYPE, Collections.singletonList(fuelTank.getTank().getFluid()), new ArrayList<>());
    }

    @Override
    public void load(CompoundTag tag) {
        fuelCounter = tag.getInt("fuel_counter");
        fuelLength = tag.getInt("fuel_length");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("fuel_counter", fuelCounter);
        tag.putInt("fuel_length", fuelLength);
        super.saveAdditional(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }


    private ItemStackHandler createHandler() {
        GasFiredFurnaceTile tile = this;
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                tile.markRecipeDirty();
                tile.markFluidInputDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if ((slot == 0 || slot == 1) && stack.getItem() instanceof BucketItem bucketItem) {
                    if (bucketItem.getFluid().isSame(Fluids.EMPTY)) return true;
                    FluidStack fluidStack = new FluidStack(bucketItem.getFluid(), 250);
                    return VERecipe.getCachedRecipes(CombustionGeneratorFuelRecipe.RECIPE_TYPE).stream().anyMatch(r -> ((VEFluidRecipe)r).getFluidIngredient(0).test(fluidStack));
                } else if (slot == 2) {
                    return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level).orElse(null) != null
                            || level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(stack), level).orElse(null) != null;
                } else if (slot == 3) {
                    var furnaceRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(inputItemStack.get()), level).orElse(null);
                    var blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(inputItemStack.get()), level).orElse(null);

                    // If both recipes are null, then don't bother
                    if (blastingRecipe == null && furnaceRecipe == null) return false;

                    return stack.getItem() == Objects.requireNonNullElse(furnaceRecipe, blastingRecipe).value().getResultItem(level.registryAccess()).getItem();

                } else if (slot == 4) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!

                if (!isItemValid(slot, stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (level != null && !simulate) {
                    var furnaceRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(referenceStack.get()), level).orElse(null);
                    var blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(referenceStack.get()), level).orElse(null);
                    if (blastingRecipe != null) {
                        if (inventory.getStackInSlot(slot).getItem() == blastingRecipe.value().getResultItem(level.registryAccess()).getItem()) {
                            if (blastingRecipe.value().getExperience() > 0) {
                                generateXP(amount, blastingRecipe.value().getExperience());
                            }
                        }
                    } else if (furnaceRecipe != null) {
                        if (inventory.getStackInSlot(slot).getItem() == furnaceRecipe.value().getResultItem(level.registryAccess()).getItem()) {
                            if (furnaceRecipe.value().getExperience() > 0) {
                                generateXP(amount, furnaceRecipe.value().getExperience());
                            }
                        }
                    }
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    private void generateXP(int craftedAmount, float experience) {
        if (level == null) return;
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) ++i;

        while (i > 0) {
            int j = ExperienceOrb.getExperienceValue(i);
            i -= j;
            level.addFreshEntity(new ExperienceOrb(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), j));
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new GasFiredFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressFuelCounterPX(int px) {
        if (fuelCounter == 0) {
            return 0;
        } else {
            return (px * (((fuelCounter * 100) / fuelLength))) / 100;
        }
    }

    @Deprecated // Use method that doesn't take in an int instead
    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getFluidFromTank() {
        return fuelTank.getTank().getFluid();
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    public int getFuelCounter() {
        return fuelCounter;
    }

    public int getCounter() {
        return counter;
    }


    public int progressFuelCounterPercent() {
        if (length != 0) {
            return (int) (100 - (((float) fuelCounter / (float) fuelLength) * 100));
        } else {
            return 0;
        }
    }

    public boolean countChecker(SmeltingRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack) {
        if (furnaceRecipe != null) {
            return (itemStack.getCount() + furnaceRecipe.getResultItem(level.registryAccess()).getCount()) <= 64;
        } else if (blastingRecipe != null) {
            return (itemStack.getCount() + blastingRecipe.getResultItem(level.registryAccess()).getCount()) <= 64;
        }
        return false;
    }

    public boolean itemChecker(SmeltingRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack) {
        if (furnaceRecipe != null) {
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return furnaceRecipe.getResultItem(level.registryAccess()).getItem() == itemStack.getItem();
        } else if (blastingRecipe != null) {
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return blastingRecipe.getResultItem(level.registryAccess()).getItem() == itemStack.getItem();
        }
        return false;
    }

    public RelationalTank getFuelTank() {
        return fuelTank;
    }
}
