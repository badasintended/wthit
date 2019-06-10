package mcp.mobius.waila.gui;

import com.google.common.collect.Lists;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponent;

import java.util.List;

public abstract class GuiOptions extends Screen {

    private final Screen parent;
    private final Runnable saver;
    private final Runnable canceller;
    private OptionsListWidget options;

    public GuiOptions(Screen parent, TextComponent title, Runnable saver, Runnable canceller) {
        super(title);

        this.parent = parent;
        this.saver = saver;
        this.canceller = canceller;
    }

    public GuiOptions(Screen parent, TextComponent title) {
        this(parent, title, null, null);
    }

    @Override
    public void init(Minecraft client, int width, int height) {
        super.init(client, width, height);

        options = getOptions();
        children.add(options);
        setFocused(options);

        if (saver != null && canceller != null) {
            addButton(new Button(width / 2 - 100, height - 25, 100, 20, I18n.format("gui.done"), w -> {
                options.save();
                saver.run();
                onClose();
            }));
            addButton(new Button(width / 2 + 5, height - 25, 100, 20, I18n.format("gui.cancel"), w -> {
                canceller.run();
                onClose();
            }));
        } else {
            addButton(new Button(width / 2 - 50, height - 25, 100, 20, I18n.format("gui.done"), w -> {
                options.save();
                onClose();
            }));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        options.render(mouseX, mouseY, partialTicks);
        drawCenteredString(font, title.getFormattedText(), width / 2, 12, 16777215);
        super.render(mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32)
            return;

        OptionsListWidget.Entry entry = options.getSelected();
        if (entry instanceof OptionsEntryValue) {
            OptionsEntryValue value = (OptionsEntryValue) entry;

            if (I18n.hasKey(value.getDescription())) {
                int valueX = value.getX() + 10;
                String title = value.getTitle().getFormattedText();
                if (mouseX < valueX || mouseX > valueX + font.getStringWidth(title))
                    return;

                List<String> tooltip = Lists.newArrayList(title);
                tooltip.addAll(font.listFormattedStringToWidth(I18n.format(value.getDescription()), 200));
                renderTooltip(tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public void onClose() {
        minecraft.displayGuiScreen(parent);
    }

    public void addListener(IGuiEventListener listener) {
        children.add(listener);
    }

    public abstract OptionsListWidget getOptions();
}
