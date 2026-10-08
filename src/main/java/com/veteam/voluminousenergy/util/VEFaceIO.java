package com.veteam.voluminousenergy.util;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.List;

public class VEFaceIO {
    public enum Mode {
        PASSIVE, PUSH, PULL, BOTH;

        public boolean accepts(VEIOPort port) {
            return switch (this) {
                case PASSIVE -> false;
                case PUSH -> port.canPush();
                case PULL -> port.canPull();
                case BOTH -> port.canPush() || port.canPull();
            };
        }
    }

    private final EnumMap<Direction, Mode> modes = new EnumMap<>(Direction.class);

    public VEFaceIO() {
        for (Direction face : Direction.values()) {
            modes.put(face, Mode.PASSIVE);
        }
    }

    public Mode getMode(Direction face) {
        return modes.get(face);
    }

    public boolean isEnabled(Direction face, Mode action) {
        Mode state = getMode(face);
        return switch (action) {
            case PUSH -> state == Mode.PUSH || state == Mode.BOTH;
            case PULL -> state == Mode.PULL || state == Mode.BOTH;
            case PASSIVE, BOTH -> false;
        };
    }

    public void setEnabled(Direction face, Mode action, boolean enabled) {
        if (action != Mode.PUSH && action != Mode.PULL) {
            throw new IllegalArgumentException("Only Push and Pull are toggles");
        }
        boolean push = action == Mode.PUSH ? enabled : isEnabled(face, Mode.PUSH);
        boolean pull = action == Mode.PULL ? enabled : isEnabled(face, Mode.PULL);
        modes.put(face, push ? pull ? Mode.BOTH : Mode.PUSH : pull ? Mode.PULL : Mode.PASSIVE);
    }

    public static boolean supports(List<VEIOPort> ports, Direction face, Mode mode) {
        return mode == Mode.PUSH || mode == Mode.PULL
                && ports.stream().anyMatch(port -> port.getDirection() == face && mode.accepts(port));
    }

    public boolean shouldTransfer(VEIOPort port) {
        return port.isAssigned() && getMode(port.getDirection()).accepts(port);
    }

    public Mode getTransferMode(VEIOPort port, long transferCycle) {
        if (!port.isAssigned()) {
            return Mode.PASSIVE;
        }
        boolean push = isEnabled(port.getDirection(), Mode.PUSH) && port.canPush();
        boolean pull = isEnabled(port.getDirection(), Mode.PULL) && port.canPull();
        if (push && pull) {
            return (transferCycle & 0x1L) == 0x0L ? Mode.PUSH : Mode.PULL;
        }
        return push ? Mode.PUSH : pull ? Mode.PULL : Mode.PASSIVE;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        for (Direction face : Direction.values()) {
            tag.putInt(face.getName(), getMode(face).ordinal());
        }
        return tag;
    }

    public void load(CompoundTag tag) {
        for (Direction face : Direction.values()) {
            modes.put(face, Mode.values()[tag.getInt(face.getName())]);
        }
    }
}
