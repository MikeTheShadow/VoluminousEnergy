package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class VEServerSideListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDataPackSync(OnDatapackSyncEvent event) {

        MinecraftServer server;
        // When event.getPlayer() != null it's a player joining, so we check to make sure it's a group reload
        if (event.getPlayer() == null) {
            server = event.getPlayer().getServer();
            VERecipe.updateCache();
        } else {
            server = event.getPlayerList().getServer();
        }

        doDataProcess(server);
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        ChunkFluids.loadInstance(level);
        VERecipe.updateCache();
        doDataProcess(event.getServer());
    }

    /**
     * Register our data processors here for the server
     * @param server The minecraft server to process with
     */
    private static void doDataProcess(MinecraftServer server) {
        ResourceManager manager = server.getResourceManager();
        CombustibleFluidsData.loadData(manager);
        OxidizerFluidsData.loadData(manager);
        VoluminousEnergy.LOGGER.info("Finished data processing!");
    }
}
