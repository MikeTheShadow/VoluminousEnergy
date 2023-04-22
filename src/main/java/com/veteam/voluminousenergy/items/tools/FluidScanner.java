package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.setup.VESetup;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class FluidScanner extends Item {

    public FluidScanner() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
        );
    }

    public int getUseDuration(ItemStack p_40680_) {
        return 72000;
    }

    public @NotNull UseAnim getUseAnimation(ItemStack p_40678_) {
        return UseAnim.CROSSBOW;
    }

    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {

        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        ChunkAccess chunkAccess = level.getChunk(blockpos);

        Player player = useOnContext.getPlayer();

        if (player == null || level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);

        BlockPos pos = new BlockPos(16 * chunkAccess.getPos().x, 320, 16 * chunkAccess.getPos().z);

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
                return InteractionResult.sidedSuccess(false);
            }

            PlayerInvWrapper inventory = new PlayerInvWrapper(player.getInventory());
            int freeSlot = player.getInventory().getFreeSlot();

            if (freeSlot == -1) {
                player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.rfid.inventory_full"));
            } else {
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    ItemStack itemStack = inventory.getStackInSlot(slot);
                    if (itemStack.getItem() instanceof RFIDChip) {
                        if (itemStack.hasTag()) {
                            continue;
                        }
                        if (itemStack.getOrCreateTag().contains("ve_x")) {
                            continue;
                        }
                        if (!itemStack.getOrCreateTag().contains("ve_x")) {
                            itemStack.setCount(itemStack.getCount() - 1);
                            ItemStack dataStack = new ItemStack(VEItems.RFID_CHIP.get(), 1);
                            CompoundTag data = dataStack.getOrCreateTag();

                            int x = 0;

                            chunkFluid.save(data);

                            data.putInt("ve_x", chunkAccess.getPos().x);
                            data.putInt("ve_z", chunkAccess.getPos().z);
                            dataStack.setTag(data);
                            inventory.insertItem(freeSlot, dataStack, false);

                            player.sendSystemMessage(TextUtil.translateString(ChatFormatting.GREEN, "text.voluminousenergy.rfid.write_success"));
                        }
                        return InteractionResult.sidedSuccess(false);
                    }
                }
            }
            player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.fluid_scanner.needs_empty_rfid"));
            return InteractionResult.sidedSuccess(false);
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

        CompoundTag tag = hand.getOrCreateTag();

        if (tag.contains("ve_x")) {
            tag.remove("ve_x");
            tag.remove("ve_z");
        }
        tag.putInt("ve_x", chunkAccess.getPos().x);
        tag.putInt("ve_z", chunkAccess.getPos().z);
        fluid.save(tag);
        hand.setTag(tag);

        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> componentList, @NotNull TooltipFlag tooltipFlag) {
        CompoundTag tag = itemStack.getOrCreateTag();

        if (tag.contains("ve_x")) {

            int x = tag.getInt("ve_x");
            int z = tag.getInt("ve_z");

            ChunkFluid fluid = new ChunkFluid(tag);
            //componentList.add(Component.nullToEmpty(""));
            fluid.getFluids().forEach(f -> componentList.add(TextUtil.fluidNameAndAmountWithUnitsAndColours(f)));

            componentList.add(Component.nullToEmpty("Chunk X: " + x + " | Chunk Z: " + z));
        }

        super.appendHoverText(itemStack, level, componentList, tooltipFlag);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return true;
    }
}
