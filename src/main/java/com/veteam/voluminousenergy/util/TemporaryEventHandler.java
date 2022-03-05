package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;

public class TemporaryEventHandler {
    @SubscribeEvent
    public void startedEvent(ServerStartedEvent event) {
        event.getServer().reloadResources(Collections.singleton("mod:voluminousenergy")).exceptionally((throwable) -> {
            VoluminousEnergy.LOGGER.warn("Failed to fix JEI issues in TemporaryEventHandler", throwable);
            return null;
        });
    }
}