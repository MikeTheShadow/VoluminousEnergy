package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.util.RegistryLookups;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class VETileFactoryBuilder {




    public static class VETileEntity extends BlockEntity implements MenuProvider {

        public VETileEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState) {
            super(entityType, blockPos, blockState);
        }

        @Override
        public @Nonnull Component getDisplayName() {
            ResourceLocation name = RegistryLookups.getBlockEntityTypeKey(this);
            if (name == null)
                throw new NotImplementedException("Missing registry name for class: " + this.getClass().getName());
            return Component.nullToEmpty(name.getPath());
        }
        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return null;
        }
    }

}
