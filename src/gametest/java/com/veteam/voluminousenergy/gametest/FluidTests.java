package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.fluids.RedFumingNitricAcid;
import com.veteam.voluminousenergy.fluids.SulfuricAcid;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;
import java.util.function.Supplier;

/**
 * Fluid properties, hazards, and item movement (specs fluids/movement and fluids/hazards). Each test
 * fills a one-block-wide stone shaft at the centre of the structure, capped so liquids cannot spread
 * and gases cannot rise into neighbouring tests.
 */
public class FluidTests {
    private static final BlockPos SHAFT = new BlockPos(2, 1, 2);
    private static final int TICKS = 100;
    private static final int FULL_EFFECT_TICKS = 1200;

    private FluidTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("fluid_type_properties", 20, FluidTests::fluidTypeProperties));
        tests.add(GameTestDefinition.of("fluid_sulfuric_acid_ignores_fire_resistance", TICKS, FluidTests::sulfuricAcidIgnoresFireResistance));
        tests.add(GameTestDefinition.of("fluid_red_fuming_acid_strongest_and_ignites", TICKS, FluidTests::redFumingAcidStrongestAndIgnites));
        tests.add(GameTestDefinition.of("fluid_mercury_poisons", TICKS, FluidTests::mercuryPoisons));
        tests.add(GameTestDefinition.of("fluid_gas_asphyxiates_through_water_breathing", TICKS, FluidTests::gasAsphyxiatesThroughWaterBreathing));
        tests.add(GameTestDefinition.of("fluid_oxygen_refills_air", TICKS, FluidTests::oxygenRefillsAir));
        tests.add(GameTestDefinition.of("fluid_undead_ignore_asphyxiation", TICKS, FluidTests::undeadIgnoreAsphyxiation));
        tests.add(GameTestDefinition.of("fluid_item_floats_in_mercury", TICKS, FluidTests::itemFloatsInMercury));
        tests.add(GameTestDefinition.of("fluid_item_sinks_in_gasoline", TICKS, FluidTests::itemSinksInGasoline));
        tests.add(GameTestDefinition.of("fluid_item_falls_through_gas", TICKS, FluidTests::itemFallsThroughGas));
    }

    private record FluidValues(String name, Supplier<FluidType> type, int density, int viscosity) {
    }

    /**
     * Every fluid type reports the density and viscosity of the real substance.
     */
    private static void fluidTypeProperties(GameTestHelper helper) {
        List<FluidValues> expected = List.of(
                new FluidValues("hydrogen", VEFluids.HYDROGEN_FLUID_TYPE_REG, -1, 9),
                new FluidValues("natural_gas", VEFluids.NATURAL_GAS_FLUID_TYPE_REG, -1, 11),
                new FluidValues("ammonia", VEFluids.AMMONIA_FLUID_TYPE_REG, -1, 10),
                new FluidValues("nitrogen", VEFluids.NITROGEN_FLUID_TYPE_REG, 0, 18),
                new FluidValues("oxygen", VEFluids.OXYGEN_FLUID_TYPE_REG, 0, 20),
                new FluidValues("compressed_air", VEFluids.COMPRESSED_AIR_FLUID_TYPE_REG, 0, 18),
                new FluidValues("light_fuel", VEFluids.LIGHT_FUEL_FLUID_TYPE_REG, 0, 8),
                new FluidValues("gasoline", VEFluids.GASOLINE_FLUID_TYPE_REG, 740, 600),
                new FluidValues("naphtha", VEFluids.NAPHTHA_FLUID_TYPE_REG, 720, 550),
                new FluidValues("treethanol", VEFluids.TREETHANOL_FLUID_TYPE_REG, 789, 1200),
                new FluidValues("dinitrogen_tetroxide", VEFluids.DINITROGEN_TETROXIDE_FLUID_TYPE_REG, 1440, 420),
                new FluidValues("diesel", VEFluids.DIESEL_FLUID_TYPE_REG, 840, 3000),
                new FluidValues("biofuel", VEFluids.BIOFUEL_FLUID_TYPE_REG, 880, 5000),
                new FluidValues("ammonium_nitrate_solution", VEFluids.AMMONIUM_NITRATE_SOLUTION_FLUID_TYPE_REG, 1230, 2500),
                new FluidValues("wfna", VEFluids.WFNA_FLUID_TYPE_REG, 1510, 760),
                new FluidValues("rfna", VEFluids.RFNA_FLUID_TYPE_REG, 1550, 1000),
                new FluidValues("sulfuric_acid", VEFluids.SULFURIC_ACID_FLUID_TYPE_REG, 1830, 24600),
                new FluidValues("nitroglycerin", VEFluids.NITROGLYCERIN_FLUID_TYPE_REG, 1600, 36000),
                new FluidValues("crude_oil", VEFluids.CRUDE_OIL_FLUID_TYPE_REG, 900, 5000000),
                new FluidValues("tree_sap", VEFluids.TREE_SAP_FLUID_TYPE_REG, 1050, 2000000),
                new FluidValues("liquefied_coal", VEFluids.LIQUEFIED_COAL_TYPE_REG, 1200, 1000000),
                new FluidValues("liquefied_coke", VEFluids.LIQUEFIED_COKE_FLUID_TYPE_REG, 1250, 1000000),
                new FluidValues("mercury", VEFluids.MERCURY_FLUID_TYPE_REG, 13534, 1530));
        for (FluidValues values : expected) {
            helper.assertValueEqual(values.type().get().getDensity(), values.density(), values.name() + " density");
            helper.assertValueEqual(values.type().get().getViscosity(), values.viscosity(), values.name() + " viscosity");
        }
        GameTestLog.pass(helper, "fluid_type_properties", "fluids=" + expected.size());
    }

    /**
     * Sulfuric Acid deals its acid damage through Fire Resistance and does not ignite.
     */
    private static void sulfuricAcidIgnoresFireResistance(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_SULFURIC_ACID_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, SHAFT);
        pig.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FULL_EFFECT_TICKS));
        float expectedDamage = Config.ACID_DAMAGE.get().floatValue() * SulfuricAcid.ACID_STRENGTH;
        helper.runAfterDelay(5, () -> {
            assertLastDamage(helper, pig, VEDamageTypes.ACID);
            helper.assertValueEqual(pig.getMaxHealth() - pig.getHealth(), expectedDamage, "acid damage taken");
            helper.assertValueEqual(pig.getRemainingFireTicks() > 0, false, "set on fire by sulfuric acid");
            GameTestLog.pass(helper, "fluid_sulfuric_acid_ignores_fire_resistance", "damage=" + expectedDamage);
        });
    }

    /**
     * Red Fuming Nitric Acid deals 1.5 times the base acid damage and sets entities on fire.
     */
    private static void redFumingAcidStrongestAndIgnites(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_RFNA_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, SHAFT);
        pig.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FULL_EFFECT_TICKS));
        float expectedDamage = Config.ACID_DAMAGE.get().floatValue() * RedFumingNitricAcid.ACID_STRENGTH;
        helper.runAfterDelay(5, () -> {
            assertLastDamage(helper, pig, VEDamageTypes.ACID);
            helper.assertValueEqual(pig.getMaxHealth() - pig.getHealth(), expectedDamage, "acid damage taken");
            helper.assertValueEqual(pig.getRemainingFireTicks() > 0, true, "set on fire by red fuming nitric acid");
            GameTestLog.pass(helper, "fluid_red_fuming_acid_strongest_and_ignites", "damage=" + expectedDamage);
        });
    }

    /**
     * Mercury poisons entities in it and cannot drown them.
     */
    private static void mercuryPoisons(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_MERCURY_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, SHAFT);
        helper.runAfterDelay(5, () -> {
            helper.assertValueEqual(pig.hasEffect(MobEffects.POISON), true, "poisoned by mercury");
            helper.assertValueEqual(pig.canDrownInFluidType(VEFluids.MERCURY_FLUID_TYPE_REG.get()), false, "can drown in mercury");
            GameTestLog.pass(helper, "fluid_mercury_poisons", "");
        });
    }

    /**
     * A pig under Water Breathing with its head in Nitrogen runs out of air and takes asphyxiation
     * damage.
     */
    private static void gasAsphyxiatesThroughWaterBreathing(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_NITROGEN_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, SHAFT);
        pig.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, FULL_EFFECT_TICKS));
        pig.setAirSupply(0);
        helper.runAfterDelay(40, () -> {
            assertLastDamage(helper, pig, VEDamageTypes.ASPHYXIATION);
            helper.assertValueEqual(pig.getHealth() < pig.getMaxHealth(), true, "hurt while asphyxiating");
            GameTestLog.pass(helper, "fluid_gas_asphyxiates_through_water_breathing", "health=" + pig.getHealth());
        });
    }

    /**
     * Oxygen refills a pig's air supply.
     */
    private static void oxygenRefillsAir(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_OXYGEN_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, SHAFT);
        int startAir = pig.getMaxAirSupply() / 2;
        pig.setAirSupply(startAir);
        helper.runAfterDelay(10, () -> {
            helper.assertValueEqual(pig.getAirSupply() > startAir, true, "air refilled in oxygen");
            GameTestLog.pass(helper, "fluid_oxygen_refills_air", "air=" + pig.getAirSupply());
        });
    }

    /**
     * A husk, which does not breathe, takes no asphyxiation damage in Nitrogen.
     */
    private static void undeadIgnoreAsphyxiation(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_NITROGEN_BLOCK_REG, 2);
        Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, SHAFT);
        husk.setAirSupply(0);
        helper.runAfterDelay(40, () -> {
            helper.assertValueEqual(husk.getHealth(), husk.getMaxHealth(), "husk health");
            GameTestLog.pass(helper, "fluid_undead_ignore_asphyxiation", "air=" + husk.getAirSupply());
        });
    }

    /**
     * An iron ingot dropped into Mercury rises to the surface.
     */
    private static void itemFloatsInMercury(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_MERCURY_BLOCK_REG, 1);
        Vec3 start = new Vec3(2.5D, 1.1D, 2.5D);
        ItemEntity ingot = helper.spawnItem(Items.IRON_INGOT, start);
        double startY = helper.absoluteVec(start).y;
        helper.runAfterDelay(20, () -> {
            helper.assertValueEqual(ingot.getY() > startY, true, "ingot rose in mercury");
            GameTestLog.pass(helper, "fluid_item_floats_in_mercury", "rise=" + (ingot.getY() - startY));
        });
    }

    /**
     * An iron ingot dropped into Gasoline sinks.
     */
    private static void itemSinksInGasoline(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_GASOLINE_BLOCK_REG, 2);
        Vec3 start = new Vec3(2.5D, 2.5D, 2.5D);
        ItemEntity ingot = helper.spawnItem(Items.IRON_INGOT, start);
        double startY = helper.absoluteVec(start).y;
        helper.runAfterDelay(20, () -> {
            helper.assertValueEqual(ingot.getY() < startY, true, "ingot sank in gasoline");
            GameTestLog.pass(helper, "fluid_item_sinks_in_gasoline", "drop=" + (startY - ingot.getY()));
        });
    }

    /**
     * An iron ingot dropped at the top of a Hydrogen column falls to the floor.
     */
    private static void itemFallsThroughGas(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_HYDROGEN_BLOCK_REG, 3);
        Vec3 start = new Vec3(2.5D, 3.5D, 2.5D);
        ItemEntity ingot = helper.spawnItem(Items.IRON_INGOT, start);
        double floorY = helper.absoluteVec(new Vec3(2.5D, 1.0D, 2.5D)).y;
        helper.runAfterDelay(20, () -> {
            helper.assertValueEqual(ingot.getY() < floorY + 0.5D, true, "ingot reached the floor through hydrogen");
            GameTestLog.pass(helper, "fluid_item_falls_through_gas", "height=" + (ingot.getY() - floorY));
        });
    }

    /**
     * Builds a stone shaft one block wide over {@link #SHAFT}, with a floor and a cap, and fills it with
     * source blocks of the fluid up to the given depth.
     */
    private static void fillShaft(GameTestHelper helper, Supplier<LiquidBlock> fluidBlock, int depth) {
        for (int y = 0; y <= depth + 1; y++) {
            for (int x = SHAFT.getX() - 1; x <= SHAFT.getX() + 1; x++) {
                for (int z = SHAFT.getZ() - 1; z <= SHAFT.getZ() + 1; z++) {
                    boolean inShaft = x == SHAFT.getX() && z == SHAFT.getZ() && y >= SHAFT.getY() && y <= depth;
                    helper.setBlock(new BlockPos(x, y, z), inShaft ? fluidBlock.get().defaultBlockState() : Blocks.STONE.defaultBlockState());
                }
            }
        }
        FluidState placed = helper.getLevel().getFluidState(helper.absolutePos(SHAFT));
        helper.assertValueEqual(placed.isSource(), true, "shaft holds a source block");
    }

    private static void assertLastDamage(GameTestHelper helper, LivingEntity entity, ResourceKey<DamageType> type) {
        DamageSource lastDamage = entity.getLastDamageSource();
        helper.assertValueEqual(lastDamage != null && lastDamage.is(type), true, "last damage was " + type.location());
    }
}
