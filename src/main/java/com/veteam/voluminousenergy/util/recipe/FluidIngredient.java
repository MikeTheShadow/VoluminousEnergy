package com.veteam.voluminousenergy.util.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.FLUID_NONAIR_CODEC;

/**
 * An attempt to copy net.minecraft.world.item.crafting.Ingredient for fluids
 */
public class FluidIngredient {

    private static final java.util.concurrent.atomic.AtomicInteger INVALIDATION_COUNTER = new java.util.concurrent.atomic.AtomicInteger();

    public static void invalidateAll() {
        INVALIDATION_COUNTER.incrementAndGet();
    }

    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
    private final FluidIngredient.Value[] values;
    @Nullable
    private FluidStack[] fluidStacks;
    @Nullable
    private IntList stackingIds;
    private int invalidationCounter;

    public static final Codec<FluidIngredient> CODEC = codec(true);
    public static final Codec<FluidIngredient> CODEC_NONEMPTY = codec(false);

    public FluidIngredient(FluidIngredient ingredient) {
        values = ingredient.values;
        fluidStacks = ingredient.fluidStacks;
        stackingIds = ingredient.stackingIds;
        invalidationCounter = ingredient.invalidationCounter;
    }

    protected FluidIngredient(Stream<? extends FluidIngredient.Value> stream) {
        this.values = stream.toArray(Value[]::new);
    }

    protected FluidIngredient(FluidIngredient.Value... values) {
        this.values = values;
    }

    public FluidStack[] getFluids() {
        if (this.fluidStacks == null) {
            this.fluidStacks =
                    Arrays.stream(this.values)
                            .flatMap((map) -> map.getFluids().stream())
                            .distinct()
                            .toArray(FluidStack[]::new);
        }
        return this.fluidStacks;
    }

    public int getAmountNeeded() {
        return getFluids()[0].getAmount();
    }

    public boolean test(@Nullable FluidStack inputFluid) {
        if (inputFluid == null) {
            return false;
        } else if (this.isEmpty()) {
            return inputFluid.isEmpty();
        } else {
            for (FluidStack stack : this.getFluids()) {
                if (stack.isFluidEqual(inputFluid)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean test(@Nullable Fluid fluid) {
        if (fluid == null) {
            return false;
        } else {
            for (FluidStack stack : this.getFluids()) {
                if (stack.getFluid().isSame(fluid)) {
                    return true;
                }
            }

            return false;
        }
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null || checkInvalidation()) {
            this.markValid();
            FluidStack[] afluidstack = this.getFluids();
            this.stackingIds = new IntArrayList(afluidstack.length);

            for (FluidStack fluidStack : afluidstack) {
                this.stackingIds.add(getStackingIndex(fluidStack));
            }
            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    public static int getStackingIndex(FluidStack fluidStack) {
        return BuiltInRegistries.FLUID.getId(fluidStack.getFluid());
    }

    public final void toNetwork(FriendlyByteBuf p_43924_) {
        write(p_43924_, this);
    }

    public static void write(FriendlyByteBuf buffer, FluidIngredient ingredient) {
        VEFluidIngredientSerializer serializer = ingredient.getSerializer();
        serializer.write(buffer, ingredient);
    }

    public boolean isEmpty() {
        return this.values.length == 0;
    }

    public final boolean checkInvalidation() {
        int currentInvalidationCounter = INVALIDATION_COUNTER.get();
        if (this.invalidationCounter != currentInvalidationCounter) {
            invalidate();
            return true;
        }
        return false;
    }

    protected final void markValid() {
        this.invalidationCounter = INVALIDATION_COUNTER.get();
    }

    protected void invalidate() {
        this.fluidStacks = null;
        this.stackingIds = null;
    }

    public boolean isSimple() {
        return true;
    }

    public VEFluidIngredientSerializer getSerializer() {
        return VEFluidIngredientSerializer.INSTANCE;
    }

    public static FluidIngredient fromValues(Stream<? extends FluidIngredient.Value> stream) {
        FluidIngredient ingredient = new FluidIngredient(stream);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static FluidIngredient of() {
        return EMPTY;
    }

    public static FluidIngredient of(FluidStack... p_43928_) {
        return of(Arrays.stream(p_43928_));
    }

    public static FluidIngredient of(Stream<FluidStack> p_43922_) {
        return fromValues(p_43922_.filter((p_43944_) -> {
            return !p_43944_.isEmpty();
        }).map(m -> new FluidValue(m,m.getAmount(),m.getFluid())));
    }

    public static FluidIngredient of(TagKey<Fluid> key, int amount) {
        return fromValues(Stream.of(new FluidIngredient.TagValue(key, amount)));
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf byteBuf) {
        var size = byteBuf.readVarInt();
        VEFluidIngredientSerializer serializer = VEFluidIngredientSerializer.INSTANCE;
        if (size == -1) return serializer.parse(byteBuf);
        FluidStack stack = byteBuf.readFluidStack();
        return fromValues(Stream.generate(() -> new FluidIngredient.FluidValue(stack,stack.getAmount(),stack.getFluid())).limit(size));
    }

    public static FluidIngredient fromJson(@Nullable JsonObject json) {
        return fromJson(json, true);
    }

    public static FluidIngredient fromJson(@Nullable JsonObject jsonObject, boolean allowEmpty) {

        if (jsonObject == null) throw new IllegalStateException("Recipe has null object!");

        ResourceLocation fluidTagLocation;

        boolean isTag = false;

        if (jsonObject.has("fluid")) {
            fluidTagLocation = ResourceLocation.of(jsonObject.get("fluid").getAsString(), ':');
        } else if (jsonObject.has("tag")) {
            isTag = true;
            fluidTagLocation = ResourceLocation.of(jsonObject.get("tag").getAsString(), ':');
        } else {
            throw new IllegalStateException("Recipe missing fluid tag!");
        }
        if (!jsonObject.has("amount")) throw new IllegalStateException("Recipe missing amount!");

        if (!isTag) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidTagLocation);
            if (fluid == null) {
                throw new IllegalStateException("Fluid does not exist for a recipe!");
            }
            return FluidIngredient.of(new FluidStack(fluid, jsonObject.get("amount").getAsInt()));
        }

        TagKey<Fluid> tag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), fluidTagLocation);

        return FluidIngredient.of(tag, jsonObject.get("amount").getAsInt());
    }

    // Only use this is if the amount object is not present in the passed in jsonObject
    public static FluidIngredient fromJsonNoAmount(@Nullable JsonObject jsonObject, int rawAmount) {

        if (jsonObject == null) throw new IllegalStateException("Recipe has null object!");

        ResourceLocation fluidTagLocation;

        if (jsonObject.has("fluid")) {
            fluidTagLocation = ResourceLocation.of(jsonObject.get("fluid").getAsString(), ':');
        } else if (jsonObject.has("tag")) {
            fluidTagLocation = ResourceLocation.of(jsonObject.get("tag").getAsString(), ':');
        } else {
            throw new IllegalStateException("Recipe missing fluid tag!");
        }

        TagKey<Fluid> tag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), fluidTagLocation);

        return FluidIngredient.of(tag, rawAmount);
    }

