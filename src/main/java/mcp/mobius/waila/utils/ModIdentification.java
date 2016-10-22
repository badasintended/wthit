package mcp.mobius.waila.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.HashMap;
import java.util.Map;

public class ModIdentification {

    public static Map<String, ModContainer> containers = new HashMap<String, ModContainer>();

    public static void init() {

    }

    public static String nameFromStack(ItemStack stack) {
        if (stack == null)
            return "";

        ResourceLocation resource = stack.getItem().getRegistryName();
        ModContainer container = containers.get(resource.getResourceDomain());
        if (container == null)
            container = findModContainer(resource.getResourceDomain());

        return container == null ? "Minecraft" : container.getName();
    }

    public static ModContainer findModContainer(String modID) {
        for (ModContainer container : Loader.instance().getModList()) {
            if (modID.equalsIgnoreCase(container.getModId())) {
                containers.put(modID, container);
                return container;
            }
        }

        return null;
    }
}