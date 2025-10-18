package mcp.mobius.waila.gui.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.mixin.AbstractSelectionListAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigListWidget extends ContainerObjectSelectionList<ConfigListWidget.Entry> implements ContainerEventHandler {

    private final ConfigScreen owner;
    private final @Nullable Runnable diskWriter;

    public final List<ConfigListWidget.Entry> children;
    public final Set<ConfigValue<?, ?>> values = new HashSet<>();

    private int topOffset;
    private int bottomOffset;

    public boolean headerSeparator = true;
    public boolean footerSeparator = true;

    public boolean enableSearchBox = true;
    private @Nullable SearchBoxEntry searchBox;

    public @Nullable String filter = null;

    // the fuck? apparently @Nullable String[] means a non-null array containing nullable string
    public String @Nullable [] splitFilter = null;

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight, @Nullable Runnable diskWriter) {
        super(client, width, height, top, itemHeight - 4);

        this.owner = owner;
        this.diskWriter = diskWriter;
        this.children = ((AbstractSelectionListAccess) this).wthit_children();

        resize(top, bottom);
    }

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        this(owner, client, width, height, top, bottom, itemHeight, null);
    }

    public boolean isChanged() {
        return values.stream().anyMatch(ConfigValue::isChanged);
    }

    @Override
    public int getRowWidth() {
        return Math.min(width - 20, 450);
    }

    @Override
    protected int scrollBarX() {
        return minecraft.getWindow().getGuiScaledWidth() - 5;
    }

    public void tick() {
        children.forEach(Entry::tick);
    }

    public boolean save(boolean ignoreErrors) {
        if (values.stream().allMatch(ConfigValue::isValueValid)) {
            values.forEach(ConfigValue::save);
            if (diskWriter != null) diskWriter.run();
            return true;
        }

        if (!ignoreErrors) showErrorToast(minecraft);
        return ignoreErrors;
    }

    public static void showErrorToast(Minecraft minecraft) {
        minecraft.getToastManager().addToast(new SystemToast(
            SystemToast.SystemToastId.PACK_COPY_FAILURE,
            Component.translatable(Tl.Config.InvalidInput.TITLE),
            Component.translatable(Tl.Config.InvalidInput.DESC)));
    }

    public void search() {
        Preconditions.checkState(enableSearchBox);
        Objects.requireNonNull(searchBox);
        scrollToEntry(searchBox);
        owner.setInitialFocus(searchBox.box);
    }

    public void init() {
        var dirtyChildren = List.copyOf(children);
        for (var child : dirtyChildren) {
            child.clear(this);
        }

        var rootChildren = List.copyOf(children);
        var index = 0;

        if (enableSearchBox && searchBox == null) {
            var box = new EditBox(minecraft.font, 0, 0, 160, 18, Component.empty());
            box.setHint(Component.translatable(Tl.Config.SEARCH_PROMPT));
            box.setResponder(filter -> {
                var isBlank = filter.isBlank();
                if ((isBlank && this.filter == null) || (filter.equals(this.filter))) return;

                children.clear();
                children.add(searchBox);

                if (isBlank) {
                    this.filter = null;
                    this.splitFilter = null;
                    children.addAll(rootChildren);
                } else {
                    this.filter = filter;
                    this.splitFilter = filter.split("\\s");
                    children.addAll(rootChildren.stream().filter(it -> it.match(this.splitFilter)).toList());
                }
                init();
                search();
            });
            searchBox = new SearchBoxEntry(box);
            with(0, searchBox);
            searchBox.init(this, 0);
            index++;
        }

        for (var child : rootChildren) {
            index += child.init(this, index);
        }

        for (var child : children) {
            child.setFocused(null);
        }

        resize(topOffset, owner.height + bottomOffset);
        setScrollAmount(scrollAmount());
        ((AbstractSelectionListAccess) this).wthit_repositionEntries();
    }

    public ConfigListWidget with(Entry entry) {
        return with(children.size(), entry);
    }

    public ConfigListWidget with(int index, Entry entry) {
        if (entry instanceof ConfigValue<?, ?> cv) withHidden(cv);
        entry.setHeight(defaultEntryHeight);
        children.add(index, entry);
        return this;
    }

    public ConfigListWidget withHidden(ConfigValue<?, ?> value) {
        values.add(value);
        return this;
    }

    @Override
    public int getY() {
        return topOffset;
    }

    public void resize(int top, int bottom) {
        this.topOffset = top;
        this.bottomOffset = bottom - owner.height;
        setSize(owner.width, owner.height - (topOffset - bottomOffset));
    }

    @Override
    protected void renderListSeparators(GuiGraphics ctx) {
        if (headerSeparator) {
            var texture = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
            ctx.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
        }

        if (footerSeparator) {
            var texture = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
            ctx.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
        }
    }

    private static class SearchBoxEntry extends Entry {

        final EditBox box;

        private SearchBoxEntry(EditBox box) {
            this.box = box;
        }

        @Override
        protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
            children.add(box);
        }

        @Override
        public boolean match(String[] filter) {
            return true;
        }

        @Override
        protected void buildSearchKey(StringBuilder sb) {
            throw new IllegalStateException();
        }

        @Override
        protected void drawEntry(GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            box.setPosition(rowLeft, rowTop);
            box.setWidth(width);
            //            box.setHeight(height);
            box.render(ctx, mouseX, mouseY, deltaTime);
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
            Preconditions.checkState(list.children.get(index) == this);

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

        public boolean match(String[] filter) {
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
        public void renderContent(GuiGraphics ctx, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            var rowLeft = list.getRowLeft();
            var rowTop = getY();
            var width = getWidth();
            var height = getHeight();

            if (category != null) {
                for (var i = 0; i < categoryDepth; i++) {
                    var lineX1 = rowLeft + 5 + i * 16;
                    var lineX2 = lineX1 + 2;
                    var lineY1 = rowTop - height / 2 + 4;
                    var lineY2 = lineY1 + height;

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
