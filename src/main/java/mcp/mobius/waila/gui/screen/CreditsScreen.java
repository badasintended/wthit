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
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class CreditsScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen implements TabbedScreen {

    public static final Component TITLE = Component.translatable(Tl.Gui.CREDITS);
    private final Screen parent;

    protected CreditsScreen(Screen parent) {
        super(TITLE);

        this.parent = parent;
    }

    @Override
    public Screen getParent() {
        return parent;
    }

    @Override
    protected void init() {
        super.init();
        initBar(width, this::addRenderableWidget, this::setInitialFocus);

        try {
            var credits = new Gson().fromJson(minecraft.getResourceManager().getResource(Waila.id("credits.json")).orElseThrow().openAsReader(), CreditMap.class);
            var listWidget = new ListWidget(minecraft, width, height - 56, 24, minecraft.font.lineHeight + 6);

            credits.forEach((key, category) -> {
                listWidget.addEntry(new CreditLine(1, List.of(Component.translatable(Tl.Gui.CREDITS + "." + key).withStyle(ChatFormatting.GRAY))));

                for (var chunk : Lists.partition(category.values.stream().map(Component::literal).toList(), category.width)) {
                    listWidget.addEntry(new CreditLine(category.width, chunk));
                }

                listWidget.addEntry(new CreditLine(1, List.of()));
            });

            listWidget.init();
            addRenderableWidget(listWidget);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addRenderableWidget(createButton(width / 2 - 50, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> onClose()));
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

    private class ListWidget extends ContainerObjectSelectionList<CreditLine> {

        private ListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
        }

        private void init() {
            var totalHeight = (children().size() - 1) * defaultEntryHeight;
            if (totalHeight < height) {
                addEntryToTop(new CreditLine(1, List.of()), (height - totalHeight) / 2 - getY());
            }
        }

        @Override
        protected int addEntry(CreditLine entry) {
            return super.addEntry(entry);
        }

        @Override
        public int getRowWidth() {
            return Math.min(width - 20, 360);
        }

        @Override
        protected int scrollBarX() {
            return minecraft.getWindow().getGuiScaledWidth() - 5;
        }

        @Override
        protected void renderListSeparators(GuiGraphics ctx) {
            var texture = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
            ctx.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
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
        public void renderContent(GuiGraphics ctx, int mouseX, int mouseY, boolean hovered, float delta) {
            if (components.isEmpty()) return;

            var rowLeft = getX();
            var rowTop = getY();
            var columnWidth = getWidth() / column;

            for (var i = 0; i < components.size(); i++) {
                var component = components.get(i);
                ctx.drawCenteredString(minecraft.font, component, rowLeft + (columnWidth * i) + (columnWidth / 2), rowTop + 3, 0xFFFFFFFF);
            }
        }

    }

}
