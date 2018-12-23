package mcp.mobius.waila.utils;

import com.google.common.collect.Maps;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;
import java.util.Map;

public class ModIdentification {

    private static final Map<String, ModInfo> CONTAINER_CACHE = Maps.newHashMap();
    private static final ModInfo MC_MOD_INFO = createModInfo("minecraft", "Minecraft");
    static {
        CONTAINER_CACHE.put(MC_MOD_INFO.getId(), MC_MOD_INFO);
    }

    public static ModInfo getModInfo(String namespace) {
        return CONTAINER_CACHE.computeIfAbsent(namespace, s -> FabricLoader.INSTANCE.getModContainers().stream()
                .filter(c -> c.getInfo().getId().equals(s))
                .map(ModContainer::getInfo)
                .findFirst()
                .orElse(createModInfo(s, null)));
    }

    public static ModInfo getModInfo(Identifier id) {
        return getModInfo(id.getNamespace());
    }

    public static ModInfo getModInfo(Block block) {
        return getModInfo(Registry.BLOCK.getId(block));
    }

    public static ModInfo getModInfo(Item item) {
        return getModInfo(Registry.ITEM.getId(item));
    }

    public static ModInfo getModInfo(Entity entity) {
        Identifier id = Registry.ENTITY_TYPE.getId(entity.getType());
        return getModInfo(id == null ? new Identifier("nope") : id);
    }

    private static ModInfo createModInfo(String modId, String name) {
        ModInfo info = new ModInfo();
        try {
            Field _id = ModInfo.class.getDeclaredField("id");
            _id.setAccessible(true);
            _id.set(info, modId);

            Field _name = ModInfo.class.getDeclaredField("name");
            _name.setAccessible(true);
            _name.set(info, name == null ? modId : name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }
}