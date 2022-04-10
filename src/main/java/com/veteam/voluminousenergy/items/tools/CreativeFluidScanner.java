package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraft.world.level.material.Fluids.LAVA;
import static net.minecraft.world.level.material.Fluids.WATER;

public class CreativeFluidScanner extends Item {

    public CreativeFluidScanner() {
        super(new Item.Properties()
                .stacksTo(1)
                .tab(VESetup.itemGroup)
                .rarity(Rarity.RARE)
        );
        setRegistryName("creative_fluid_scanner");
    }

    public int getUseDuration(ItemStack itemStack) {
        return 72_000;
    }

    public @NotNull UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.CROSSBOW;
    }

    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {

        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        ChunkAccess chunkAccess = level.getChunk(blockpos);
        Player player = useOnContext.getPlayer();

        // Return if on client side
        if (player == null || level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);

        // Get blockPos to query, AKA chunk corner
        BlockPos pos = new BlockPos(16 * chunkAccess.getPos().x, 320, 16 * chunkAccess.getPos().z);

        // Sample climate and print out paramaters that we use
        HashMap<WorldUtil.ClimateParameters, Double> climateMap = WorldUtil.sampleClimate(level, pos);
        StringBuilder climateString = new StringBuilder();
        climateString.append("\nC: " + climateMap.get(WorldUtil.ClimateParameters.CONTINENTALNESS));
        climateString.append("\nE: " + climateMap.get(WorldUtil.ClimateParameters.EROSION));
        climateString.append("\nH: " + climateMap.get(WorldUtil.ClimateParameters.HUMIDITY));
        climateString.append("\nT: " + climateMap.get(WorldUtil.ClimateParameters.TEMPERATURE));

        // Print out message to chat
        player.sendMessage(new TextComponent("Scanning..."), player.getUUID());
        player.sendMessage(Component.nullToEmpty(climateString.toString()), player.getUUID());

        ArrayList<Pair<Fluid, Integer>> fluidsList = WorldUtil.queryForFluids(level, pos);
        StringBuilder message = new StringBuilder();

        Fluid fluid = Fluids.EMPTY;

        for (Pair<Fluid, Integer> pair : fluidsList) {
            //fluid = pair.getA();
            message.append("\nFound Entry: ").append(pair.getA().getRegistryName()).append(" Amount: ").append(pair.getB());
        }
        player.sendMessage(Component.nullToEmpty(message.toString()), player.getUUID());

        // MAP START
        StringBuilder builder = new StringBuilder("______________MAP______________\n");

        int mapSize = 16;
        int middle = mapSize / 2;

        for (int x = 1; x < mapSize; x++) {
            for (int z = 1; z < mapSize; z++) {
                pos = new BlockPos(
                        16 * (chunkAccess.getPos().x - middle + x),
                        320,
                        16 * (chunkAccess.getPos().z - middle + z));
                ArrayList<Pair<Fluid,Integer>> items = WorldUtil.queryForFluids(level, pos);
                if (items.size() > 0) {

                    fluid = items.get(0).getA();

                    if(fluid.isSame(VEFluids.CRUDE_OIL_REG.get().getFlowing())) {
                        builder.append(" C |");
                    } else if(fluid.isSame(WATER.getFlowing())) {
                        builder.append(" W |");
                    } else if(fluid.isSame(LAVA.getFlowing())) {
                        builder.append(" L |");
                    } else if (fluid.isSame(VEFluids.LIGHT_FUEL_REG.get().getFlowing())) {
                        builder.append(" F |");
                    } else {
                        builder.append(" ? |");
                    }

                } else {
                    builder.append(" 0 |");
                }
            }
            builder.append("\n");
        }
        player.sendMessage(new TextComponent(builder.toString()), player.getUUID());
        // MAP END

        return InteractionResult.sidedSuccess(false);
    }
}
