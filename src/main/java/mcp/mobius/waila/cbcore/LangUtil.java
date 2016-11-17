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
        String ret = I18n.translateToLocal(s);//LanguageRegistry.instance().getStringLocalization(s);
        if (ret.length() == 0)
            ret = I18n.translateToLocal(s);//LanguageRegistry.instance().getStringLocalization(s, "en_US");
        if (ret.length() == 0)
            ret = I18n.translateToLocal(s);
        if (ret.length() == 0)
            return s;
        if (format.length > 0)
            ret = String.format(ret, format);
        return ret;
    }

    public static String translateG(String s, Object... format) {
        return instance.translate(s, format);
    }
}
