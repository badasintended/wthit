package mcp.mobius.waila.gui.config.value;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.function.Consumer;

public abstract class OptionsEntryValue<T> extends OptionsListWidget.Entry {

    private final ITextComponent title;
    private final String description;
    protected final Consumer<T> save;
    protected T value;

    public OptionsEntryValue(String optionName, Consumer<T> save) {
        this.title = new TextComponentTranslation(optionName);
        this.description = optionName + "_desc";
        this.save = save;
    }

    @Override
    public final void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
        client.fontRenderer.drawStringWithShadow(title.getFormattedText(), getX() + 10, getY() + (entryHeight / 4) + (client.fontRenderer.FONT_HEIGHT / 2), 16777215);
        drawValue(entryWidth, entryHeight, getX(), getY(), mouseX, mouseY, selected, partialTicks);
    }

    public void save() {
        save.accept(value);
    }

    public IGuiEventListener getListener() {
        return null;
    }

    public ITextComponent getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return super.getX();
    }

    public int getY() {
        return super.getY();
    }

    protected abstract void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);
}
