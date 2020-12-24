package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;

public class Network {
    private static final String VERSION = "venet";

    public static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(VoluminousEnergy.MODID, "network"))
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        channel.messageBuilder(BoolButtonPacket.class, 1)
                .decoder(BoolButtonPacket::fromBytes)
                .encoder(BoolButtonPacket::toBytes)
                .consumer(BoolButtonPacket::handle)
                .add();

        channel.messageBuilder(DirectionButtonPacket.class, 2)
                .decoder(DirectionButtonPacket::fromBytes)
                .encoder(DirectionButtonPacket::toBytes)
                .consumer(DirectionButtonPacket::handle)
                .add();
    }

    private Network(){

    }

    public static void init(){

    }
}
