package mcp.mobius.waila.gui.config.value;

import java.util.function.Consumer;
import java.util.function.Predicate;

import mcp.mobius.waila.mixin.AccessorTextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class OptionsEntryValueInput<T> extends OptionsEntryValue<T> {

    public static final Predicate<String> ANY = s -> true;
    public static final Predicate<String> INTEGER = s -> s.matches("[-+]?[0-9]*$");
    public static final Predicate<String> POSITIVE_INTEGER = s -> s.matches("[0-9]*$");
    public static final Predicate<String> FLOAT = s -> s.matches("[-+]?[0-9]*([.][0-9]*)?");
    public static final Predicate<String> POSITIVE_FLOAT = s -> s.matches("[0-9]*([.][0-9]*)?");

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
    protected void drawValue(MatrixStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        textField.setX(x + width - textField.getWidth());
        textField.y = y + (height - textField.getHeight()) / 2;
        textField.render(matrices, mouseX, mouseY, partialTicks);
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
            super(fontRenderer, x, y, width, height, new LiteralText(""));
            this.setChangedListener(watcher::setValue);
        }

        @Override
        public void write(String string) {
            AccessorTextFieldWidget self = (AccessorTextFieldWidget) this;

            int i = self.getSelectionStart() < self.getSelectionEnd() ? self.getSelectionStart() : self.getSelectionEnd();
            int j = self.getSelectionStart() < self.getSelectionEnd() ? self.getSelectionEnd() : self.getSelectionStart();
            int k = self.getMaxLength() - getText().length() - (i - j);
            String string2 = string;
            int l = string2.length();
            if (k < l) {
                string2 = string2.substring(0, k);
                l = k;
            }

            String string3 = (new StringBuilder(getText())).replace(i, j, string2).toString();
            if (self.getTextPredicate().test(string3)) {
                self.set(string3);
                this.setSelectionStart(i + l);
                this.setSelectionEnd(self.getSelectionStart());
                self.callOnChanged(getText());
            }
        }

    }

}
