package com.veteam.voluminousenergy.tools.buttons;

import com.veteam.voluminousenergy.tools.networking.packets.PortConfigurationPacket;
import com.veteam.voluminousenergy.tools.networking.packets.PortConfigurationPacket.PortConfigurationPayload;
import com.veteam.voluminousenergy.tools.networking.packets.PortConfigurationPacket.Setting;
import com.veteam.voluminousenergy.util.VEIOPort;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class VEIOInteraction {
    public static final int SELECTED_COLOUR = 0xFF55FF55;
    private static final double DRAG_DISTANCE_SQUARED = 16.0;
    private final Consumer<PortConfigurationPayload> sender;
    private VEIOPort selected;
    private VEIOPort pressed;
    private double pressX;
    private double pressY;
    private boolean dragging;

    public VEIOInteraction(Consumer<PortConfigurationPayload> sender) {
        this.sender = sender;
    }

    public @Nullable VEIOPort getSelectedPort() {
        return selected;
    }

    public void select(@Nullable VEIOPort port) {
        selected = port;
        cancelGesture();
    }

    public void press(VEIOPort port, double mouseX, double mouseY, int button) {
        cancelGesture();
        if (button != 0 && button != 1) {
            return;
        }
        selected = port;
        if (button == 1) {
            send(port, Setting.UNASSIGN, 0);
            return;
        }
        pressed = port;
        pressX = mouseX;
        pressY = mouseY;
    }

    public void move(double mouseX, double mouseY) {
        if (pressed == null) {
            return;
        }
        double distanceX = mouseX - pressX;
        double distanceY = mouseY - pressY;
        dragging |= distanceX * distanceX + distanceY * distanceY > DRAG_DISTANCE_SQUARED;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void release(double mouseX, double mouseY, @Nullable Direction target) {
        if (pressed == null) {
            return;
        }
        move(mouseX, mouseY);
        VEIOPort port = pressed;
        boolean wasDragging = dragging;
        cancelGesture();
        if (wasDragging) {
            if (target != null) {
                send(port, Setting.FACE, target.get3DDataValue());
            }
            return;
        }
    }

    public void cancelGesture() {
        pressed = null;
        dragging = false;
    }

    private void send(VEIOPort port, Setting setting, int value) {
        if (PortConfigurationPacket.apply(port, setting, value)) {
            sender.accept(new PortConfigurationPayload(port.isFluid(), port.getSlotNum(), setting, value));
        }
    }
}
