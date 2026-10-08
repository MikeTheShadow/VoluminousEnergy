package com.veteam.voluminousenergy.util;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface VEIOPort {
    int getSlotNum();
    String getHoverName();
    boolean isFluid();
    @Nullable Direction getDirection();
    void setDirection(@Nullable Direction direction);
    default boolean isAssigned() {
        return getDirection() != null;
    }
    boolean canPush();
    boolean canPull();
}
