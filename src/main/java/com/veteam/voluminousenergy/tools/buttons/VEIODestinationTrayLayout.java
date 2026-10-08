package com.veteam.voluminousenergy.tools.buttons;

import com.veteam.voluminousenergy.util.VEFaceIO.Mode;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class VEIODestinationTrayLayout {
    public static final List<Direction> LEFT_FACES = List.of(Direction.UP, Direction.WEST, Direction.SOUTH);
    public static final List<Direction> RIGHT_FACES = List.of(Direction.NORTH, Direction.EAST, Direction.DOWN);
    public static final List<Direction> FACES = List.of(Direction.UP, Direction.WEST, Direction.SOUTH,
            Direction.NORTH, Direction.EAST, Direction.DOWN);
    private static final int HEADER_HEIGHT = 40;
    private static final int CELL_WIDTH = 20;
    private static final int CELL_HEIGHT = 24;
    private static final int TRAY_GAP = 5;
    private final EnumMap<Direction, Rect2i> trays = new EnumMap<>(Direction.class);
    private final List<Rect2i> banks = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();

    public VEIODestinationTrayLayout(Rect2i machine, int viewportWidth, int viewportHeight, List<Direction> assignments) {
        int sideSpace = Math.min(machine.getX(), viewportWidth - machine.getX() - machine.getWidth());
        int trayWidth = Math.max(24, Math.min(96, sideSpace - 8));
        int columns = Math.max(1, (trayWidth - 4) / CELL_WIDTH);
        addBank(LEFT_FACES, machine.getX() - trayWidth - 4, machine.getY() + 4,
                trayWidth, columns, viewportHeight, assignments);
        addBank(RIGHT_FACES, machine.getX() + machine.getWidth() + 4, machine.getY() + 4,
                trayWidth, columns, viewportHeight, assignments);
    }

    private void addBank(List<Direction> faces, int left, int preferredTop, int trayWidth, int columns,
            int viewportHeight, List<Direction> assignments) {
        EnumMap<Direction, Integer> heights = new EnumMap<>(Direction.class);
        int bankHeight = TRAY_GAP * (faces.size() - 1);
        for (Direction face : faces) {
            int count = (int) assignments.stream().filter(direction -> direction == face).count();
            int rows = (count + columns - 1) / columns;
            int trayHeight = HEADER_HEIGHT + Math.max(12, rows * CELL_HEIGHT) + 2;
            heights.put(face, trayHeight);
            bankHeight += trayHeight;
        }
        int top = Math.max(4, Math.min(preferredTop, viewportHeight - bankHeight - 4));
        banks.add(new Rect2i(left, top, trayWidth, bankHeight));
        for (Direction face : faces) {
            int trayHeight = heights.get(face);
            trays.put(face, new Rect2i(left, top, trayWidth, trayHeight));
            int position = 0;
            for (int portIndex = 0; portIndex < assignments.size(); portIndex++) {
                if (assignments.get(portIndex) == face) {
                    tokens.add(new Token(portIndex, new Rect2i(left + 3 + position % columns * CELL_WIDTH,
                            top + HEADER_HEIGHT + position / columns * CELL_HEIGHT, 18, 22)));
                    position++;
                }
            }
            top += trayHeight + TRAY_GAP;
        }
    }

    public Rect2i getFaceArea(Direction face) {
        return trays.get(face);
    }

    public List<Rect2i> getBankAreas() {
        return List.copyOf(banks);
    }

    public List<Token> getTokens() {
        return List.copyOf(tokens);
    }

    public Rect2i getModeArea(Direction face, Mode mode) {
        Rect2i tray = getFaceArea(face);
        int topOffset = switch (mode) {
            case PUSH -> 16;
            case PULL -> 28;
            case PASSIVE, BOTH -> throw new IllegalArgumentException("Only Push and Pull have checkboxes");
        };
        return new Rect2i(tray.getX() + 4, tray.getY() + topOffset, tray.getWidth() - 8, 10);
    }

    public @Nullable ModeTarget getModeAt(double mouseX, double mouseY) {
        for (Direction face : FACES) {
            for (Mode mode : List.of(Mode.PUSH, Mode.PULL)) {
                if (contains(getModeArea(face, mode), mouseX, mouseY)) {
                    return new ModeTarget(face, mode);
                }
            }
        }
        return null;
    }

    public @Nullable Token getTokenAt(double mouseX, double mouseY) {
        for (Token token : tokens) {
            if (contains(token.bounds(), mouseX, mouseY)) {
                return token;
            }
        }
        return null;
    }

    public @Nullable Direction getFaceAt(double mouseX, double mouseY) {
        for (Direction face : FACES) {
            if (contains(trays.get(face), mouseX, mouseY)) {
                return face;
            }
        }
        return null;
    }

    public boolean contains(double mouseX, double mouseY) {
        return banks.stream().anyMatch(bank -> contains(bank, mouseX, mouseY));
    }

    private static boolean contains(Rect2i area, double x, double y) {
        return x >= area.getX() && x < area.getX() + area.getWidth()
                && y >= area.getY() && y < area.getY() + area.getHeight();
    }

    public static int faceColour(Direction face) {
        return switch (face) {
            case SOUTH -> 0xFFE8B34D;
            case NORTH -> 0xFF5BD1D7;
            case WEST -> 0xFFB394F6;
            case EAST -> 0xFFF08B78;
            case UP -> 0xFFC4DB6A;
            case DOWN -> 0xFF79ACEC;
        };
    }

    public record Token(int portIndex, Rect2i bounds) {
    }

    public record ModeTarget(Direction face, Mode mode) {
    }
}
