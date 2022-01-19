package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.VoluminousEnergy.LOGGER;

public class TankTile extends VEFluidTileEntity{ // TODO: 2 items slots, 1 tank

    private int capacity;
    private RelationalTank tank;
    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(this::createInputFluidHandler);

    // ItemHandlers
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> bucketTop = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 1));
    private LazyOptional<IItemHandlerModifiable> bucketBottom = LazyOptional.of(() -> new RangedWrapper(this.inventory, 1, 2));

    public VESlotManager bucketTopSlotManager = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager bucketBottomSlotManager = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    private ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public TankTile(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);
        this.capacity = capacity;
        tank = new RelationalTank(new FluidTank(this.capacity),0,null,null, TankType.OUTPUT);
    }

    public TankTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack bucketTop = inventory.getStackInSlot(0).copy(); // Bucket Top slot
        ItemStack bucketBottom = inventory.getStackInSlot(1).copy(); // Bucket Bottom slot

        tank.setInput(bucketTop.copy());
        tank.setOutput(bucketBottom.copy());

        if(this.inputFluid(tank,0,1)) return;
        if(this.outputFluid(tank,0,1)) return;

    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (stack.getItem() instanceof BucketItem) return true;
                return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IFluidHandler createInputFluidHandler() {
        return this.createFluidHandler(tank);
    }

    public IFluidHandler createFluidHandler(RelationalTank... relationalTanks) { // Derived from the Super Class

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
                LOGGER.warn("Invalid tankId in TankTile for getFluidInTank, id: " + tank + ", max id: " + (relationalTanks.length - 1));
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {

                for(RelationalTank t : relationalTanks) {
                    if(t.getId() == tank) {
                        return t.getTank() == null ? 0 : t.getTank().getCapacity();
                    }
                }
                LOGGER.warn("Invalid tankId in TankTile for getTankCapacity, id: " + tank + ", max id: " + (relationalTanks.length - 1));
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                try {
                    for (RelationalTank t : relationalTanks){
                        return t.getTank() != null && t.getTank().isFluidValid(stack);
                    }
                } catch (Exception e){
                    LOGGER.warn("ERROR with isFluidValid in TankTile");
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) return handler.cast();
            else if (bucketTopSlotManager.getStatus() && bucketTopSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return bucketTop.cast();
            else if (bucketBottomSlotManager.getStatus() && bucketBottomSlotManager.getDirection().get3DDataValue() == side.get3DDataValue())
                return bucketBottom.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null){ // TODO: Better Null direction handling
            if(tank.getSideStatus() && tank.getSideDirection().get3DDataValue() == side.get3DDataValue())
                return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public RelationalTank getTank(){
        return this.tank;
    }


    @Override
    public Component getDisplayName() {return new TextComponent(getType().getRegistryName().getPath());}

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return null; // TODO: Container Menu
    }

}
