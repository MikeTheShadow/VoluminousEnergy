package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.WorldUtil;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FluidScanner extends Item {

    public FluidScanner() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
        );
    }

    public @NotNull ItemUseAnimation getUseAnimation(ItemStack p_40678_) {
        return ItemUseAnimation.CROSSBOW;
    }

    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {

        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        ChunkAccess chunkAccess = level.getChunk(blockpos);

        Player player = useOnContext.getPlayer();

        if (player == null || level.isClientSide()) return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;

        BlockPos pos = new BlockPos(16 * chunkAccess.getPos().x(), 320, 16 * chunkAccess.getPos().z());

        HashMap<WorldUtil.ClimateParameters, Double> climateMap = WorldUtil.sampleClimate(level, pos);
        StringBuilder climateString = new StringBuilder();
        climateString.append("\nC: " + climateMap.get(WorldUtil.ClimateParameters.CONTINENTALNESS));
        climateString.append("\nE: " + climateMap.get(WorldUtil.ClimateParameters.EROSION));
        climateString.append("\nH: " + climateMap.get(WorldUtil.ClimateParameters.HUMIDITY));
        climateString.append("\nT: " + climateMap.get(WorldUtil.ClimateParameters.TEMPERATURE));


        if (player.isShiftKeyDown()) {
            ChunkFluid chunkFluid = ChunkFluids.getInstance().getChunkFluid(chunkAccess.getPos());
            if (chunkFluid == null) {
                player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.rfid.chunk_not_scanned"));
                return InteractionResult.SUCCESS_SERVER;
            }

            PlayerInvWrapper inventory = new PlayerInvWrapper(player.getInventory());
            int freeSlot = player.getInventory().getFreeSlot();

            if (freeSlot == -1) {
                player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.rfid.inventory_full"));
            } else {
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    ItemStack itemStack = inventory.getStackInSlot(slot);
                    if (itemStack.getItem() instanceof RFIDChip) {

                        ChunkFluidData data = itemStack.get(VEDataComponents.CHUNK_FLUID_DATA);

                        if (data == null) {
                            itemStack.setCount(itemStack.getCount() - 1);
                            ItemStack dataStack = new ItemStack(VEItems.RFID_CHIP.get(), 1);
                            data = new ChunkFluidData(chunkAccess.getPos().x(),
                                    chunkAccess.getPos().z(), new ArrayList<>());
                            dataStack.set(VEDataComponents.CHUNK_FLUID_DATA, data);
                            inventory.insertItem(freeSlot, dataStack, false);
                            player.sendSystemMessage(TextUtil.translateString(ChatFormatting.GREEN, "text.voluminousenergy.rfid.write_success"));
                        } else {
                            continue;
                        }
                        return InteractionResult.SUCCESS_SERVER;
                    }
                }
            }
            player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.fluid_scanner.needs_empty_rfid"));
            return InteractionResult.SUCCESS_SERVER;
        }

        player.sendSystemMessage(TextUtil.translateString(ChatFormatting.YELLOW, "text.voluminousenergy.fluid_scanner.scanning")
                .copy()
                .append(Component.nullToEmpty(ChatFormatting.YELLOW + "..."))
        );

        ChunkFluid fluid = WorldUtil.getFluidFromPosition(level, pos);

        StringBuilder builder = new StringBuilder();
        for (SingleChunkFluid singleChunkFluid : fluid.getFluids()) {
            builder.append(TextUtil.fluidNameAndAmountWithUnitsAndColours(singleChunkFluid).getString());
        }

        player.sendSystemMessage(Component.nullToEmpty(builder.toString()));

        ItemStack hand = useOnContext.getItemInHand();
        ChunkFluidData data = new ChunkFluidData(chunkAccess.getPos().x(),chunkAccess.getPos().z(),fluid.getFluids().stream()
                .map(c -> new FluidStack(c.getFluid(),c.getAmount())).toList());
        hand.set(VEDataComponents.CHUNK_FLUID_DATA,data);

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext pContext, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> componentList, @NotNull TooltipFlag tooltipFlag) {


        ChunkFluidData data = itemStack.get(VEDataComponents.CHUNK_FLUID_DATA);

        if (data != null) {
            ChunkFluid fluid = new ChunkFluid(data);
            fluid.getFluids().forEach(f -> componentList.accept(TextUtil.fluidNameAndAmountWithUnitsAndColours(f)));
            componentList.accept(Component.nullToEmpty("Chunk X: " + data.x() + " | Chunk Z: " + data.z()));
        }

        super.appendHoverText(itemStack, pContext, tooltipDisplay, componentList, tooltipFlag);
    }
}
