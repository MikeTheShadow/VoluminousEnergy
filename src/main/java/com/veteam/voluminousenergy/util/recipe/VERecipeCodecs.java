package com.veteam.voluminousenergy.util.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class VERecipeCodecs {
    private static final Codec<Item> ITEM_NONAIR_CODEC = ExtraCodecs.validate(BuiltInRegistries.ITEM.byNameCodec(), (instance) -> {
        return instance == Items.AIR ? DataResult.error(() -> {
            return "Crafting result must not be minecraft:air";
        }) : DataResult.success(instance);
    });

    public static final Codec<RegistryIngredient> VE_LAZY_INGREDIENT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "tag", "")
                        .forGetter(RegistryIngredient::tag),
                ExtraCodecs.strictOptionalField(Codec.STRING, "item", "")
                        .forGetter(RegistryIngredient::item),
                ExtraCodecs.strictOptionalField(Codec.INT, "count", 1)
                        .forGetter((ingredient) -> 1)
        ).apply(instance, RegistryIngredient::new);
    });

    public record RegistryIngredient(String tag, String item, int count) {
        public Ingredient getIngredient() {

            if (!tag.isBlank()) {
                ResourceLocation res = ResourceLocation.of(tag, ':');
                TagKey<Item> tag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), res);
                HolderSet<Item> holderSet = BuiltInRegistries.ITEM.getOrCreateTag(tag);
                AtomicReference<ArrayList<ItemStack>> itemSet = new AtomicReference<>(new ArrayList<>());
                holderSet.stream().forEach(itemHolder -> {
                    itemSet.get().add(new ItemStack(itemHolder.value(), this.count));
                });
                return Ingredient.of(itemSet.get().stream());
            } else if (!item.isBlank()) {
                ResourceLocation res = ResourceLocation.of(item, ':');
                Item single = ForgeRegistries.ITEMS.getValue(res);
                if (single == null) {
                    throw new IllegalStateException("Invalid recipe ingredient object: " + item + " | " + tag);
                }
                ItemStack stack = new ItemStack(single, this.count);
                return Ingredient.of(stack);
            } else {
                throw new IllegalStateException("Recipe missing item/tag JSON syntax!");
            }
        }
    }

    public static final Codec<ItemStack> VE_OUTPUT_ITEM_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ITEM_NONAIR_CODEC.fieldOf("item").forGetter(ItemStack::getItem), ExtraCodecs.strictOptionalField(Codec.INT, "amount", 1).forGetter(ItemStack::getCount)).apply(instance, ItemStack::new);
    });

    public static final Codec<VEChancedItemWithCount> VE_CHANCED_OUTPUT_ITEM_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ITEM_NONAIR_CODEC.fieldOf("item").forGetter(VEChancedItemWithCount::item),
            ExtraCodecs.strictOptionalField(Codec.INT, "count", 1).forGetter(VEChancedItemWithCount::count),
            ExtraCodecs.strictOptionalField(Codec.FLOAT, "chance", 1.0F).forGetter(VEChancedItemWithCount::chance)
    ).apply(instance, VEChancedItemWithCount::new));

    public static final Codec<Fluid> FLUID_NONAIR_CODEC = ExtraCodecs.validate(BuiltInRegistries.FLUID.byNameCodec(), (p_297750_) -> {
        return p_297750_ == Fluids.EMPTY ? DataResult.error(() -> {
            return "Crafting result must not be fluid:empty";
        }) : DataResult.success(p_297750_);
    });

    public static final Codec<RegistryFluidIngredient> VE_FLUID_INGREDIENT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "tag", "")
                        .forGetter(RegistryFluidIngredient::tag),
                ExtraCodecs.strictOptionalField(Codec.STRING, "fluid", "")
                        .forGetter(RegistryFluidIngredient::fluid),
                ExtraCodecs.strictOptionalField(Codec.INT, "amount", 1000)
                        .forGetter((ingredient) -> 1)
        ).apply(instance, RegistryFluidIngredient::new);
    });

    public static final Codec<FloatPair> VE_MIN_MAX_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ExtraCodecs.strictOptionalField(Codec.FLOAT, "minimum", 0.0F)
                        .forGetter(FloatPair::min),
                ExtraCodecs.strictOptionalField(Codec.FLOAT, "maximum", 0.0F)
                        .forGetter(FloatPair::max)
        ).apply(instance, FloatPair::new);
    });

    public static final Codec<FluidMinMax> VE_MIN_MAX_FLUID_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter(FluidMinMax::fluid),
                ExtraCodecs.strictOptionalField(Codec.INT, "minimum", 0)
                        .forGetter(FluidMinMax::min),
                ExtraCodecs.strictOptionalField(Codec.INT, "maximum", 0)
                        .forGetter(FluidMinMax::max)
        ).apply(instance, FluidMinMax::new);
    });

    public static final Codec<IntPair> VE_MIN_MAX_INT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ExtraCodecs.strictOptionalField(Codec.INT, "minimum", 0)
                        .forGetter(IntPair::min),
                ExtraCodecs.strictOptionalField(Codec.INT, "maximum", 0)
                        .forGetter(IntPair::max)
        ).apply(instance, IntPair::new);
    });

    public static final Codec<ClimateData> VE_CLIMATE_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ExtraCodecs.strictOptionalField(VE_MIN_MAX_CODEC, "continentalness", new FloatPair(0,0))
                        .forGetter(ClimateData::continentalness),
                ExtraCodecs.strictOptionalField(VE_MIN_MAX_CODEC, "erosion", new FloatPair(0,0))
                        .forGetter(ClimateData::continentalness),
                ExtraCodecs.strictOptionalField(VE_MIN_MAX_CODEC, "humidity", new FloatPair(0,0))
                        .forGetter(ClimateData::continentalness),
                ExtraCodecs.strictOptionalField(VE_MIN_MAX_CODEC, "temperature", new FloatPair(0,0))
                        .forGetter(ClimateData::continentalness)
                ).apply(instance, ClimateData::new);
    });

    public record FloatPair(float min, float max) {}

    public record IntPair(int min, int max) {}

    public record ClimateData(FloatPair continentalness,FloatPair erosion, FloatPair humidity, FloatPair temperature ) {}

    public record FluidMinMax(Fluid fluid,int min,int max) {

    }

    public record RegistryFluidIngredient(String tag, String fluid, int amount) {
        public FluidIngredient getIngredient() {

            if (!tag.isBlank()) {
                ResourceLocation res = ResourceLocation.of(tag, ':');
                TagKey<Fluid> tag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), res);
                HolderSet<Fluid> holderSet = BuiltInRegistries.FLUID.getOrCreateTag(tag);
                AtomicReference<ArrayList<FluidStack>> fluidSet = new AtomicReference<>(new ArrayList<>());
                holderSet.stream().forEach(itemHolder -> {
                    fluidSet.get().add(new FluidStack(itemHolder.value(), amount));
                });
                return FluidIngredient.of(fluidSet.get().stream());
            } else if (!fluid.isBlank()) {
                ResourceLocation res = ResourceLocation.of(fluid, ':');
                Fluid single = ForgeRegistries.FLUIDS.getValue(res);
                if (single == null) {
                    throw new IllegalStateException("Invalid recipe fluid ingredient object: " + fluid);
                }
                FluidStack stack = new FluidStack(single, amount);
                return FluidIngredient.of(stack);
            } else {
                throw new IllegalStateException("Recipe missing fluid/tag JSON syntax!");
            }
        }
    }

    public static final Codec<FluidStack> VE_OUTPUT_FLUID_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter(FluidStack::getFluid),
                ExtraCodecs.strictOptionalField(Codec.INT, "amount", 1)
                        .forGetter(FluidStack::getAmount)).apply(instance, FluidStack::new);
    });

    public static final Codec<VERecipeExperience> VE_EXPERIENCE_RANGE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ExtraCodecs.strictOptionalField(Codec.INT, "minimum", 0).forGetter(VERecipeExperience::minimum),
            ExtraCodecs.strictOptionalField(Codec.INT, "maximum", 0).forGetter(VERecipeExperience::maximum)
    ).apply(instance, VERecipeExperience::new));

    private static JsonElement getBadItemElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ingredient", "minecraft:barrier");
        return jsonObject;
    }

    public record VEChancedItemWithCount(Item item, int count, float chance) {
        public ItemStack getAsItemStack() {
            return new ItemStack(item, count);
        }

        public Pair<ItemStack, Float> getItemStackWithChance() {
            return new Pair<>(new ItemStack(item, count), chance);
        }
    }

    public record VERecipeExperience(int minimum, int maximum) {
    }
}
