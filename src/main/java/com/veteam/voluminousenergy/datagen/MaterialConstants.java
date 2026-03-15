package com.veteam.voluminousenergy.datagen;

import net.minecraft.world.level.block.Block;

public class MaterialConstants {

    public static void setBauxiteTier(Block bauxiteBlock) {
        VETagDataGenerator.setRequiresStoneAndBlacklistLowerTiers(bauxiteBlock);
    }

    public static void setCinnabarTier(Block cinnabarBlock) {
        VETagDataGenerator.setRequiresIronAndBlacklistLowerTiers(cinnabarBlock);
    }

    public static void setGalenaTier(Block galenaBlock) {
        VETagDataGenerator.setRequiresIronAndBlacklistLowerTiers(galenaBlock);
    }

    public static void setRutileTier(Block rutileBlock) {
        VETagDataGenerator.setRequiresDiamondAndBlacklistLowerTiers(rutileBlock);
    }

    public static void setNighaliteTier(Block nighaliteBlock) {
        VETagDataGenerator.setRequiresNetheriteAndBlacklistLowerTiers(nighaliteBlock);
    }

    public static void setEighzoTier(Block eighzoBlock) {
        VETagDataGenerator.setRequiresNighaliteAndBlacklistLowerTiers(eighzoBlock);
    }
}
