package mcp.mobius.waila.gui;

import com.google.common.collect.Lists;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

import java.util.List;

public abstract class GuiOptions extends Screen {

    private final Screen parent;
    private final TextComponent title;
    private final Runnable saver;
    private final Runnable canceller;
    private OptionsListWidget options;

    public GuiOptions(Screen parent, TextComponent title, Runnable saver, Runnable canceller) {
        this.parent = parent;
        this.title = title;
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
            addButton(new ButtonWidget(1, width / 2 - 100, height - 25, 100, 20, I18n.translate("gui.done")) {
                @Override
                public void onPressed(double mouseX, double mouseY) {
                    options.save();
                    saver.run();
                    close();
                }
            });
            addButton(new ButtonWidget(2, width / 2 + 5, height - 25, 100, 20, I18n.translate("gui.cancel")) {
                @Override
                public void onPressed(double mouseX, double mouseY) {
                    canceller.run();
                    close();
                }
            });
        } else {
            addButton(new ButtonWidget(2, width / 2 - 50, height - 25, 100, 20, I18n.translate("gui.done")) {
                @Override
                public void onPressed(double mouseX, double mouseY) {
                    options.save();
                    close();
                }
            });
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        drawBackground();
        options.draw(mouseX, mouseY, partialTicks);
        drawStringCentered(fontRenderer, title.getFormattedText(), width / 2, 12, 16777215);
        super.draw(mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32)
            return;

        int selectedIndex = options.getSelectedEntry(mouseX, mouseY);
        if (selectedIndex < 0 || selectedIndex >= options.getEntries().size())
            return;

        OptionsListWidget.Entry entry = options.getEntries().get(selectedIndex);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && options.mouseClicked(mouseX, mouseY, button)) {
            setActive(true);
            setFocused(options);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && options.mouseReleased(mouseX, mouseY, button)) {
            setActive(false);
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        client.openScreen(parent);
    }

    public void addListener(GuiEventListener listener) {
        listeners.add(listener);
    }

    public abstract OptionsListWidget getOptions();
}
