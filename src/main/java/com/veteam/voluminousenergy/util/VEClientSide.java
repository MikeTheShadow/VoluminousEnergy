package com.veteam.voluminousenergy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VEClientSide {

    public static ClientLevel getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

}
