package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = VoluminousEnergy.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class VEClientSideListener {

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        ChunkFluids.loadInstance(level);
        doDataProcess(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDataPackSync(RecipesUpdatedEvent event) {
        VERecipe.updateCache(event.getRecipeManager());
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            doDataProcess(server);
        }
    }

    /**
     * Register our data processors here for the server
     *
     * @param server The minecraft server to process with
     */
    private static void doDataProcess(MinecraftServer server) {
        ResourceManager manager = server.getResourceManager();
        CombustibleFluidsData.loadData(manager);
        OxidizerFluidsData.loadData(manager);
    }


    /**
     *
     * @param renderEvent for player block highlighting
     */

    private static Block lastBlockCache = null;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerHighlightBlock(RenderHighlightEvent.Block renderEvent) {
        BlockHitResult result = renderEvent.getTarget();
        if (result.getType() == HitResult.Type.MISS) {
            Player player = Minecraft.getInstance().player;

            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(mainHand.getItem() instanceof Multitool multitool) {
                VoluminousEnergy.LOGGER.info("Setting as null!");
                multitool.setToolState(mainHand,null);
            }
            return;
        }

        BlockPos pos = result.getBlockPos();

        Player player = Minecraft.getInstance().player;

        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);

        if(mainHand.getItem() instanceof Multitool multitool) {

            BlockState block = Minecraft.getInstance().level.getBlockState(pos);

            if (block.getBlock() == lastBlockCache) return;
            lastBlockCache = block.getBlock();
            multitool.setToolState(mainHand,block);
        }
    }
}
