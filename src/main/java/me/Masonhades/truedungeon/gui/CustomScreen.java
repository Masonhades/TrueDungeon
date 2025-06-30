package me.Masonhades.truedungeon.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.Masonhades.truedungeon.network.NetworkHandler;
import me.Masonhades.truedungeon.network.UpdateQuestsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CustomScreen extends AbstractContainerScreen<CustomMenu> {

    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation("truedungeon", "textures/gui/custom_gui.png");

    public CustomScreen(CustomMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 176, 166);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels (GuiGraphics guiGraphics, int mouseX, int mouseY){
        guiGraphics.drawString(font, title, 8,6, 0x404040, false);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.hasPermissions(2)) {
            this.addRenderableWidget(Button.builder(Component.literal("Обновить квесты"), button -> {
                NetworkHandler.INSTANCE.sendToServer(new UpdateQuestsPacket());
            }).bounds(this.leftPos + 140, this.topPos + 5, 100, 20).build());
        }
    }

}

