package com.veteam.voluminousenergy.blocks.blocks.machines.tanks;

import com.veteam.voluminousenergy.blocks.blocks.util.FaceableBlock;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.NumberUtil;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.text.DecimalFormat;
import java.util.List;

public class TankBlock extends FaceableBlock {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");

    public TankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider menuProvider && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException(this.getClass().getName() + " named container provider is missing!");
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag tag = stack.getTag();
        FluidStack fluid;
        if (tag != null) {
            CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
            CompoundTag tankTag = blockEntityTag.getCompound("tank");
            FluidTank tank = new FluidTank(0);
            tank.readFromNBT(tankTag);
            fluid = tank.getFluid();
        } else {
            fluid = FluidStack.EMPTY;
        }
        int tankCapacity = this.getTankCapacity() * 1000;

        if (Config.SHORTEN_ITEM_TOOLTIP_VALUES.get()) {
            tooltip.add(
                    TextUtil.translateString(fluid.getTranslationKey()).copy()
                            .append(": ")
                            .append(NumberUtil.numberToString4Fluids(fluid.getAmount()))
                            .append(" / ")
                            .append(NumberUtil.numberToString4Fluids(tankCapacity)
                            ));
        } else {
            String amount = String.format("%s mB", DECIMAL_FORMAT.format(fluid.getAmount()));
            String capacity = String.format("%s mB", DECIMAL_FORMAT.format(tankCapacity));
            tooltip.add(TextUtil.translateString(fluid.getTranslationKey()).copy().append(": " + amount + " / " + capacity));
        }
    }

    public int getTankCapacity() {
        return 0;
    }
}
