package mcp.mobius.waila.gui;

import com.google.common.collect.Lists;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class GuiOptions extends GuiScreen {

    private final GuiScreen parent;
    private final ITextComponent title;
    private final Runnable saver;
    private final Runnable canceller;
    private OptionsListWidget options;

    public GuiOptions(GuiScreen parent, ITextComponent title, Runnable saver, Runnable canceller) {
        this.parent = parent;
        this.title = title;
        this.saver = saver;
        this.canceller = canceller;
    }

    public GuiOptions(GuiScreen parent, ITextComponent title) {
        this(parent, title, null, null);
    }

    @Override
    protected void initGui() {
        options = getOptions();
        children.add(options);
        setFocused(options);

        if (saver != null && canceller != null) {
            addButton(new GuiButton(1, width / 2 - 100, height - 25, 100, 20, I18n.format("gui.done")) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    options.save();
                    saver.run();
                    close();
                }
            });
            addButton(new GuiButton(2, width / 2 + 5, height - 25, 100, 20, I18n.format("gui.cancel")) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    canceller.run();
                    close();
                }
            });
        } else {
            addButton(new GuiButton(2, width / 2 - 50, height - 25, 100, 20, I18n.format("gui.done")) {
                @Override
                public void onClick(double mouseX, double mouseY) {
                    options.save();
                    close();
                }
            });
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        options.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, title.getFormattedText(), width / 2, 12, 16777215);
        super.render(mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32)
            return;

        int selectedIndex = options.getEntryAt(mouseX, mouseY);
        if (selectedIndex < 0 || selectedIndex >= options.getChildren().size())
            return;

        OptionsListWidget.Entry entry = options.getChildren().get(selectedIndex);
        if (entry instanceof OptionsEntryValue) {
            OptionsEntryValue value = (OptionsEntryValue) entry;

            if (I18n.hasKey(value.getDescription())) {
                int valueX = value.getX() + 10;
                String title = value.getTitle().getFormattedText();
                if (mouseX < valueX || mouseX > valueX + fontRenderer.getStringWidth(title))
                    return;

                List<String> tooltip = Lists.newArrayList(title);
                tooltip.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(value.getDescription()), 200));
                drawHoveringText(tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && options.mouseClicked(mouseX, mouseY, button)) {
            setDragging(true);
            setFocused(options);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && options.mouseReleased(mouseX, mouseY, button)) {
            setDragging(false);
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        mc.displayGuiScreen(parent);
    }

    public void addListener(IGuiEventListener listener) {
        children.add(listener);
    }

    public abstract OptionsListWidget getOptions();
}
