package mcp.mobius.waila.gui.config.value;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

import java.util.function.Consumer;

public abstract class OptionsEntryValue<T> extends OptionsListWidget.Entry {

    private final TextComponent title;
    private final String description;
    protected final Consumer<T> save;
    protected T value;
    private int x;

    public OptionsEntryValue(String optionName, Consumer<T> save) {
        this.title = new TranslatableTextComponent(optionName);
        this.description = optionName + "_desc";
        this.save = save;
    }

    @Override
    public final void render(int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        client.textRenderer.drawWithShadow(title.getFormattedText(), rowLeft + 10, rowTop + (height / 4) + (client.textRenderer.fontHeight / 2), 16777215);
        drawValue(width, height, rowLeft, rowTop, mouseX, mouseY, hovered, deltaTime);
        this.x = rowLeft;
    }

    public void save() {
        save.accept(value);
    }

    public Element getListener() {
        return null;
    }

    public TextComponent getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return x;
    }

    protected abstract void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks);
}
