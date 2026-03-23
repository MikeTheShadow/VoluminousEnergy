package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.iolisteners.SlotWithIOListener;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory.ListenedItemInputSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntityFactory.TileSlot;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class VEContainerFactory {
    private Supplier<MenuType<VEContainer>> menuTypeRegistryObject;
    private Supplier<Block> block;
    private final List<Slot> slots = new ArrayList<>();
    private final List<TileSlot> tileSlots = new ArrayList<>();

    private int upgradeSlotId = -1;

    public int getNumberOfSlots() {
        return VEContainerFactory.this.slots.size();
    }

    public int upgradeSlotId() {
        return upgradeSlotId;
    }

    public VEContainer create(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        return new VEContainer(menuTypeRegistryObject.get(), id, world, pos, inventory, player, block.get()) {
            @Override
            protected void addSlotsToGUI(IItemHandler h) {
                List<Slot> slots = VEContainerFactory.this.slots;

                int energySlotId = -1;

                if (this.tileEntity.getEnergy() != null) {
                    energySlotId = this.tileEntity.getEnergy().getUpgradeSlotId();
                }

                boolean isClientSide = tileEntity.getLevel().isClientSide;

                for (int i = 0; i < slots.size(); i++) {
                    if (i == energySlotId) {
                        Slot slot = slots.get(i);
                        addSlot(new VESlot(h, slot.index, slot.x, slot.y, true, slot.listener, isClientSide));
                        continue;
                    }
                    if (i >= this.tileEntity.getSlotManagers().size()) {
                        VoluminousEnergy.LOGGER.error("Unable to properly create " + menuTypeRegistryObject.get() + ". The VEContainerFactory tried to process more slots than were available.");
                        break;
                    }
                    SlotType slotType = this.tileEntity.getSlotManagers().get(i).getSlotType();
                    boolean isOutput = (slotType == SlotType.FLUID_OUTPUT || slotType == SlotType.OUTPUT);
                    Slot slot = slots.get(i);
                    addSlot(new VESlot(h, slot.index, slot.x, slot.y, !isOutput, slot.listener, isClientSide));
                }
            }
        };
    }

    public List<VESlotManager> getTileSlotsAsManagers() {
        AtomicInteger i = new AtomicInteger(0);
        return tileSlots.stream().map(t -> t.asManager(i.getAndIncrement())).toList();
    }

    public static class VEContainerFactoryBuilder {

        private VEContainerFactory factory;

        public VEContainerFactoryBuilder create(Supplier<MenuType<VEContainer>> menuRegistry, Supplier<Block> blockRegistry) {
            this.factory = new VEContainerFactory();
            this.factory.menuTypeRegistryObject = menuRegistry;
            this.factory.block = blockRegistry;
            return this;
        }

        private int index = 0;

        public VEContainerFactoryBuilder addUpgradeSlot(int x, int y) {
            this.factory.upgradeSlotId = index;
            this.factory.slots.add(new Slot(index++, x, y, null));
            return this;
        }

        public VEContainerFactoryBuilder addSlot(int x, int y, TileSlot slot) {
            if (slot instanceof ListenedItemInputSlot listenedItemInputSlot)
                this.factory.slots.add(new Slot(index++, x, y, listenedItemInputSlot.listener()));
            else
                this.factory.slots.add(new Slot(index++, x, y, null));

            this.factory.tileSlots.add(slot);
            return this;
        }

        public VEContainerFactory build() {
            return this.factory;
        }
    }

    private record Slot(int index, int x, int y, @Nullable SlotWithIOListener listener) {

    }

    public static class VESlot extends SlotItemHandler {

        private final boolean allowInsertion;
        private final @Nullable SlotWithIOListener listener;
        private final boolean isClientSide;

        public VESlot(IItemHandler itemHandler, int index, int xPos, int yPos, boolean allowInsertion, @Nullable SlotWithIOListener listener, boolean isClientSide) {
            super(itemHandler, index, xPos, yPos);
            this.allowInsertion = allowInsertion;
            this.listener = listener;
            this.isClientSide = isClientSide;
        }

        public boolean mayPlace(@NotNull ItemStack stack) {
            return allowInsertion && super.mayPlace(stack);
        }

        @Override
        public @NotNull ItemStack remove(int amount) {
            if (listener != null) listener.onRemoved(getItemHandler(), amount, getSlotIndex(), isClientSide);
            return super.remove(amount);
        }

        @Override
        public void onTake(@NotNull Player pPlayer, @NotNull ItemStack stack) {
            if (listener != null) {
                listener.onTake(getItemHandler(), stack, isClientSide);
            }
            super.onTake(pPlayer, stack);
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            if (listener != null) listener.onSet(stack, getSlotIndex(), getItemHandler(), isClientSide);
            super.set(stack);
        }

        public void preQuickMoveStack(ItemStack stack) {
            if (listener != null) listener.preQuickMoveStack(getItemHandler(), stack, isClientSide);
        }
    }
}