    private static Codec<FluidIngredient> codec(boolean p_298496_) {
        Codec<FluidIngredient.Value[]> codec = Codec.list(FluidIngredient.Value.CODEC).comapFlatMap((p_296902_) -> {
            return !p_298496_ && p_296902_.isEmpty() ? DataResult.error(() -> {
                return "Item array cannot be empty, at least one item must be defined";
            }) : DataResult.success(p_296902_.toArray(new FluidIngredient.Value[0]));
        }, List::of);
        return ExtraCodecs.either(codec, FluidIngredient.Value.CODEC).flatComapMap((p_296900_) -> {
            return p_296900_.map(FluidIngredient::new, (p_296903_) -> {
                return new FluidIngredient(new FluidIngredient.Value[]{p_296903_});
            });
        }, (p_296899_) -> {
            if (p_296899_.values.length == 1) {
                return DataResult.success(Either.right(p_296899_.values[0]));
            } else {
                return p_296899_.values.length == 0 && !p_298496_ ? DataResult.error(() -> {
                    return "Item array cannot be empty, at least one item must be defined";
                }) : DataResult.success(Either.left(p_296899_.values));
            }
        });
    }

    public record FluidValue(FluidStack stack,int amount,Fluid fluid) implements FluidIngredient.Value {

        static FluidValue fromAmounts(Fluid fluid, int amount) {
            return new FluidValue(new FluidStack(fluid,amount), amount,fluid);
        }

        static final Codec<FluidValue> CODEC = RecordCodecBuilder.create((p_300421_) -> {
            return p_300421_.group(FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter((fluid) -> fluid.fluid),
                    ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "amount", 1).forGetter((amount) -> amount.amount)
            ).apply(p_300421_, FluidValue::fromAmounts);
        });

        public boolean equals(Object obj) {
            if (!(obj instanceof FluidValue otherFluid)) {
                return false;
            } else {
                return otherFluid.stack.isFluidStackIdentical(this.stack);
            }
        }

        public Collection<FluidStack> getFluids() {
            return Collections.singleton(this.stack);
        }
    }

    //Merges several vanilla Ingredients together. As a quirk of how the json is structured, we can't tell if its a single Ingredient type or multiple so we split per item and re-merge here.
    //Only public for internal use, so we can access a private field in here.
    public static FluidIngredient merge(Collection<FluidIngredient> parts) {
        return fromValues(parts.stream().flatMap(i -> Arrays.stream(i.values)));
    }

    public record TagValue(TagKey<Fluid> tag,int amount) implements FluidIngredient.Value {
        static final Codec<FluidIngredient.TagValue> CODEC = RecordCodecBuilder.create((p_300241_) -> {
            return p_300241_.group(TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter((p_301340_) -> {
                return p_301340_.tag;
            }),ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "amount", 1).forGetter((amount) -> amount.amount)
            ).apply(p_300241_, FluidIngredient.TagValue::new);
        });

        public boolean equals(Object p_298268_) {
            if (p_298268_ instanceof FluidIngredient.TagValue ingredient$tagvalue) {
                return ingredient$tagvalue.tag.location().equals(this.tag.location());
            } else {
                return false;
            }
        }

        public Collection<FluidStack> getFluids() {
            List<FluidStack> list = Lists.newArrayList();

            for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(this.tag)) {
                list.add(new FluidStack(holder.value(),this.amount));
            }

            if (list.isEmpty()) {
                // TODO figure out how we produce errors
                list.add(new FluidStack(Fluids.WATER,1));
            }
            return list;
        }
    }

    public interface Value {
        Codec<FluidIngredient.Value> CODEC = ExtraCodecs.xor(FluidValue.CODEC, FluidIngredient.TagValue.CODEC).xmap((p_300070_) -> {
            return p_300070_.map((p_301348_) -> {
                return p_301348_;
            }, (p_298354_) -> {
                return p_298354_;
            });
        }, (p_299608_) -> {
            if (p_299608_ instanceof FluidIngredient.TagValue ingredient$tagvalue) {
                return Either.right(ingredient$tagvalue);
            } else if (p_299608_ instanceof FluidValue ingredient$itemvalue) {
                return Either.left(ingredient$itemvalue);
            } else {
                throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
            }
        });

        Collection<FluidStack> getFluids();
    }
}
