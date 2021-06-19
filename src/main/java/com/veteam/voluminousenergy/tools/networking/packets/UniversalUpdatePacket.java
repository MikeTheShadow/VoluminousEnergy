package com.veteam.voluminousenergy.tools.networking.packets;

/*
    Universal Packet to update clients on information about the TE without spamming clients with 50 billion packets
    like how many IO packets are sent when the player opens the IO management menu
 */

import com.veteam.voluminousenergy.blocks.containers.VoluminousContainer;
import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;

public class UniversalUpdatePacket {
    private int energy;
    private byte slotCount;
    private byte tankCount;
    private ItemStackHandler inventory;
    private FluidTank[] tanks;

    public UniversalUpdatePacket() {} // Do nothing

    public UniversalUpdatePacket(int energy, byte slotCount, byte tankCount, ItemStackHandler inventory, RelationalTank... relationalTanks) {
        this.energy = energy;
        this.slotCount = slotCount;
        this.tankCount = tankCount;
        this.inventory = inventory;
        assert relationalTanks.length == this.tankCount;

        this.tanks = new FluidTank[this.tankCount];
        byte i = 0;
        for (RelationalTank t : relationalTanks) {
                this.tanks[i] = t.getTank();
                i++;
        }

    }

    public UniversalUpdatePacket(int energy, byte slotCount, byte tankCount, ItemStackHandler inventory, FluidTank... tanks) {
        this.energy = energy;
        this.slotCount = slotCount;
        this.tankCount = tankCount;
        this.inventory = inventory;
        this.tanks = tanks;
    }

    public UniversalUpdatePacket(int energy, byte tankCount, ItemStackHandler inventory, RelationalTank... relationalTanks) {
        new UniversalUpdatePacket(energy, (byte) inventory.getSlots(), tankCount, inventory, relationalTanks);
    }

    public UniversalUpdatePacket(int energy, byte tankCount, ItemStackHandler inventory, FluidTank... tanks) {
        new UniversalUpdatePacket(energy, (byte) inventory.getSlots(), tankCount, inventory, tanks);
    }

    public UniversalUpdatePacket(int energy, ItemStackHandler inventory) {
        new UniversalUpdatePacket(energy, (byte) inventory.getSlots(), (byte) 0, inventory, (RelationalTank) null);
    }

    public UniversalUpdatePacket(int energy){
        new UniversalUpdatePacket(energy, (byte) 0, (byte) 0, null, (RelationalTank) null);
    }

    public static UniversalUpdatePacket fromBytes(PacketBuffer buffer){
        UniversalUpdatePacket packet = new UniversalUpdatePacket();
        packet.energy = buffer.readInt();
        packet.slotCount = buffer.readByte();
        packet.tankCount = buffer.readByte();

        for (int i = 0; i < packet.slotCount; i++) packet.inventory.setStackInSlot(i, buffer.readItem().copy());

        if(packet.tankCount > 0){
            FluidTank[] packetTanks = new FluidTank[packet.tankCount];
            for (int i = 0; i < packet.tankCount; i++) {
                packetTanks[i] = new FluidTank(VEFluidTileEntity.TANK_CAPACITY,
                        (Predicate<FluidStack>) buffer.readFluidStack());
            }
            packet.tanks = packetTanks;
        }

        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeInt(this.energy);
        buffer.writeByte(this.slotCount);
        buffer.writeByte(this.tankCount);

        for (int i = 0; i < this.slotCount; i++) buffer.writeItem(this.inventory.getStackInSlot(i));
        if(this.tankCount > 0) for (int i = 0; i < this.tankCount; i++) buffer.writeFluidStack(this.tanks[i].getFluid());
    }

    public static void handle(UniversalUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();

        // We don't care about Client --> Server in this case since we want the server to be absolute
        if(packetDirection == PLAY_TO_CLIENT) {
            Container clientContainer = Minecraft.getInstance().player.containerMenu;
            contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer));
        }
    }

    public static void handlePacket(UniversalUpdatePacket packet, Container openContainer){
        if(openContainer != null){
            if(openContainer instanceof VoluminousContainer){
                VoluminousTileEntity tileEntity = (VoluminousTileEntity) ((VoluminousContainer) openContainer).getTileEntity();
                tileEntity.readUniversalUpdatePacket(packet);
            }
        }
    }

    // Get methods
    public int getEnergy(){
        return this.energy;
    }

    public byte getSlotCount() {
        return this.slotCount;
    }

    public byte getTankCount() {
        return this.tankCount;
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public FluidTank[] getTanks() {
        return this.tanks;
    }

    public void updateRelationalTank(RelationalTank tank) {
        if (tank.getId() < this.tankCount) tank.setTank(this.tanks[tank.getId()]);
    }

    public void updateTank(FluidTank tank, int id){
        if (id < this.tankCount) tank.setFluid(this.tanks[id].getFluid());
    }
}
