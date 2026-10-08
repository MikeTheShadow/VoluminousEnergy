package com.veteam.voluminousenergy.tools.buttons;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.VEIODestinationTrayLayout.ModeTarget;
import com.veteam.voluminousenergy.tools.buttons.VEIODestinationTrayLayout.Token;
import com.veteam.voluminousenergy.tools.networking.packets.FaceConfigurationPacket;
import com.veteam.voluminousenergy.tools.networking.packets.FaceConfigurationPacket.FaceConfigurationPayload;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VEFaceIO;
import com.veteam.voluminousenergy.util.VEFaceIO.Mode;
import com.veteam.voluminousenergy.util.VEIOPort;
import com.veteam.voluminousenergy.util.tiles.CapabilityMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class VEIODestinationTrays extends AbstractWidget {
    private final Rect2i machine;
    private final VETileEntity tile;
    private final AbstractContainerMenu menu;
    private final VEIOInteraction interaction;
    private final List<PortArea> ports;
    private final List<VEIOPort> ioPorts;
    private final VEFaceIO faceIO;
    private final Consumer<FaceConfigurationPayload> faceSender;
    private List<Direction> cachedFaces = List.of();
    private VEIODestinationTrayLayout layout;
    private VEIOPort hoveredPort;

    public VEIODestinationTrays(Rect2i machine, int viewportWidth, int viewportHeight, VETileEntity tile,
            AbstractContainerMenu menu, VEIOInteraction interaction, VEFaceIO faceIO, List<PortArea> ports,
            Consumer<FaceConfigurationPayload> faceSender) {
        super(0, 0, viewportWidth, viewportHeight, Component.translatable("gui.voluminousenergy.io.destination_trays"));
        this.machine = machine;
        this.tile = tile;
        this.menu = menu;
        this.interaction = interaction;
        this.ports = List.copyOf(ports);
        this.ioPorts = ports.stream().map(PortArea::port).toList();
        this.faceIO = faceIO;
        this.faceSender = faceSender;
        setOpen(false);
    }

    public void setOpen(boolean open) {
        visible = open;
        active = open;
    }

    public VEIODestinationTrayLayout getLayout() {
        List<Direction> faces = ports.stream().map(area -> area.port().getDirection()).toList();
        if (layout == null || !faces.equals(cachedFaces)) {
            cachedFaces = faces;
            layout = new VEIODestinationTrayLayout(machine, width, height, faces);
        }
        return layout;
    }

    public void setHoveredPort(@Nullable VEIOPort port) {
        hoveredPort = port;
    }

    public int getPortNumber(VEIOPort port) {
        for (int index = 0; index < ports.size(); index++) {
            if (ports.get(index).port() == port) {
                return index + 1;
            }
        }
        return 0;
    }

    public boolean contains(double mouseX, double mouseY) {
        return visible && getLayout().contains(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return contains(mouseX, mouseY);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return contains(mouseX, mouseY);
    }

    public @Nullable Direction getFaceAt(double mouseX, double mouseY) {
        return visible ? getLayout().getFaceAt(mouseX, mouseY) : null;
    }

    public @Nullable VEIOPort getPortAt(double mouseX, double mouseY) {
        Token token = visible ? getLayout().getTokenAt(mouseX, mouseY) : null;
        return token == null ? null : ports.get(token.portIndex()).port();
    }

    public @Nullable ModeTarget getModeAt(double mouseX, double mouseY) {
        ModeTarget target = visible ? getLayout().getModeAt(mouseX, mouseY) : null;
        return target != null && VEFaceIO.supports(ioPorts, target.face(), target.mode()) ? target : null;
    }

    public boolean clickMode(double mouseX, double mouseY, int button) {
        ModeTarget target = getModeAt(mouseX, mouseY);
        if (target == null || button != 0) {
            return false;
        }
        boolean enabled = !faceIO.isEnabled(target.face(), target.mode());
        if (FaceConfigurationPacket.apply(faceIO, ioPorts, target.face(), target.mode(), enabled)) {
            faceSender.accept(new FaceConfigurationPayload(target.face(), target.mode(), enabled));
            return true;
        }
        return false;
    }

    public List<Rect2i> getControlAreas() {
        return visible ? getLayout().getBankAreas() : List.of();
    }

    public int getConnectedPortCount(Direction face) {
        return (int) ports.stream().filter(area -> area.port().getDirection() == face).count();
    }

    public static Component faceLetter(Direction face) {
        return Component.translatable("gui.voluminousenergy.io.face_" + TextUtil.directionToLocalDirection(face) + "_short");
    }

    private @Nullable BlockState getNeighborState(Direction face) {
        Level level = tile.getLevel();
        if (level == null) {
            return null;
        }
        BlockPos neighbor = tile.getBlockPos().relative(CapabilityMap.toWorldDirection(face, tile));
        return level.hasChunkAt(neighbor) ? level.getBlockState(neighbor) : null;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        VEIODestinationTrayLayout current = getLayout();
        Direction hoveredFace = getFaceAt(mouseX, mouseY);
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        for (Direction face : VEIODestinationTrayLayout.FACES) {
            Rect2i area = current.getFaceArea(face);
            int left = area.getX();
            int top = area.getY();
            int colour = VEIODestinationTrayLayout.faceColour(face);
            int border = face == hoveredFace ? interaction.isDragging() ? VEIOInteraction.SELECTED_COLOUR : 0xFFDDE4EA : 0xFF58616B;
            graphics.fill(left, top, left + area.getWidth(), top + area.getHeight(), border);
            graphics.fill(left + 1, top + 1, left + area.getWidth() - 1, top + area.getHeight() - 1, 0xFF20262D);
            graphics.fill(left + 1, top + 1, left + area.getWidth() - 1, top + 2, colour);
            graphics.drawString(font, font.plainSubstrByWidth(TextUtil.translateDirection(face).getString(), area.getWidth() - 26),
                    left + 4, top + 4, colour, false);
            BlockState state = getNeighborState(face);
            if (state != null && state.getRenderShape() != RenderShape.INVISIBLE) {
                renderBlock(graphics, state, left + area.getWidth() - 12, top + 9);
            } else {
                graphics.drawCenteredString(font, state == null ? Component.literal("?") : faceLetter(face),
                        left + area.getWidth() - 12, top + 4, colour);
            }
            renderModeToggle(graphics, face, Mode.PUSH, mouseX, mouseY);
            renderModeToggle(graphics, face, Mode.PULL, mouseX, mouseY);
            if (getConnectedPortCount(face) == 0) {
                graphics.drawCenteredString(font, Component.translatable("gui.voluminousenergy.io.empty_tray"),
                        left + area.getWidth() / 2, top + 42, 0xFF77838F);
            }
        }
        for (Token token : current.getTokens()) {
            VEIOPort port = ports.get(token.portIndex()).port();
            boolean faceHovered = hoveredPort == null && port.getDirection() == hoveredFace;
            renderToken(graphics, port, token.bounds(), port == hoveredPort || faceHovered,
                    port == interaction.getSelectedPort());
        }
        graphics.pose().popPose();
    }

    private void renderBlock(GuiGraphics graphics, BlockState state, int centerX, int centerY) {
        graphics.flush();
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 32);
        graphics.pose().scale(8.0F, -8.0F, 8.0F);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(30.0F));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(225.0F));
        graphics.pose().translate(-0.5F, -0.5F, -0.5F);
        Lighting.setupFor3DItems();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, graphics.pose(), graphics.bufferSource(),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        graphics.flush();
        graphics.pose().popPose();
    }

    private void renderModeToggle(GuiGraphics graphics, Direction face, Mode mode, int mouseX, int mouseY) {
        if (!VEFaceIO.supports(ioPorts, face, mode)) {
            return;
        }
        Rect2i area = getLayout().getModeArea(face, mode);
        ModeTarget hovered = getModeAt(mouseX, mouseY);
        boolean activeMode = faceIO.isEnabled(face, mode);
        int colour = modeColour(mode);
        int border = hovered != null && hovered.face() == face && hovered.mode() == mode ? 0xFFFFFFFF
                : activeMode ? colour : 0xFF58616B;
        int left = area.getX();
        int top = area.getY();
        graphics.fill(left, top, left + 9, top + 9, border);
        graphics.fill(left + 1, top + 1, left + 8, top + 8, activeMode ? colour : 0xFF10171E);
        if (activeMode) {
            graphics.fill(left + 2, top + 4, left + 4, top + 6, 0xFF10171E);
            graphics.fill(left + 4, top + 3, left + 6, top + 5, 0xFF10171E);
            graphics.fill(left + 6, top + 2, left + 7, top + 4, 0xFF10171E);
        }
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.voluminousenergy.io."
                + (mode == Mode.PUSH ? "push_short" : "pull_short")), left + 13, top + 1,
                activeMode ? colour : 0xFFADB6BF, false);
    }

    private static int modeColour(Mode mode) {
        return switch (mode) {
            case PUSH -> 0xFFFFA43A;
            case PULL -> 0xFFC58BFF;
            case PASSIVE -> 0xFFADB6BF;
            case BOTH -> 0xFFDDC681;
        };
    }

    private static Component modeName(Mode mode) {
        return Component.translatable("gui.voluminousenergy.io." + switch (mode) {
            case PUSH -> "auto_push";
            case PULL -> "auto_pull";
            case PASSIVE -> "status_passive";
            case BOTH -> "auto_push_pull";
        });
    }

    private void renderToken(GuiGraphics graphics, VEIOPort port, Rect2i area, boolean hovered, boolean selected) {
        int left = area.getX();
        int top = area.getY();
        int border = selected ? VEIOInteraction.SELECTED_COLOUR : hovered ? 0xFFFFFFFF : 0xFF48525E;
        graphics.fill(left, top, left + area.getWidth(), top + area.getHeight(), border);
        graphics.fill(left + 1, top + 1, left + area.getWidth() - 1, top + area.getHeight() - 1, 0xFF10171E);
        graphics.pose().pushPose();
        graphics.pose().translate(left + 2, top + 1, 0);
        graphics.pose().scale(0.875F, 0.875F, 1.0F);
        renderPortIcon(graphics, port, 0, 0);
        graphics.pose().popPose();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        Font font = Minecraft.getInstance().font;
        int numberColour = port.isAssigned() ? VEIODestinationTrayLayout.faceColour(port.getDirection()) : 0xFF89939E;
        String number = Integer.toString(getPortNumber(port));
        graphics.drawString(font, number, left + (area.getWidth() - font.width(number)) / 2, top + 14, numberColour, false);
        graphics.pose().popPose();
    }

    private void renderPortIcon(GuiGraphics graphics, VEIOPort port, int left, int top) {
        if (port.isFluid()) {
            FluidStack fluid = tile.getFluidStackFromTank(port.getSlotNum());
            if (!fluid.isEmpty()) {
                graphics.flush();
                VERender.renderGuiTank(graphics, tile.getLevel(), tile.getBlockPos(), fluid, fluid.getAmount(), left, top, 0, 16, 16);
                return;
            }
            graphics.fill(left + 4, top + 2, left + 12, top + 14, 0xFF738DA3);
            graphics.fill(left + 5, top + 3, left + 11, top + 13, 0xFF18212B);
            graphics.fill(left + 6, top + 10, left + 10, top + 12, 0xFF517B9D);
            return;
        }
        ItemStack item = menu.getSlot(port.getSlotNum()).getItem();
        if (!item.isEmpty()) {
            graphics.renderItem(item, left, top);
            return;
        }
        graphics.fill(left + 3, top + 4, left + 13, top + 13, 0xFF8F969D);
        graphics.fill(left + 4, top + 5, left + 12, top + 12, 0xFF18212B);
    }

    public void renderDragToken(GuiGraphics graphics, int mouseX, int mouseY) {
        VEIOPort port = interaction.getSelectedPort();
        if (!interaction.isDragging() || port == null) {
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 700);
        renderToken(graphics, port, new Rect2i(Math.min(mouseX + 8, width - 22), Math.min(mouseY + 8, height - 26), 18, 22), false, true);
        graphics.pose().popPose();
    }

    public void renderFaceTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        Direction face = getFaceAt(mouseX, mouseY);
        if (face == null || interaction.isDragging()) {
            return;
        }
        BlockState state = getNeighborState(face);
        Component name = state == null ? Component.translatable("gui.voluminousenergy.io.neighbor_unavailable")
                : state.isAir() ? Component.translatable("gui.voluminousenergy.io.neighbor_empty") : state.getBlock().getName();
        List<Component> tooltip = new ArrayList<>();
        ModeTarget target = getModeAt(mouseX, mouseY);
        if (target != null) {
            tooltip.add(modeName(target.mode()).copy().withColor(modeColour(target.mode()) & 0xFFFFFF));
            tooltip.add(Component.translatable("gui.voluminousenergy.io.face_mode_help"));
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 800);
            graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            graphics.pose().popPose();
            return;
        }
        tooltip.add(TextUtil.translateDirection(face));
        tooltip.add(name);
        tooltip.add(Component.translatable("gui.voluminousenergy.io.face_mode", modeName(faceIO.getMode(face))));
        tooltip.add(Component.translatable("gui.voluminousenergy.io.port_count", getConnectedPortCount(face)));
        tooltip.add(Component.translatable("gui.voluminousenergy.io.drop_help"));
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 800);
        graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        graphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, getMessage());
    }

    public record PortArea(VEIOPort port, Rect2i bounds) {
    }
}
