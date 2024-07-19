package mcp.mobius.waila.gui.screen;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class CreditsScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

    private final Screen parent;

    protected CreditsScreen(Screen parent) {
        super(Component.translatable(Tl.Gui.CREDITS));

        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        try {
            var credits = new Gson().fromJson(minecraft.getResourceManager().getResource(Waila.id("credits.json")).orElseThrow().openAsReader(), CreditMap.class);
            var listWidget = new ListWidget(minecraft, width, height - 64, 32, minecraft.font.lineHeight + 6);

            credits.forEach((key, category) -> {
                var children = listWidget.children();

                children.add(new CreditLine(1, List.of(Component.translatable(Tl.Gui.CREDITS + "." + key).withStyle(ChatFormatting.GRAY))));

                for (var chunk : Lists.partition(category.values.stream().map(Component::literal).toList(), category.width)) {
                    children.add(new CreditLine(category.width, chunk));
                }

                children.add(new CreditLine(1, List.of()));
            });

            listWidget.init();
            addRenderableWidget(listWidget);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addRenderableWidget(createButton(width / 2 - 50, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> onClose()));
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        super.render(ctx, mouseX, mouseY, partialTicks);
        ctx.drawCenteredString(font, title.getString(), width / 2, 12, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private static class CreditMap extends LinkedHashMap<String, CreditCategory> {

    }

    private static class CreditCategory {

        int width = 0;
        List<String> values = List.of();

    }

    private static class ListWidget extends ContainerObjectSelectionList<CreditLine> {

        private ListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
        }

        private void init() {
            var totalHeight = (children().size() - 1) * itemHeight;
            if (totalHeight < height) {
                setRenderHeader(true, (height - totalHeight) / 2 - getY());
            }
        }

        @Override
        public int getRowWidth() {
            return Math.min(width - 20, 360);
        }

        @Override
        protected int getScrollbarPosition() {
            return minecraft.getWindow().getGuiScaledWidth() - 5;
        }

    }

    private class CreditLine extends ContainerObjectSelectionList.Entry<CreditLine> {

        private final int column;
        private final List<MutableComponent> components;

        private CreditLine(int column, List<MutableComponent> components) {
            this.column = column;
            this.components = components;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public void render(@NotNull GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            if (components.isEmpty()) return;

            var columnWidth = width / column;

            for (var i = 0; i < components.size(); i++) {
                var component = components.get(i);
                ctx.drawCenteredString(minecraft.font, component, rowLeft + (columnWidth * i) + (columnWidth / 2), rowTop + 3, 0xFFFFFF);
            }
        }

    }

}
