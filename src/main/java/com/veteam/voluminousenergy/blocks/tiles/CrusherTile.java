package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.Mth.abs;

public class CrusherTile extends VoluminousTileEntity {
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    // Slot Managers
    public VESlotManager inputSlotProp = new VESlotManager(0,Direction.UP,true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager outputSlotProp = new VESlotManager(1,Direction.DOWN,true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);
    public VESlotManager rngSlotProp = new VESlotManager(2, Direction.NORTH,true, "slot.voluminousenergy.rng_slot",SlotType.OUTPUT);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(inputSlotProp);
        add(outputSlotProp);
        add(rngSlotProp);
    }};

    // Sided Item Handlers
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public CrusherTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CRUSHER_TILE, pos, state);
    }

    @Deprecated
    public CrusherTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.CRUSHER_TILE, pos, state);
    }

    public ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            ItemStack referenceStack = stack.copy();
            referenceStack.setCount(64);
            CrusherRecipe recipe = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
            CrusherRecipe recipe1 = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if (slot == 0 && recipe != null){
                return recipe.ingredient.test(stack);
            } else if (slot == 1 && recipe1 != null){
                return stack.getItem() == recipe1.result.getItem();
            } else if (slot == 2 && recipe1 != null){
                return stack.getItem() == recipe1.getRngItem().getItem();
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
            CrusherRecipe recipe = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(referenceStack), level).orElse(null);
            CrusherRecipe recipe1 = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(inputItemStack.get().copy()),level).orElse(null);

            if(slot == 0 && recipe != null) {
                for (ItemStack testStack : recipe.ingredient.getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            } else if (slot == 1 && recipe1 != null){
                if (stack.getItem() == recipe1.result.getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 2 && recipe1 != null){
                if (stack.getItem() == recipe1.getRngItem().getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 3){
                if(stack.getItem() == VEItems.QUARTZ_MULTIPLIER){
                    return super.insertItem(slot, stack, simulate);
                }
            }
            return stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if(level != null){
                Random rand = new Random();
                if (inventory.getStackInSlot(slot).getItem() == VEItems.BAUXITE_DUST)
                    level.addFreshEntity(new ExperienceOrb(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), amount*Mth.nextInt(rand, 1, 3)));
                if (inventory.getStackInSlot(slot).getItem() == VEItems.CINNABAR_DUST)
                    level.addFreshEntity(new ExperienceOrb(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), amount*Mth.nextInt(rand, 1, 3)));
                if (inventory.getStackInSlot(slot).getItem() == VEItems.GALENA_DUST)
                    level.addFreshEntity(new ExperienceOrb(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), amount*Mth.nextInt(rand, 2, 5)));
                if (inventory.getStackInSlot(slot).getItem() == VEItems.RUTILE_DUST)
                    level.addFreshEntity(new ExperienceOrb(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), amount*Mth.nextInt(rand, 5, 7)));
                if (inventory.getStackInSlot(slot).getItem() == VEItems.SALTPETERCHUNK)
                    level.addFreshEntity(new ExperienceOrb(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), amount*Mth.nextInt(rand, 1, 3)));
            }
            
            return super.extractItem(slot,amount,simulate);
        }

        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);
            CrusherTile.this.setChanged();
        }
    };

    @Override
    public void tick(){

        updateClients();

        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack output = inventory.getStackInSlot(1).copy();
        ItemStack rng = inventory.getStackInSlot(2).copy();

        CrusherRecipe recipe = level.getRecipeManager().getRecipeFor(CrusherRecipe.RECIPE_TYPE, new SimpleContainer(input), level).orElse(null);
        inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

        if (!input.isEmpty()){
            if (output.getCount() + recipe.getOutputAmount() < 64 && rng.getCount() + recipe.getOutputRngAmount() < 64 && canConsumeEnergy()) {
                if (counter == 1){ //The processing is about to be complete
                    // Extract the inputted item
                    inventory.extractItem(0,recipe.ingredientCount,false);

                    // Get output stack from the recipe
                    ItemStack newOutputStack = recipe.getResult().copy();

                    //LOGGER.debug("output: " + output + " rng: " + rng + " newOutputStack: "  + newOutputStack);

                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                        if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                            output.setCount(1);
                        }
                        newOutputStack.setCount(recipe.getOutputAmount());
                        inventory.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                    } else { // Assuming the recipe output item is already in the output slot
                        output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                        inventory.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                    }

                    // Manipulating the RNG slot
                    if (recipe.getChance() != 0){ // If the chance is ZERO, this functionality won't be used
                        ItemStack newRngStack = recipe.getRngItem().copy();

                        // Generate Random floats
                        Random r = new Random();
                        float random = abs(0 + r.nextFloat() * (0 - 1));

                        // ONLY manipulate the slot if the random float is under or is identical to the chance float
                        if(random <= recipe.getChance()){
                            if (rng.getItem() != recipe.getRngItem().getItem()){
                                if (rng.getItem() == Items.AIR){
                                    rng.setCount(1);
                                }
                                newRngStack.setCount(recipe.getOutputRngAmount());
                                inventory.insertItem(2, newRngStack.copy(),false); // CRASH the game if this is not empty!
                            } else { // Assuming the recipe output item is already in the output slot
                                rng.setCount(recipe.getOutputRngAmount()); // Simply change the stack to equal the output amount
                                inventory.insertItem(2,rng.copy(),false); // Place the new output stack on top of the old one
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
                    if (output.isEmpty() && rng.isEmpty() || output.isEmpty() && rng.getItem() == recipe.getRngItem().getItem() || output.getItem() == recipe.getResult().getItem() && rng.getItem() == recipe.getRngItem().getItem() || output.getItem() == recipe.getResult().getItem() && rng.isEmpty()){
                        counter = this.calculateCounter(recipe.getProcessTime(), inventory.getStackInSlot(3).copy());
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
    }

    // Extract logic for energy management, since this is getting quite complex now.
    private void consumeEnergy(){
        energy.ifPresent(e -> e
                .consumeEnergy(this.consumptionMultiplier(Config.CRUSHER_POWER_USAGE.get(),
                        this.inventory.getStackInSlot(3).copy()
                        )
                )
        );
    }

    private boolean canConsumeEnergy(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)
                > this.consumptionMultiplier(Config.CRUSHER_POWER_USAGE.get(), this.inventory.getStackInSlot(3).copy());
    }

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        this.inventory.deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));
        counter = tag.getInt("counter");
        length = tag.getInt("length");

        inputSlotProp.read(tag, "input_slot");
        outputSlotProp.read(tag, "output_slot");
        rngSlotProp.read(tag, "rng_slot");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", this.inventory.serializeNBT());
        energy.ifPresent(h -> h.serializeNBT(tag));
        tag.putInt("counter", counter);
        tag.putInt("length", length);

        inputSlotProp.write(tag, "input_slot");
        outputSlotProp.write(tag, "output_slot");
        rngSlotProp.write(tag, "rng_slot");
    }

    private VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.CRUSHER_MAX_POWER.get(),Config.CRUSHER_TRANSFER.get()); // Max Power Storage, Max transfer
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
        return new CrusherContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter != 0 && length != 0) return (px * (100 - ((counter * 100) / length))) / 100;
        return 0;
    }

    @Override
    public void updatePacketFromGui(boolean status, int slotId){
        processGUIPacketStatus(status,slotId,inputSlotProp,outputSlotProp,rngSlotProp);
    }

    public void updatePacketFromGui(int direction, int slotId){
        processGUIPacketDirection(direction,slotId,inputSlotProp,outputSlotProp,rngSlotProp);
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)) {
                        bulkSendSMPacket(s, inputSlotProp, outputSlotProp, rngSlotProp);
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
                    if(!(player.containerMenu instanceof CrusherContainer)){
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
