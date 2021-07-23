package com.veteam.voluminousenergy.setup;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IProxy {

    void init();

    Level getClientWorld();

    Player getClientPlayer();
}
