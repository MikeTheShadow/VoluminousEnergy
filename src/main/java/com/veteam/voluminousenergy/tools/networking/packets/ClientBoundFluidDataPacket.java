package com.veteam.voluminousenergy.tools.networking.packets;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.event.network.CustomPayloadEvent;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.registries.ForgeRegistries;

import java.util.HashMap;

/**
 * Update the client(s) with server-side fluid information.
 */
public class ClientBoundFluidDataPacket {

    final HashMap<Fluid, Integer> combustibleFluids;
    final HashMap<Fluid, Float> oxidizerFluids;

    public ClientBoundFluidDataPacket(HashMap<Fluid, Integer> combustibleFluids, HashMap<Fluid, Float> oxidizerFluids) {
        this.combustibleFluids = combustibleFluids;
        this.oxidizerFluids = oxidizerFluids;
    }


    /**
     * We write a size and then the data to make it easier to decode it
     * @param buffer The friendly buffer
     */
    public void toBytes(FriendlyByteBuf buffer) {

        var combustibleSet =  CombustibleFluidsData.getDataForNetworkTransfer().entrySet();
        buffer.writeInt(combustibleSet.size());
        for(var data: combustibleSet) {
            buffer.writeRegistryId(ForgeRegistries.FLUIDS, data.getKey());
            buffer.writeVarInt(data.getValue());
        }

        var oxidizerSet = OxidizerFluidsData.getDataForNetworkTransfer().entrySet();
        buffer.writeInt(oxidizerSet.size());
        for(var data : oxidizerSet) {
            buffer.writeRegistryId(ForgeRegistries.FLUIDS, data.getKey());
            buffer.writeFloat(data.getValue());
        }
    }

    public static ClientBoundFluidDataPacket fromBytes(FriendlyByteBuf buffer) {
        HashMap<Fluid, Integer> combustibleFluids = new HashMap<>();
        HashMap<Fluid, Float> oxidizerFluids = new HashMap<>();
        int combustibleSize = buffer.readInt();
        for(int i = 0;i < combustibleSize; i++) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(buffer.readRegistryId());
            combustibleFluids.put(fluid, buffer.readVarInt());
        }
        int oxidizerSize = buffer.readInt();
        for(int i = 0;i < oxidizerSize; i++) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(buffer.readRegistryId());
            oxidizerFluids.put(fluid, buffer.readFloat());
        }
        return new ClientBoundFluidDataPacket(combustibleFluids, oxidizerFluids);
    }

    public static void handle(ClientBoundFluidDataPacket packet, CustomPayloadEvent.Context contextSupplier) {
        NetworkDirection packetDirection = contextSupplier.getDirection();
        switch (packetDirection) {
            case PLAY_TO_CLIENT: // Packet is received on client
                CombustibleFluidsData.updateFromPacket(packet.combustibleFluids);
                OxidizerFluidsData.updateFromPacket(packet.oxidizerFluids);
                contextSupplier.setPacketHandled(true);
                break;
            default:
                throw new IllegalStateException("ClientBoundFluidDataPacket must only be sent to clients!");
        }
    }

}
