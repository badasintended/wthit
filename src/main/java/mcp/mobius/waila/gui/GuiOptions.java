package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

public abstract class GuiOptions extends Gui {

    private final Gui parent;
    private final TextComponent title;
    private final Runnable saver;
    private final Runnable canceller;
    private OptionsListWidget options;

    public GuiOptions(Gui parent, TextComponent title, Runnable saver, Runnable canceller) {
        this.parent = parent;
        this.title = title;
        this.saver = saver;
        this.canceller = canceller;
    }

    public GuiOptions(Gui parent, TextComponent title) {
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
    public void draw(int x, int y, float partialTicks) {
        drawBackground();
        options.draw(x, y, partialTicks);
        drawStringCentered(fontRenderer, title.getFormattedText(), width / 2, 12, 16777215);
        super.draw(x, y, partialTicks);
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
        client.openGui(parent);
    }

    public void addListener(GuiEventListener listener) {
        listeners.add(listener);
    }

    public abstract OptionsListWidget getOptions();
}
