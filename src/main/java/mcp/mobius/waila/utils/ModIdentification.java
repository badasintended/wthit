package mcp.mobius.waila.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.HashMap;
import java.util.Map;

public class ModIdentification {

    public static Map<String, ModContainer> containers = new HashMap<>();

    public static void init() {
        containers.put("minecraft", Loader.instance().getMinecraftModContainer());
        containers.put("forge", ForgeModContainer.getInstance());
    }

    public static String nameFromStack(ItemStack stack) {
        if (stack.isEmpty())
            return "";

        String domain = stack.getItem().getCreatorModId(stack);
        ModContainer container = findModContainer(domain);

        return container.getName();
    }

    public static ModContainer findModContainer(String modID) {
        return containers.computeIfAbsent(modID, s -> {
            for (ModContainer container : Loader.instance().getModList())
                if (modID.equalsIgnoreCase(container.getModId()))
                    return container;

            return Loader.instance().getMinecraftModContainer();
        });
    }
}