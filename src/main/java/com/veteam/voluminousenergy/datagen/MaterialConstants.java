package com.veteam.voluminousenergy.datagen;

import net.minecraft.world.level.block.Block;

public class MaterialConstants {

    public static void setBauxiteTier(Block bauxiteBlock){
        VETagDataGenerator.setRequiresStone(bauxiteBlock);
    }

    public static void setCinnabarTier(Block cinnabarBlock){
        VETagDataGenerator.setRequiresIron(cinnabarBlock);
    }

    public static void setGalenaTier(Block galenaBlock){
        VETagDataGenerator.setRequiresIron(galenaBlock);
    }

    public static void setRutileTier(Block rutileBlock){
        VETagDataGenerator.setRequiresDiamond(rutileBlock);
    }

    public static void setNighaliteTier(Block nighaliteBlock){
        VETagDataGenerator.setRequiresNetherite(nighaliteBlock);
    }

    public static void setEighzoTier(Block eighzoBlock){
        VETagDataGenerator.setRequiresNighalite(eighzoBlock);
    }
}
