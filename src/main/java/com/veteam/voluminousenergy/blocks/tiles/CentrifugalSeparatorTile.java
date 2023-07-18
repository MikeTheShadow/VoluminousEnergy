package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalSeparatorContainer;
import com.veteam.voluminousenergy.recipe.CentrifugalSeparatorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CentrifugalSeparatorTile extends VETileEntity implements IVEPoweredTileEntity,IVECountable {
    private final ItemStackHandler handler = createHandler(7);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0,0, Direction.UP,true, SlotType.INPUT));
        add(new VESlotManager(1,1,Direction.WEST,true,SlotType.INPUT));
        add(new VESlotManager(2,0,Direction.DOWN,true,SlotType.OUTPUT));
        add(new VESlotManager(3,1, Direction.NORTH, true,SlotType.OUTPUT));
        add(new VESlotManager(4,2,Direction.SOUTH,true,SlotType.OUTPUT));
        add(new VESlotManager(5,3,Direction.EAST,true,SlotType.OUTPUT));
    }};

    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public CentrifugalSeparatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_TILE.get(), pos, state,CentrifugalSeparatorRecipe.RECIPE_TYPE);
    }

    @Override
    public void tick(){

        updateClients();
        super.tick();
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
                if (one.getItem() != recipe.getResult(0).getItem() && one.getItem() != Items.AIR){
                    return false;
                } else if (two.getItem() != recipe.getResult(1).getItem() && two.getItem() != Items.AIR){
                    return false;
                } else if (three.getItem() != recipe.getResult(2).getItem() && three.getItem() != Items.AIR){
                    return false;
                } else if (four.getItem() != recipe.getResult(3).getItem() && four.getItem() != Items.AIR){
                    return false;
                } else {
                    return true;
                }
            }
        }
        return isEmpty;
    }

    private ItemStackHandler inventory = new ItemStackHandler(7) {
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
                for (ItemStack testStack : recipe.getIngredient(0).getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return true;
                    }
                }
            } else if (slot == 1 && stack.getItem() == Items.BUCKET){
                return true;
            } else if (slot == 2 && recipe1 != null){ // Output slot
                return stack.getItem() == recipe1.getResult(0).getItem();
            } else if (slot == 3 && recipe1 != null){ // RNG 0 slot
                return stack.getItem() == recipe1.getResult(1).getItem();
            } else if (slot == 4 && recipe1 != null){ // RNG 1 slot
                return stack.getItem() == recipe1.getResult(2).getItem();
            } else if (slot == 5 && recipe1 != null){ // RNG 2 slot
                return stack.getItem() == recipe1.getResult(3).getItem();
            } else if (slot == 6){
                return TagUtil.isTaggedMachineUpgradeItem(stack);
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
                for (ItemStack testStack : recipe.getIngredient(0).getItems()){
                    if(stack.getItem() == testStack.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            } else if ( slot == 1 && stack.getItem() == Items.BUCKET) {
                return super.insertItem(slot, stack, simulate);
            } else if (slot == 2 && recipe1 != null){
                if (stack.getItem() == recipe1.getResult(0).getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 3 && recipe1 != null){
                if (stack.getItem() == recipe1.getResult(1).getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 4 && recipe1 != null){
                if (stack.getItem() == recipe1.getResult(2).getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 5 && recipe1 != null){
                if (stack.getItem() == recipe1.getResult(3).getItem()){
                    return super.insertItem(slot, stack, simulate);
                }
            } else if (slot == 6 && TagUtil.isTaggedMachineUpgradeItem(stack)){
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }
    };

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CentrifugalSeparatorContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return handler;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }
    
    @Override
    public int getMaxPower() {
        return Config.CENTRIFUGAL_SEPARATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.CENTRIFUGAL_SEPARATOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.CENTRIFUGAL_SEPARATOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 6;
    }
}