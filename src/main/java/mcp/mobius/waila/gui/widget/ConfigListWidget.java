package mcp.mobius.waila.gui.widget;

import java.util.List;

import com.google.common.collect.ImmutableList;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigListWidget extends ContainerObjectSelectionList<ConfigListWidget.Entry> {

    private final ConfigScreen owner;
    private final Runnable diskWriter;

    private int topOffset;
    private int bottomOffset;

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight, Runnable diskWriter) {
        super(client, width, height, top, bottom, itemHeight - 4);

        this.owner = owner;
        this.diskWriter = diskWriter;

        resize(top, bottom);
        setRenderBackground(false);
    }

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        this(owner, client, width, height, top, bottom, itemHeight, null);
    }

    @Override
    public int getRowWidth() {
        return Math.min(width - 20, 450);
    }

    @Override
    protected int getScrollbarPosition() {
        return minecraft.getWindow().getGuiScaledWidth() - 5;
    }

    public void tick() {
        children().forEach(Entry::tick);
    }

    public void save() {
        children()
            .stream()
            .filter(e -> e instanceof ConfigValue)
            .map(e -> (ConfigValue<?>) e)
            .forEach(ConfigValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void init() {
        resize(topOffset, owner.height + bottomOffset);
        setScrollAmount(getScrollAmount());
    }

    public void add(Entry entry) {
        add(children().size(), entry);
    }

    public void add(int index, Entry entry) {
        children().add(index, entry);
    }

    public ConfigListWidget with(Entry entry) {
        return with(children().size(), entry);
    }

    public ConfigListWidget with(int index, Entry entry) {
        add(index, entry);
        return this;
    }

    public void resize(int top, int bottom) {
        this.topOffset = top;
        this.bottomOffset = bottom - owner.height;
        updateSize(owner.width, owner.height, topOffset, owner.height + bottomOffset);
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        protected final Minecraft client;
        protected @Nullable List<? extends GuiEventListener> children;
        protected @Nullable List<? extends NarratableEntry> narratables;

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        public void tick() {
        }

        protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        }

        protected void gatherNarratables(ImmutableList.Builder<NarratableEntry> narratables) {
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            if (children == null) {
                ImmutableList.Builder<GuiEventListener> builder = ImmutableList.builder();
                gatherChildren(builder);
                children = builder.build();
            }

            return children;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            if (narratables == null) {
                ImmutableList.Builder<NarratableEntry> builder = ImmutableList.builder();
                gatherNarratables(builder);
                narratables = builder.build();
            }

            return narratables;
        }

        @Override
        public void render(@NotNull GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            if (rowTop <= mouseY && mouseY < rowTop + height + 4) {
                ctx.fill(0, rowTop - 2, client.getWindow().getGuiScaledWidth(), rowTop + height + 2, 0x22FFFFFF);
            }
        }

    }

}
