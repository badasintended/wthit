package mcp.mobius.waila.gui.widget.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public abstract class ConfigValue<T, C extends ConfigValue<T, C>> extends ConfigListWidget.Entry {

    private final Map<Object, Consumer<C>> watchers = new HashMap<>();

    protected final Consumer<T> save;
    protected final String translationKey;

    @Nullable
    protected final T defaultValue;
    protected final T initialValue;

    private final MutableComponent title;
    private final String description;
    private final Button resetButton;

    @Nullable
    private String disabledReason = null;
    private boolean disabled = false;

    @Nullable
    private String id;

    private T value;
    private int x;

    public ConfigValue(String translationKey, T value, @Nullable T defaultValue, Consumer<T> save) {
        this.translationKey = translationKey;
        this.title = Component.translatable(translationKey);
        this.description = translationKey + "_desc";
        this.initialValue = value;
        this.value = value;
        this.save = save;
        this.defaultValue = defaultValue;
        this.resetButton = defaultValue == null ? null
            : createButton(0, 0, 40, 20, Component.translatable("controls.reset"), button -> resetValue());
    }

    @Override
    protected void drawEntry(GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        var title = getTitle();

        if (isDisabled()) title.withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
        else if (!isValueValid()) title.withStyle(ChatFormatting.ITALIC, ChatFormatting.RED);
        else if (!value.equals(initialValue)) title.withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW);
        else title.withStyle(ChatFormatting.RESET);

        ctx.drawString(client.font, title.copy(), rowLeft, rowTop + (height - client.font.lineHeight) / 2, 0xFFFFFFFF);

        var w = width;
        if (resetButton != null) {
            w -= resetButton.getWidth() + 2;
            resetButton.setX(rowLeft + width - resetButton.getWidth());
            resetButton.setY(rowTop + (height - resetButton.getHeight()) / 2);
            resetButton.active = !isValueValid() || (!isDisabled() && !getValue().equals(defaultValue));
            resetButton.render(ctx, mouseX, mouseY, deltaTime);
        }

        drawValue(ctx, w, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    public void renderTooltip(GuiGraphics ctx, int mouseX, int mouseY) {
        for (var child : children()) {
            if (child instanceof AbstractWidget widget) {
                var x1 = widget.getX() - 2;
                var y1 = widget.getY();
                var x2 = widget.getX() + widget.getWidth() + 4;
                var y2 = widget.getY() + widget.getHeight() + 4;
                if (x1 <= mouseX && mouseX <= x2 && y1 <= mouseY && mouseY <= y2) return;
            }
        }

        var desc = getDescription();
        if (id != null || desc != null || (isDisabled() && disabledReason != null)) {
            var title = getTitle().getString();
            List<FormattedCharSequence> tooltip = Lists.newArrayList(Component.literal(title).getVisualOrderText());
            if (desc != null) {
                tooltip.addAll(client.font.split(desc, 250));
            }
            if (isDisabled() && disabledReason != null) {
                tooltip.addAll(client.font.split(Component.translatable(disabledReason).withStyle(ChatFormatting.RED), 250));
            }
            if (id != null) {
                tooltip.add(Component.literal(id).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            }
            ctx.setTooltipForNextFrame(client.font, tooltip, mouseX, mouseY);
        }
    }

    public boolean isChanged() {
        if (!isValueValid()) return true;
        return !value.equals(initialValue);
    }

    public boolean isValueValid() {
        return true;
    }

    @Override
    protected void buildSearchKey(StringBuilder sb) {
        sb.append(getTitle().getString());
        var desc = getDescription();
        if (desc != null) sb.append(" ").append(desc.getString());
    }

    @Override
    protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        var element = getListener();
        if (element != null) {
            children.add(element);
        }

        var resetButton = getResetButton();
        if (resetButton != null) {
            children.add(resetButton);
        }
    }

    public void save() {
        if (!isDisabled()) {
            save.accept(getValue());
        }
    }

    @Nullable
    public GuiEventListener getListener() {
        return null;
    }

    @Nullable
    public Button getResetButton() {
        return resetButton;
    }

    public MutableComponent getTitle() {
        return title;
    }

    @Nullable
    public Component getDescription() {
        return I18n.exists(description) ? Component.translatable(description).withStyle(ChatFormatting.GRAY) : null;
    }

    public int getX() {
        return x;
    }

    public final @NotNull T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    protected final void callWatchers() {
        watchers.values().forEach(w -> w.accept((C) this));
    }

    protected final void setValue(T value, boolean notify) {
        this.value = value;
        if (notify) callWatchers();
    }

    public void setValue(T value) {
        setValue(value, true);
    }

    protected void resetValue() {
        setValue(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public final void addWatcher(Object key, Consumer<C> watcher) {
        watchers.put(key, watcher);
        watcher.accept((C) this);
    }

    public void enable() {
        this.disabledReason = null;
        this.disabled = false;
    }

    public void disable(@Nullable String reason) {
        this.disabledReason = reason;
        this.disabled = true;
    }

    public final boolean isDisabled() {
        return disabled;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    protected abstract void drawValue(GuiGraphics ctx, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);

}
