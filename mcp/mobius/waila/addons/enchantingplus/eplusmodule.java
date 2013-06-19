package mcp.mobius.waila.addons.enchantingplus;

import net.minecraft.enchantment.Enchantment;

import java.lang.reflect.Method;

/**
 * @author Freyja
 */
public class EplusModule {

    static Class EplusApi;
    static Method Tooltip;

    public static String getEnchantmentToolTip(Enchantment enchantment) {
        try {

            if (EplusApi == null) {
                EplusApi = Class.forName("eplus.api.EplusApi");
            }

            if (Tooltip == null) {
                Tooltip = EplusApi.getMethod("getEnchantmentToolTip", Enchantment.class);
            }

            return String.valueOf(Tooltip.invoke(null, enchantment));

        } catch (Exception e) {
            return "";
        }
    }
}
