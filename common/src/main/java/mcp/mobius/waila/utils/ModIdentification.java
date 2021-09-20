package mcp.mobius.waila.utils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModIdentification {

    public static Function<String, Optional<Info>> supplier;
    public static Function<ItemStack, String> itemStackSupplier;

    private static final Map<String, Info> CONTAINER_CACHE = Maps.newHashMap();
    private static final Info MC_MOD_INFO = new Info("minecraft", "Minecraft");
    private static final Info COMMON_MOD_INFO = new Info("c", "Common");

    static {
        CONTAINER_CACHE.put(MC_MOD_INFO.getId(), MC_MOD_INFO);
        CONTAINER_CACHE.put(COMMON_MOD_INFO.getId(), COMMON_MOD_INFO);
    }

    public static Info getModInfo(String namespace) {
        return CONTAINER_CACHE.computeIfAbsent(namespace, s -> supplier.apply(namespace).orElse(new Info(s, s)));
    }

    public static Info getModInfo(Identifier id) {
        return getModInfo(id.getNamespace());
    }

    public static Info getModInfo(Block block) {
        return getModInfo(Registry.BLOCK.getId(block));
    }

    public static Info getModInfo(ItemStack stack) {
        return getModInfo(itemStackSupplier.apply(stack));
    }

    public static Info getModInfo(Item item) {
        return getModInfo(Registry.ITEM.getId(item));
    }

    public static Info getModInfo(Entity entity) {
        Identifier id = Registry.ENTITY_TYPE.getId(entity.getType());
        return getModInfo(id == null ? new Identifier("nope") : id);
    }

    public static class Info {

        private final String id;
        private final String name;

        public Info(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

}
