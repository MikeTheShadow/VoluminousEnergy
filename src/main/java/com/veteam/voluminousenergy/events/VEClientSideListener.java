package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class VEClientSideListener {

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        ChunkFluids.loadInstance(level);
        doDataProcess(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDataPackSync(RecipesUpdatedEvent event) {
        VERecipe.updateCache();
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            doDataProcess(server);
        }
    }

    /**
     * Register our data processors here for the server
     *
     * @param server The minecraft server to process with
     */
    private static void doDataProcess(MinecraftServer server) {
        ResourceManager manager = server.getResourceManager();
        CombustibleFluidsData.loadData(manager);
        OxidizerFluidsData.loadData(manager);
        VoluminousEnergy.LOGGER.info("Finished data processing!");
    }
}
