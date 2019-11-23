package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OptionsEntryValueInput<T> extends OptionsEntryValue<T> {

    public static final Predicate<String> ANY = s -> true;
    public static final Predicate<String> INTEGER = s -> s.matches("^[0-9]*$");
    public static final Predicate<String> FLOAT = s -> s.matches("[-+]?([0-9]*\\.[0-9]+|[0-9]+)") || s.endsWith(".") || s.isEmpty();

    private final TextFieldWidget textField;

    public OptionsEntryValueInput(String optionName, T value, Consumer<T> save, Predicate<String> validator) {
        super(optionName, save);

        this.value = value;
        this.textField = new WatchedTextfield(this, client.textRenderer, 0, 0, 98, 18);
        textField.setText(String.valueOf(value));
        textField.setTextPredicate(validator);
    }

    public OptionsEntryValueInput(String optionName, T value, Consumer<T> save) {
        this(optionName, value, save, s -> true);
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        textField.setX(x + 135);
        textField.y = y + entryHeight / 6;
        textField.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public Element getListener() {
        return textField;
    }

    @SuppressWarnings("unchecked")
    private void setValue(String text) {
        if (value instanceof String)
            value = (T) text;

        try {
            if (value instanceof Integer)
                value = (T) Integer.valueOf(text);
            else if (value instanceof Short)
                value = (T) Short.valueOf(text);
            else if (value instanceof Byte)
                value = (T) Byte.valueOf(text);
            else if (value instanceof Long)
                value = (T) Long.valueOf(text);
            else if (value instanceof Double)
                value = (T) Double.valueOf(text);
            else if (value instanceof Float)
                value = (T) Float.valueOf(text);
        } catch (NumberFormatException e) {
            // no-op
        }
    }

    private static class WatchedTextfield extends TextFieldWidget {
        public WatchedTextfield(OptionsEntryValueInput<?> watcher, TextRenderer fontRenderer, int x, int y, int width, int height) {
            super(fontRenderer, x, y, width, height, "");
            this.setChangedListener(watcher::setValue);
        }
    }
}
