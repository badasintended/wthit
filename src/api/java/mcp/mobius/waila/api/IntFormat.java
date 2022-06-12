package mcp.mobius.waila.api;

import java.util.Locale;
import java.util.function.Predicate;

import org.intellij.lang.annotations.RegExp;

public enum IntFormat implements Predicate<String> {
    BINARY(2, "[-+]?[01]*$"),
    OCTAL(8, "[-+]?[0-7]*$"),
    DECIMAL(10, "[-+]?\\d*$"),
    HEXADECIMAL(16, "[0-9a-fA-F]*$") {
        @Override
        public String serialize(int integer) {
            return Integer.toHexString(integer).toUpperCase(Locale.ROOT);
        }

        @Override
        public int deserialize(String string) {
            return string.isEmpty() ? 0 : Integer.parseUnsignedInt(string, radix);
        }
    };

    public final int radix;
    public final @RegExp String regex;

    IntFormat(int radix, @RegExp String regex) {
        this.radix = radix;
        this.regex = regex;
    }

    public String serialize(int integer) {
        return Integer.toString(integer, radix);
    }

    public int deserialize(String string) {
        return Integer.parseInt(string, radix);
    }

    @Override
    public boolean test(String string) {
        return string.matches(regex);
    }
}
