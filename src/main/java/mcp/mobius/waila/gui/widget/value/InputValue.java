package mcp.mobius.waila.gui.widget.value;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.mixin.EditBoxAccess;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputValue<T> extends ConfigValue<T> {

    public static final Predicate<String> ANY = s -> true;
    public static final Predicate<String> INTEGER = s -> s.matches("[-+]?\\d*$");
    public static final Predicate<String> POSITIVE_INTEGER = s -> s.matches("\\d*$");
    public static final Predicate<String> DECIMAL = s -> s.matches("[-+]?\\d*([.]\\d*)?");
    public static final Predicate<String> POSITIVE_DECIMAL = s -> s.matches("\\d*([.]\\d*)?");
    public static final Predicate<String> IDENTIFIER = s -> s.matches("^[a-z\\d_./-]*$") || s.matches("^[a-z\\d_.-]*:[a-z\\d_./-]*$");

    private final Serializer<T> serializer;
    private final EditBox textField;

    private boolean valueFromTextField = false;

    public InputValue(String optionName, T value, @Nullable T defaultValue, Consumer<T> save, Predicate<String> validator) {
        this(optionName, value, defaultValue, save, validator, new Serializer<>() {
            @Override
            public String serialize(T t) {
                return String.valueOf(t);
            }

            @Override
            @SuppressWarnings("unchecked")
            public T deserialize(String s) {
                if (value instanceof String)
                    return (T) s;
                if (value instanceof Integer)
                    return (T) (s.isEmpty() ? Integer.valueOf(0) : Integer.valueOf(s));
                else if (value instanceof Short)
                    return (T) (s.isEmpty() ? Short.valueOf((short) 0) : Short.valueOf(s));
                else if (value instanceof Byte)
                    return (T) (s.isEmpty() ? Byte.valueOf((byte) 0) : Byte.valueOf(s));
                else if (value instanceof Long)
                    return (T) (s.isEmpty() ? Long.valueOf(0L) : Long.valueOf(s));
                else if (value instanceof Double)
                    return (T) (s.isEmpty() ? Double.valueOf(0) : Double.valueOf(s));
                else if (value instanceof Float)
                    return (T) (s.isEmpty() ? Float.valueOf(0) : Float.valueOf(s));

                throw new UnsupportedOperationException("Unsupported value type");
            }
        });
    }

    public InputValue(String optionName, T value, @Nullable T defaultValue, Consumer<T> save, Predicate<String> validator, Serializer<T> serializer) {
        super(optionName, value, defaultValue, save);

        this.serializer = serializer;
        this.textField = new WatchedTextfield();
        textField.setValue(serializer.serialize(value));
        textField.setFilter(validator);
    }

    @Override
    protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        textField.setEditable(!isDisabled());
        textField.setX(x + width - textField.getWidth());
        textField.y = y + (height - textField.getHeight()) / 2;
        textField.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        textField.tick();
    }

    @Override
    public GuiEventListener getListener() {
        return textField;
    }

    @Override
    protected void resetValue() {
        textField.setValue(serializer.serialize(defaultValue));
    }

    private void setValue(String text) {
        valueFromTextField = true;
        try {
            setValue(serializer.deserialize(text));
        } catch (Throwable t) {
            // no-op
        }
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);

        if (!valueFromTextField) {
            EditBoxAccess access = (EditBoxAccess) textField;
            access.wthit_value(serializer.serialize(value));
            textField.setCursorPosition(access.wthit_value().length());
            textField.setHighlightPos(textField.getCursorPosition());
        }

        valueFromTextField = false;
    }

    private class WatchedTextfield extends EditBox {

        public WatchedTextfield() {
            super(client.font, 0, 0, 160, 18, Component.empty());
            this.setResponder(InputValue.this::setValue);
        }

        @Override
        public void insertText(@NotNull String string) {
            EditBoxAccess access = (EditBoxAccess) this;
            int i = Math.min(getCursorPosition(), access.wthit_highlightPos());
            int j = Math.max(getCursorPosition(), access.wthit_highlightPos());
            int k = access.wthit_maxLength() - getValue().length() - (i - j);
            String string2 = string;
            int l = string2.length();
            if (k < l) {
                string2 = string2.substring(0, k);
                l = k;
            }

            String string3 = (new StringBuilder(getValue())).replace(i, j, string2).toString();
            if (access.wthit_filter().test(string3)) {
                access.wthit_value(string3);
                this.setCursorPosition(i + l);
                this.setHighlightPos(getCursorPosition());
                access.wthit_onValueChange(string3);
            }
        }

    }

    public interface Serializer<T> {

        String serialize(T t);

        T deserialize(String s);

    }

}
