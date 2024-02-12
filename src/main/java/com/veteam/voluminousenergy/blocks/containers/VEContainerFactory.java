package com.veteam.voluminousenergy.blocks.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class VEContainerFactory {

    //    private int numSlots = 0; // TODO this needs to be pulled from the client later:tm:
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
                for (Slot slot : VEContainerFactory.this.slots) {
                    addSlot(new SlotItemHandler(h, slot.index, slot.x, slot.y));
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
}
