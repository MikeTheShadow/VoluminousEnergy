package com.veteam.voluminousenergy.datagen;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;

public class VETagDataGenerator extends BlockTagsProvider {
    private static ArrayList<Block> mineableWithAxe = new ArrayList<>();
    private static ArrayList<Block> mineableWithPickaxe = new ArrayList<>();
    private static ArrayList<Block> mineableWithHoe = new ArrayList<>();
    private static ArrayList<Block> mineableWithShovel = new ArrayList<>();

    private static ArrayList<Block> requiresWood = new ArrayList<>();
    private static ArrayList<Block> requiresStone = new ArrayList<>();
    private static ArrayList<Block> requiresIron = new ArrayList<>();
    private static ArrayList<Block> requiresDiamond = new ArrayList<>();
    private static ArrayList<Block> requiresNetherite = new ArrayList<>();
    private static ArrayList<Block> requiresNighalite = new ArrayList<>();
    private static ArrayList<Block> requiresEighzo = new ArrayList<>();
    private static ArrayList<Block> requiresSolarium = new ArrayList<>();

    public VETagDataGenerator(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, VoluminousEnergy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        // Setup Forge tags
        final Tags.IOptionalNamedTag<Block> NEEDS_WOOD_TOOL = BlockTags.createOptional(new ResourceLocation("forge", "needs_wood_tool"));
        final Tags.IOptionalNamedTag<Block> NEEDS_NETHERITE_TOOL = BlockTags.createOptional(new ResourceLocation("forge", "needs_netherite_tool"));
        final Tags.IOptionalNamedTag<Block> NEEDS_NIGHALITE_TOOL = BlockTags.createOptional(new ResourceLocation(VoluminousEnergy.MODID, "needs_nighalite_tool"));
        final Tags.IOptionalNamedTag<Block> NEEDS_EIGHZO_TOOL = BlockTags.createOptional(new ResourceLocation(VoluminousEnergy.MODID, "needs_eighzo_tool"));
        final Tags.IOptionalNamedTag<Block> NEEDS_SOLARIUM_TOOL = BlockTags.createOptional(new ResourceLocation(VoluminousEnergy.MODID, "needs_solarium_tool"));

        // To Mine With Tool
        mineableWithAxe.forEach(toMineWithAxe -> tag(BlockTags.MINEABLE_WITH_AXE).add(toMineWithAxe));
        mineableWithPickaxe.forEach(toMineWithPickaxe -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(toMineWithPickaxe));
        mineableWithHoe.forEach(toMineWithHoe -> tag(BlockTags.MINEABLE_WITH_HOE).add(toMineWithHoe));
        mineableWithShovel.forEach(toMineWithShovel -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(toMineWithShovel));

        requiresWood.forEach(needsWood -> tag(NEEDS_WOOD_TOOL).add(needsWood));
        requiresStone.forEach(needsStone -> tag(BlockTags.NEEDS_STONE_TOOL).add(needsStone));
        requiresIron.forEach(needsIron -> tag(BlockTags.NEEDS_IRON_TOOL).add(needsIron));
        requiresDiamond.forEach(needsDiamond -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(needsDiamond));
        requiresNetherite.forEach(needsNetherite -> tag(NEEDS_NETHERITE_TOOL).add(needsNetherite));
        requiresNighalite.forEach(needsNighalite -> tag(NEEDS_NIGHALITE_TOOL).add(needsNighalite));
        requiresEighzo.forEach(needsEighzo -> tag(NEEDS_EIGHZO_TOOL).add(needsEighzo));
        requiresSolarium.forEach(needsSolarium -> tag(NEEDS_SOLARIUM_TOOL).add(needsSolarium));
    }

    @Deprecated
    public static void addTierBasedOnInt(int tier, Block block){
        switch (tier) {
            case 0 -> requiresWood.add(block);
            case 1 -> requiresStone.add(block);
            case 2 -> requiresIron.add(block);
            case 3 -> requiresDiamond.add(block);
            case 4 -> requiresNetherite.add(block);
            case 5 -> requiresNighalite.add(block);
            case 6 -> requiresEighzo.add(block);
            case 7 -> requiresSolarium.add(block);
        }
    }

    // Tier setter
    public static void setRequiresWood(Block block){
        requiresWood.add(block);
    }

    public static void setRequiresStone(Block block){
        requiresStone.add(block);
    }

    public static void setRequiresIron(Block block){
        requiresIron.add(block);
    }

    public static void setRequiresDiamond(Block block){
        requiresDiamond.add(block);
    }

    public static void setRequiresNetherite(Block block){
        requiresNetherite.add(block);
    }

    public static void setRequiresNighalite(Block block){
        requiresNighalite.add(block);
    }

    public static void setRequiresEighzo(Block block){
        requiresEighzo.add(block);
    }

    public static void setRequiresSolarium(Block block){
        requiresSolarium.add(block);
    }

    // Tools setter
    public static void setRequiresAxe(Block block){
        mineableWithAxe.add(block);
    }

    public static void setRequiresPickaxe(Block block){
        mineableWithPickaxe.add(block);
    }

    public static void setRequiresHoe(Block block){
        mineableWithHoe.add(block);
    }

    public static void setRequiresShovel(Block block){
        mineableWithShovel.add(block);
    }

    @Override
    public String getName() {
        return "Voluminous Energy Block Tool Tags";
    }
}
