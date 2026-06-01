package com.cybrisoft.createmoderntech.ui;

import com.cybrisoft.createmoderntech.network.UpdateAudioTriggerPacket;
import com.cybrisoft.createmoderntech.registry.ModPackets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class AudioTriggerScreen extends Screen {
    private final BlockPos blockPos;
    private String currentMessage;
    private EditBox messageField;

    public AudioTriggerScreen(BlockPos pos, String currentMessage) {
        super(Component.literal("Audio Trigger"));
        this.blockPos = pos;
        this.currentMessage = currentMessage;
    }

    @Override
    protected void init() {
        super.init();
        int x = width / 2 - 150;
        int y = height / 2 - 10;

        messageField = new EditBox(font, x, y, 300, 20, Component.literal("Message"));
        messageField.setMaxLength(256);
        messageField.setValue(currentMessage);
        messageField.setFocused(true);
        addRenderableWidget(messageField);

        addRenderableWidget(Button.builder(Component.literal("Confirm"), btn -> confirm())
                .bounds(width / 2 - 50, height / 2 + 20, 100, 20)
                .build());
    }

    private void confirm() {
        PacketDistributor.sendToServer(new UpdateAudioTriggerPacket(blockPos, messageField.getValue()));
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, "Audio Trigger Message", width / 2, height / 2 - 30, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            confirm();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}