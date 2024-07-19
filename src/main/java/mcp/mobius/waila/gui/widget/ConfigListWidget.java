package mcp.mobius.waila.gui.widget;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.client.Minecraft;
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
    private final @Nullable Runnable diskWriter;

    private int topOffset;
    private int bottomOffset;

    public boolean enableSearchBox = true;
    private @Nullable EditBox searchBox;

    public @Nullable String filter = null;

    // the fuck? apparently @Nullable String[] means a non-null array containing nullable string
    public String @Nullable [] splitFilter = null;

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight, @Nullable Runnable diskWriter) {
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
        if (searchBox != null) searchBox.tick();
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
        Preconditions.checkState(enableSearchBox);
        return Objects.requireNonNull(searchBox);
    }

    public void init() {
        var dirtyChildren = List.copyOf(children());
        for (var child : dirtyChildren) {
            child.clear(this);
        }

        var rootChildren = List.copyOf(children());
        var index = 0;
        for (var child : rootChildren) {
            index += child.init(this, index);
        }

        for (var child : children()) {
            child.setFocused(null);
        }

        if (enableSearchBox && searchBox == null) {
            searchBox = new EditBox(minecraft.font, 0, 0, 160, 18, Component.empty());
            searchBox.setHint(Component.translatable(Tl.Config.SEARCH_PROMPT));
            searchBox.setResponder(filter -> {
                var isBlank = filter.isBlank();
                if ((isBlank && this.filter == null) || (filter.equals(this.filter))) return;

                children().clear();
                if (isBlank) {
                    this.filter = null;
                    this.splitFilter = null;
                    children().addAll(rootChildren);
                } else {
                    this.filter = filter;
                    this.splitFilter = filter.split("\\s");
                    children().addAll(rootChildren.stream().filter(it -> it.match(this.splitFilter)).toList());
                }
                init();
            });
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
        if (searchBox != null) {
            searchBox.setX(getRowLeft() + getRowWidth() - 160);
            searchBox.y =  (top - 18) / 2;
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        protected final Minecraft client;
        protected @Nullable List<? extends GuiEventListener> children;
        protected @Nullable List<? extends NarratableEntry> narratables;

        protected ConfigListWidget list;
        protected int index;

        public @Nullable CategoryEntry category;
        public int categoryDepth;

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        public void tick() {
        }

        public final int init(ConfigListWidget list, int index) {
            Preconditions.checkState(list.children().get(index) == this);

            this.list = list;
            this.index = index;
            return init();
        }

        public int init() {
            return 1;
        }

        public void clear(ConfigListWidget list) {
        }

        protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        }

        protected void gatherNarratables(ImmutableList.Builder<NarratableEntry> narratables) {
        }

        protected abstract void buildSearchKey(StringBuilder sb);

        public final boolean match(String[] filter) {
            var sb = new StringBuilder();
            buildSearchKey(sb);

            for (var s : filter) {
                if (StringUtils.containsIgnoreCase(sb.toString(), s)) {
                    return true;
                }
            }

            return false;
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
        public final void render(@NotNull GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            if (category != null) {
                for (var i = 0; i < categoryDepth; i++) {
                    var lineX1 = rowLeft + 5 + i * 16;
                    var lineX2 = lineX1 + 2;
                    var lineY1 = rowTop - height / 2 - 4;
                    var lineY2 = lineY1 + height + 4;

                    if (i == (categoryDepth - 1) && (index - category.index) == 1) {
                        lineY1 += 8;
                    }

                    ctx.fill(lineX1, lineY1, lineX2, lineY2, 0x22FAFAFA);
                }

                var offset = categoryDepth * 16;
                rowLeft += offset;
                width -= offset;
            }

            drawEntry(ctx, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);
        }

        protected abstract void drawEntry(GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);

    }

}
