package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ImplosionCompressorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.ImplosionCompressorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ImplosionCompressorTile extends VoluminousTileEntity {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager inputSlotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager gunpowderSlotManager = new VESlotManager(1, Direction.EAST, true, "slot.voluminousenergy.input_slot",SlotType.INPUT);
    public VESlotManager outputSlotManager = new VESlotManager(2, Direction.DOWN, true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(inputSlotManager);
       add(gunpowderSlotManager);
       add(outputSlotManager);
    }};

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public ImplosionCompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.IMPLOSION_COMPRESSOR_TILE, pos, state);
    }

    @Deprecated
    public ImplosionCompressorTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.IMPLOSION_COMPRESSOR_TILE, pos, state);
    }

    private ItemStackHandler inventory = createHandler();

    @Override
    public void tick(){
        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack gunpowderInput = inventory.getStackInSlot(1).copy();
        ItemStack output = inventory.getStackInSlot(2).copy();

        ImplosionCompressorRecipe recipe = level.getRecipeManager().getRecipeFor(ImplosionCompressorRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty() && !gunpowderInput.isEmpty()){
            if (output.getCount() + recipe.getOutputAmount() < 64 && canConsumeEnergy()) {
                if (this.counter == 1) { //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.ingredientCount,false);

                    // Extract the gunpowder
                    inventory.extractItem(1, 1, false);

                    // Get output stack and RNG stack from the recipe
                    ItemStack newOutputStack = recipe.getResult().copy();

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem()) {
                        if (output.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                            output.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputAmount());
                        inventory.insertItem(2,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(2,output.copy(),false); // Place the new output stack on top of the old one
                    }

                    this.counter--;
                    consumeEnergy();
                    setChanged();
                } else if (this.counter > 0){ //In progress
                    this.counter--;
                    consumeEnergy();
                } else { // Check if we should start processing
                    if (output.isEmpty() || output.getItem() == recipe.getResult().getItem()){
                        this.counter = recipe.getProcessTime();
                        this.counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(3));
                        this.length = this.counter;
                    } else {
                        this.counter = 0;
                    }
                }
            } else { // This is if we reach the maximum in the slots
                this.counter = 0;
            }
        } else { // This is if the input slot is empty
            this.counter = 0;
        }
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.COMPRESSOR_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(2).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.COMPRESSOR_POWER_USAGE.get(), this.inventory.getStackInSlot(2).copy());
    }

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));
        counter = tag.getInt("counter");
        length = tag.getInt("length");

        inputSlotManager.read(tag, "input_manager");
        gunpowderSlotManager.read(tag, "gunpowder_manager");
        outputSlotManager.read(tag, "output_manager");

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

        inputSlotManager.write(tag, "input_manager");
        gunpowderSlotManager.write(tag, "gunpowder_manager");
        outputSlotManager.write(tag, "output_manager");
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
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt){
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                ImplosionCompressorRecipe recipe = level.getRecipeManager().getRecipeFor(ImplosionCompressorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
                ImplosionCompressorRecipe recipe1 = level.getRecipeManager().getRecipeFor(ImplosionCompressorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.test(stack);
                } else if (slot == 1) {
                    return stack.getItem() == Items.GUNPOWDER;
                } else if (slot == 2 && recipe1 != null){
                    return stack.getItem() == recipe1.result.getItem();
                } else if (slot == 3){
                    return stack.getItem() == VEItems.QUARTZ_MULTIPLIER;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                ImplosionCompressorRecipe recipe = level.getRecipeManager().getRecipeFor(ImplosionCompressorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
                ImplosionCompressorRecipe recipe1 = level.getRecipeManager().getRecipeFor(ImplosionCompressorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getItems()) {
                        if (stack.getItem() == testStack.getItem()) {
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if (slot == 1 && stack.getItem() == Items.GUNPOWDER){
                    return super.insertItem(slot, stack, simulate);
                } else if (slot == 2 && recipe1 != null){
                    if (stack.getItem() == recipe1.result.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                } else if (slot == 3 && stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
        };
    }

    private @NotNull VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.IMPLOSION_COMPRESSOR_MAX_POWER.get(), Config.IMPLOSION_COMPRESSOR_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap,side,handler,inventory,slotManagers,null,energy);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity)
    {
        return new ImplosionCompressorContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status, slotId, inputSlotManager, gunpowderSlotManager, outputSlotManager);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction, slotId, inputSlotManager, gunpowderSlotManager, outputSlotManager);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        bulkSendSMPacket(s, inputSlotManager, gunpowderSlotManager, outputSlotManager);
                    }
                });
            });
        }
    }
}
