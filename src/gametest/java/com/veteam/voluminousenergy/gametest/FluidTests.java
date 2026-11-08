package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.fluids.RedFumingNitricAcid;
import com.veteam.voluminousenergy.fluids.SulfuricAcid;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.fluids.WhiteFumingNitricAcid;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
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
    private static final List<GasBlock> ASPHYXIANT_GASES = List.of(
            new GasBlock("hydrogen", VEFluids.FLOWING_HYDROGEN_BLOCK_REG),
            new GasBlock("natural_gas", VEFluids.FLOWING_NATURAL_GAS_BLOCK_REG),
            new GasBlock("ammonia", VEFluids.FLOWING_AMMONIA_BLOCK_REG),
            new GasBlock("nitrogen", VEFluids.FLOWING_NITROGEN_BLOCK_REG),
            new GasBlock("light_fuel", VEFluids.FLOWING_LIGHT_FUEL_BLOCK_REG));

    private FluidTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("fluid_type_properties", 20, FluidTests::fluidTypeProperties));
        tests.add(GameTestDefinition.of("fluid_sulfuric_acid_ignores_fire_resistance", TICKS, FluidTests::sulfuricAcidIgnoresFireResistance));
        tests.add(GameTestDefinition.of("fluid_red_fuming_acid_strongest_and_ignites", TICKS, FluidTests::redFumingAcidStrongestAndIgnites));
        tests.add(GameTestDefinition.of("fluid_mercury_poisons", TICKS, FluidTests::mercuryPoisons));
        for (GasBlock gas : ASPHYXIANT_GASES) {
            addAsphyxiationTest(tests, gas, MobEffects.WATER_BREATHING, "water_breathing");
        }
        tests.add(GameTestDefinition.of("fluid_oxygen_refills_air", TICKS, FluidTests::oxygenRefillsAir));
        tests.add(GameTestDefinition.of("fluid_undead_ignore_asphyxiation", TICKS, FluidTests::undeadIgnoreAsphyxiation));
        tests.add(GameTestDefinition.of("fluid_item_floats_in_mercury", TICKS, FluidTests::itemFloatsInMercury));
        tests.add(GameTestDefinition.of("fluid_item_sinks_in_gasoline", TICKS, FluidTests::itemSinksInGasoline));
        tests.add(GameTestDefinition.of("fluid_item_falls_through_gas", TICKS, FluidTests::itemFallsThroughGas));
        tests.add(GameTestDefinition.of("fluid_gas_column_lifts_from_base", TICKS, FluidTests::gasColumnLiftsFromBase));
        addAsphyxiationTest(tests, ASPHYXIANT_GASES.get(1), MobEffects.CONDUIT_POWER, "conduit_power");
        tests.add(GameTestDefinition.of("fluid_respiration_slows_asphyxiation", TICKS, FluidTests::respirationSlowsAsphyxiation));
        tests.add(GameTestDefinition.of("fluid_white_fuming_acid_ignites_for_fire_duration", TICKS, FluidTests::whiteFumingAcidIgnitesForFireDuration));
    }

    private record FluidValues(String name, Supplier<FluidType> type, int density, int viscosity) {
    }

    private record GasBlock(String name, Supplier<LiquidBlock> block) {
    }

    private static void addAsphyxiationTest(List<GameTestDefinition> tests, GasBlock gas, Holder<MobEffect> protection, String protectionName) {
        String name = "fluid_" + gas.name() + "_asphyxiates_through_" + protectionName;
        tests.add(GameTestDefinition.of(name, TICKS, helper -> asphyxiatesThroughEffect(helper, name, gas.block(), protection)));
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
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
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
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
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
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
        helper.runAfterDelay(5, () -> {
            helper.assertValueEqual(pig.hasEffect(MobEffects.POISON), true, "poisoned by mercury");
            helper.assertValueEqual(pig.canDrownInFluidType(VEFluids.MERCURY_FLUID_TYPE_REG.get()), false, "can drown in mercury");
            GameTestLog.pass(helper, "fluid_mercury_poisons", "");
        });
    }

    /**
     * A pig under an underwater breathing effect with its head in an asphyxiant gas drains its remaining
     * air and takes asphyxiation damage with the asphyxiation death message.
     */
    private static void asphyxiatesThroughEffect(GameTestHelper helper, String name, Supplier<LiquidBlock> gas, Holder<MobEffect> protection) {
        fillShaft(helper, gas, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
        pig.addEffect(new MobEffectInstance(protection, FULL_EFFECT_TICKS));
        pig.setAirSupply(10);
        helper.runAfterDelay(40, () -> {
            assertLastDamage(helper, pig, VEDamageTypes.ASPHYXIATION);
            assertDeathMessage(helper, pig, "death.attack.voluminousenergy.asphyxiation");
            helper.assertValueEqual(pig.getHealth() < pig.getMaxHealth(), true, "hurt while asphyxiating");
            GameTestLog.pass(helper, name, "health=" + pig.getHealth());
        });
    }

    /**
     * Respiration III makes a pig's air drain in Nitrogen markedly slower than one point per tick, without
     * stopping it.
     */
    private static void respirationSlowsAsphyxiation(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_NITROGEN_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
        Holder<Enchantment> respiration = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.RESPIRATION);
        ItemStack helmet = new ItemStack(Items.IRON_HELMET);
        helmet.enchant(respiration, 3);
        pig.setItemSlot(EquipmentSlot.HEAD, helmet);
        int startAir = pig.getMaxAirSupply();
        pig.setAirSupply(startAir);
        int ticks = 40;
        helper.runAfterDelay(ticks, () -> {
            int drained = startAir - pig.getAirSupply();
            helper.assertValueEqual(drained > 0, true, "air drained with respiration");
            helper.assertValueEqual(drained < ticks * 3 / 4, true, "air drain slowed by respiration");
            GameTestLog.pass(helper, "fluid_respiration_slows_asphyxiation", "drained=" + drained + " over " + ticks + " ticks");
        });
    }

    /**
     * Oxygen refills a pig's air supply.
     */
    private static void oxygenRefillsAir(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_OXYGEN_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
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
        Husk husk = helper.spawnWithNoFreeWill(EntityTypes.HUSK, SHAFT);
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
     * A pig standing on the floor in the source block at the base of a rising Natural Gas column is lifted
     * without jumping. The room is wider than the usual shaft because gas only rises once it can spread
     * sideways.
     */
    private static void gasColumnLiftsFromBase(GameTestHelper helper) {
        int ceiling = 4;
        for (int y = 0; y <= ceiling; y++) {
            for (int x = 0; x <= 4; x++) {
                for (int z = 0; z <= 4; z++) {
                    boolean inRoom = x > 0 && x < 4 && z > 0 && z < 4 && y > 0 && y < ceiling;
                    helper.setBlock(new BlockPos(x, y, z), inRoom ? Blocks.AIR.defaultBlockState() : Blocks.STONE.defaultBlockState());
                }
            }
        }
        helper.setBlock(SHAFT, VEFluids.FLOWING_NATURAL_GAS_BLOCK_REG.get().defaultBlockState());
        helper.runAfterDelay(30, () -> {
            FluidState column = helper.getLevel().getFluidState(helper.absolutePos(SHAFT.above()));
            helper.assertValueEqual(!column.isSource() && column.getType().isSame(VEFluids.NATURAL_GAS_REG.get()), true, "flowing gas above the source");
            Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
            double floorY = pig.getY();
            helper.runAfterDelay(40, () -> {
                helper.assertValueEqual(pig.getY() > floorY + 1.0D, true, "pig rose from the column's base");
                GameTestLog.pass(helper, "fluid_gas_column_lifts_from_base", "rise=" + (pig.getY() - floorY));
            });
        });
    }

    /**
     * White Fuming Nitric Acid deals its acid damage through Fire Resistance and keeps an entity burning
     * for at least the configured acid fire duration.
     */
    private static void whiteFumingAcidIgnitesForFireDuration(GameTestHelper helper) {
        fillShaft(helper, VEFluids.FLOWING_WFNA_BLOCK_REG, 1);
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SHAFT);
        pig.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FULL_EFFECT_TICKS));
        float expectedDamage = Config.ACID_DAMAGE.get().floatValue() * WhiteFumingNitricAcid.ACID_STRENGTH;
        int fireDuration = Config.ACID_FIRE_DURATION.get();
        helper.runAfterDelay(5, () -> {
            assertLastDamage(helper, pig, VEDamageTypes.ACID);
            assertDeathMessage(helper, pig, "death.attack.voluminousenergy.acid");
            helper.assertValueEqual(pig.getMaxHealth() - pig.getHealth(), expectedDamage, "acid damage taken");
            helper.assertValueEqual(pig.getRemainingFireTicks() >= fireDuration - 2, true, "fire ticks near the acid fire duration of " + fireDuration);
            GameTestLog.pass(helper, "fluid_white_fuming_acid_ignites_for_fire_duration", "fire_ticks=" + pig.getRemainingFireTicks());
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
        helper.assertValueEqual(lastDamage != null && lastDamage.is(type), true, "last damage was " + type.identifier());
    }

    /**
     * Asserts that the entity's death message from its last damage uses the given translation key and
     * that the key has a translation.
     */
    private static void assertDeathMessage(GameTestHelper helper, LivingEntity entity, String key) {
        DamageSource lastDamage = entity.getLastDamageSource();
        boolean usesKey = lastDamage != null
                && lastDamage.getLocalizedDeathMessage(entity).getContents() instanceof TranslatableContents translatable
                && translatable.getKey().equals(key);
        helper.assertValueEqual(usesKey, true, "death message uses " + key);
        helper.assertValueEqual(Language.getInstance().has(key), true, "translation exists for " + key);
    }
}
