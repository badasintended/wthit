package mcp.mobius.waila.gui.widget.value;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputValue<T> extends ConfigValue<T> {

    public static final Predicate<String> ANY = s -> true;
    public static final Predicate<String> INTEGER = s -> s.matches("[-+]?\\d*$");
    public static final Predicate<String> POSITIVE_INTEGER = s -> s.matches("\\d*$");
    public static final Predicate<String> DECIMAL = s -> s.matches("[-+]?\\d*([.]\\d*)?");
    public static final Predicate<String> POSITIVE_DECIMAL = s -> s.matches("\\d*([.]\\d*)?");

    private final EditBox textField;

    public InputValue(String optionName, T value, @Nullable T defaultValue, Consumer<T> save, Predicate<String> validator) {
        super(optionName, value, defaultValue, save);

        this.textField = new WatchedTextfield(this, client.font, 0, 0, 160, 18);
        textField.setValue(String.valueOf(value));
        textField.setFilter(validator);
    }

    @Override
    protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        textField.setEditable(!serverOnly);
        textField.setX(x + width - textField.getWidth());
        textField.y = y + (height - textField.getHeight()) / 2;
        textField.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
        return textField;
    }

    @Override
    protected void resetValue() {
        textField.setValue(String.valueOf(defaultValue));
    }

    @SuppressWarnings("unchecked")
    private void setValue(String text) {
        final T value = getValue();
        if (value instanceof String)
            setValue((T) text);

        try {
            if (value instanceof Integer)
                setValue((T) Integer.valueOf(text));
            else if (value instanceof Short)
                setValue((T) Short.valueOf(text));
            else if (value instanceof Byte)
                setValue((T) Byte.valueOf(text));
            else if (value instanceof Long)
                setValue((T) Long.valueOf(text));
            else if (value instanceof Double)
                setValue((T) Double.valueOf(text));
            else if (value instanceof Float)
                setValue((T) Float.valueOf(text));
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
        public void insertText(@NotNull String string) {
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
