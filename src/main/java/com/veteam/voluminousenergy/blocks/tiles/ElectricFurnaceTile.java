package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ElectricFurnaceContainer;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ElectricFurnaceTile extends VETileEntity implements IVEPoweredTileEntity, IVECountable {

    public VESlotManager inputSlotManager = new VESlotManager(0, Direction.UP, true, SlotType.INPUT);
    public VESlotManager outputSlotManager = new VESlotManager(1, Direction.DOWN, true, SlotType.OUTPUT);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(inputSlotManager);
        add(outputSlotManager);
    }};

    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<>(new ItemStack(Items.AIR, 0));
    private final AtomicReference<ItemStack> referenceStack = new AtomicReference<>(new ItemStack(Items.AIR, 0));

    public ElectricFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.ELECTRIC_FURNACE_TILE.get(), pos, state, null);
    }

    public ItemStackHandler inventory = createHandler();

    private SmeltingRecipe furnaceRecipe;
    private BlastingRecipe blastingRecipe;

    @Override
    public void tick() {
        updateClients();
        validateRecipe();
        ItemStack furnaceInput = inventory.getStackInSlot(0).copy();
        ItemStack furnaceOutput = inventory.getStackInSlot(1).copy();

        inputItemStack.set(furnaceInput.copy());
        if (!canConsumeEnergy()) return;

        if ((furnaceRecipe != null || blastingRecipe != null) && countChecker(furnaceRecipe, blastingRecipe, furnaceOutput.copy()) && itemChecker(furnaceRecipe, blastingRecipe, furnaceOutput.copy())) {
            if (counter == 1) {
                // Extract item
                inventory.extractItem(0, 1, false);

                // Set output based on recipe
                ItemStack newOutputStack;
                newOutputStack = Objects.requireNonNullElse(furnaceRecipe, blastingRecipe).getResultItem(level.registryAccess()).copy();
                //LOGGER.debug("NewOutputStack: " + newOutputStack);

                // Output Item
                if (furnaceOutput.getItem() != newOutputStack.getItem() || furnaceOutput.getItem() == Items.AIR) {
                    //LOGGER.debug("The output is not equal to the new output Stack");
                    if (furnaceOutput.getItem() == Items.AIR) { // Fix air >1 jamming slots
                        furnaceOutput.setCount(1);
                    }
                    if (furnaceRecipe != null) {
                        newOutputStack.setCount(furnaceRecipe.getResultItem(level.registryAccess()).getCount());
                    } else {
                        newOutputStack.setCount(blastingRecipe.getResultItem(level.registryAccess()).getCount());
                    }
                    //LOGGER.debug("About to insert in pt1: " + newOutputStack);
                    inventory.insertItem(1, newOutputStack.copy(), false); // CRASH the game if this is not empty!

                } else { // Assuming the recipe output item is already in the output slot
                    // Simply change the item to equal the output amount
                    furnaceOutput.setCount(Objects.requireNonNullElse(furnaceRecipe, blastingRecipe).getResultItem(level.registryAccess()).getCount());
                    //LOGGER.debug("About to insert in pt2: " + furnaceOutput);
                    inventory.insertItem(1, furnaceOutput.copy(), false); // Place the new output item on top of the old one
                }

                consumeEnergy();
                counter--;
                this.setChanged();
            } else if (counter > 0) {
                consumeEnergy();
                counter--;
                if (++sound_tick == 19) {
                    sound_tick = 0;
                    if (Config.PLAY_MACHINE_SOUNDS.get()) {
                        level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            } else {
                counter = this.calculateCounter(200, inventory.getStackInSlot(this.getUpgradeSlotId()));
                length = counter;
                this.referenceStack.set(furnaceInput.copy());
            }

        } else counter = 0;
    }

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        this.isRecipeDirty = false;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                markRecipeDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0) {
                    return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level).orElse(null) != null
                            || level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(stack), level).orElse(null) != null;
                } else if (slot == 1) {
                    var furnaceRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(inputItemStack.get()), level).orElse(null);
                    var blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(inputItemStack.get()), level).orElse(null);

                    // If both recipes are null, then don't bother
                    if (blastingRecipe == null && furnaceRecipe == null) return false;

                    return stack.getItem() == Objects.requireNonNullElse(furnaceRecipe, blastingRecipe).value().getResultItem(level.registryAccess()).getItem();

                } else if (slot == 2) {
                    return TagUtil.isTaggedMachineUpgradeItem(stack);
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                if (slot == 0) {
                    ItemStack referenceStack = stack.copy();
                    referenceStack.setCount(64);
                    SmeltingRecipe recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(referenceStack), level).orElse(null).value();
                    BlastingRecipe blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(referenceStack), level).orElse(null).value();

                    if (recipe != null || blastingRecipe != null) {
                        return super.insertItem(slot, stack, simulate);
                    }

                } else if (slot == 1) {
                    return super.insertItem(slot, stack, simulate);
                } else if (slot == 2 && TagUtil.isTaggedMachineUpgradeItem(stack)) {
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
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
        return new ElectricFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
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

    public int getCounter() {
        return counter;
    }

    @Override
    public int getMaxPower() {
        return Config.ELECTRIC_FURNACE_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.ELECTRIC_FURNACE_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.ELECTRIC_FURNACE_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 2;
    }
}