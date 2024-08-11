package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class CategoryEntry extends ConfigListWidget.Entry {

    private static final IJsonConfig<Map<String, Boolean>> STATES = IJsonConfig.of(new TypeToken<Map<String, Boolean>>() {})
        .file(WailaConstants.NAMESPACE + "/category_entries")
        .json5()
        .factory(HashMap::new)
        .commenter(() -> p -> !p.isEmpty() ? null : """
            This config controls the category entries collapsed state.
            You shouldn't edit this config by hand.
            """)
        .build();

    public final Component title;

    private final String key;
    private final Button collapseButton;
    private final Button expandAllButton;
    private final List<ConfigListWidget.Entry> children = new ArrayList<>();

    private boolean collapsed;
    private boolean hasNested = false;
    private boolean filterMatchParent = false;

    public CategoryEntry(String title) {
        this.key = title;
        this.title = Component.translatable(title).withStyle(ChatFormatting.BOLD);
        this.collapseButton = new Button(0, 0, 12, 12, CommonComponents.EMPTY, b -> toggleCollapse());
        this.expandAllButton = new Button(0, 0, 20, 12, Component.literal("++"), b -> {
            setCollapse(false, true);
            list.init();
            list.setFocused(this);
            setFocused(b);
        });

        collapsed = Boolean.TRUE.equals(STATES.get().putIfAbsent(key, false));
        STATES.save();
    }

    public CategoryEntry with(ConfigListWidget.Entry child) {
        child.category = this;
        child.categoryDepth = this.categoryDepth + 1;
        children.add(child);
        if (child instanceof CategoryEntry) hasNested = true;
        return this;
    }

    @Override
    public int init() {
        var expand = !collapsed || list.filter != null;
        collapseButton.setMessage(Component.literal(!expand ? "+" : "-"));

        var added = 1;
        var matchCategory = StringUtils.containsIgnoreCase(title.getString(), list.filter);
        if (category != null) matchCategory = matchCategory || filterMatchParent;

        if (expand) {
            for (var child : children) {
                if (child instanceof CategoryEntry nest) {
                    nest.filterMatchParent = matchCategory;
                }

                if (!matchCategory && list.splitFilter != null && !child.match(list.splitFilter)) {
                    continue;
                }

                list.add(index + added, child);
                added += child.init(list, index + added);
            }
        }

        return added;
    }

    @Override
    public void clear(ConfigListWidget list) {
        list.children().removeIf(it -> it.category == this);
        for (var child : children) {
            child.clear(list);
        }
    }

    private void setCollapse(boolean collapsed, boolean deep) {
        this.collapsed = collapsed;
        STATES.get().put(key, collapsed);
        STATES.save();

        if (deep) for (var child : children) {
            if (child instanceof CategoryEntry entry) entry.setCollapse(collapsed, true);
        }
    }

    private void toggleCollapse() {
        if (!collapsed) setCollapse(true, true);
        else setCollapse(false, false);

        list.init();
        list.setFocused(this);
        setFocused(collapseButton);
    }

    @Override
    protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        children.add(collapseButton);
        children.add(expandAllButton);
    }

    @Override
    public void renderTooltip(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (expandAllButton.isMouseOver(mouseX, mouseY)) {
            screen.renderTooltip(matrices, Component.translatable(Tl.Config.EXPAND_ALL), mouseX, mouseY);
        }
    }

    @Override
    protected void drawEntry(PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        var buttonY = rowTop + (height - collapseButton.getHeight()) / 2;
        var hasFilter = list.filter != null;

        collapseButton.active = !hasFilter;
        collapseButton.x = rowLeft;
        collapseButton.y = buttonY;
        collapseButton.render(matrices, mouseX, mouseY, deltaTime);
        client.font.drawShadow(matrices, title, rowLeft + collapseButton.getWidth() + 4, rowTop + ((height - client.font.lineHeight) / 2f) + 1, 0xFFFFFF);

        expandAllButton.active = hasNested && !hasFilter;
        if (expandAllButton.active) {
            expandAllButton.x = rowLeft + width - expandAllButton.getWidth();
            expandAllButton.y = buttonY;
            expandAllButton.render(matrices, mouseX, mouseY, deltaTime);
        }
    }

    @Override
    protected void buildSearchKey(StringBuilder sb) {
        sb.append(title.getString());
        if (!children.isEmpty()) {
            sb.append(" ");
            for (var child : children) {
                child.buildSearchKey(sb);
            }
        }
    }

}
