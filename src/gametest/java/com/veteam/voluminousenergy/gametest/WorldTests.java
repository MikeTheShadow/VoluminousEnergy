package com.veteam.voluminousenergy.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.veteam.voluminousenergy.achievements.triggers.VECriteriaTriggers;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.loot.modifiers.AnimalFatLootModifier;
import com.veteam.voluminousenergy.loot.modifiers.MysteriousMultiplierModifier;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifierManager;
import net.neoforged.neoforge.resource.NeoForgeReloadListeners;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Crops, gases, piston behaviour, loot, the laser advancement, and the laser's draw (test plan
 * T2.17 for the Dimensional Laser, and T2.29 to T2.35).
 */
public class WorldTests {

    private WorldTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("world_rice_bone_meal", 20, WorldTests::riceBoneMeal));
        tests.add(GameTestDefinition.large("world_gas_rises", 400, WorldTests::gasRises));
        tests.add(GameTestDefinition.of("world_gas_washes_away", 100, WorldTests::gasWashesAway));
        tests.add(GameTestDefinition.large("world_piston_pops", 40, WorldTests::pistonPops));
        tests.add(GameTestDefinition.of("world_loot_modifier_conditions", 20, WorldTests::lootModifierConditions));
        tests.add(GameTestDefinition.of("world_loot_animal_fat_rates", 20, WorldTests::lootAnimalFatRates));
        tests.add(GameTestDefinition.large("world_dimensional_laser", 2400, WorldTests::dimensionalLaser));
    }

    /**
     * T2.29: bone meal grows rice.
     */
    private static void riceBoneMeal(GameTestHelper helper) {
        Block rice = VEBlocks.RICE_CROP.get();
        BlockPos lower = new BlockPos(2, 1, 2);
        BlockPos upper = lower.above();
        helper.setBlock(lower.below(), Blocks.DIRT);
        helper.setBlock(lower, rice.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, true));
        helper.setBlock(upper, rice.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
        BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), helper.getLevel(), helper.absolutePos(upper), null);
        helper.assertValueEqual(helper.getBlockState(upper).getValue(BlockStateProperties.AGE_2), 1, "rice age after bone meal");
        GameTestLog.pass(helper, "world_rice_bone_meal", "");
    }

    /**
     * T2.30: natural gas released on a stone floor climbs, and never sinks below its source. A gas
     * source spreads sideways first and rises through its flowing neighbours, as on 26.2.
     */
    private static void gasRises(GameTestHelper helper) {
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
            }
        }
        BlockPos source = new BlockPos(4, 1, 4);
        helper.setBlock(source, VEFluids.FLOWING_NATURAL_GAS_BLOCK_REG.get());
        helper.succeedWhen(() -> {
            int highest = 0;
            for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(1, 1, 1), new BlockPos(7, 7, 7))) {
                if (helper.getBlockState(pos).getFluidState().getType().isSame(VEFluids.NATURAL_GAS_REG.get())) {
                    highest = Math.max(highest, pos.getY());
                }
            }
            helper.assertTrue(highest >= 3, "gas has not climbed: highest gas at y " + highest);
            helper.assertBlockPresent(Blocks.STONE, source.below());
            GameTestLog.note("world_gas_rises", "highest gas at y " + highest);
        });
    }

    /**
     * T2.31: gas washes away a block in #minecraft:washed_away_by_fluids and stops at a solid block.
     */
    private static void gasWashesAway(GameTestHelper helper) {
        BlockPos washableGas = new BlockPos(1, 1, 2);
        BlockPos solidGas = new BlockPos(3, 1, 2);
        helper.setBlock(washableGas.above(), Blocks.OAK_SAPLING);
        helper.setBlock(solidGas.above(), Blocks.STONE);
        helper.setBlock(washableGas, VEFluids.FLOWING_NATURAL_GAS_BLOCK_REG.get());
        helper.setBlock(solidGas, VEFluids.FLOWING_NATURAL_GAS_BLOCK_REG.get());
        helper.succeedWhen(() -> {
            helper.assertTrue(helper.getBlockState(washableGas.above()).getFluidState().getType().isSame(VEFluids.NATURAL_GAS_REG.get()), "gas did not wash away the sapling");
            helper.assertBlockPresent(Blocks.STONE, solidGas.above());
            GameTestLog.note("world_gas_washes_away", "");
        });
    }

    /**
     * T2.32: a piston pops every fluid block of the mod and the Pressure Ladder.
     */
    private static void pistonPops(GameTestHelper helper) {
        List<Block> targets = new ArrayList<>();
        for (DeferredHolder<Block, ? extends Block> fluidBlock : VEFluids.VE_FLUID_BLOCKS.getEntries()) {
            targets.add(fluidBlock.get());
        }
        targets.add(VEBlocks.PRESSURE_LADDER.get());
        List<BlockPos> targetPositions = new ArrayList<>();
        for (int index = 0; index < targets.size(); index++) {
            int x = 1 + index % 7;
            int y = 1 + index / 7 % 6;
            BlockPos piston = new BlockPos(x, y, 1);
            BlockPos target = piston.south();
            helper.setBlock(piston, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.SOUTH));
            BlockState targetState = targets.get(index).defaultBlockState();
            if (targetState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                targetState = targetState.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
            }
            helper.setBlock(target, targetState);
            helper.setBlock(piston.north(), Blocks.REDSTONE_BLOCK);
            targetPositions.add(target);
        }
        helper.succeedWhen(() -> {
            for (int index = 0; index < targets.size(); index++) {
                Block present = helper.getBlockState(targetPositions.get(index)).getBlock();
                helper.assertTrue(present != targets.get(index), MachineKit.name(targets.get(index)) + " was not popped by the piston");
            }
            GameTestLog.note("world_piston_pops", targets.size() + " blocks");
        });
    }

    /**
     * T2.33: every loot modifier of the mod reads back with one all_of condition that holds the
     * 26.2 conditions in their old order.
     */
    private static void lootModifierConditions(GameTestHelper helper) {
        LootModifierManager manager = helper.getLevel().getServer().getServerResources().managers().getListener(NeoForgeReloadListeners.LOOT_MODIFIERS_KEY);
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        int checked = 0;
        for (IGlobalLootModifier modifier : manager.getSortedModifiers()) {
            if (!(modifier instanceof AnimalFatLootModifier) && !(modifier instanceof MysteriousMultiplierModifier)) {
                continue;
            }
            String id = manager.getId(modifier).toString();
            JsonObject encoded = encode(modifier, ops).getAsJsonObject();
            JsonObject condition = encoded.getAsJsonObject("condition");
            helper.assertValueEqual(condition.get("type").getAsString(), "minecraft:all_of", id + " condition type");
            List<String> terms = new ArrayList<>();
            condition.getAsJsonArray("terms").forEach(term -> terms.add(term.getAsJsonObject().get("type").getAsString()));
            String expectedLast = "neoforge:loot_table_id";
            helper.assertTrue(terms.size() == 2 && (terms.get(1).equals(expectedLast) || id.contains("sulfur_cube")), id + " terms " + terms);
            ++checked;
        }
        helper.assertTrue(checked >= 20, "only " + checked + " mod loot modifiers loaded");
        GameTestLog.pass(helper, "world_loot_modifier_conditions", checked + " modifiers");
    }

    @SuppressWarnings("unchecked")
    private static JsonElement encode(IGlobalLootModifier modifier, RegistryOps<JsonElement> ops) {
        MapCodec<IGlobalLootModifier> codec = (MapCodec<IGlobalLootModifier>) modifier.codec();
        return codec.codec().encodeStart(ops, modifier).getOrThrow();
    }

    /**
     * T2.34: a mooshroom killed by a player drops Animal Fat half the time without Looting, and
     * nearly always with Looting III.
     */
    private static void lootAnimalFatRates(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = MachineKit.mockPlayer(helper);
        MushroomCow mooshroom = helper.spawn(EntityTypes.MOOSHROOM, new BlockPos(2, 1, 2));
        LootTable table = level.getServer().reloadableRegistries().getLootTable(mooshroom.getLootTable().orElseThrow());

        double plain = animalFatRate(level, table, mooshroom, player, 2000);
        Holder<Enchantment> looting = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(looting, 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, sword);
        double withLooting = animalFatRate(level, table, mooshroom, player, 2000);

        GameTestLog.value("world_loot_animal_fat_rates", "looting_0", plain);
        GameTestLog.value("world_loot_animal_fat_rates", "looting_3", withLooting);
        helper.assertTrue(Math.abs(plain - 0.5) <= 0.05, "animal fat rate without looting " + plain);
        helper.assertTrue(withLooting >= 0.9, "animal fat rate with looting III " + withLooting);
        GameTestLog.pass(helper, "world_loot_animal_fat_rates", "looting 0: " + plain + ", looting 3: " + withLooting);
    }

    private static double animalFatRate(ServerLevel level, LootTable table, MushroomCow mooshroom, ServerPlayer player, int rolls) {
        int withFat = 0;
        for (int roll = 0; roll < rolls; roll++) {
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, mooshroom)
                    .withParameter(LootContextParams.ORIGIN, mooshroom.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player))
                    .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .create(LootContextParamSets.ENTITY);
            if (table.getRandomItems(params).stream().anyMatch(stack -> stack.is(VEItems.ANIMAL_FAT.get()))) {
                ++withFat;
            }
        }
        return (double) withFat / rolls;
    }

    /**
     * T2.17 for the Dimensional Laser, T2.35, and the laser half of T2.39: the laser builds over its
     * three Solarium casings, its construction trigger awards the advancement, and the laser then
     * draws Crude Oil from a chunk saved with 50 000 mB.
     */
    private static void dimensionalLaser(GameTestHelper helper) {
        BlockPos laserPos = new BlockPos(4, 4, 4);
        for (int depth = 1; depth <= 3; depth++) {
            helper.setBlock(laserPos.below(depth), VEBlocks.SOLARIUM_MACHINE_CASING_BLOCK.get());
        }
        VETileEntity laser = MachineKit.place(helper, laserPos, VEBlocks.DIMENSIONAL_LASER.block().get());
        ServerPlayer player = MachineKit.mockPlayer(helper);
        helper.onEachTick(() -> laser.getEnergy().set(laser.getEnergy().getCapacity()));

        // The gametest world is flat, so its climate is zero everywhere; use a saved deposit instead of a scan.
        ChunkPos chunk = ChunkPos.containing(helper.absolutePos(laserPos));
        ChunkFluid scanned = new ChunkFluid(chunk, List.of(new SingleChunkFluid(VEFluids.CRUDE_OIL_REG.get(), 50_000)));
        ChunkFluids.getInstance().add(scanned);
        ItemStack chip = new ItemStack(VEItems.RFID_CHIP.get());
        chip.set(VEDataComponents.CHUNK_FLUID_DATA, new ChunkFluidData(chunk.x(), chunk.z(), new ArrayList<>()));
        laser.getInventory().setStack(2, chip);
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements()
                .get(Identifier.fromNamespaceAndPath("voluminousenergy", "machine/activate_dimensional_laser"));

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertValueEqual(laser.getData(VEAttachments.BUILD_TICK), 1000, "laser build tick"))
                .thenExecute(() -> {
                    // A mock player cannot join the level, so fire the laser's trigger for it as the laser does for nearby players.
                    VECriteriaTriggers.CONSTRUCT_DIMENSIONAL_LASER_TRIGGER.get().trigger(player, 3);
                    helper.assertTrue(player.getAdvancements().getOrStartProgress(advancement).isDone(), "construction advancement not awarded");
                })
                .thenWaitUntil(() -> helper.assertTrue(laser.getFluidStackFromTank(0).is(VEFluids.CRUDE_OIL_REG.get()), "the laser has not drawn crude oil"))
                .thenExecute(() -> GameTestLog.note("world_dimensional_laser", laser.getFluidStackFromTank(0).getAmount() + " mB of crude oil"))
                .thenSucceed();
    }
}
