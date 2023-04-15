package com.veteam.voluminousenergy.items.tools;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.util.NumberUtil;
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
import net.minecraft.world.level.ChunkPos;
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



        if(player.isShiftKeyDown()) {
            ChunkFluid chunkFluid = ChunkFluids.getInstance().getChunkFluid(chunkAccess.getPos());
            if(chunkFluid == null) {
                player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.rfid.chunk_not_scanned"));
                return InteractionResult.sidedSuccess(false);
            }

            PlayerInvWrapper inventory = new PlayerInvWrapper(player.getInventory());
            int freeSlot = player.getInventory().getFreeSlot();

            if(freeSlot == -1) {
                // TODO translations
                player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED, "text.voluminousenergy.rfid.inventory_full")/*, player.getUUID()*/);
            } else {
                for(int slot = 0; slot < inventory.getSlots(); slot++) {
                    ItemStack itemStack = inventory.getStackInSlot(slot);
                    if(itemStack.getItem() instanceof RFIDChip) {
                        if(itemStack.hasTag()) {
                            continue;
                        }
                        if(itemStack.getOrCreateTag().contains("ve_x")){
                            continue;
                        }
                        if(!itemStack.getOrCreateTag().contains("ve_x")) {
                            itemStack.setCount(itemStack.getCount() - 1);
                            ItemStack dataStack = new ItemStack(VEItems.RFID_CHIP.get(),1);
                            CompoundTag data = dataStack.getOrCreateTag();

                            int x = 0;

                            chunkFluid.save(data);

                            data.putInt("ve_x",chunkAccess.getPos().x);
                            data.putInt("ve_z",chunkAccess.getPos().z);
                            dataStack.setTag(data);
                            inventory.insertItem(freeSlot,dataStack,false);
                            //player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Written to a RFID Chip!"),player.getUUID());
                            player.sendSystemMessage(TextUtil.translateString(ChatFormatting.GREEN, "text.voluminousenergy.rfid.write_success"));
                        }
                        return InteractionResult.sidedSuccess(false);
                    }
                }
            }
            player.sendSystemMessage(TextUtil.translateString(ChatFormatting.RED,"text.voluminousenergy.fluid_scanner.needs_empty_rfid" ));
            return InteractionResult.sidedSuccess(false);
        }

        player.sendSystemMessage(TextUtil.translateString(ChatFormatting.YELLOW, "text.voluminousenergy.fluid_scanner.scanning")
                .copy()
                .append(Component.nullToEmpty(ChatFormatting.YELLOW + "..."))
                /*player.getUUID()*/
        );

        player.sendSystemMessage(Component.nullToEmpty(climateString.toString())/*, player.getUUID()*/);

        StringBuilder message = new StringBuilder();
        ChunkFluid fluid = WorldUtil.getFluidFromPosition(level,pos);
        for (SingleChunkFluid singleChunkFluid : fluid.getFluids()) {
            message.append("\nFound Fluid: ").append(singleChunkFluid.getFluid().getFluidType().getDescriptionId()).append(" Amount: ").append(NumberUtil.formatNumber(singleChunkFluid.getAmount())).append(" mB");
        }
        player.sendSystemMessage(Component.nullToEmpty(message.toString())/*, player.getUUID()*/);

        ItemStack hand = useOnContext.getItemInHand();

        CompoundTag tag = hand.getOrCreateTag();

        if(tag.contains("ve_x")) {
            tag.remove("ve_x");
            tag.remove("ve_z");
        }
        tag.putInt("ve_x",chunkAccess.getPos().x);
        tag.putInt("ve_z",chunkAccess.getPos().z);
        fluid.save(tag);
        hand.setTag(tag);

//        StringBuilder builder = new StringBuilder("______________MAP______________\n");
//
//        int mapSize = 16;
//        int middle = mapSize / 2;
//
//        for (int x = 1; x < mapSize; x++) {
//            for (int z = 1; z < mapSize; z++) {
//                pos = new BlockPos(
//                        16 * (chunkAccess.getPos().x - middle + x),
//                        320,
//                        16 * (chunkAccess.getPos().z - middle + z));
//                var items = WorldUtil.queryForFluids(level, pos);
//                if (items.size() > 0) {
//
//                    Fluid fluid = items.get(0).getA();
//                    if(fluid.isSame(VEFluids.CRUDE_OIL_REG.get().getFlowing())) {
//                        builder.append(" C |");
//                    } else if(fluid.isSame(WATER.getFlowing())) {
//                        builder.append(" W |");
//                    } else if(fluid.isSame(LAVA.getFlowing())) {
//                        builder.append(" L |");
//                    } else {
//                        builder.append(" ? |");
//                    }
//                } else {
//                    builder.append(" 0 |");
//                }
//            }
//            builder.append("\n");
//        }
//
//        player.sendMessage(new TextComponent(builder.toString()), player.getUUID());

//        ChunkFluid chunkFluid = chunkFluids.getOrCreateChunkFluid(serverLevel,new ChunkPos(blockpos));
//
//        FluidStack fluid = chunkFluid.getFluid();

        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> componentList, @NotNull TooltipFlag tooltipFlag) {
        CompoundTag tag = itemStack.getOrCreateTag();

        if(tag.contains("ve_x")) {

            int x = tag.getInt("ve_x");
            int z = tag.getInt("ve_z");

            ChunkFluid fluid = new ChunkFluid(tag);
            if(fluid == null) {
                componentList.add(Component.nullToEmpty("Error chunk data is null / not saved!"));
            } else {
                componentList.add(Component.nullToEmpty(""));
                fluid.getFluids().forEach(f -> {
                    Component translatedComponent = TextUtil.translateString(f.getFluid().getFluidType().getDescriptionId());
                    String translatedString = translatedComponent.getString();
                    Component textComponent = Component.nullToEmpty(ChatFormatting.DARK_PURPLE + translatedString + ": " + ChatFormatting.LIGHT_PURPLE + f.getAmount());
                    componentList.add(textComponent);
                });
            }

            componentList.add(Component.nullToEmpty("Chunk X: " + x + " | Chunk Z: " + z));
        }

        super.appendHoverText(itemStack, level, componentList, tooltipFlag);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return true;
    }
}
