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

    public boolean serverOnly = false;

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
            : new Button(0, 0, 40, 20, Component.translatable("controls.reset"), button -> resetValue());
    }

    @Override
    public final void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        super.render(matrices, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);

        Component title = !serverOnly ? this.title : this.title.copy().withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
        client.font.drawShadow(matrices, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 0xFFFFFF);

        int w = width;
        if (resetButton != null) {
            w -= resetButton.getWidth() + 2;
            resetButton.x = rowLeft + width - resetButton.getWidth();
            resetButton.y = rowTop + (height - resetButton.getHeight()) / 2;
            resetButton.active = !serverOnly && !getValue().equals(defaultValue);
            resetButton.render(matrices, mouseX, mouseY, deltaTime);
        }

        drawValue(matrices, w, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    public void renderTooltip(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
        boolean hasDescTl = I18n.exists(getDescription());
        if (serverOnly || hasDescTl) {
            String title = getTitle().getString();
            List<FormattedCharSequence> tooltip = Lists.newArrayList(Component.literal(title).getVisualOrderText());
            if (hasDescTl) {
                tooltip.addAll(client.font.split(Component.translatable(getDescription()).withStyle(ChatFormatting.GRAY), 250));
            }
            if (serverOnly) {
                tooltip.addAll(client.font.split(Component.translatable("config.waila.server_only").withStyle(ChatFormatting.RED), 250));
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
        if (!serverOnly) {
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

    protected abstract void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);

}
