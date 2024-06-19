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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

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
                if (stack.is(inputFluid.getFluid())) {
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
        return fromValues(p_43922_.filter((p_43944_)
                -> !p_43944_.isEmpty()).map(m -> new FluidValue(m, m.getAmount(), m.getFluid())));
    }

    public static FluidIngredient of(TagKey<Fluid> key, int amount) {
        return fromValues(Stream.of(new FluidIngredient.TagValue(key, amount)));
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf byteBuf) {
        var size = byteBuf.readVarInt();
        VEFluidIngredientSerializer serializer = VEFluidIngredientSerializer.INSTANCE;
        if (size == -1) return serializer.parse(byteBuf);
        FluidStack stack = FluidStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) byteBuf);
        return fromValues(Stream.generate(() -> new FluidIngredient.FluidValue(stack, stack.getAmount(), stack.getFluid())).limit(size));
    }

    public record FluidValue(FluidStack stack, int amount, Fluid fluid) implements FluidIngredient.Value {

        static FluidValue fromAmounts(Fluid fluid, int amount) {
            return new FluidValue(new FluidStack(fluid, amount), amount, fluid);
        }

        static final Codec<FluidValue> CODEC = RecordCodecBuilder.create((p_300421_) -> p_300421_.group(
                FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter((fluid) -> fluid.fluid),
                ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter((amount) -> amount.amount)
        ).apply(p_300421_, FluidValue::fromAmounts));

        public boolean equals(Object obj) {
            if (!(obj instanceof FluidValue otherFluid)) {
                return false;
            } else {
                return otherFluid.stack.is(this.stack.getFluid());
            }
        }

        public Collection<FluidStack> getFluids() {
            return Collections.singleton(this.stack);
        }
    }

    //Merges several vanilla Ingredients together. As a quirk of how the json is structured, we can't tell if it's a single Ingredient type or multiple so we split per item and re-merge here.
    //Only public for internal use, so we can access a private field in here.
    public static FluidIngredient merge(Collection<FluidIngredient> parts) {
        return fromValues(parts.stream().flatMap(i -> Arrays.stream(i.values)));
    }

    public record TagValue(TagKey<Fluid> tag, int amount) implements FluidIngredient.Value {
        static final Codec<FluidIngredient.TagValue> CODEC = RecordCodecBuilder.create((p_300241_) -> {
            return p_300241_.group(TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter((p_301340_) -> {
                        return p_301340_.tag;
                    }), ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter((amount) -> amount.amount)
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
                list.add(new FluidStack(holder.value(), this.amount));
            }

            if (list.isEmpty()) {
                // TODO figure out how we produce errors
                list.add(new FluidStack(Fluids.WATER, 1));
            }
            return list;
        }
    }

    public interface Value {
        Codec<FluidIngredient.Value> CODEC =
                Codec.xor(FluidValue.CODEC, FluidIngredient.TagValue.CODEC)
                        .xmap((p_300070_) -> p_300070_.map((p_301348_) -> p_301348_, (p_298354_) -> p_298354_), (p_299608_) -> {
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
