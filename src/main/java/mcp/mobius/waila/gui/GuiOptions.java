package mcp.mobius.waila.gui;

import com.google.common.collect.Lists;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

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
    protected void onInitialized() {
        options = getOptions();
        listeners.add(options);
        setFocused(options);

        if (saver != null && canceller != null) {
            addButton(new ButtonWidget(screenWidth / 2 - 100, screenHeight - 25, 100, 20, I18n.translate("gui.done"), w -> {
                options.save();
                saver.run();
                close();
            }));
            addButton(new ButtonWidget(screenWidth / 2 + 5, screenHeight - 25, 100, 20, I18n.translate("gui.cancel"), w -> {
                canceller.run();
                close();
            }));
        } else {
            addButton(new ButtonWidget(screenWidth / 2 - 50, screenHeight - 25, 100, 20, I18n.translate("gui.done"), w -> {
                options.save();
                close();
            }));
        }
    }



    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawBackground();
        options.render(mouseX, mouseY, partialTicks);
        drawStringCentered(fontRenderer, title.getFormattedText(), screenWidth / 2, 12, 16777215);
        super.render(mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > screenHeight - 32)
            return;

        int selectedIndex = options.getSelectedEntry(mouseX, mouseY);
        if (selectedIndex < 0 || selectedIndex >= options.getInputListeners().size())
            return;

        OptionsListWidget.Entry entry = options.getInputListeners().get(selectedIndex);
        if (entry instanceof OptionsEntryValue) {
            OptionsEntryValue value = (OptionsEntryValue) entry;

            if (I18n.hasTranslation(value.getDescription())) {
                int valueX = value.getX() + 10;
                String title = value.getTitle().getFormattedText();
                if (mouseX < valueX || mouseX > valueX + fontRenderer.getStringWidth(title))
                    return;

                List<String> tooltip = Lists.newArrayList(title);
                tooltip.addAll(fontRenderer.wrapStringToWidthAsList(I18n.translate(value.getDescription()), 200));
                drawTooltip(tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public void close() {
        client.openScreen(parent);
    }

    public void addListener(InputListener listener) {
        listeners.add(listener);
    }

    public abstract OptionsListWidget getOptions();
}
