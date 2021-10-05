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
    public static ArrayList<Block> mineableWithAxe = new ArrayList<>();
    public static ArrayList<Block> mineableWithPickaxe = new ArrayList<>();
    public static ArrayList<Block> mineableWithHoe = new ArrayList<>();
    public static ArrayList<Block> mineableWithShovel = new ArrayList<>();

    public static ArrayList<Block> requiresWood = new ArrayList<>();
    public static ArrayList<Block> requiresStone = new ArrayList<>();
    public static ArrayList<Block> requiresIron = new ArrayList<>();
    public static ArrayList<Block> requiresDiamond = new ArrayList<>();
    public static ArrayList<Block> requiresNetherite = new ArrayList<>();
    public static ArrayList<Block> requiresNighalite = new ArrayList<>();
    public static ArrayList<Block> requiresEighzo = new ArrayList<>();
    public static ArrayList<Block> requiresSolarium = new ArrayList<>();

    public VETagDataGenerator(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, VoluminousEnergy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        // Setup Forge tags
        final Tags.IOptionalNamedTag<Block> NEEDS_WOOD_TOOL = BlockTags.createOptional(new ResourceLocation("forge", "needs_wood_tool"));
        final Tags.IOptionalNamedTag<Block> NEEDS_NETHERITE_TOOL = BlockTags.createOptional(new ResourceLocation("forge", "needs_netherite_tool"));

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
    }

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

    @Override
    public String getName() {
        return "Voluminous Energy Mineable Tags";
    }
}
