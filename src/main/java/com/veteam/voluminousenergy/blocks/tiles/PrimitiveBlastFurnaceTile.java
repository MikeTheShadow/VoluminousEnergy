package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class PrimitiveBlastFurnaceTile extends VoluminousTileEntity {

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);

    public VESlotManager inputSm = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager outputSm = new VESlotManager(1, Direction.DOWN,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(inputSm);
       add(outputSm);
    }};

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public PrimitiveBlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE, pos, state);
    }

    @Deprecated
    public PrimitiveBlastFurnaceTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE, pos, state);
    }

    private ItemStackHandler inventory = createHandler();

    @Override
    public void tick() { //Tick method to run every tick
        updateClients();
        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();

        PrimitiveBlastFurnaceRecipe recipe = level.getRecipeManager().getRecipeFor(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty()){
            if (output.getCount() + recipe.getOutputAmount() < 64) {
                if (this.counter == 1){ //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.ingredientCount,false);

                    // Get output stack and RNG stack from the recipe
                    ItemStack newOutputStack = recipe.getResult().copy();

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem()) {
                        if (output.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                            output.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputAmount());
                        inventory.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                    }
                    this.counter--;
                    setChanged();
                } else if (this.counter > 0){ //In progress
                    this.counter--;
                } else { // Check if we should start processing
                    if (output.isEmpty() || output.getItem() == recipe.getResult().getItem()){
                        this.counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(2).copy());
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

    @Override
    public void load(CompoundTag tag) {
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        counter = tag.getInt("counter");
        length = tag.getInt("length");

        this.inputSm.read(tag, "input_gui");
        this.outputSm.read(tag,"output_gui");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        handler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>)h).serializeNBT();
            tag.put("inv",compound);
        });
        tag.putInt("counter", counter);
        tag.putInt("length", length);

        this.inputSm.write(tag, "input_gui");
        this.outputSm.write(tag,"output_gui");
    }


    /*
        Sync on block update
     */

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
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                PrimitiveBlastFurnaceRecipe recipeOutput = level.getRecipeManager().getRecipeFor(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()), level).orElse(null);
                PrimitiveBlastFurnaceRecipe recipe = level.getRecipeManager().getRecipeFor(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack),level).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.test(stack);
                } else if (slot == 1 && recipeOutput != null){
                    return stack.getItem() == recipeOutput.result.getItem();
                } else if (slot == 2){
                    return stack.getItem() == VEItems.QUARTZ_MULTIPLIER;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                PrimitiveBlastFurnaceRecipe recipeOut = level.getRecipeManager().getRecipeFor(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()), level).orElse(null);
                PrimitiveBlastFurnaceRecipe recipe = level.getRecipeManager().getRecipeFor(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack),level).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getItems()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if (slot == 1 && recipeOut != null){
                    if (stack.getItem() == recipeOut.result.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                } else if (slot == 2 && stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                    return super.insertItem(slot,stack,simulate);
                }
                return stack;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap,side,handler,inventory,slotManagers,null,null);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new PrimitiveBlastFurnaceContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    public int getCounter(){ return counter; }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status, slotId, inputSm, outputSm);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction, slotId, inputSm, outputSm);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        bulkSendSMPacket(s, inputSm, outputSm);
                    }
                });
            });
        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || level == null) return;
        if(level.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            level.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.containerMenu != null){
                    if(!(player.containerMenu instanceof PrimitiveBlastFurnaceContainer)){
                        toRemove.add(player.getUUID());
                    }
                } else if (player.containerMenu == null){
                    toRemove.add(player.getUUID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}