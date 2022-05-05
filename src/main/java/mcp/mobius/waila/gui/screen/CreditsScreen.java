package mcp.mobius.waila.gui.screen;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.Waila;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CreditsScreen extends Screen {

    private final Screen parent;

    protected CreditsScreen(Screen parent) {
        super(Component.translatable("gui.waila.credits"));

        this.parent = parent;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void init() {
        super.init();

        try {
            CreditMap credits = new Gson().fromJson(minecraft.getResourceManager().getResource(Waila.id("credits.json")).get().openAsReader(), CreditMap.class);

            ListWidget listWidget = new ListWidget(minecraft, width, height, 32, height - 32, minecraft.font.lineHeight + 6);
            credits.forEach((key, list) -> {
                List<Entry> children = listWidget.children();
                children.add(new Entry(Component.translatable("gui.waila.credits." + key).withStyle(ChatFormatting.GRAY)));
                for (String person : list) {
                    children.add(new Entry(Component.literal("        " + person)));
                }
                children.add(new Entry(Component.empty()));
            });

            addRenderableWidget(listWidget);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addRenderableWidget(new Button(width / 2 - 50, height - 25, 100, 20, Component.translatable("gui.done"), w -> onClose()));
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, partialTicks);
        drawCenteredString(matrices, font, title.getString(), width / 2, 12, 0xFFFFFF);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private static class CreditMap extends LinkedHashMap<String, List<String>> {

    }

    private static class ListWidget extends ContainerObjectSelectionList<Entry> {

        private ListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);

            setRenderBackground(false);
        }

        @Override
        protected int getScrollbarPosition() {
            return minecraft.getWindow().getGuiScaledWidth() - 5;
        }

    }

    private static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private final Component component;

        private Entry(Component component) {
            this.component = component;
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
        public void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            Minecraft.getInstance().font.drawShadow(matrices, component, rowLeft, rowTop + 3, 0xFFFFFF);
        }

    }

}
