package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.networking.packets.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;

public class VENetwork {
    private static final String VERSION = "venet";
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(VoluminousEnergy.MODID, "network");

    public static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_ID)
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

        channel.messageBuilder(UuidPacket.class, 3)
                .decoder(UuidPacket::fromBytes)
                .encoder(UuidPacket::toBytes)
                .consumer(UuidPacket::handle)
                .add();

        channel.messageBuilder(TankBoolPacket.class, 4)
                .decoder(TankBoolPacket::fromBytes)
                .encoder(TankBoolPacket::toBytes)
                .consumer(TankBoolPacket::handle)
                .add();

        channel.messageBuilder(TankDirectionPacket.class, 5)
                .decoder(TankDirectionPacket::fromBytes)
                .encoder(TankDirectionPacket::toBytes)
                .consumer(TankDirectionPacket::handle)
                .add();

        channel.messageBuilder(BatteryBoxSlotPairPacket.class, 6)
                .decoder(BatteryBoxSlotPairPacket::fromBytes)
                .encoder(BatteryBoxSlotPairPacket::toBytes)
                .consumer(BatteryBoxSlotPairPacket::handle)
                .add();

        channel.messageBuilder(BatteryBoxSendOutPowerPacket.class, 7)
                .decoder(BatteryBoxSendOutPowerPacket::fromBytes)
                .encoder(BatteryBoxSendOutPowerPacket::toBytes)
                .consumer(BatteryBoxSendOutPowerPacket::handle)
                .add();
    }

    private VENetwork(){

    }

    public static void init(){

    }
}
