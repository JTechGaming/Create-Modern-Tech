package com.cybrisoft.createmoderntech.ui;

import com.cybrisoft.createmoderntech.util.TriggerVariableEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TriggerVariableListWidget extends ObjectSelectionList<TriggerVariableListWidget.Entry> {
    public TriggerVariableListWidget(Minecraft client, int width, int height, int y, int itemHeight, List<TriggerVariableEntry> vars) {
        super(client, width, height, y, itemHeight);

        for (TriggerVariableEntry var : vars) {
            this.addEntry(new SelectionEntry(var));
        }
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth();
    }

    @OnlyIn(Dist.CLIENT)
    public class SelectionEntry extends Entry {
        private final TriggerVariableEntry triggerVariable;
        private final ItemStack iconStack;

        SelectionEntry(final TriggerVariableEntry triggerVariable) {
            this.triggerVariable = triggerVariable;

            ResourceLocation loc = ResourceLocation.parse(triggerVariable.getBlockId());
            Block block = BuiltInRegistries.BLOCK.get(loc);
            iconStack = new ItemStack(block.asItem());
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String direction = switch (triggerVariable.getDirection()) {
                case DOWN ->  "D";
                case UP ->    "U";
                case NORTH -> "N";
                case SOUTH -> "S";
                case WEST ->  "W";
                case EAST ->  "E";
            };
            context.drawString(Minecraft.getInstance().font, Component.literal(direction), x, y, -1);
            context.renderItem(iconStack, x + 20, y - 5);
            context.drawString(Minecraft.getInstance().font, Component.literal("Value: "), x + 50, y, -1);
            context.drawString(Minecraft.getInstance().font, Component.literal(triggerVariable.getValue()), x + 100, y, -1);
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry extends ObjectSelectionList.Entry<Entry> {
        public Entry() {
        }
    }
}
