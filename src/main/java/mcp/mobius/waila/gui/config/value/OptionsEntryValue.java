package mcp.mobius.waila.gui.config.value;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.resource.language.I18n;

import java.util.function.Consumer;

public abstract class OptionsEntryValue<T> extends OptionsListWidget.Entry {

    private final String title;
    private final String description;
    protected final Consumer<T> save;
    protected T value;

    public OptionsEntryValue(String optionName, Consumer<T> save) {
        this.title = I18n.translate(optionName);
        this.description = I18n.translate(optionName + "_desc");
        this.save = save;
    }

    @Override
    public final void draw(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
//        DisplayUtil.drawGradientRect(getX(), getY(), entryWidth, entryHeight, Color.RED.getRGB(), Color.BLUE.getRGB());
        client.fontRenderer.drawWithShadow(title, getX() + 10, getY() + (entryHeight / 4) + (client.fontRenderer.fontHeight / 2), 16777215);
        drawValue(entryWidth, entryHeight, getX(), getY(), mouseX, mouseY, selected, partialTicks);
        // TODO - Draw tooltip
    }

    public void save() {
        save.accept(value);
    }

    public GuiEventListener getListener() {
        return null;
    }

    protected abstract void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);
}
