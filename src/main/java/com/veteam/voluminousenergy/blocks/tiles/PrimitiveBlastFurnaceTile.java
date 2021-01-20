package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class PrimitiveBlastFurnaceTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> inputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,0,1));
    private LazyOptional<IItemHandlerModifiable> outputHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory,1,2));

    public VESlotManager inputSm = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot");
    public VESlotManager outputSm = new VESlotManager(1, Direction.DOWN,true,"slot.voluminousenergy.output_slot");

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private static final Logger LOGGER = LogManager.getLogger();

    public PrimitiveBlastFurnaceTile() {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE);
    }

    private final ItemStackHandler inventory = createHandler();

    @Override
    public void tick() { //Tick method to run every tick
        updateClients();
        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();

        PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(input), world).orElse(null);
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
                    markDirty();
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
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);

        this.inputSm.read(tag, "input_gui");
        this.outputSm.read(tag,"output_gui");

        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv",compound);
        });

        this.inputSm.write(tag, "input_gui");
        this.outputSm.write(tag,"output_gui");

        return super.write(tag);
    }


    /*
        Sync on block update
     */

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
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                PrimitiveBlastFurnaceRecipe recipeOutput = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()), world).orElse(null);
                PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(referenceStack),world).orElse(null);

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
                PrimitiveBlastFurnaceRecipe recipeOut = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()), world).orElse(null);
                PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(referenceStack),world).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
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
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null)
                return handler.cast();
            if (inputSm.getStatus() && inputSm.getDirection().getIndex() == side.getIndex())
                return inputHandler.cast();
            else if (outputSm.getStatus() && outputSm.getDirection().getIndex() == side.getIndex())
                return outputHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("voluminousenergy:primitiveblastfurnace");
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new PrimitiveBlastFurnaceContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int counter(){
        return counter;
    }

    public int progressCounter(){
        return 100-((counter*100)/length);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
        }
    }

    public int getCounter(){return counter;}

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == inputSm.getSlotNum()){
            inputSm.setStatus(status);
        } else if (slotId == outputSm.getSlotNum()){
            outputSm.setStatus(status);
        }
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == inputSm.getSlotNum()){
            inputSm.setDirection(direction);
        } else if (slotId == outputSm.getSlotNum()){
            outputSm.setDirection(direction);
        }
    }

    @Override
    public void sendPacketToClient(){
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(inputSm.getStatus(), inputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(outputSm.getStatus(), outputSm.getSlotNum()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(inputSm.getDirection().getIndex(),inputSm.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(outputSm.getDirection().getIndex(),outputSm.getSlotNum()));
                    }
                });
            });
        } else if (!playerUuid.isEmpty()){ // Legacy solution
            double x = this.getPos().getX();
            double y = this.getPos().getY();
            double z = this.getPos().getZ();
            final double radius = 16;
            RegistryKey<World> worldRegistryKey = this.getWorld().getDimensionKey();
            PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x,y,z,radius,worldRegistryKey);

            // Boolean Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(inputSm.getStatus(), inputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(outputSm.getStatus(), outputSm.getSlotNum()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(inputSm.getDirection().getIndex(),inputSm.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(outputSm.getDirection().getIndex(),outputSm.getSlotNum()));
        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || world == null) return;
        if(world.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            world.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.openContainer != null){
                    if(!(player.openContainer instanceof PrimitiveBlastFurnaceContainer)){
                        toRemove.add(player.getUniqueID());
                    }
                } else if (player.openContainer == null){
                    toRemove.add(player.getUniqueID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}