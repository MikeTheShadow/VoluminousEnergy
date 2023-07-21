package com.veteam.voluminousenergy.util.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.VoluminousEnergy;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

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

    public int getCount() {
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

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();

            for (FluidIngredient.Value ingredient$value : this.values) {
                jsonarray.add(ingredient$value.serialize());
            }

            return jsonarray;
        }
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
        }).map(FluidValue::new));
    }

    public static FluidIngredient of(TagKey<Fluid> key,int amount) {
        return fromValues(Stream.of(new FluidIngredient.TagValue(key,amount)));
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf byteBuf) {
        var size = byteBuf.readVarInt();
        VEFluidIngredientSerializer serializer = VEFluidIngredientSerializer.INSTANCE;
        if (size == -1) return serializer.parse(byteBuf);
        return fromValues(Stream.generate(() -> new FluidIngredient.FluidValue(byteBuf.readFluidStack())).limit(size));
    }

    public static FluidIngredient fromJson(@Nullable JsonObject json) {
        return fromJson(json, true);
    }

    public static FluidIngredient fromJson(@Nullable JsonObject jsonObject, boolean allowEmpty) {

        if(jsonObject == null) throw new IllegalStateException("Recipe has null object!");

        ResourceLocation fluidTagLocation;

        boolean isTag = false;

        if(jsonObject.has("fluid")) {
            fluidTagLocation = ResourceLocation.of(jsonObject.get("fluid").getAsString(),':');
        } else if(jsonObject.has("tag")) {
            isTag = true;
            fluidTagLocation = ResourceLocation.of(jsonObject.get("tag").getAsString(),':');
        } else {
            throw new IllegalStateException("Recipe missing fluid tag!");
        }
        if(!jsonObject.has("amount")) throw new IllegalStateException("Recipe missing amount!");

        if(!isTag) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidTagLocation);
            if(fluid == null) {
                throw new IllegalStateException("Fluid does not exist for a recipe!");
            }
            return FluidIngredient.of(new FluidStack(fluid,jsonObject.get("amount").getAsInt()));
        }

        TagKey<Fluid> tag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), fluidTagLocation);

        return FluidIngredient.of(tag,jsonObject.get("amount").getAsInt());
    }

    // Only use this is if the amount object is not present in the passed in jsonObject
    public static FluidIngredient fromJsonNoAmount(@Nullable JsonObject jsonObject,int rawAmount) {

        if(jsonObject == null) throw new IllegalStateException("Recipe has null object!");

        ResourceLocation fluidTagLocation;

        if(jsonObject.has("fluid")) {
            fluidTagLocation = ResourceLocation.of(jsonObject.get("fluid").getAsString(),':');
        } else if(jsonObject.has("tag")) {
            fluidTagLocation = ResourceLocation.of(jsonObject.get("tag").getAsString(),':');
        } else {
            throw new IllegalStateException("Recipe missing fluid tag!");
        }

        TagKey<Fluid> tag = TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), fluidTagLocation);

        return FluidIngredient.of(tag,rawAmount);
    }

    public static class FluidValue implements FluidIngredient.Value {
        private final FluidStack fluid;

        public FluidValue(FluidStack fluid) {
            this.fluid = fluid;
        }

        public Collection<FluidStack> getFluids() {
            return Collections.singleton(this.fluid);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            ResourceLocation fluidTagKey = ForgeRegistries.FLUIDS.getKey(this.fluid.getFluid());
            if(fluidTagKey == null) throw new IllegalStateException("Cannot serialize null for: " + this.fluid.getFluid().getFluidType());
            jsonobject.addProperty("fluid", fluidTagKey.toString());
            return jsonobject;
        }
    }

    public static class TagValue implements FluidIngredient.Value {
        private final TagKey<Fluid> tag;
        private final int amount;

        public TagValue(TagKey<Fluid> fluidTagKey,int amount) {
            this.tag = fluidTagKey;
            this.amount = amount;
        }

        public Collection<FluidStack> getFluids() {
            List<FluidStack> list = Lists.newArrayList();

            for (Fluid holder : ForgeRegistries.FLUIDS.tags().getTag(this.tag)) {
                list.add(new FluidStack(holder, amount));
            }

            if (list.size() == 0) {
                list.add(new FluidStack(Fluids.WATER, 1));
            }
            return list;
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.tag.location().toString());
            return jsonobject;
        }
    }

    public interface Value {
        Collection<FluidStack> getFluids();

        JsonObject serialize();
    }
}
