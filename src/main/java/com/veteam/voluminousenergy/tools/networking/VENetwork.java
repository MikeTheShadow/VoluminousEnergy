package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.tools.networking.packets.*;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket.BoolButtonPayload;
import com.veteam.voluminousenergy.tools.networking.packets.ClientBoundFluidDataPacket.ClientBoundFluidDataPayload;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket.DirectionButtonPayload;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket.TankBoolPacketPayload;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket.TankDirectionPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class VENetwork {

    public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("ve_1");
        registrar.playBidirectional(BoolButtonPayload.TYPE, BoolButtonPayload.STREAM_CODEC,BoolButtonPacket::handle);
        registrar.playBidirectional(DirectionButtonPayload.TYPE, DirectionButtonPayload.STREAM_CODEC,DirectionButtonPacket::handle);
        registrar.playToServer(TankBoolPacketPayload.TYPE, TankBoolPacketPayload.STREAM_CODEC,TankBoolPacket::handle);
        registrar.playToServer(TankDirectionPayload.TYPE, TankDirectionPayload.STREAM_CODEC,TankDirectionPacket::handle);
        registrar.playToServer(BatteryBoxSendOutPowerPayload.TYPE, BatteryBoxSendOutPowerPayload.STREAM_CODEC,BatteryBoxSendOutPowerPacket::handle);
        registrar.playToClient(ClientBoundFluidDataPayload.TYPE, ClientBoundFluidDataPayload.STREAM_CODEC,ClientBoundFluidDataPacket::handle);
    }
}
