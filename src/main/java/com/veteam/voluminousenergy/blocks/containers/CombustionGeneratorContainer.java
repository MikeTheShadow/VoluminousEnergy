package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.COMBUSTION_GENERATOR_CONTAINER;

public class CombustionGeneratorContainer extends VoluminousContainer {

    private static final int NUMBER_OF_SLOTS = 4;

    public CombustionGeneratorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(COMBUSTION_GENERATOR_CONTAINER.get(), id);
        this.tileEntity = (VETileEntity) world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new VEBucketSlot(h, 0, 38, 18)); // Oxidizer input slot
            addSlot(new VEBucketSlot(h, 1, 38, 49)); // Extract fluid from oxidizer slot
            addSlot(new VEBucketSlot(h, 2, 138, 18)); // Fuel input slot
            addSlot(new VEBucketSlot(h, 3, 138, 49)); // Extract fluid from fuel output
        });
        layoutPlayerInventorySlots(8, 84);

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
            }
        });
    }

    public int powerScreen(int px) {
        int stored = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        return (((stored * 100 / max * 100) / 100) * px) / 100;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, VEBlocks.COMBUSTION_GENERATOR_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

}
