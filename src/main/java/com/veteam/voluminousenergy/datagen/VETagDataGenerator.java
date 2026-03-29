package com.veteam.voluminousenergy.datagen;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class VETagDataGenerator extends BlockTagsProvider {
    // Whitelist for type of tool in use
    private static ArrayList<Block> mineableWithAxe = new ArrayList<>();
    private static ArrayList<Block> mineableWithPickaxe = new ArrayList<>();
    private static ArrayList<Block> mineableWithHoe = new ArrayList<>();
    private static ArrayList<Block> mineableWithShovel = new ArrayList<>();

    // Whitelist for tier in use
    private static ArrayList<Block> requiresWood = new ArrayList<>();
    private static ArrayList<Block> requiresStone = new ArrayList<>();
    private static ArrayList<Block> requiresIron = new ArrayList<>();
    private static ArrayList<Block> requiresDiamond = new ArrayList<>();
    private static ArrayList<Block> requiresNetherite = new ArrayList<>();
    private static ArrayList<Block> requiresNighalite = new ArrayList<>();
    private static ArrayList<Block> requiresEighzo = new ArrayList<>();
    private static ArrayList<Block> requiresSolarium = new ArrayList<>();

    // Blacklist for tier in use
    private static ArrayList<Block> incorrectForWood = new ArrayList<>();
    private static ArrayList<Block> incorrectForGold = new ArrayList<>();
    private static ArrayList<Block> incorrectForStone = new ArrayList<>();
    private static ArrayList<Block> incorrectForIron = new ArrayList<>();
    private static ArrayList<Block> incorrectForDiamond = new ArrayList<>();
    private static ArrayList<Block> incorrectForTitanium = new ArrayList<>();
    private static ArrayList<Block> incorrectForTungsten = new ArrayList<>();
    private static ArrayList<Block> incorrectForNetherite = new ArrayList<>();
    private static ArrayList<Block> incorrectForTungstensteel = new ArrayList<>();
    private static ArrayList<Block> incorrectForNighalite = new ArrayList<>();
    private static ArrayList<Block> incorrectForEighzo = new ArrayList<>();
    private static ArrayList<Block> incorrectForSolarium = new ArrayList<>();

    public VETagDataGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, VoluminousEnergy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // Setup Needs Tags
        final TagKey<Block> NEEDS_WOOD_TOOL = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("needs_wood_tool"));
        final TagKey<Block> NEEDS_NETHERITE_TOOL = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("needs_netherite_tool"));
        final TagKey<Block> NEEDS_NIGHALITE_TOOL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "needs_nighalite_tool"));
        final TagKey<Block> NEEDS_EIGHZO_TOOL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "needs_eighzo_tool"));
        final TagKey<Block> NEEDS_SOLARIUM_TOOL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "needs_solarium_tool"));

        // Setup Incorrect tags
        final TagKey<Block> INCORRECT_FOR_WOOD = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("incorrect_for_wood_tool"));
        final TagKey<Block> INCORRECT_FOR_GOLD = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("incorrect_for_gold_tool"));
        final TagKey<Block> INCORRECT_FOR_STONE = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("incorrect_for_stone_tool"));
        final TagKey<Block> INCORRECT_FOR_IRON = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("incorrect_for_iron_tool"));
        final TagKey<Block> INCORRECT_FOR_DIAMOND = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("incorrect_for_diamond_tool"));
        final TagKey<Block> INCORRECT_FOR_TITANIUM = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_titanium_tool"));
        final TagKey<Block> INCORRECT_FOR_TUNGSTEN = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_tungsten_tool"));
        final TagKey<Block> INCORRECT_FOR_NETHERITE = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("incorrect_for_netherite_tool"));
        final TagKey<Block> INCORRECT_FOR_TUNGSTENSTEEL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_tungstensteel_tool"));
        final TagKey<Block> INCORRECT_FOR_NIGHALITE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_nighalite_tool"));
        final TagKey<Block> INCORRECT_FOR_EIGHZO = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_eighzo_tool"));
        final TagKey<Block> INCORRECT_FOR_SOLARIUM = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "incorrect_for_solarium_tool"));

        // To Mine With Tool
        mineableWithAxe.forEach(toMineWithAxe -> tag(BlockTags.MINEABLE_WITH_AXE).add(toMineWithAxe));
        mineableWithPickaxe.forEach(toMineWithPickaxe -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(toMineWithPickaxe));
        mineableWithHoe.forEach(toMineWithHoe -> tag(BlockTags.MINEABLE_WITH_HOE).add(toMineWithHoe));
        mineableWithShovel.forEach(toMineWithShovel -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(toMineWithShovel));

        // Needs tier
        requiresWood.forEach(needsWood -> tag(NEEDS_WOOD_TOOL).add(needsWood));
        requiresStone.forEach(needsStone -> tag(BlockTags.NEEDS_STONE_TOOL).add(needsStone));
        requiresIron.forEach(needsIron -> tag(BlockTags.NEEDS_IRON_TOOL).add(needsIron));
        requiresDiamond.forEach(needsDiamond -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(needsDiamond));
        requiresNetherite.forEach(needsNetherite -> tag(NEEDS_NETHERITE_TOOL).add(needsNetherite));
        requiresNighalite.forEach(needsNighalite -> tag(NEEDS_NIGHALITE_TOOL).add(needsNighalite));
        requiresEighzo.forEach(needsEighzo -> tag(NEEDS_EIGHZO_TOOL).add(needsEighzo));
        requiresSolarium.forEach(needsSolarium -> tag(NEEDS_SOLARIUM_TOOL).add(needsSolarium));

        // Incorrect for
        incorrectForWood.forEach(incorrectWood -> tag(INCORRECT_FOR_WOOD).add(incorrectWood));
        incorrectForGold.forEach(incorrectGold -> tag(INCORRECT_FOR_GOLD).add(incorrectGold));
        incorrectForStone.forEach(incorrectStone -> tag(INCORRECT_FOR_STONE).add(incorrectStone));
        incorrectForIron.forEach(incorrectIron -> tag(INCORRECT_FOR_IRON).add(incorrectIron));
        incorrectForDiamond.forEach(incorrectDiamond -> tag(INCORRECT_FOR_DIAMOND).add(incorrectDiamond));
        incorrectForTitanium.forEach(incorrectTitanium -> tag(INCORRECT_FOR_TITANIUM).add(incorrectTitanium));
        incorrectForNetherite.forEach(incorrectNetherite -> tag(INCORRECT_FOR_NETHERITE).add(incorrectNetherite));
        incorrectForTungstensteel.forEach(incorrectTungstensteel -> tag(INCORRECT_FOR_TUNGSTENSTEEL).add(incorrectTungstensteel));
        incorrectForNighalite.forEach(incorrectNighalite -> tag(INCORRECT_FOR_NIGHALITE).add(incorrectNighalite));
        incorrectForEighzo.forEach(incorrectEighzo -> tag(INCORRECT_FOR_EIGHZO).add(incorrectEighzo));
        incorrectForSolarium.forEach(incorrectSolarium -> tag(INCORRECT_FOR_SOLARIUM).add(incorrectSolarium));

    }

    // Tier setter
    public static void setRequiresWoodAndBlacklistLowerTiers(Block block) {
        requiresWood.add(block);
    }

    public static void setRequiresStoneAndBlacklistLowerTiers(Block block) {
        requiresStone.add(block);
        incorrectForGold(block);
    }

    public static void setRequiresIronAndBlacklistLowerTiers(Block block) {
        requiresIron.add(block);
        incorrectForStone(block);
    }

    public static void setRequiresDiamondAndBlacklistLowerTiers(Block block) {
        requiresDiamond.add(block);
        incorrectForIron(block);
    }

    public static void setRequiresNetheriteAndBlacklistLowerTiers(Block block) {
        requiresNetherite.add(block);
        incorrectForTungsten(block);
    }

    public static void setRequiresNighaliteAndBlacklistLowerTiers(Block block) {
        requiresNighalite.add(block);
        incorrectForTungstensteel(block);
    }

    public static void setRequiresEighzoAndBlacklistLowerTiers(Block block) {
        requiresEighzo.add(block);
        incorrectForNighalite(block);
    }

    public static void setRequiresSolariumAndBlacklistLowerTiers(Block block) {
        requiresSolarium.add(block);
        incorrectForEighzo(block);
    }


    static void incorrectForEighzo(Block block) {
        incorrectForEighzo.add(block);
        incorrectForNighalite(block);
    }

    static void incorrectForNighalite(Block block) {
        incorrectForNighalite.add(block);
        incorrectForTungstensteel(block);
    }

    static void incorrectForTungstensteel(Block block) {
        incorrectForTungstensteel.add(block);
        incorrectForNetherite(block);
    }

    static void incorrectForNetherite(Block block) {
        incorrectForNetherite.add(block);
        incorrectForTungsten(block);
    }

    static void incorrectForTungsten(Block block) {
        incorrectForTungsten.add(block);
        incorrectForTitanium(block);
    }

    static void incorrectForTitanium(Block block) {
        incorrectForTitanium.add(block);
        incorrectForDiamond(block);
    }

    static void incorrectForDiamond(Block block) {
        incorrectForDiamond.add(block);
        incorrectForIron(block);
    }

    static void incorrectForIron(Block block) {
        incorrectForIron.add(block);
        incorrectForStone(block);
    }

    static void incorrectForStone(Block block) {
        incorrectForStone.add(block);
        incorrectForGold(block);
    }

    static void incorrectForGold(Block block) {
        incorrectForGold.add(block);
        incorrectForWood.add(block);
    }

    static void incorrectForWood(Block block) {
        incorrectForWood.add(block);
    }

    // Tools setter
    public static void setRequiresAxe(Block block) {
        mineableWithAxe.add(block);
    }

    public static void setRequiresPickaxe(Block block) {
        mineableWithPickaxe.add(block);
    }

    public static void setRequiresHoe(Block block) {
        mineableWithHoe.add(block);
    }

    public static void setRequiresShovel(Block block) {
        mineableWithShovel.add(block);
    }

    @Override
    public String getName() {
        return "Voluminous Energy Block Tool Tags";
    }
}
