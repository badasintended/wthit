package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class CategoryEntry extends ConfigListWidget.Entry {

    public final Component title;
    private final Button collapseButton;
    private final Button expandAllButton;
    private final List<ConfigListWidget.Entry> children = new ArrayList<>();

    private boolean hasNested = false;
    private boolean collapsed = false;
    private boolean filterMatchParent = false;

    public CategoryEntry(String title) {
        this.title = Component.translatable(title).withStyle(ChatFormatting.BOLD);
        this.collapseButton = DisplayUtil.createButton(0, 0, 12, 12, CommonComponents.EMPTY, b -> toggleCollapse());
        this.expandAllButton = DisplayUtil.createButton(0, 0, 20, 12, Component.literal("++"), b -> {
            setCollapse(false, true);
            list.init();
            list.setFocused(this);
            setFocused(b);
        });

        expandAllButton.setTooltip(Tooltip.create(Component.translatable(Tl.Config.EXPAND_ALL)));
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
        collapseButton.setMessage(Component.literal(collapsed ? "+" : "-"));

        var added = 1;
        var matchCategory = StringUtils.containsIgnoreCase(title.getString(), list.filter);
        if (category != null) matchCategory = matchCategory || filterMatchParent;

        if (!collapsed) {
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
    protected void drawEntry(GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        var buttonY = rowTop + (height - collapseButton.getHeight()) / 2;

        collapseButton.setX(rowLeft);
        collapseButton.setY(buttonY);
        collapseButton.render(ctx, mouseX, mouseY, deltaTime);
        ctx.drawString(client.font, title, rowLeft + collapseButton.getWidth() + 4, rowTop + ((height - client.font.lineHeight) / 2) + 1, 0xFFFFFF);

        expandAllButton.active = hasNested;
        if (hasNested) {
            expandAllButton.setX(rowLeft + width - expandAllButton.getWidth());
            expandAllButton.setY(buttonY);
            expandAllButton.render(ctx, mouseX, mouseY, deltaTime);
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
