package mcp.mobius.waila.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.HashMap;
import java.util.Map;

public class ModIdentification {

    public static Map<String, ModContainer> containers = new HashMap<String, ModContainer>();

    public static void init() {
        containers.put("minecraft", Loader.instance().getMinecraftModContainer());
        containers.put("forge", ForgeModContainer.getInstance());
    }

    public static String nameFromStack(ItemStack stack) {
        if (stack.isEmpty())
            return "";

        ResourceLocation resource = stack.getItem().getRegistryName();
        ModContainer container = findModContainer(resource.getResourceDomain());

        return container.getName();
    }

    public static ModContainer findModContainer(String modID) {
        if (containers.get(modID) != null)
            return containers.get(modID);

        ModContainer modContainer = null;
        for (ModContainer container : Loader.instance().getModList()) {
            if (modID.equalsIgnoreCase(container.getModId())) {
                containers.put(modID, container);
                modContainer = container;
            }
        }

        return modContainer == null ? Loader.instance().getMinecraftModContainer() : modContainer;
    }
}