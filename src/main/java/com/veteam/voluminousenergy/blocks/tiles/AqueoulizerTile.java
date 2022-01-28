package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AqueoulizerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    // Slot Managers
    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"input_0_sm");
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.INPUT,"input_1_sm");
    public VESlotManager output0sm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.input_slot",SlotType.OUTPUT,"output_0_sm");
    // Actually an input slot omegalul
    public VESlotManager output1sm = new VESlotManager(3, Direction.SOUTH,true,"slot.voluminousenergy.input_slot",SlotType.INPUT,"output_1_sm");

    private final ItemStackHandler inventory = createHandler();

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(input0sm);
            add(input1sm);
            add(output0sm);
            add(output1sm);
        }

    };

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT,"inputTank:input_tank_gui");
    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),1,null,null, TankType.OUTPUT,0,"outputTank:output_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(inputTank);
            add(outputTank);
        }
    };

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public AqueoulizerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AQUEOULIZER_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy(); // Input insert
        ItemStack input1 = inventory.getStackInSlot(1).copy(); // Input extract
        ItemStack output0 = inventory.getStackInSlot(2).copy(); // Output extract
        ItemStack inputItem = inventory.getStackInSlot(3).copy(); // Repurpose to Item input

        inputTank.setInput(input.copy());
        inputTank.setOutput(input1.copy());

        outputTank.setOutput(output0);

        if(this.inputFluid(inputTank,0,1)) return;
        if(this.outputFluid(inputTank,0,1)) return;
        if(this.outputFluidStatic(outputTank,2)) return;

        // Main Fluid Processing occurs here:
        VEFluidRecipe recipe = RecipeUtil.getAqueoulizerRecipe(level, this.inputTank.getTank().getFluid(),inputItem.copy());
        // Manually find the recipe since we have 2 conditions rather than the 1 input the vanilla getRecipe supports

        if (inputTank != null && !inputTank.getTank().isEmpty() && recipe != null) {
            //ItemStack inputFluidStack = new ItemStack(inputTank.getTank().getFluid().getRawFluid().getFilledBucket(),1);

            if (recipe.getRawFluids().contains(inputTank.getTank().getFluid().getRawFluid())) {
                if (outputTank != null) {

                    // Tank fluid amount check + tank cap checks
                    if (inputTank.getTank().getFluidAmount() >= recipe.getInputAmount() && outputTank.getTank().getFluidAmount() + recipe.getOutputAmount() <= TANK_CAPACITY){
                        // Check for power
                        if (canConsumeEnergy()){
                            if (counter == 1){

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // Output Tank
                                if (outputTank.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                    outputTank.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                inventory.extractItem(3, recipe.ingredientCount,false);

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0){
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(4).copy());
                                length = counter;
                            }
                        } // Energy Check
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                } else counter = 0;
            } else counter = 0;
        } else {
            counter = 0;
        }
        //LOGGER.debug("Fluid: " + inputTank.getFluid().getRawFluid().getFilledBucket().getTranslationKey() + " amount: " + inputTank.getFluid().getAmount());
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    private IFluidHandler createInputFluidHandler() {
        return this.createFluidHandler(new AqueoulizerRecipe(), inputTank);
    }

    private IFluidHandler createOutputFluidHandler(){
        return this.createFluidHandler(new AqueoulizerRecipe(), outputTank);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1) {
                    if (!(stack.getItem() instanceof BucketItem)) return false;
                }
                if(slot == 3) {
                    // NOTE: If enough people complain that they have to insert a proper fluid first. Then change this so that it only checks item
                    VEFluidRecipe recipe = RecipeUtil.getAqueoulizerRecipe(level,inputTank.getTank().getFluid(),stack);
                    return recipe != null;
                }

                if (slot == 4) return stack.getItem().equals(VEItems.QUARTZ_MULTIPLIER); // this is the upgrade slot
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new AqueoulizerContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return inputTank.getTank().getFluid();
        } else if (num == 1){
            return outputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank(){
        return this.inputTank;
    }

    public RelationalTank getOutputTank(){
        return this.outputTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return this.fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.AIR_COMPRESSOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.AIR_COMPRESSOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.AIR_COMPRESSOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 3;
    }
}
