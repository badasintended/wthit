package mcp.mobius.waila.gui.screen;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigScreen extends Screen {

    private final Screen parent;
    private final Runnable saver;
    private final Runnable canceller;

    @SuppressWarnings("unchecked")
    private final List<GuiEventListener> children = (List<GuiEventListener>) children();
    private ConfigListWidget options;

    public ConfigScreen(Screen parent, Component title, Runnable saver, Runnable canceller) {
        super(title);

        this.parent = parent;
        this.saver = saver;
        this.canceller = canceller;
    }

    public ConfigScreen(Screen parent, Component title) {
        this(parent, title, null, null);
    }

    @Override
    public void init() {
        super.init();

        options = getOptions();
        children.add(options);
        setFocused(options);

        if (saver != null && canceller != null) {
            addRenderableWidget(new Button(width / 2 - 102, height - 25, 100, 20, Component.translatable("gui.done"), w -> {
                options.save();
                saver.run();
                onClose();
            }));
            addRenderableWidget(new Button(width / 2 + 2, height - 25, 100, 20, Component.translatable("gui.cancel"), w -> {
                canceller.run();
                onClose();
            }));
        } else {
            addRenderableWidget(new Button(width / 2 - 50, height - 25, 100, 20, Component.translatable("gui.done"), w -> {
                options.save();
                onClose();
            }));
        }
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        options.render(matrices, mouseX, mouseY, partialTicks);
        drawCenteredString(matrices, font, title.getString(), width / 2, 12, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32) {
            return;
        }

        options.getChildAt(mouseX, mouseY).ifPresent(element -> {
            if (element instanceof ConfigValue<?> value) {
                boolean hasDescTl = I18n.exists(value.getDescription());
                if (value.serverOnly || hasDescTl) {
                    String title = value.getTitle().getString();
                    List<FormattedCharSequence> tooltip = Lists.newArrayList(Component.literal(title).getVisualOrderText());
                    if (hasDescTl) {
                        tooltip.addAll(font.split(Component.translatable(value.getDescription()).withStyle(ChatFormatting.GRAY), 250));
                    }
                    if (value.serverOnly) {
                        tooltip.addAll(font.split(Component.translatable("config.waila.server_only").withStyle(ChatFormatting.RED), 250));
                    }
                    renderTooltip(matrices, tooltip, mouseX, mouseY);
                }
            }
        });
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public void addListener(GuiEventListener listener) {
        children.add(listener);
    }

    public abstract ConfigListWidget getOptions();

}
