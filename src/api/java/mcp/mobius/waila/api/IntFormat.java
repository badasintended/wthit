package mcp.mobius.waila.api;

import java.util.Locale;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.Language;

public enum IntFormat implements Predicate<String> {
    BINARY(2, "^[-+]?[01]*$"),
    OCTAL(8, "^[-+]?[0-7]*$"),
    DECIMAL(10, "^[-+]?\\d*$"),

    HEXADECIMAL(16, "^[\\da-fA-F]*$") {
        @Override
        public String serialize(int integer) {
            return Integer.toHexString(integer).toUpperCase(Locale.ROOT);
        }

        @Override
        public int deserialize(String string) {
            return IntFormat.deserializeHex(string);
        }
    },

    RGB_HEX(16, "^[\\da-fA-F]{0,6}$") {
        @Override
        public String serialize(int integer) {
            return IntFormat.serializeHex(integer, 6);
        }

        @Override
        public int deserialize(String string) {
            return IntFormat.deserializeHex(string);
        }
    },

    ARGB_HEX(16, "^[\\da-fA-F]{0,8}$") {
        @Override
        public String serialize(int integer) {
            return IntFormat.serializeHex(integer, 8);
        }

        @Override
        public int deserialize(String string) {
            return IntFormat.deserializeHex(string);
        }
    };

    public final int radix;
    public final String regex;

    IntFormat(int radix, @Language("RegExp") String regex) {
        this.radix = radix;
        this.regex = regex;
    }

    public String serialize(int integer) {
        return Integer.toString(integer, radix);
    }

    public int deserialize(String string) {
        return string.isEmpty() ? 0 : Integer.parseInt(string, radix);
    }

    @Override
    public boolean test(String string) {
        return string.matches(regex);
    }

    private static String serializeHex(int integer, int lenght) {
        var res = Integer.toHexString(integer).toUpperCase(Locale.ROOT);
        if (res.length() < lenght) {
            res = StringUtils.repeat('0', lenght - res.length()) + res;
        }
        return res;
    }

    private static int deserializeHex(String string) {
        return string.isEmpty() ? 0 : Integer.parseUnsignedInt(string, 16);
    }
}
