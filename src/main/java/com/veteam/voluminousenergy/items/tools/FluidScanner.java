package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraft.world.level.material.Fluids.LAVA;
import static net.minecraft.world.level.material.Fluids.WATER;

public class FluidScanner extends Item {

    private static ChunkFluids chunkFluids;

    public FluidScanner() {
        super(new Item.Properties()
                .stacksTo(1)
                .tab(VESetup.itemGroup)
                .rarity(Rarity.UNCOMMON)
        );
        setRegistryName("fluid_scanner");
    }

    public int getUseDuration(ItemStack p_40680_) {
        return 72000;
    }

    public @NotNull UseAnim getUseAnimation(ItemStack p_40678_) {
        return UseAnim.CROSSBOW;
    }

    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {

        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        ChunkAccess chunkAccess = level.getChunk(blockpos);


        Player player = useOnContext.getPlayer();

        if (player == null || level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);

        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());
        /*
        String message = "C: " + continentalness
                + " E: " + erosion
                + " H: " + humidity
                + " T: " + temperature;

        */

        BlockPos pos = new BlockPos(16 * chunkAccess.getPos().x, 320, 16 * chunkAccess.getPos().z);

        HashMap<WorldUtil.ClimateParameters, Double> climateMap = WorldUtil.sampleClimate(level, pos);
        StringBuilder climateString = new StringBuilder();
        climateString.append("\nC: " + climateMap.get(WorldUtil.ClimateParameters.CONTINENTALNESS));
        climateString.append("\nE: " + climateMap.get(WorldUtil.ClimateParameters.EROSION));
        climateString.append("\nH: " + climateMap.get(WorldUtil.ClimateParameters.HUMIDITY));
        climateString.append("\nT: " + climateMap.get(WorldUtil.ClimateParameters.TEMPERATURE));

//        chunkFluids = serverLevel.getDataStorage().computeIfAbsent((compoundTag)
//                -> ChunkFluids.load(serverLevel, compoundTag),
//                () -> new ChunkFluids(serverLevel),
//                ChunkFluids.getFileId(serverLevel.dimensionTypeRegistration()));

        player.sendMessage(new TextComponent("Scanning..."), player.getUUID());
        //player.sendMessage(new TextComponent(message), player.getUUID());
        player.sendMessage(Component.nullToEmpty(climateString.toString()), player.getUUID());

        ArrayList<Pair<Fluid, Integer>> fluidsList = WorldUtil.queryForFluids(level, pos);
        StringBuilder message = new StringBuilder();
        for (Pair<Fluid, Integer> pair : fluidsList) {
            message.append("\nFound Entry: ").append(pair.getA().getRegistryName()).append(" Amount: ").append(pair.getB());
        }
        player.sendMessage(Component.nullToEmpty(message.toString()), player.getUUID());

        StringBuilder builder = new StringBuilder("______________MAP______________\n");

        int mapSize = 16;
        int middle = mapSize / 2;

        for (int x = 1; x < mapSize; x++) {
            for (int z = 1; z < mapSize; z++) {
                pos = new BlockPos(
                        16 * (chunkAccess.getPos().x - middle + x),
                        320,
                        16 * (chunkAccess.getPos().z - middle + z));
                var items = WorldUtil.queryForFluids(level, pos);
                if (items.size() > 0) {

                    Fluid fluid = items.get(0).getA();
                    if(fluid.isSame(VEFluids.CRUDE_OIL_REG.get().getFlowing())) {
                        builder.append(" C |");
                    } else if(fluid.isSame(WATER.getFlowing())) {
                        builder.append(" W |");
                    } else if(fluid.isSame(LAVA.getFlowing())) {
                        builder.append(" L |");
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

//        ChunkFluid chunkFluid = chunkFluids.getOrCreateChunkFluid(serverLevel,new ChunkPos(blockpos));
//
//        FluidStack fluid = chunkFluid.getFluid();

        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return true;
    }
}
