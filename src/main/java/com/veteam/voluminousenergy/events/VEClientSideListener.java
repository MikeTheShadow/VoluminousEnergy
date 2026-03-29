package com.veteam.voluminousenergy.events;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.*;
import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.extensions.VEFluidClientExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = VoluminousEnergy.MODID, value = Dist.CLIENT)
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(Oxygen.OXYGEN_FLUID_TYPE.getFluidClientExtension(), VEFluids.OXYGEN_FLUID_TYPE_REG.get());
        event.registerFluidType(CrudeOil.CRUDE_OIL_FLUID_TYPE.getFluidClientExtension(), VEFluids.CRUDE_OIL_FLUID_TYPE_REG.get());
        event.registerFluidType(Naphtha.NAPHTHA_FLUID_TYPE.getFluidClientExtension(), VEFluids.NAPHTHA_FLUID_TYPE_REG.get());
        event.registerFluidType(RedFumingNitricAcid.RFNA_FLUID_TYPE.getFluidClientExtension(), VEFluids.RFNA_FLUID_TYPE_REG.get());
        event.registerFluidType(WhiteFumingNitricAcid.WFNA_FLUID_TYPE.getFluidClientExtension(), VEFluids.WFNA_FLUID_TYPE_REG.get());
        event.registerFluidType(Mercury.MERCURY_FLUID_TYPE.getFluidClientExtension(), VEFluids.MERCURY_FLUID_TYPE_REG.get());
        event.registerFluidType(SulfuricAcid.SULFURIC_ACID_FLUID_TYPE.getFluidClientExtension(), VEFluids.SULFURIC_ACID_FLUID_TYPE_REG.get());
        event.registerFluidType(DinitrogenTetroxide.DINITROGEN_TETROXIDE_FLUID_TYPE.getFluidClientExtension(), VEFluids.DINITROGEN_TETROXIDE_FLUID_TYPE_REG.get());
        event.registerFluidType(CompressedAir.COMPRESSED_AIR_FLUID_TYPE.getFluidClientExtension(), VEFluids.COMPRESSED_AIR_FLUID_TYPE_REG.get());
        event.registerFluidType(Nitrogen.NITROGEN_FLUID_TYPE.getFluidClientExtension(), VEFluids.NITROGEN_FLUID_TYPE_REG.get());
        event.registerFluidType(Biofuel.BIOFUEL_FLUID_TYPE.getFluidClientExtension(), VEFluids.BIOFUEL_FLUID_TYPE_REG.get());
        event.registerFluidType(Diesel.DIESEL_FLUID_TYPE.getFluidClientExtension(), VEFluids.DIESEL_FLUID_TYPE_REG.get());
        event.registerFluidType(Gasoline.GASOLINE_FLUID_TYPE.getFluidClientExtension(), VEFluids.GASOLINE_FLUID_TYPE_REG.get());
        event.registerFluidType(Nitroglycerin.NITROGLYCERIN_FLUID_TYPE.getFluidClientExtension(), VEFluids.NITROGLYCERIN_FLUID_TYPE_REG.get());
        event.registerFluidType(LightFuel.LIGHT_FUEL_FLUID_TYPE.getFluidClientExtension(), VEFluids.LIGHT_FUEL_FLUID_TYPE_REG.get());
        event.registerFluidType(LiquefiedCoal.LIQUEFIED_COAL_FLUID_TYPE.getFluidClientExtension(), VEFluids.LIQUEFIED_COAL_TYPE_REG.get());
        event.registerFluidType(LiquefiedCoke.LIQUEFIED_COKE_FLUID_TYPE.getFluidClientExtension(), VEFluids.LIQUEFIED_COKE_FLUID_TYPE_REG.get());
        event.registerFluidType(TreeSap.TREE_SAP_FLUID_TYPE.getFluidClientExtension(), VEFluids.TREE_SAP_FLUID_TYPE_REG.get());
        event.registerFluidType(Treethanol.TREETHANOL_FLUID_TYPE.getFluidClientExtension(), VEFluids.TREETHANOL_FLUID_TYPE_REG.get());
        event.registerFluidType(Ammonia.AMMONIA_FLUID_TYPE.getFluidClientExtension(), VEFluids.AMMONIA_FLUID_TYPE_REG.get());
        event.registerFluidType(AmmoniumNitrateSolution.AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE.getFluidClientExtension(), VEFluids.AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE_REG.get());
        event.registerFluidType(Hydrogen.HYDROGEN_FLUID_TYPE.getFluidClientExtension(), VEFluids.HYDROGEN_FLUID_TYPE_REG.get());
    }
}
