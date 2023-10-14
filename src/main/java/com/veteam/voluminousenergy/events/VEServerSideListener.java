package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.server.level.ServerLevel;
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
        // When event.getPlayer() != null it's a player joining, so we check to make sure it's a group reload
        if(event.getPlayer() == null) {
            VoluminousEnergy.LOGGER.info("Finalizing recipe cache on server reload!");
            VERecipe.updateCache();
        }
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        ChunkFluids.loadInstance(level);
        VoluminousEnergy.LOGGER.info("Loaded chunk fluids!");
        VoluminousEnergy.LOGGER.info("Finalizing recipe cache on server!");
        VERecipe.updateCache();
    }
}
