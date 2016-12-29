package mcp.mobius.waila.cbcore;

import net.minecraft.util.text.translation.I18n;

/**
 * Easy localisation access.
 */
public class LangUtil {
    public static LangUtil instance = new LangUtil(null);

    public String prefix;

    public LangUtil(String prefix) {
        this.prefix = prefix;
    }

    public String translate(String s, Object... format) {
        if (prefix != null && !s.startsWith(prefix + "."))
            s = prefix + "." + s;
        return I18n.translateToLocalFormatted(s, format);
    }

    public static String translateG(String s, Object... format) {
        return instance.translate(s, format);
    }
}
