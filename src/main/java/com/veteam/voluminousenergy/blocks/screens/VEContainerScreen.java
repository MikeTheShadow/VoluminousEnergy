package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.VERender;
import com.veteam.voluminousenergy.tools.buttons.VEIODestinationTrays;
import com.veteam.voluminousenergy.tools.buttons.VEIODestinationTrays.PortArea;
import com.veteam.voluminousenergy.tools.buttons.VEIODestinationTrayLayout;
import com.veteam.voluminousenergy.tools.buttons.VEIOInteraction;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.networking.packets.TankInteractionPacket.Action;
import com.veteam.voluminousenergy.tools.networking.packets.TankInteractionPacket.TankInteractionPayload;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.TextUtil;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.VEIOPort;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class VEContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private VETileEntity tileEntity;
    private VEIODestinationTrays ioTrays;
    private VEIOInteraction ioInteraction;
    private ioMenuButton ioMenu;
    private boolean ioCaptured;
    private final List<TankArea> tankAreas = new ArrayList<>();

    private static final int TANK_WIDTH = 12;
    private static final int TANK_HEIGHT = 50;

    public static final int WHITE_TEXT_COLOUR = 16777215;
    public static final int GREY_TEXT_COLOUR = 0x606060;

    public static final Style WHITE_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(WHITE_TEXT_COLOUR);
    public static final Style GREY_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(GREY_TEXT_COLOUR);

    public VEContainerScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        if (menu instanceof VEContainer VEContainer) {
            this.tileEntity = VEContainer.getTileEntity();
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY) {
    }

    private void renderPortBadges(GuiGraphics graphics, int mouseX, int mouseY) {
        VEIOPort hovered = getHoveredIOPort(mouseX, mouseY);
        Direction hoveredFace = hovered == null ? ioTrays.getFaceAt(mouseX, mouseY) : null;
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos, topPos, 700);
        for (VEIOPort port : tileEntity.getIOPorts()) {
            Rect2i bounds = getPortArea(port);
            if (bounds != null) {
                int colour = port.isAssigned() ? VEIODestinationTrayLayout.faceColour(port.getDirection()) : 0xFF89939E;
                int outline = !port.isAssigned() ? 0xFF626B75 : 0xFFADB6BF;
                if (port == hovered) {
                    outline = 0xFFFFFFFF;
                } else if (hoveredFace != null && port.getDirection() == hoveredFace) {
                    outline = colour;
                }
                drawPortHighlight(graphics, bounds.getX() - 1, bounds.getY() - 1, bounds.getWidth() + 2, bounds.getHeight() + 2, outline);
                int badgeX = bounds.getX();
                int badgeY = bounds.getY();
                String number = Integer.toString(ioTrays.getPortNumber(port));
                graphics.fill(badgeX, badgeY, badgeX + font.width(number) + 2, badgeY + 9, 0xF010171E);
                graphics.drawString(font, number, badgeX + 1, badgeY, colour, false);
            }
        }
        VEIOPort selected = ioInteraction.getSelectedPort();
        if (selected != null) {
            Rect2i bounds = getPortArea(selected);
            if (bounds != null) {
                drawPortHighlight(graphics, bounds.getX() - 2, bounds.getY() - 2, bounds.getWidth() + 4, bounds.getHeight() + 4,
                        VEIOInteraction.SELECTED_COLOUR);
            }
        }
        graphics.pose().popPose();
    }

    private @Nullable Rect2i getPortArea(VEIOPort port) {
        if (!port.isFluid()) {
            Slot slot = menu.getSlot(port.getSlotNum());
            return new Rect2i(slot.x, slot.y, 16, 16);
        }
        for (TankArea area : tankAreas) {
            if (area.tankId() == port.getSlotNum()) {
                return area.bounds();
            }
        }
        return null;
    }

    private void drawPortHighlight(GuiGraphics graphics, int x, int y, int width, int height, int colour) {
        graphics.fill(x, y, x + width, y + 1, colour);
        graphics.fill(x, y + height - 1, x + width, y + height, colour);
        graphics.fill(x, y, x + 1, y + height, colour);
        graphics.fill(x + width - 1, y, x + width, y + height, colour);
    }

    public void renderIOMenu(VETileEntity tileEntity, int menuButtonX, int menuButtonY) {
        if (tileEntity.getIOPorts().isEmpty()) {
            return;
        }
        boolean wasOpen = isIOModeActive();
        VEIOPort previous = ioInteraction == null ? null : ioInteraction.getSelectedPort();
        ioInteraction = new VEIOInteraction(payload -> PacketDistributor.sendToServer(payload));
        ioCaptured = false;
        cancelContainerDrag();
        ioMenu = addRenderableWidget(new ioMenuButton(menuButtonX, menuButtonY, button -> {
            ioInteraction.select(null);
            ioTrays.setOpen(((ioMenuButton) button).shouldIOBeOpen());
            cancelContainerDrag();
        }));
        List<PortArea> ports = new ArrayList<>();
        for (VEIOPort port : tileEntity.getIOPorts()) {
            Rect2i area = getPortArea(port);
            if (area != null) {
                ports.add(new PortArea(port, new Rect2i(leftPos + area.getX(), topPos + area.getY(), area.getWidth(), area.getHeight())));
            }
        }
        ioTrays = addRenderableWidget(new VEIODestinationTrays(new Rect2i(leftPos, topPos, imageWidth, imageHeight),
                width, height, tileEntity, menu, ioInteraction, tileEntity.getFaceIO(), ports,
                payload -> PacketDistributor.sendToServer(payload)));
        ioMenu.setOpen(wasOpen);
        ioTrays.setOpen(wasOpen);
        if (wasOpen && previous != null) {
            ioInteraction.select(tileEntity.getIOPort(previous.isFluid(), previous.getSlotNum()));
        }
    }

    private boolean isIOModeActive() {
        return ioMenu != null && ioMenu.shouldIOBeOpen();
    }

    private @Nullable VEIOPort getHoveredIOPort(double mouseX, double mouseY) {
        VEIOPort token = ioTrays == null ? null : ioTrays.getPortAt(mouseX, mouseY);
        if (token != null) {
            return token;
        }
        for (VESlotManager manager : tileEntity.getSlotManagers()) {
            Slot slot = menu.getSlot(manager.getSlotNum());
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                return manager;
            }
        }
        for (TankArea area : tankAreas) {
            if (isHovering(area.bounds(), mouseX, mouseY)) {
                return tileEntity.getIOPort(true, area.tankId());
            }
        }
        return null;
    }

    protected boolean isIOControlsHovered(double mouseX, double mouseY) {
        return isIOModeActive() && (ioTrays.contains(mouseX, mouseY) || getHoveredIOPort(mouseX, mouseY) != null);
    }

    public boolean isIOControlsCovering(double guiMouseX, double guiMouseY) {
        return isIOControlsHovered(leftPos + guiMouseX, topPos + guiMouseY);
    }

    public List<Rect2i> getIOControlAreas() {
        if (!isIOModeActive()) {
            return List.of();
        }
        return ioTrays.getControlAreas();
    }

    public void renderIOMenu(VETileEntity tileEntity) {
        renderIOMenu(tileEntity, 64 + (this.width / 2), this.topPos - 18);
    }

    protected boolean isHovering(Rect2i rect, double x, double y) {
        return this.isHovering(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), x, y);
    }

    protected void addTankArea(int tankId, int x, int y) {
        tankAreas.add(new TankArea(tankId, new Rect2i(x, y, TANK_WIDTH, TANK_HEIGHT)));
    }

    protected void renderTank(GuiGraphics matrixStack, int tankId) {
        for (TankArea tankArea : tankAreas) {
            if (tankArea.tankId() == tankId) {
                Rect2i bounds = tankArea.bounds();
                VERender.renderGuiTank(matrixStack, tileEntity.getLevel(), tileEntity.getBlockPos(),
                        tileEntity.getFluidStackFromTank(tankId), tileEntity.getTankCapacity(tankId),
                        leftPos + bounds.getX(), topPos + bounds.getY(), 0, bounds.getWidth(), bounds.getHeight());
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ioCaptured = false;
        if (ioTrays != null && ioTrays.contains(mouseX, mouseY)) {
            ioCaptured = true;
            if (ioTrays.getModeAt(mouseX, mouseY) != null) {
                ioInteraction.cancelGesture();
                ioTrays.clickMode(mouseX, mouseY, button);
                return true;
            }
            VEIOPort port = ioTrays.getPortAt(mouseX, mouseY);
            if (port != null) {
                ioInteraction.press(port, mouseX, mouseY, button);
            } else {
                ioInteraction.cancelGesture();
            }
            return true;
        }
        if (ioMenu != null && ioMenu.mouseClicked(mouseX, mouseY, button)) {
            ioCaptured = true;
            return true;
        }
        if (isIOModeActive()) {
            VEIOPort port = getHoveredIOPort(mouseX, mouseY);
            if (port != null) {
                ioCaptured = true;
                ioInteraction.press(port, mouseX, mouseY, button);
                return true;
            }
        }
        if (button == 0 || button == 1) {
            for (TankArea tankArea : tankAreas) {
                if (isHovering(tankArea.bounds(), mouseX, mouseY)) {
                    Action action = button == 0 ? Action.TAKE : Action.PUT;
                    PacketDistributor.sendToServer(new TankInteractionPayload(action, tankArea.tankId()));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (ioCaptured || isIOControlsHovered(mouseX, mouseY)) {
            if (isIOModeActive() && button == 0) {
                ioInteraction.release(mouseX, mouseY, ioTrays.getFaceAt(mouseX, mouseY));
            } else if (ioInteraction != null) {
                ioInteraction.cancelGesture();
            }
            ioCaptured = false;
            cancelContainerDrag();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (ioCaptured) {
            ioInteraction.move(mouseX, mouseY);
            return true;
        }
        if (isIOControlsHovered(mouseX, mouseY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void cancelContainerDrag() {
        isQuickCrafting = false;
        quickCraftSlots.clear();
        clearDraggingState();
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int slotId, int button, ClickType clickType) {
        if (isIOModeActive() && slot != null && tileEntity.getIOPort(false, slot.index) != null) {
            return;
        }
        super.slotClicked(slot, slotId, button, clickType);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (isIOModeActive()) {
            ioInteraction.move(mouseX, mouseY);
            ioTrays.setHoveredPort(getHoveredIOPort(mouseX, mouseY));
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        if (isIOModeActive()) {
            renderPortBadges(graphics, mouseX, mouseY);
            ioTrays.renderDragToken(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY) {
        if (isIOModeActive()) {
            VEIOPort port = getHoveredIOPort(mouseX, mouseY);
            if (port != null) {
                if (!ioInteraction.isDragging()) {
                    matrixStack.pose().pushPose();
                    matrixStack.pose().translate(0, 0, 800);
                    matrixStack.renderComponentTooltip(font, getPortTooltip(port), mouseX, mouseY);
                    matrixStack.pose().popPose();
                }
                return;
            }
            if (ioTrays.contains(mouseX, mouseY)) {
                ioTrays.renderFaceTooltip(matrixStack, mouseX, mouseY);
                return;
            }
        }
        super.renderTooltip(matrixStack, mouseX, mouseY);
        for (TankArea tankArea : tankAreas) {
            if (!isHovering(tankArea.bounds(), mouseX, mouseY)
                    || tankArea.tankId() < 0
                    || tankArea.tankId() >= tileEntity.getRelationalTanks().size()) {
                continue;
            }

            VERelationalTank tank = tileEntity.getRelationalTank(tankArea.tankId());
            int amount = tank.getTank().getFluidAmount();
            String name = tank.getTank().getFluid().getHoverName().getString();
            matrixStack.renderComponentTooltip(
                    font,
                    TextUtil.tankTooltip(
                            name,
                            amount,
                            tank.getTank().getCapacity(),
                            tank.getTankType() != TankType.OUTPUT),
                    mouseX,
                    mouseY);
            return;
        }
    }

    private List<Component> getPortTooltip(VEIOPort port) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("gui.voluminousenergy.io.numbered_port", ioTrays.getPortNumber(port),
                Component.translatable(port.getHoverName())));
        if (port.isFluid()) {
            if (!tileEntity.getFluidStackFromTank(port.getSlotNum()).isEmpty()) {
                tooltip.add(tileEntity.getFluidStackFromTank(port.getSlotNum()).getHoverName());
            }
        } else if (!menu.getSlot(port.getSlotNum()).getItem().isEmpty()) {
            tooltip.add(menu.getSlot(port.getSlotNum()).getItem().getHoverName());
        }
        tooltip.add(port.isAssigned()
                ? Component.translatable("gui.voluminousenergy.io.assigned_face", TextUtil.translateDirection(port.getDirection()))
                : Component.translatable("gui.voluminousenergy.io.unassigned"));
        return tooltip;
    }

    private record TankArea(int tankId, Rect2i bounds) {
    }

}
