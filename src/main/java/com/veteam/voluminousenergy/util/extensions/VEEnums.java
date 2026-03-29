package com.veteam.voluminousenergy.util.extensions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class VEEnums {
    public static final EnumProxy<Rarity> ELECTRONIC_RARITY = new EnumProxy<>(
        Rarity.class,
        -1, // -1 tells the system to automatically assign the next available ID
        "voluminousenergy:electronic",
        (UnaryOperator<Style>) style -> style.applyFormat(ChatFormatting.GREEN)
    );
}
