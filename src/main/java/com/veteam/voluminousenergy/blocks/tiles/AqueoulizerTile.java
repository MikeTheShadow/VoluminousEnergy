package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AqueoulizerTile extends VEFluidTileEntity {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);
    RelationalTank outputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),1,null,null, TankType.OUTPUT,0);

    private int counter;
    private int length;

    private static final Logger LOGGER = LogManager.getLogger();

    private final ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public AqueoulizerTile() {
        super(VEBlocks.AQUEOULIZER_TILE);
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
        VEFluidRecipe recipe = world.getRecipeManager().getRecipe(AqueoulizerRecipe.RECIPE_TYPE, new Inventory(inputItem.copy()),world).orElse(null);

        if (inputTank != null && !inputTank.getTank().isEmpty() && recipe != null) {
            ItemStack inputFluidStack = new ItemStack(inputTank.getTank().getFluid().getRawFluid().getFilledBucket(),1);

            if (inputTank.getTank().getFluid().isFluidEqual(recipe.getFluids().get(0))) {
                if (outputTank != null) {

                    // Tank fluid amount check + tank cap checks
                    if (inputTank.getTank().getFluidAmount() >= recipe.getInputAmount() && outputTank.getTank().getFluidAmount() + recipe.getOutputAmount() <= TANK_CAPACITY){
                        // Check for power
                        if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                            if (counter == 1){

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // Output Tank
                                if (outputTank.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                    outputTank.getTank().setFluid(recipe.getOutputFluid());
                                } else {
                                    outputTank.getTank().fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                inventory.extractItem(3, recipe.ingredientCount,false);

                                counter--;
                                energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.AQUEOULIZER_POWER_USAGE.get()));
                                this.markDirty();
                            } else if (counter > 0){
                                counter--;
                                energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.AQUEOULIZER_POWER_USAGE.get()));
                            } else {
                                counter = recipe.getProcessTime();
                                length = counter;
                            }
                        } // Energy Check
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                }
            }
        }
        //LOGGER.debug("Fluid: " + inputTank.getFluid().getRawFluid().getFilledBucket().getTranslationKey() + " amount: " + inputTank.getFluid().getAmount());
    }

    /*
        Read and Write on World save
     */

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        // Tanks
        fluid.ifPresent(f -> {
            CompoundNBT inputTank = tag.getCompound("inputTank");
            CompoundNBT outputTank = tag.getCompound("outputTank");

            this.inputTank.getTank().readFromNBT(inputTank);
            this.outputTank.getTank().readFromNBT(outputTank);
        });

        super.read(state,tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("energy", compound);
        });

        // Tanks
        fluid.ifPresent(f -> {
            CompoundNBT inputNBT = new CompoundNBT();
            CompoundNBT outputNBT = new CompoundNBT();

            this.inputTank.getTank().writeToNBT(inputNBT);
            this.outputTank.getTank().writeToNBT(outputNBT);

            tag.put("inputTank", inputNBT);
            tag.put("outputTank", outputNBT);
        });

        return super.write(tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createFluid() {
        return this.createFluidHandler(new AqueoulizerRecipe(), inputTank, outputTank);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.AQUEOULIZER_MAX_POWER.get(), Config.AQUEOULIZER_TRANSFER.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            return fluid.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new AqueoulizerContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (100 - ((counter * 100) / length))) / 100;
        }
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return inputTank.getTank().getFluid();
        } else if (num == 1){
            return outputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }
}
