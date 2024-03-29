package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.networking.packets.*;
import io.netty.util.AttributeKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.ForgePacketHandler;
import net.minecraftforge.network.SimpleChannel;

public class VENetwork {
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(VoluminousEnergy.MODID, "network");

    public static final AttributeKey<ForgePacketHandler> CONTEXT = AttributeKey.newInstance(CHANNEL_ID.toString());

    public static SimpleChannel channel;
    static {
        channel = ChannelBuilder.named(CHANNEL_ID)
                .networkProtocolVersion(1)
                .simpleChannel();

        channel.messageBuilder(BoolButtonPacket.class, 1)
                .decoder(BoolButtonPacket::fromBytes)
                .encoder(BoolButtonPacket::toBytes)
                .consumerMainThread(BoolButtonPacket::handle)
                .add();

        channel.messageBuilder(DirectionButtonPacket.class, 2)
                .decoder(DirectionButtonPacket::fromBytes)
                .encoder(DirectionButtonPacket::toBytes)
                .consumerMainThread(DirectionButtonPacket::handle)
                .add();

        channel.messageBuilder(UuidPacket.class, 3)
                .decoder(UuidPacket::fromBytes)
                .encoder(UuidPacket::toBytes)
                .consumerMainThread(UuidPacket::handle)
                .add();

        channel.messageBuilder(TankBoolPacket.class, 4)
                .decoder(TankBoolPacket::fromBytes)
                .encoder(TankBoolPacket::toBytes)
                .consumerMainThread(TankBoolPacket::handle)
                .add();

        channel.messageBuilder(TankDirectionPacket.class, 5)
                .decoder(TankDirectionPacket::fromBytes)
                .encoder(TankDirectionPacket::toBytes)
                .consumerMainThread(TankDirectionPacket::handle)
                .add();

        channel.messageBuilder(BatteryBoxSlotPairPacket.class, 6)
                .decoder(BatteryBoxSlotPairPacket::fromBytes)
                .encoder(BatteryBoxSlotPairPacket::toBytes)
                .consumerMainThread(BatteryBoxSlotPairPacket::handle)
                .add();

        channel.messageBuilder(BatteryBoxSendOutPowerPacket.class, 7)
                .decoder(BatteryBoxSendOutPowerPacket::fromBytes)
                .encoder(BatteryBoxSendOutPowerPacket::toBytes)
                .consumerMainThread(BatteryBoxSendOutPowerPacket::handle)
                .add();
    }

    private VENetwork(){

    }

    public static void init(){

    }
}
