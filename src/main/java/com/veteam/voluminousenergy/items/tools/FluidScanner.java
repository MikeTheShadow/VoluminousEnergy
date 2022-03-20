package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

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
        ServerChunkCache serverchunkcache = serverLevel.getChunkSource();
        ChunkGenerator chunkgenerator = serverchunkcache.getGenerator();
        Climate.Sampler climateSampler = chunkgenerator.climateSampler();
        Climate.TargetPoint targetPoint = climateSampler.sample(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        double continentalness = targetPoint.continentalness();
        double erosion = Climate.unquantizeCoord(targetPoint.erosion());
        double humidity = Climate.unquantizeCoord(targetPoint.humidity());
        double temperature = Climate.unquantizeCoord(targetPoint.temperature());

        String message = "C: " + continentalness
                + " E: " + erosion
                + " H: " + humidity
                + " T: " + temperature;

        player.sendMessage(new TextComponent("Scanning..."), player.getUUID());
        player.sendMessage(new TextComponent(message), player.getUUID());


//        chunkFluids = serverLevel.getDataStorage().computeIfAbsent((compoundTag)
//                -> ChunkFluids.load(serverLevel, compoundTag),
//                () -> new ChunkFluids(serverLevel),
//                ChunkFluids.getFileId(serverLevel.dimensionTypeRegistration()));
//
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
