package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.flowingFluidBlocks.FumingAcidFlowingFluidBlock;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class WhiteFumingNitricAcid {
    public static final ResourceLocation WFNA_STILL_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/wfna_still");
    public static final ResourceLocation WFNA_FLOWING_TEXTURE = new ResourceLocation(VoluminousEnergy.MODID,"/block/fluids/wfna_flowing");

    public static Block.Properties stdProp = Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable();

    public static FlowingFluid WHITE_FUMING_NITRIC_ACID;
    public static FlowingFluid FLOWING_WHITE_FUMING_NITRIC_ACID;
    public static FumingAcidFlowingFluidBlock WHITE_FUMING_NITRIC_ACID_BLOCK;
    public static Item WHITE_FUMING_NITRIC_ACID_BUCKET;

    public static FlowingFluid WhiteFumingNitricAcidFluid(){
        WHITE_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Source(WhiteFumingNitricAcid.properties);
        return WHITE_FUMING_NITRIC_ACID;
    }

    public static FlowingFluid FlowingWhiteFumingNitricAcidFluid(){
        FLOWING_WHITE_FUMING_NITRIC_ACID = new ForgeFlowingFluid.Flowing(WhiteFumingNitricAcid.properties);
        return FLOWING_WHITE_FUMING_NITRIC_ACID;
    }

    public static FumingAcidFlowingFluidBlock FlowingWhiteFumingNitricAcidBlock(){
        WHITE_FUMING_NITRIC_ACID_BLOCK = new FumingAcidFlowingFluidBlock(() -> WHITE_FUMING_NITRIC_ACID, stdProp);
        return WHITE_FUMING_NITRIC_ACID_BLOCK;
    }

    public static Item WhiteFumingNitricAcidBucket(){
        WHITE_FUMING_NITRIC_ACID_BUCKET = new BucketItem(() -> WHITE_FUMING_NITRIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VESetup.itemGroup));
        return WHITE_FUMING_NITRIC_ACID_BUCKET;
    }


    public static final FluidType WFNA_FLUID_TYPE = new VEFluidType(FluidType.Properties.create()
            .adjacentPathType(BlockPathTypes.DAMAGE_FIRE)
            .canConvertToSource(false)
            .canDrown(true)
            .canExtinguish(false)
            .canHydrate(false)
            .canPushEntity(false)
            .canConvertToSource(false)
            .canSwim(false)
            .lightLevel(0)
            .density(1)
            .temperature(300)
            .viscosity(1)
            .motionScale(0)
            .fallDistanceModifier(0)
            .rarity(Rarity.COMMON)
            .supportsBoating(false)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
            WFNA_STILL_TEXTURE,
            WFNA_FLOWING_TEXTURE
    );

    public static final ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> WFNA_FLUID_TYPE, () -> WHITE_FUMING_NITRIC_ACID, () -> FLOWING_WHITE_FUMING_NITRIC_ACID)
            .block(() -> WHITE_FUMING_NITRIC_ACID_BLOCK).bucket(() -> WHITE_FUMING_NITRIC_ACID_BUCKET);

}