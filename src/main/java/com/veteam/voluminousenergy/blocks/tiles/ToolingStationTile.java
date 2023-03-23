package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ToolingStationContainer;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitools;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.ToolingRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ToolingStationTile extends VEFluidTileEntity implements IVEPoweredTileEntity {

    // Slot Managers
    public VESlotManager fuelTopSlotSM = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT,"fuel_top_slot");
    public VESlotManager fuelBottomSlotSM = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"fuel_bottom_slot");
    public VESlotManager mainToolSlotSM = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT,"main_tool_slot");
    public VESlotManager bitSlotSM = new VESlotManager(3, Direction.SOUTH,true,"slot.voluminousenergy.input_slot",SlotType.INPUT,"bit_slot");
    public VESlotManager multitoolBaseSM = new VESlotManager(4, Direction.EAST, true, "slot.voluminousenergy.input_slot",SlotType.INPUT,"multitool_base_slot");

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(fuelTopSlotSM);
        add(fuelBottomSlotSM);
        add(mainToolSlotSM);
        add(bitSlotSM);
        add(multitoolBaseSM);
    }};

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT,"fuel_tank:fuel_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
       add(fuelTank);
    }};

    private final ItemStackHandler inventory = createHandler();

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public ToolingStationTile(BlockPos pos, BlockState state) {
        super(VEBlocks.TOOLING_STATION_TILE.get(), pos, state);
        fuelTank.setValidFluids(RecipeUtil.getCombustibleFuelsWithoutLevel());
    }

    @Override
    public void tick() {
        updateClients();

        ItemStack fuelInput = inventory.getStackInSlot(0).copy(); // Fuel bucket insert
        ItemStack fuelOutput = inventory.getStackInSlot(1).copy(); // Fuel bucket extract
        ItemStack mainTool = inventory.getStackInSlot(2); // This will act like a POINTER, not a clone
        ItemStack toolBit = inventory.getStackInSlot(3).copy(); // this is where the bit would be put into
        ItemStack toolBase = inventory.getStackInSlot(4).copy(); // this is where the base of the tool would be put into

        fuelTank.setInput(fuelInput.copy());
        fuelTank.setOutput(fuelOutput.copy());

        if(this.inputFluid(fuelTank,0,1)) return;
        if(this.outputFluid(fuelTank,0,1)) return;

        VEFluidRecipe fuelRecipe = RecipeUtil.getFuelCombustionRecipe(this.level,this.fuelTank.getTank().getFluid());

        if(fuelRecipe != null){
            // Logic for refueling the base
            if (!mainTool.isEmpty()){
                mainTool.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluid -> {
                    FluidStack itemFluid = fluid.getFluidInTank(0);
                    FluidStack toolingStationFluid = this.fuelTank.getTank().getFluid().copy();
                    int tankCapacity = fluid.getTankCapacity(0);

                    if(itemFluid.getAmount() < tankCapacity && (itemFluid.isFluidEqual(toolingStationFluid) || itemFluid.isEmpty())){
                        int toTransfer;

                        if (!itemFluid.isEmpty()){
                            toTransfer = Math.min(toolingStationFluid.getAmount(), itemFluid.getAmount()); // Which amount is smaller
                            toTransfer = Math.min(toTransfer, (tankCapacity - itemFluid.getAmount())); // Previous value versus the delta between the tankCapacity in the item and the current fluid amount
                        } else { // Clean slate, check only against the tank capacity
                            toTransfer = Math.min(toolingStationFluid.getAmount(), tankCapacity);
                        }

                        if (toTransfer > 0){
                            // Drain the fluid from the Tooling Station
                            this.fuelTank.getTank().drain(toTransfer, IFluidHandler.FluidAction.EXECUTE);
                            toolingStationFluid.setAmount(toTransfer); // Set the fluid that is going to go into the item
                            // Fill the item
                            fluid.fill(toolingStationFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                            // Fill the fluid in the base as well
                            inventory.getStackInSlot(4).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                                    .ifPresent(baseFluid -> baseFluid.fill(toolingStationFluid, IFluidHandler.FluidAction.EXECUTE));
                        }
                    }
                });
            }
        }

        if(mainTool.isEmpty() && inventory.getStackInSlot(2).isEmpty()){
            if(!toolBit.isEmpty() && !toolBase.isEmpty()){
                ToolingRecipe toolingRecipe = RecipeUtil.getToolingRecipeFromBitAndBase(level, toolBit.copy(), toolBase.copy());
                if (toolingRecipe != null){
                    ItemStack craftedTool = new ItemStack(toolingRecipe.result.getItem(),1);

                    // Fill the crafted Multitool with fluid from the emptyMultitool
                    craftedTool.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(
                            fluidTool -> toolBase.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluidBase ->{
                                FluidStack baseFluid = fluidBase.getFluidInTank(0).copy();
                                fluidTool.fill(baseFluid, IFluidHandler.FluidAction.EXECUTE);
                            }));

                    inventory.setStackInSlot(2, craftedTool);
                }
            }
        } else if (!mainTool.isEmpty() && toolBase.isEmpty() && toolBit.isEmpty()){
            ToolingRecipe toolingRecipe = RecipeUtil.getToolingRecipeFromResult(level, mainTool.copy());
            if (toolingRecipe != null){
                inventory.setStackInSlot(3, new ItemStack(toolingRecipe.getBits().get(0)));
                ItemStack baseStack = new ItemStack(toolingRecipe.getBases().get(0));

                // Fill the base with the same fluid as the mainTool
                baseStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(baseFluid ->
                    mainTool.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(toolFluid -> {
                        FluidStack fluidTool = toolFluid.getFluidInTank(0).copy();
                        baseFluid.fill(fluidTool, IFluidHandler.FluidAction.EXECUTE);
                }));

                inventory.setStackInSlot(4, baseStack);
                inventory.setStackInSlot(2, mainTool.copy());
            }
        }


    }

    private IFluidHandler createFuelFluidHandler() {
        return this.createFluidHandler(new CombustionGeneratorFuelRecipe(), fuelTank);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                if(slot == 2 && this.getStackInSlot(2).isEmpty()){ // If the crafted multitool is removed, delete the components
                    if (this.getStackInSlot(3).isEmpty() || this.getStackInSlot(4).isEmpty()){

                    } else {
                        this.setStackInSlot(3, ItemStack.EMPTY);
                        this.setStackInSlot(4, ItemStack.EMPTY);
                    }
                } else if ((slot == 3 || slot == 4) && (!this.getStackInSlot(2).isEmpty())){
                    if(this.getStackInSlot(3).isEmpty() || this.getStackInSlot(4).isEmpty()){ // If one of the components of the multitool is removed, delete the multitool
                        this.setStackInSlot(2, ItemStack.EMPTY);
                    }
                }

                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot < 2) return stack.getItem() instanceof BucketItem;
                if (slot == 2) return stack.getItem() instanceof Multitool && stack.getItem() != VEMultitools.EMPTY_MULTITOOL.get(); // TODO: Remove Multitool base?
                if (slot == 3) return stack.getItem() instanceof BitItem;
                if (slot == 4) return (stack.getItem() == VEMultitools.EMPTY_MULTITOOL.get()); // TODO: Remove Multitool base?
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                if (slot == 2 && (!this.getStackInSlot(3).isEmpty() || !this.getStackInSlot(4).isEmpty()) ) { // main Multitool slot
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new ToolingStationContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public boolean hasValidRecipe(){
        return (inventory.getStackInSlot(2) != ItemStack.EMPTY)
                && (inventory.getStackInSlot(3) != ItemStack.EMPTY)
                && (inventory.getStackInSlot(4) != ItemStack.EMPTY);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank(){
        return this.fuelTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.TOOLING_STATION_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() { // Tooling Station atm doesn't use power. Transfer to recharge electric tools (if support added) should be capped by this#getTransferRate();
        return 0;
    }

    @Override
    public int getTransferRate() {
        return Config.TOOLING_STATION_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}