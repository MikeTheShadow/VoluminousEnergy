package com.veteam.voluminousenergy.blocks.screens;

import com.veteam.voluminousenergy.blocks.containers.VoluminousContainer;
import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.buttons.VEIOButton;
import com.veteam.voluminousenergy.tools.buttons.ioMenuButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotBoolButton;
import com.veteam.voluminousenergy.tools.buttons.slots.SlotDirectionButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankBoolButton;
import com.veteam.voluminousenergy.tools.buttons.tanks.TankDirectionButton;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UuidPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.UUID;

public abstract class VEContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private VETileEntity tileEntity;

    public static final int WHITE_TEXT_COLOUR = 16777215;
    public static final int GREY_TEXT_COLOUR = 0x606060;

    public static final Style WHITE_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(WHITE_TEXT_COLOUR);
    public static final Style GREY_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(GREY_TEXT_COLOUR);

    public VEContainerScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        if (menu instanceof VoluminousContainer voluminousContainer) {
            this.tileEntity = voluminousContainer.getTileEntity();
        }
    }

    @Override
    protected void renderLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        this.renderables.stream().filter(Renderable -> Renderable instanceof ioMenuButton).forEach(button -> {
            if (((ioMenuButton) button).shouldIOBeOpen()) {
                renderSlotAndTankLabels(matrixStack, mouseX, mouseY);
            }
        });
    }

    public void renderIOMenu(VEFluidTileEntity tileEntity) {
        renderIOMenu(tileEntity,64 + (this.width / 2),this.topPos - 18);
    }

    public void renderIOMenu(VEFluidTileEntity tileEntity, int menuButtonX, int menuButtonY) {

        // Buttons
        addRenderableWidget(new ioMenuButton(menuButtonX,menuButtonY , buttons -> {

        }));


        int increase = 0;

        for (VESlotManager manager : tileEntity.getSlotManagers()) {
            addRenderableWidget(new SlotBoolButton(manager, (this.width / 2) - 198, this.topPos + (20 * increase), button -> {
            }));

            addRenderableWidget(new SlotDirectionButton(manager, (this.width / 2) - 184, this.topPos + (20 * increase), button -> {
            }));
            increase++;
        }

        for (RelationalTank tank : tileEntity.getRelationalTanks()) {
            // Input Tank
            addRenderableWidget(new TankBoolButton(tank, (this.width / 2) - 198, this.topPos + (20 * increase), button -> {
                // Do nothing
            }));

            addRenderableWidget(new TankDirectionButton(tank, (this.width / 2) - 184, this.topPos + (20 * increase), button -> {
                // Do nothing
            }));
            increase++;
        }
    }


    public void renderIOMenu(VETileEntity tileEntity, int menuButtonX, int menuButtonY) {

        // Buttons
        addRenderableWidget(new ioMenuButton(menuButtonX,menuButtonY , buttons -> {

        }));


        int increase = 0;

        for (VESlotManager manager : tileEntity.getSlotManagers()) {
            addRenderableWidget(new SlotBoolButton(manager, (this.width / 2) - 198, this.topPos + (20 * increase), button -> {
            }));

            addRenderableWidget(new SlotDirectionButton(manager, (this.width / 2) - 184, this.topPos + (20 * increase), button -> {
            }));
            increase++;
        }
    }

    public void renderIOMenu(VETileEntity tileEntity) {
        // Buttons
        addRenderableWidget(new ioMenuButton(64 + (this.width / 2), this.topPos - 18, buttons -> {

        }));


        int increase = 0;

        for (VESlotManager manager : tileEntity.getSlotManagers()) {
            addRenderableWidget(new SlotBoolButton(manager, (this.width / 2) - 198, this.topPos + (20 * increase), button -> {
            }));

            addRenderableWidget(new SlotDirectionButton(manager, (this.width / 2) - 184, this.topPos + (20 * increase), button -> {
            }));
            increase++;
        }
    }

    protected void renderSlotAndTankLabels(GuiGraphics matrixStack, int mouseX, int mouseY) {
        for (int i = 0; i < this.tileEntity.getSlotManagers().size(); i++) {
            Slot slot = this.menu.getSlot(i);
            TextUtil.renderShadowedText(matrixStack, this.font, (TextUtil.translateString("gui.voluminousenergy.slot_short").copy().append(String.valueOf(i))), slot.x, slot.y, WHITE_TEXT_STYLE);
        }
    }

    /* GuiGraphics matrixStack, int i, int j, int mouseX, int mouseY, float partialTicks old arguments in case you want them back*/
    public void drawIOSideHelper(){

        for(Renderable Renderable : this.renderables){
            if (Renderable instanceof ioMenuButton){
                if (((ioMenuButton) Renderable).shouldIOBeOpen()) { // This means IO Should be open
                    this.renderables.forEach(button ->{
                        if (button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(true);
                        }
                    });
                } else {
                    this.renderables.forEach(button ->{
                        if(button instanceof VEIOButton){
                            ((VEIOButton) button).toggleRender(false);
                        }
                    });
                }
            }
        }
    }

    public void updateButtonDirection(int direction, int slotId){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof SlotDirectionButton && ((SlotDirectionButton) Renderable).getAssociatedSlotId() == slotId ){
                ((SlotDirectionButton) Renderable).setDirectionFromInt(direction);
            }
        }
    }

    public void updateBooleanButton(boolean status, int slotId){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof SlotBoolButton && ((SlotBoolButton) Renderable).getAssociatedSlotId() == slotId){
                ((SlotBoolButton) Renderable).toggleRender(true);
                ((SlotBoolButton) Renderable).setStatus(status);
                ((SlotBoolButton) Renderable).toggleRender(false);
            }
        }
    }

    public void updateTankDirection(int direction, int id){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof TankDirectionButton && ((TankDirectionButton) Renderable).getId() == id ){
                ((TankDirectionButton) Renderable).setDirectionFromInt(direction);
            }
        }
    }

    public void updateTankStatus(boolean status, int id){
        for(Renderable Renderable: this.renderables){
            if(Renderable instanceof TankBoolButton && ((TankBoolButton) Renderable).getId() == id){
                ((TankBoolButton) Renderable).toggleRender(true);
                ((TankBoolButton) Renderable).setStatus(status);
                ((TankBoolButton) Renderable).toggleRender(false);
            }
        }
    }

    public void informTileOfIOButton(boolean connection){
        UUID uuid = Minecraft.getInstance().player.getUUID();
        VENetwork.channel.sendToServer(new UuidPacket(uuid, connection));
    }

    protected boolean isHovering(Rect2i rect, double x, double y) {
        return this.isHovering(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), x, y);
    }

}
