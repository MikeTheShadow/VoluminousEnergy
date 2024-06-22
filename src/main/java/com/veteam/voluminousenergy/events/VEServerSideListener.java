package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class VEServerSideListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDataPackSync(OnDatapackSyncEvent event) {

        MinecraftServer server;
        ServerPlayer player = event.getPlayer();
        // When event.getPlayer() != null it's a player joining, so we check to make sure it's a group reload
        if (event.getPlayer() != null) {
            updateOnePlayer(event.getPlayer());
            return;
        }
        server = event.getPlayerList().getServer();
        VERecipe.updateCache();
        doDataProcess(server);
        updateAllPlayers();
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        ChunkFluids.loadInstance(level);
        VERecipe.updateCache();
        doDataProcess(event.getServer());
    }

    /**
     * Register our data processors here for the server.
     * Whenever a user joins we must first sync
     *
     * @param server The minecraft server to process with
     */
    private static void doDataProcess(MinecraftServer server) {
        ResourceManager manager = server.getResourceManager();
        CombustibleFluidsData.loadData(manager);
        OxidizerFluidsData.loadData(manager);
    }

    private static void updateAllPlayers() {
        PacketDistributor.sendToAllPlayers(
                new ClientBoundFluidDataPacket.ClientBoundFluidDataPayload(
                        CombustibleFluidsData.getDataForNetworkTransfer(),
                        OxidizerFluidsData.getDataForNetworkTransfer()));
    }

    private static void updateOnePlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,
                new ClientBoundFluidDataPacket.ClientBoundFluidDataPayload(
                        CombustibleFluidsData.getDataForNetworkTransfer(),
                        OxidizerFluidsData.getDataForNetworkTransfer()));
    }

    @SubscribeEvent
    public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("ve_1");
        registrar.playBidirectional(BoolButtonPacket.BoolButtonPayload.TYPE, BoolButtonPacket.BoolButtonPayload.STREAM_CODEC,BoolButtonPacket::handle);
        registrar.playBidirectional(DirectionButtonPacket.DirectionButtonPayload.TYPE, DirectionButtonPacket.DirectionButtonPayload.STREAM_CODEC,DirectionButtonPacket::handle);
        registrar.playToServer(TankBoolPacket.TankBoolPacketPayload.TYPE, TankBoolPacket.TankBoolPacketPayload.STREAM_CODEC,TankBoolPacket::handle);
        registrar.playToServer(TankDirectionPacket.TankDirectionPayload.TYPE, TankDirectionPacket.TankDirectionPayload.STREAM_CODEC,TankDirectionPacket::handle);
        registrar.playToServer(BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload.TYPE, BatteryBoxSendOutPowerPacket.BatteryBoxSendOutPowerPayload.STREAM_CODEC,BatteryBoxSendOutPowerPacket::handle);
        registrar.playToClient(ClientBoundFluidDataPacket.ClientBoundFluidDataPayload.TYPE, ClientBoundFluidDataPacket.ClientBoundFluidDataPayload.STREAM_CODEC,ClientBoundFluidDataPacket::handle);
    }
}
