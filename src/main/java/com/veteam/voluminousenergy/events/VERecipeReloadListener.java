package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class VERecipeReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        VoluminousEnergy.LOGGER.info("We are reloading right now!");
    }

}

