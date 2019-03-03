package mcp.mobius.waila.gui.config.value;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.gui.InputListener;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

import java.util.function.Consumer;

public abstract class OptionsEntryValue<T> extends OptionsListWidget.Entry {

    private final TextComponent title;
    private final String description;
    protected final Consumer<T> save;
    protected T value;

    public OptionsEntryValue(String optionName, Consumer<T> save) {
        this.title = new TranslatableTextComponent(optionName);
        this.description = optionName + "_desc";
        this.save = save;
    }

    @Override
    public final void draw(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
        client.textRenderer.drawWithShadow(title.getFormattedText(), getX() + 10, getY() + (entryHeight / 4) + (client.textRenderer.fontHeight / 2), 16777215);
        drawValue(entryWidth, entryHeight, getX(), getY(), mouseX, mouseY, selected, partialTicks);
    }

    public void save() {
        save.accept(value);
    }

    public InputListener getListener() {
        return null;
    }

    public TextComponent getTitle() {
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
