package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.IVEPoweredTileEntity;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VEContainerFactory {
    private RegistryObject<MenuType<VEContainer>> menuTypeRegistryObject;
    private RegistryObject<Block> block;
    private final List<Slot> slots = new ArrayList<>();

    public int numberOfSlots() {
        return VEContainerFactory.this.slots.size();
    }

    public VEContainer create(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        return new VEContainer(menuTypeRegistryObject.get(), id, world, pos, inventory, player, block.get()) {
            @Override
            protected void addSlotsToGUI(IItemHandler h) {
                List<Slot> slots = VEContainerFactory.this.slots;

                int energySlotId = -1;

                if( this.tileEntity instanceof IVEPoweredTileEntity poweredTileEntity) {
                    energySlotId = poweredTileEntity.getUpgradeSlotId();
                }

                for(int i = 0; i < slots.size(); i++) {
                    if(i == energySlotId) {
                        Slot slot = slots.get(i);
                        addSlot(new VESlot(h, slot.index, slot.x, slot.y,true));
                        continue;
                    }
                    if(i >= this.tileEntity.getSlotManagers().size()) {
                        VoluminousEnergy.LOGGER.error("Unable to properly create " + menuTypeRegistryObject.getId() + ". The VEContainerFactory tried to process more slots than were available.");
                        continue;
                    }
                    SlotType slotType = this.tileEntity.getSlotManagers().get(i).getSlotType();
                    boolean isOutput = slotType == SlotType.FLUID_OUTPUT || slotType == SlotType.OUTPUT;
                    Slot slot = slots.get(i);
                    addSlot(new VESlot(h, slot.index, slot.x, slot.y,!isOutput));
                }
            }
        };
    }

    public static class VEContainerFactoryBuilder {

        private VEContainerFactory factory;

        public VEContainerFactoryBuilder create(RegistryObject<MenuType<VEContainer>> menuRegistry, RegistryObject<Block> blockRegistry) {
            this.factory = new VEContainerFactory();
            this.factory.menuTypeRegistryObject = menuRegistry;
            this.factory.block = blockRegistry;
            return this;
        }

        public VEContainerFactoryBuilder addSlot(int index, int x, int y) {
            this.factory.slots.add(new Slot(index, x, y));
            return this;
        }

        public VEContainerFactory build() {
            return this.factory;
        }
    }

    private record Slot(int index, int x, int y) {

    }

    public static class VESlot extends SlotItemHandler {

        private final IItemHandler handler;
        private final int index;
        private final boolean allowInsertion;

        public VESlot(IItemHandler itemHandler, int index, int xPos, int yPos,boolean allowInsertion) {
            super(itemHandler, index, xPos, yPos);
            this.handler = itemHandler;
            this.index = index;
            this.allowInsertion = allowInsertion;
        }

        public boolean mayPlace(@NotNull ItemStack stack) {
            if (stack.isEmpty()) return false;
            return allowInsertion; // && handler.isItemValid(index, stack); TODO fix me
        }
    }
}
