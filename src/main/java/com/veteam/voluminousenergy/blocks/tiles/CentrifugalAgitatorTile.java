package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalAgitatorContainer;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
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
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CentrifugalAgitatorTile extends VEFluidTileEntity {

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager input0sm = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager input1sm = new VESlotManager(1, Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager output0sm = new VESlotManager(2, Direction.NORTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager output1sm = new VESlotManager(3, Direction.SOUTH, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(input0sm);
        add(input1sm);
        add(output0sm);
        add(output1sm);
    }};

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);
    RelationalTank outputTank0 = new RelationalTank(new FluidTank(TANK_CAPACITY),1,null,null, TankType.OUTPUT,0);
    RelationalTank outputTank1 = new RelationalTank(new FluidTank(TANK_CAPACITY),2,null,null, TankType.OUTPUT,1);

    List<RelationalTank> fluidManagers = new ArrayList<>() {{
       add(inputTank);
       add(outputTank0);
       add(outputTank1);
    }};

    private int counter;
    private int length;

    public CentrifugalAgitatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CENTRIFUGAL_AGITATOR_TILE, pos, state);
    }

    public ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack input1 = inventory.getStackInSlot(1).copy();
        ItemStack output0 = inventory.getStackInSlot(2).copy();
        ItemStack output1 = inventory.getStackInSlot(3).copy();

        inputTank.setInput(input.copy());
        inputTank.setOutput(input1.copy());

        outputTank0.setOutput(output0);
        outputTank1.setOutput(output1);

        if(this.inputFluid(inputTank,0,1)) return;
        if(this.outputFluid(inputTank,0,1)) return;
        if(this.outputFluidStatic(outputTank0,2)) return;
        if(this.outputFluidStatic(outputTank1,3)) return;
        // Main Fluid Processing occurs here
        if (inputTank != null) {
            //ItemStack inputFluidStack = new ItemStack(inputTank.getTank().getFluid().getRawFluid().getFilledBucket(), 1);
            //lVEFluidRecipe recipe = world.getRecipeManager().getRecipe(CentrifugalAgitatorRecipe.RECIPE_TYPE, new Inventory(inputFluidStack), world).orElse(null);
            VEFluidRecipe recipe = RecipeUtil.getCentrifugalAgitatorRecipe(level,inputTank.getTank().getFluid().copy());
            if (recipe != null) {
                if (outputTank0 != null && outputTank1 != null) {

                    // Tank fluid amount check + tank cap checks
                    if (inputTank.getTank().getFluidAmount() >= recipe.getInputAmount()
                            && outputTank0.getTank().getFluidAmount() + recipe.getOutputAmount() <= TANK_CAPACITY
                            && outputTank1.getTank().getFluidAmount() + recipe.getFluids().get(1).getAmount() <= TANK_CAPACITY) {
                        // Check for power
                        if (canConsumeEnergy()) {
                            if (counter == 1) {

                                // Drain Input
                                inputTank.getTank().drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                // First Output Tank
                                if (outputTank0.getTank().getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()) {
                                    outputTank0.getTank().setFluid(recipe.getOutputFluid().copy());
                                } else {
                                    outputTank0.getTank().fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                // Second Output Tank
                                CentrifugalAgitatorRecipe centrifugalAgitatorRecipe = (CentrifugalAgitatorRecipe) recipe;
                                if (outputTank1.getTank().getFluid().getRawFluid() != centrifugalAgitatorRecipe.getSecondFluid().getRawFluid()) {
                                    outputTank1.getTank().setFluid(centrifugalAgitatorRecipe.getSecondFluid().copy());
                                } else {
                                    outputTank1.getTank().fill(centrifugalAgitatorRecipe.getSecondResult().copy(), IFluidHandler.FluidAction.EXECUTE);
                                }

                                counter--;
                                consumeEnergy();
                                this.setChanged();
                            } else if (counter > 0) {
                                counter--;
                                consumeEnergy();
                            } else {
                                counter = this.calculateCounter(recipe.getProcessTime(),inventory.getStackInSlot(4));
                                length = counter;
                            }
                        } // Energy Check
                    } else { // If fluid tank empty set counter to zero
                        counter = 0;
                    }
                }
            }
        }
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(4).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get(), this.inventory.getStackInSlot(4).copy());
    }

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag) {
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));
        counter = tag.getInt("counter");
        length = tag.getInt("length");

        input0sm.read(tag,"input_0_sm");
        input1sm.read(tag, "input_1_sm");
        output0sm.read(tag, "output_0_sm");
        output1sm.read(tag, "output_1_sm");

        // Tanks
        CompoundTag inputTank = tag.getCompound("inputTank");
        CompoundTag outputTank0 = tag.getCompound("outputTank0");
        CompoundTag outputTank1 = tag.getCompound("outputTank1");

        this.inputTank.getTank().readFromNBT(inputTank);
        this.outputTank0.getTank().readFromNBT(outputTank0);
        this.outputTank1.getTank().readFromNBT(outputTank1);

        this.inputTank.readGuiProperties(tag,"input_tank_gui");
        this.outputTank0.readGuiProperties(tag, "output_tank_0_gui");
        this.outputTank1.readGuiProperties(tag, "output_tank_1_gui");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        handler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> h.serializeNBT(tag));
        tag.putInt("counter", counter);
        tag.putInt("length", length);

        input0sm.write(tag,"input_0_sm");
        input1sm.write(tag, "input_1_sm");
        output0sm.write(tag, "output_0_sm");
        output1sm.write(tag, "output_1_sm");

        // Tanks
        CompoundTag inputNBT = new CompoundTag();
        CompoundTag outputNBT0 = new CompoundTag();
        CompoundTag outputNBT1 = new CompoundTag();

        this.inputTank.getTank().writeToNBT(inputNBT);
        this.outputTank0.getTank().writeToNBT(outputNBT0);
        this.outputTank1.getTank().writeToNBT(outputNBT1);

        tag.put("inputTank", inputNBT);
        tag.put("outputTank0", outputNBT0);
        tag.put("outputTank1", outputNBT1);

        this.inputTank.writeGuiProperties(tag, "input_tank_gui");
        this.outputTank0.writeGuiProperties(tag, "output_tank_0_gui");
        this.outputTank1.writeGuiProperties(tag, "output_tank_1_gui");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createInputTankFluidHandler() {
        return createFluidHandler(new CentrifugalAgitatorRecipe(), inputTank);
    }

    private IFluidHandler createOutputTank0FluidHandler(){
        return createFluidHandler(new CentrifugalAgitatorRecipe(), outputTank0);
    }

    private IFluidHandler createOutputTank1FluidHandler(){
        return createFluidHandler(new CentrifugalAgitatorRecipe(), outputTank1);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
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

    private @NotNull VEEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.CENTRIFUGAL_AGITATOR_MAX_POWER.get(), Config.CENTRIFUGAL_AGITATOR_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap,side,handler,inventory,slotManagers,fluidManagers,energy);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CentrifugalAgitatorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return inputTank.getTank().getFluid();
        } else if (num == 1){
            return outputTank0.getTank().getFluid();
        } else if (num == 2){
            return outputTank1.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    public RelationalTank getInputTank(){
        return this.inputTank;
    }

    public RelationalTank getOutputTank0(){
        return this.outputTank0;
    }

    public RelationalTank getOutputTank1(){
        return this.outputTank1;
    }

    @Override
public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status,slotId,input0sm,input1sm,output0sm,output1sm);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction,slotId,input0sm,input1sm,output0sm,output1sm);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        processGUIPacketFluidStatus(status,id,inputTank,outputTank0,outputTank1);
    }

    public void updateTankPacketFromGui(int direction, int id){
        processGUIPacketFluidDirection(direction,id,inputTank,outputTank0,outputTank1);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){

                        bulkSendSMPacket(s, input0sm,input1sm,output0sm,output1sm);

                        bulkSendTankPackets(s,inputTank,outputTank0,outputTank1);
                    }
                });
            });
        }
    }

}