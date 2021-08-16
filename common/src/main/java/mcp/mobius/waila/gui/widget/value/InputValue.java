package mcp.mobius.waila.gui.widget.value;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TextComponent;

public class InputValue<T> extends ConfigValue<T> {

    public static final Predicate<String> ANY = s -> true;
    public static final Predicate<String> INTEGER = s -> s.matches("[-+]?[0-9]*$");
    public static final Predicate<String> POSITIVE_INTEGER = s -> s.matches("[0-9]*$");
    public static final Predicate<String> DECIMAL = s -> s.matches("[-+]?[0-9]*([.][0-9]*)?");
    public static final Predicate<String> POSITIVE_DECIMAL = s -> s.matches("[0-9]*([.][0-9]*)?");

    private final EditBox textField;

    public InputValue(String optionName, T value, Consumer<T> save, Predicate<String> validator) {
        super(optionName, save);

        this.value = value;
        this.textField = new WatchedTextfield(this, client.font, 0, 0, 98, 18);
        textField.setValue(String.valueOf(value));
        textField.setFilter(validator);
    }

    public InputValue(String optionName, T value, Consumer<T> save) {
        this(optionName, value, save, ANY);
    }

    @Override
    protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        textField.setX(x + width - textField.getWidth());
        textField.y = y + (height - textField.getHeight()) / 2;
        textField.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
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

    private static class WatchedTextfield extends EditBox {

        public WatchedTextfield(InputValue<?> watcher, Font fontRenderer, int x, int y, int width, int height) {
            super(fontRenderer, x, y, width, height, new TextComponent(""));
            this.setResponder(watcher::setValue);
        }

        @Override
        public void insertText(String string) {
            int i = Math.min(getCursorPosition(), highlightPos);
            int j = Math.max(getCursorPosition(), highlightPos);
            int k = maxLength - getValue().length() - (i - j);
            String string2 = string;
            int l = string2.length();
            if (k < l) {
                string2 = string2.substring(0, k);
                l = k;
            }

            String string3 = (new StringBuilder(getValue())).replace(i, j, string2).toString();
            if (filter.test(string3)) {
                value = string3;
                this.setCursorPosition(i + l);
                this.setHighlightPos(getCursorPosition());
                onValueChange(string3);
            }
        }

    }

}
