package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.ClientBoundFluidDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.eventbus.api.EventPriority;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
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
        ClientBoundFluidDataPacket
                packet = new ClientBoundFluidDataPacket(CombustibleFluidsData.getDataForNetworkTransfer(),OxidizerFluidsData.getDataForNetworkTransfer());
        VENetwork.channel.send(packet, PacketDistributor.ALL.noArg());
    }

    private static void updateOnePlayer(ServerPlayer player) {
        ClientBoundFluidDataPacket
                packet = new ClientBoundFluidDataPacket(CombustibleFluidsData.getDataForNetworkTransfer(),OxidizerFluidsData.getDataForNetworkTransfer());
        VENetwork.channel.send(packet, player.connection.getConnection());
    }
}
