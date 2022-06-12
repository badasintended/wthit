package mcp.mobius.waila.gui.widget.value;

import java.util.function.Consumer;

import mcp.mobius.waila.api.IntFormat;
import org.jetbrains.annotations.Nullable;

public class IntInputValue extends InputValue<Integer> {

    public IntInputValue(String optionName, Integer value, @Nullable Integer defaultValue, Consumer<Integer> save, IntFormat format) {
        super(optionName, value, defaultValue, save, format, new Serializer<>() {
            @Override
            public String serialize(Integer integer) {
                return format.serialize(integer);
            }

            @Override
            public Integer deserialize(String s) {
                return format.deserialize(s);
            }
        });
    }

}
