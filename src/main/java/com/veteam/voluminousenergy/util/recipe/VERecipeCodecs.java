package com.veteam.voluminousenergy.util.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class VERecipeCodecs {
    public static final Codec<Holder<Item>> ITEM_NONAIR_CODEC = BuiltInRegistries.ITEM
            .holderByNameCodec()
            .validate(
                    p_330100_ -> p_330100_.is(Items.AIR.builtInRegistryHolder())
                            ? DataResult.error(() -> "Item must not be minecraft:air")
                            : DataResult.success(p_330100_)
            );

    public static final Codec<RegistryIngredient> VE_LAZY_INGREDIENT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("tag", "").forGetter(t -> t.tag),
            Codec.STRING.optionalFieldOf("item", "").forGetter(t -> t.item),
            Codec.INT.optionalFieldOf("count",1).forGetter(t -> t.count)
    ).apply(instance, RegistryIngredient::new));

    public record RegistryIngredient(String tag, String item, int count) {
        public Ingredient getIngredient() {

            if (!tag.isBlank()) {
                Identifier res = Identifier.bySeparator(tag, ':');
                TagKey<Item> tag = TagKey.create(Registries.ITEM, res);
                HolderSet<Item> holderSet = BuiltInRegistries.ITEM.getOrCreateTag(tag);

                AtomicReference<ArrayList<ItemStack>> itemSet = new AtomicReference<>(new ArrayList<>());
                holderSet.stream().forEach(itemHolder ->
                        itemSet.get().add(new ItemStack(itemHolder.value(), this.count)));

                if (holderSet.size() == 0) {
                    throw new IllegalStateException("Holder size is zero for tag "
                            + tag.identifier() + "! This likely means that there are no items in the registry with that tag!");
                }

                return Ingredient.of(itemSet.get().stream());
            } else if (!item.isBlank()) {
                Identifier res = Identifier.bySeparator(item, ':');

                boolean containsItem = BuiltInRegistries.ITEM.containsKey(res);

                if (!containsItem) {
                    throw new IllegalStateException("Invalid recipe ingredient object: " + item + " | " + tag + " does not exist!");
                }

                Item single = BuiltInRegistries.ITEM.get(res);
                ItemStack stack = new ItemStack(single, this.count);
                return Ingredient.of(stack);
            } else {
                throw new IllegalStateException("Recipe missing item/tag JSON syntax!");
            }
        }
    }

    public static final Codec<ItemStack> VE_OUTPUT_ITEM_CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(ITEM_NONAIR_CODEC.fieldOf("item").forGetter(ItemStack::getItemHolder),
                    Codec.INT.optionalFieldOf("count",1)
                            .forGetter(ItemStack::getCount)).apply(instance, ItemStack::new));

    public static final Codec<VEChancedItemWithCount> VE_CHANCED_OUTPUT_ITEM_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ITEM_NONAIR_CODEC.fieldOf("item").forGetter(VEChancedItemWithCount::item),
            Codec.INT.optionalFieldOf("count",1).forGetter(VEChancedItemWithCount::count),
            Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(VEChancedItemWithCount::chance)
    ).apply(instance, VEChancedItemWithCount::new));

    public static final Codec<Fluid> FLUID_NONAIR_CODEC = BuiltInRegistries.FLUID.byNameCodec().validate((fluid)
            -> fluid == Fluids.EMPTY ? DataResult.error(() -> "Crafting result must not be fluid:empty") : DataResult.success(fluid));

    public static final Codec<RegistryFluidIngredient> VE_FLUID_INGREDIENT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("tag", "")
                    .forGetter(RegistryFluidIngredient::tag),
            Codec.STRING.optionalFieldOf("fluid", "")
                    .forGetter(RegistryFluidIngredient::fluid),
            Codec.INT.fieldOf("amount")
                    .forGetter((ingredient) -> 1)
    ).apply(instance, RegistryFluidIngredient::new));

    public static final Codec<FloatPair> VE_MIN_MAX_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.FLOAT.fieldOf("minimum").forGetter(FloatPair::min),
            Codec.FLOAT.fieldOf("maximum").forGetter(FloatPair::max)
    ).apply(instance, FloatPair::new));

    public static final Codec<FluidMinMax> VE_MIN_MAX_FLUID_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter(FluidMinMax::fluid),
            Codec.INT.fieldOf("minimum").forGetter(FluidMinMax::min),
            Codec.INT.fieldOf("maximum").forGetter(FluidMinMax::max)
    ).apply(instance, FluidMinMax::new));

    public static final Codec<IntPair> VE_MIN_MAX_INT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("minimum").forGetter(IntPair::min),
            Codec.INT.fieldOf("maximum").forGetter(IntPair::max)
    ).apply(instance, IntPair::new));

    public static final Codec<ClimateData> VE_CLIMATE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VE_MIN_MAX_CODEC.fieldOf("continentalness").forGetter(ClimateData::continentalness),
            VE_MIN_MAX_CODEC.fieldOf("erosion").forGetter(ClimateData::continentalness),
            VE_MIN_MAX_CODEC.fieldOf("humidity").forGetter(ClimateData::continentalness),
            VE_MIN_MAX_CODEC.fieldOf("temperature").forGetter(ClimateData::continentalness)
    ).apply(instance, ClimateData::new));

    public static final Codec<RegistryFluidValue> REGISTRY_COMBUSTION_FLUID_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("tag", "").forGetter(RegistryFluidValue::tag),
            Codec.STRING.optionalFieldOf("fluid", "").forGetter(RegistryFluidValue::fluid),
            Codec.FLOAT.fieldOf("energy_per_tick").forGetter((ingredient) -> 1f)
    ).apply(instance, RegistryFluidValue::new));

    public static final Codec<RegistryFluidValue> REGISTRY_OXIDIZER_FLUID_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("tag", "").forGetter(RegistryFluidValue::tag),
            Codec.STRING.optionalFieldOf("fluid", "").forGetter(RegistryFluidValue::fluid),
            Codec.FLOAT.fieldOf("multiplier").forGetter((ingredient) -> 1f)
    ).apply(instance, RegistryFluidValue::new));

    public record FloatPair(float min, float max) {
    }

    public record IntPair(int min, int max) {
    }

    public record ClimateData(FloatPair continentalness, FloatPair erosion, FloatPair humidity, FloatPair temperature) {
    }

    public record FluidMinMax(Fluid fluid, int min, int max) {

    }

    public record RegistryFluidIngredient(String tag, String fluid, int amount) {
        public FluidIngredient getIngredient() {

            if (!tag.isBlank()) {
                Identifier res = Identifier.bySeparator(tag, ':');
                TagKey<Fluid> tag = TagKey.create(Registries.FLUID, res);
                HolderSet<Fluid> holderSet = BuiltInRegistries.FLUID.getOrCreateTag(tag);
                AtomicReference<ArrayList<FluidStack>> fluidSet = new AtomicReference<>(new ArrayList<>());

                if (holderSet.size() == 0) {
                    throw new IllegalStateException("No values found for tag "
                            + tag + ". Make sure that there are fluids with that tag in the registry.");
                }

                holderSet.stream().forEach(itemHolder -> {
                    fluidSet.get().add(new FluidStack(itemHolder.value(), amount));
                });
                return FluidIngredient.of(fluidSet.get().stream());
            } else if (!fluid.isBlank()) {
                Identifier res = Identifier.bySeparator(fluid, ':');
                if (!BuiltInRegistries.FLUID.containsKey(res)) {
                    throw new IllegalStateException("Unable to get fluid ingredient: " + fluid);
                }
                Fluid single = BuiltInRegistries.FLUID.get(res);
                FluidStack stack = new FluidStack(single, amount);
                return FluidIngredient.of(stack);
            } else {
                throw new IllegalStateException("Recipe missing fluid/tag JSON syntax!");
            }
        }
    }

    public record RegistryFluidValue(String tag, String fluid, float value) {
        public FluidSetWithValue getAsValuePair() {

            if (!tag.isBlank()) {
                Identifier res = Identifier.bySeparator(tag, ':');
                TagKey<Fluid> tag = TagKey.create(Registries.FLUID, res);
                HolderSet<Fluid> holderSet = BuiltInRegistries.FLUID.getOrCreateTag(tag);
                AtomicReference<HashSet<Fluid>> fluidSet = new AtomicReference<>(new HashSet<>());
                holderSet.stream().forEach(itemHolder -> fluidSet.get().add(itemHolder.value()));

                if (holderSet.size() == 0) {
                    throw new IllegalStateException("No values found for tag "
                            + tag + ". Make sure that there are fluids with that tag in the registry.");
                }

                return new FluidSetWithValue(fluidSet.get(), value);
            } else if (!fluid.isBlank()) {
                Identifier res = Identifier.bySeparator(fluid, ':');

                if (!BuiltInRegistries.FLUID.containsKey(res)) {
                    throw new IllegalStateException("Unable to get fluid ingredient: " + fluid + ". Please validate it exists!");
                }
                Fluid single = BuiltInRegistries.FLUID.get(res);
                return new FluidSetWithValue(Set.of(single), value);
            } else {
                throw new IllegalStateException("Recipe missing fluid/tag JSON syntax!");
            }
        }
    }

    public static class FluidSetWithValue {
        public Set<Fluid> fluids;
        public float value;

        public FluidSetWithValue(Set<Fluid> fluids, float value) {
            this.fluids = fluids;
            this.value = value;
        }

    }

    public static final Codec<FluidStack> VE_OUTPUT_FLUID_CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter(FluidStack::getFluid),
                    Codec.INT.fieldOf("amount")
                            .forGetter(FluidStack::getAmount)).apply(instance, FluidStack::new));

    public static final Codec<VERecipeExperience> VE_EXPERIENCE_RANGE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("minimum").forGetter(VERecipeExperience::minimum),
            Codec.INT.fieldOf("maximum").forGetter(VERecipeExperience::maximum)
    ).apply(instance, VERecipeExperience::new));

    private static JsonElement getBadItemElement() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ingredient", "minecraft:barrier");
        return jsonObject;
    }

    public record VEChancedItemWithCount(Holder<Item> item, int count, float chance) {
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
