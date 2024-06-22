package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.networking.packets.ClientBoundFluidDataPacket;
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

@EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
public class VEServerSideListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDataPackSync(OnDatapackSyncEvent event) {

        MinecraftServer server;
        ServerPlayer player = event.getPlayer();
        // When event.getPlayer() != null it's a player joining, so we check to make sure it's a group reload
        if (player != null) {
            updateOnePlayer(player);
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
}
