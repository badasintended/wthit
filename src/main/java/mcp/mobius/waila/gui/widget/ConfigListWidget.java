package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigListWidget extends ContainerObjectSelectionList<ConfigListWidget.Entry> {

    private final ConfigScreen owner;
    private final Runnable diskWriter;

    private int topOffset;
    private int bottomOffset;

    @Nullable
    private EditBox searchBox;
    private List<Entry> unfilteredChildren;

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

    public boolean save(boolean ignoreErrors) {
        List<? extends ConfigValue<?>> values = children()
            .stream()
            .filter(e -> e instanceof ConfigValue)
            .map(e -> (ConfigValue<?>) e)
            .toList();

        if (values.stream().allMatch(ConfigValue::isValueValid)) {
            values.forEach(ConfigValue::save);
            if (diskWriter != null) diskWriter.run();
            return true;
        }

        if (!ignoreErrors) minecraft.getToasts().addToast(new SystemToast(
            SystemToast.SystemToastIds.TUTORIAL_HINT,
            Component.translatable(Tl.Config.InvalidInput.TITLE),
            Component.translatable(Tl.Config.InvalidInput.DESC)));

        return ignoreErrors;
    }

    public EditBox getSearchBox() {
        if (searchBox != null) return searchBox;

        unfilteredChildren = new ArrayList<>(children());

        String category = "";
        for (Entry child : unfilteredChildren) {
            if (child instanceof CategoryEntry) category = child.category;
            child.category = category;
        }

        searchBox = new EditBox(minecraft.font, 0, 0, 160, 18, Component.empty());
        searchBox.setHint(Component.translatable(Tl.Config.SEARCH_PROMPT));
        searchBox.setResponder(filter -> {
            children().clear();
            if (filter.isBlank()) {
                children().addAll(unfilteredChildren);
            } else {
                children().addAll(unfilteredChildren.stream().filter(it -> it.match(filter)).toList());
            }
            init();
        });

        return searchBox;
    }

    public void init() {
        for (Entry child : children()) {
            child.setFocused(null);
        }
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
        if (searchBox != null) searchBox.setPosition(getRowLeft() + getRowWidth() - 160, (top - 18) / 2);
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        protected final Minecraft client;
        protected @Nullable List<? extends GuiEventListener> children;
        protected @Nullable List<? extends NarratableEntry> narratables;
        public String category = "";

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        public void tick() {
        }

        protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        }

        protected void gatherNarratables(ImmutableList.Builder<NarratableEntry> narratables) {
        }

        protected boolean match(String filter) {
            return StringUtils.containsIgnoreCase(category, filter);
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
