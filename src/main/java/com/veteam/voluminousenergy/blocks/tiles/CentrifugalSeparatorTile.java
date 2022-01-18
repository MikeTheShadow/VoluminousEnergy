package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalSeparatorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CentrifugalSeparatorRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.Mth.abs;

public class CentrifugalSeparatorTile extends VoluminousTileEntity implements MenuProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory); // Main item handler

    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager inputSm = new VESlotManager(0, Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager bucketSm = new VESlotManager(1,Direction.WEST,true,"slot.voluminousenergy.input_slot",SlotType.INPUT);
    public VESlotManager outputSm = new VESlotManager(2,Direction.DOWN,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager rngOneSm = new VESlotManager(3, Direction.NORTH, true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager rngTwoSm = new VESlotManager(4,Direction.SOUTH,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager rngThreeSm = new VESlotManager(5,Direction.EAST,true,"slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(inputSm);
        add(bucketSm);
        add(outputSm);
        add(rngOneSm);
        add(rngTwoSm);
        add(rngThreeSm);
    }};

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public CentrifugalSeparatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE, pos, state);
    }

    @Deprecated
    public CentrifugalSeparatorTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE, pos, state);
    }

    @Override
    public void tick(){

        updateClients();

        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy();
            ItemStack bucket = h.getStackInSlot(1).copy();
            ItemStack output = h.getStackInSlot(2).copy();
            ItemStack rngOne = h.getStackInSlot(3).copy();
            ItemStack rngTwo = h.getStackInSlot(4).copy();
            ItemStack rngThree = h.getStackInSlot(5).copy();

            CentrifugalSeparatorRecipe recipe = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
            inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

            if (usesBucket(recipe,bucket.copy())){
                if (!areSlotsFull(recipe,output.copy(),rngOne.copy(),rngTwo.copy(),rngThree.copy()) && canConsumeEnergy()) {
                    if (counter == 1){ //The processing is about to be complete
                        // Extract the inputted item
                        h.extractItem(0,recipe.ingredientCount,false);
                        // Extract bucket if it uses a bucket
                        if (recipe.needsBuckets() > 0){
                            h.extractItem(1,recipe.needsBuckets(),false);
                        }

                        // Get output stack from the recipe
                        ItemStack newOutputStack = recipe.getResult().copy();

                        // Manipulating the Output slot
                        if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                            if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                                output.setCount(1);
                            }
                            newOutputStack.setCount(recipe.getOutputAmount());
                            h.insertItem(2,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                        } else { // Assuming the recipe output item is already in the output slot
                            output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                            h.insertItem(2,output.copy(),false); // Place the new output stack on top of the old one
                        }

                        // Manipulating the RNG 0 slot
                        if (recipe.getChance0() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot0().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance0()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngOne.getItem() != recipe.getRngItemSlot0().getItem()){
                                    if (rngOne.getItem() == Items.AIR){
                                        rngOne.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount0());
                                    h.insertItem(3, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngOne.setCount(recipe.getOutputRngAmount0()); // Simply change the stack to equal the output amount
                                    h.insertItem(3,rngOne.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        // Manipulating the RNG 1 slot
                        if (recipe.getChance1() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot1().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance1()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngTwo.getItem() != recipe.getRngItemSlot1().getItem()){
                                    if (rngTwo.getItem() == Items.AIR){
                                        rngTwo.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount1());
                                    h.insertItem(4, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngTwo.setCount(recipe.getOutputRngAmount1()); // Simply change the stack to equal the output amount
                                    h.insertItem(4,rngTwo.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        // Manipulating the RNG 2 slot
                        if (recipe.getChance1() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot2().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance2()){
                                if (rngThree.getItem() != recipe.getRngItemSlot2().getItem()){
                                    if (rngThree.getItem() == Items.AIR){
                                        rngThree.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount2());
                                    h.insertItem(5, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngThree.setCount(recipe.getOutputRngAmount2()); // Simply change the stack to equal the output amount
                                    h.insertItem(5,rngThree.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        counter--;
                        consumeEnergy();
                        setChanged();
                    } else if (counter > 0){ //In progress
                        counter--;
                        consumeEnergy();
                    } else { // Check if we should start processing
                        if (areSlotsEmptyOrHaveCurrentItems(recipe,output,rngOne,rngTwo,rngThree)){
                            counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(6).copy());
                            length = counter;
                        } else {
                            counter = 0;
                        }
                    }
                } else { // This is if we reach the maximum in the slots
                    counter = 0;
                }
            } else { // this is if the input slot is empty
                counter = 0;
            }
        });
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.CENTRIFUGAL_SEPARATOR_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(6).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.CENTRIFUGAL_SEPARATOR_POWER_USAGE.get(), this.inventory.getStackInSlot(6).copy());
    }

    private boolean areSlotsFull(CentrifugalSeparatorRecipe recipe, ItemStack one, ItemStack two, ItemStack three, ItemStack four){

        if (one.getCount() + recipe.getOutputAmount() > one.getItem().getItemStackLimit(one.copy())){ // Main output slot
            return true;
        } else if (two.getCount() + recipe.getOutputRngAmount0() > two.getItem().getItemStackLimit(two.copy())){ // Rng Slot 0
            return true;
        } else if (three.getCount() + recipe.getOutputRngAmount1() > three.getItem().getItemStackLimit(three.copy())){ // Rng Slot 1
            return true;
        } else if (four.getCount() + recipe.getOutputRngAmount2() > four.getItem().getItemStackLimit(four.copy())){ // Rng Slot 2
            return true;
        } else {
            return false;
        }
    }

    private boolean usesBucket(CentrifugalSeparatorRecipe recipe, ItemStack bucket){
        if (recipe != null){ // If the recipe is null, don't bother processing
            if (recipe.needsBuckets() > 0){ // If it doesn't use a bucket, we know that it must have a valid recipe, return true
                if (!bucket.isEmpty() && bucket.getItem() == Items.BUCKET){
                    if(bucket.getCount() >= recipe.needsBuckets()) return true; // Needs a bucket, has enough buckets. Return true.
                    return false; // Needs a bucket, doesn't have enough buckets. Return false.
                } else {
                    return false; // Needs a bucket, doesn't have a bucket. Return false.
                }
            } else {
                return true; // Doesn't need a bucket, likely valid recipe. Return true.
            }
        }
        return false; // Likely empty slot, don't bother
    }

    private boolean areSlotsEmptyOrHaveCurrentItems(CentrifugalSeparatorRecipe recipe, ItemStack one, ItemStack two, ItemStack three, ItemStack four){
        ArrayList<ItemStack> outputList = new ArrayList<>();
        outputList.add(one.copy());
        outputList.add(two.copy());
        outputList.add(three.copy());
        outputList.add(four.copy());
        boolean isEmpty = true;
        boolean matchesRecipe = true;
        for (ItemStack x : outputList){
            if (!x.isEmpty()){
                //LOGGER.debug("Not Empty Slot!");
                isEmpty = false;
                if (one.getItem() != recipe.getResult().getItem() && one.getItem() != Items.AIR){
                    return false;
                } else if (two.getItem() != recipe.getRngItemSlot0().getItem() && two.getItem() != Items.AIR){
                    return false;
                } else if (three.getItem() != recipe.getRngItemSlot1().getItem() && three.getItem() != Items.AIR){
                    return false;
                } else if (four.getItem() != recipe.getRngItemSlot2().getItem() && four.getItem() != Items.AIR){
                    return false;
                } else {
                    return true;
                }
            }
        }
        return isEmpty;
    }

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(inv));
        //createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));
        counter = tag.getInt("counter");
        length = tag.getInt("length");

        inputSm.read(tag, "input_manager");
        bucketSm.read(tag, "bucket_manager");
        outputSm.read(tag, "output_manager");
        rngOneSm.read(tag, "rng_one_manager");
        rngTwoSm.read(tag, "rng_two_manager");
        rngThreeSm.read(tag, "rng_three_manager");

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

        inputSm.write(tag, "input_manager");
        bucketSm.write(tag, "bucket_manager");
        outputSm.write(tag, "output_manager");
        rngOneSm.write(tag, "rng_one_manager");
        rngTwoSm.write(tag, "rng_two_manager");
        rngThreeSm.write(tag, "rng_three_manager");
    }

    public ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            //ItemStack referenceStack1 = inputItemStack.get().copy();
            //referenceStack1.setCount(64);
            CentrifugalSeparatorRecipe recipe = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
            CentrifugalSeparatorRecipe recipe1 = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if (slot == 0 && recipe != null){
                for (ItemStack testStack : recipe.ingredient.getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return true;
                    }
                }
            } else if (slot == 1 && stack.getItem() == Items.BUCKET){
                return true;
            } else if (slot == 2 && recipe1 != null){ // Output slot
                return stack.getItem() == recipe1.result.getItem();
            } else if (slot == 3 && recipe1 != null){ // RNG 0 slot
                return stack.getItem() == recipe1.getRngItemSlot0().getItem();
            } else if (slot == 4 && recipe1 != null){ // RNG 1 slot
                return stack.getItem() == recipe1.getRngItemSlot1().getItem();
            } else if (slot == 5 && recipe1 != null){ // RNG 2 slot
                return stack.getItem() == recipe1.getRngItemSlot2().getItem();
            } else if (slot == 6){
                return stack.getItem() == VEItems.QUARTZ_MULTIPLIER;
            }
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            CentrifugalSeparatorRecipe recipe = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack.copy()), level).orElse(null);
            CentrifugalSeparatorRecipe recipe1 = level.getRecipeManager().getRecipeFor(CentrifugalSeparatorRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if(slot == 0 && recipe != null) {
                for (ItemStack testStack : recipe.ingredient.getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            } else if ( slot == 1 && stack.getItem() == Items.BUCKET) {
                return super.insertItem(slot, stack, simulate);
            } else if (slot == 2 && recipe1 != null){
                if (stack.getItem() == recipe1.result.getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 3 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItemSlot0().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 4 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItemSlot1().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 5 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItemSlot2().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 6 && stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }
    };

    private VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.CENTRIFUGAL_SEPARATOR_MAX_POWER.get(),Config.CENTRIFUGAL_SEPARATOR_TRANSFER.get()); // Max Power Storage, Max transfer
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getCapability(cap, side, handler, inventory, slotManagers);
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null) { // TODO: Better handle Null direction
            //return getCapability(cap,side,handler,fluidManagers);
            return super.getCapability(cap, side);
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public Component getDisplayName(){
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CentrifugalSeparatorContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status,slotId,inputSm,bucketSm,outputSm,rngOneSm,rngTwoSm,rngThreeSm);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction,slotId,inputSm,bucketSm,outputSm,rngOneSm,rngTwoSm,rngThreeSm);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        // Boolean Buttons
                        bulkSendSMPacket(s, inputSm,bucketSm,outputSm,rngOneSm,rngTwoSm,rngThreeSm);
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
                    if(!(player.containerMenu instanceof CentrifugalSeparatorContainer)){
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