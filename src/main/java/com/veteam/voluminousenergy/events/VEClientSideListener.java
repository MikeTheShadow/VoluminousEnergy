package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.server.level.ServerLevel;
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
        VoluminousEnergy.LOGGER.info("Loaded chunk fluids!");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDataPackSync(RecipesUpdatedEvent event) {
        VoluminousEnergy.LOGGER.info("Finalizing recipe cache on client!");
        VERecipe.updateCache();
    }
}
