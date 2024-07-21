package mcp.mobius.waila.gui.widget.value;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigValue<T> extends ConfigListWidget.Entry {

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
            : new Button(0, 0, 40, 20, Component.translatable("controls.reset"), button -> resetValue());
    }

    @Override
    protected void drawEntry(PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        var title = getTitle();

        if (isDisabled()) title.withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
        else if (!isValueValid()) title.withStyle(ChatFormatting.ITALIC, ChatFormatting.RED);
        else if (!value.equals(initialValue)) title.withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW);
        else title.withStyle(ChatFormatting.RESET);

        client.font.drawShadow(matrices, title.copy(), rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 0xFFFFFF);

        var w = width;
        if (resetButton != null) {
            w -= resetButton.getWidth() + 2;
            resetButton.x = rowLeft + width - resetButton.getWidth();
            resetButton.y = rowTop + (height - resetButton.getHeight()) / 2;
            resetButton.active = !isValueValid() || (!isDisabled() && !getValue().equals(defaultValue));
            resetButton.render(matrices, mouseX, mouseY, deltaTime);
        }

        drawValue(matrices, w, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    @Override
    public void renderTooltip(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
        for (GuiEventListener child : children()) {
            if (child instanceof AbstractWidget widget) {
                var x1 = widget.x - 2;
                var y1 = widget.y;
                var x2 = widget.x + widget.getWidth() + 4;
                var y2 = widget.y + widget.getHeight() + 4;
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
            screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
        }
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

    public void setValue(T value) {
        this.value = value;
    }

    @SuppressWarnings("DataFlowIssue")
    protected void resetValue() {
        setValue(defaultValue);
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

    protected abstract void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);

}
