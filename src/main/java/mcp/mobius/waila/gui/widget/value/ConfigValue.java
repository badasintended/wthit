package mcp.mobius.waila.gui.widget.value;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    private String id;

    private T value;
    private int x;

    public ConfigValue(String translationKey, T value, @Nullable T defaultValue, Consumer<T> save) {
        this.translationKey = translationKey;
        this.title = new TranslatableComponent(translationKey);
        this.description = translationKey + "_desc";
        this.value = value;
        this.save = save;
        this.defaultValue = defaultValue;
        this.resetButton = defaultValue == null ? null
            : new Button(0, 0, 40, 20, new TranslatableComponent("controls.reset"), button -> resetValue());
    }

    @Override
    public final void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        super.render(matrices, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);

        Component title = !isDisabled() ? this.title : this.title.copy().withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
        client.font.drawShadow(matrices, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 0xFFFFFF);

        int w = width;
        if (resetButton != null) {
            w -= resetButton.getWidth() + 2;
            resetButton.x = rowLeft + width - resetButton.getWidth();
            resetButton.y = rowTop + (height - resetButton.getHeight()) / 2;
            resetButton.active = !isDisabled() && !getValue().equals(defaultValue);
            resetButton.render(matrices, mouseX, mouseY, deltaTime);
        }

        drawValue(matrices, w, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    public void renderTooltip(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
        boolean hasDescTl = I18n.exists(getDescription());
        if (id != null || hasDescTl || isDisabled()) {
            String title = getTitle().getString();
            List<FormattedCharSequence> tooltip = Lists.newArrayList(new TextComponent(title).getVisualOrderText());
            if (hasDescTl) {
                tooltip.addAll(client.font.split(new TranslatableComponent(getDescription()).withStyle(ChatFormatting.GRAY), 250));
            }
            if (isDisabled()) {
                tooltip.addAll(client.font.split(new TranslatableComponent(disabledReason).withStyle(ChatFormatting.RED), 250));
            }
            if (id != null) {
                tooltip.add(new TextComponent(id).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            }
            screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void addToScreen(ConfigScreen screen) {
        GuiEventListener element = getListener();
        if (element != null) {
            screen.addListener(element);
        }

        Button resetButton = getResetButton();
        if (resetButton != null) {
            screen.addListener(resetButton);
        }
    }

    public void save() {
        if (!isDisabled()) {
            save.accept(getValue());
        }
    }

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

    public void disable(String reason) {
        this.disabledReason = reason;
    }

    public final boolean isDisabled() {
        return disabledReason != null;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    protected abstract void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);

}
