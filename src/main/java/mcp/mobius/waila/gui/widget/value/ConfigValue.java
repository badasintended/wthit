package mcp.mobius.waila.gui.widget.value;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public abstract class ConfigValue<T> extends ConfigListWidget.Entry {

    protected final Consumer<T> save;
    protected final String translationKey;

    @Nullable
    protected final T defaultValue;

    private final Component title;
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
        this.value = value;
        this.save = save;
        this.defaultValue = defaultValue;
        this.resetButton = defaultValue == null ? null
            : createButton(0, 0, 40, 20, Component.translatable("controls.reset"), button -> resetValue());
    }

    @Override
    public final void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        super.render(matrices, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);

        Component title = !isDisabled() ? this.title : this.title.copy().withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
        client.font.drawShadow(matrices, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 0xFFFFFF);

        int w = width;
        if (resetButton != null) {
            w -= resetButton.getWidth() + 2;
            resetButton.setX(rowLeft + width - resetButton.getWidth());
            resetButton.setY(rowTop + (height - resetButton.getHeight()) / 2);
            resetButton.active = !isDisabled() && !getValue().equals(defaultValue);
            resetButton.render(matrices, mouseX, mouseY, deltaTime);
        }

        drawValue(matrices, w, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    public void renderTooltip(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
        boolean hasDescTl = I18n.exists(getDescription());
        if (id != null || hasDescTl || (isDisabled() && disabledReason != null)) {
            String title = getTitle().getString();
            List<FormattedCharSequence> tooltip = Lists.newArrayList(Component.literal(title).getVisualOrderText());
            if (hasDescTl) {
                tooltip.addAll(client.font.split(Component.translatable(getDescription()).withStyle(ChatFormatting.GRAY), 250));
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

    @Override
    public boolean match(String filter) {
        boolean match = super.match(filter) || StringUtils.containsIgnoreCase(title.getString(), filter);
        if (id != null) match = match || StringUtils.containsIgnoreCase(id, filter);
        if (I18n.exists(getDescription())) match = match || StringUtils.containsIgnoreCase(I18n.get(getDescription()), filter);
        return match;
    }

    @Override
    protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        GuiEventListener element = getListener();
        if (element != null) {
            children.add(element);
        }

        Button resetButton = getResetButton();
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

    public Component getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return x;
    }

    public final T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    protected void resetValue() {
        setValue(defaultValue);
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
