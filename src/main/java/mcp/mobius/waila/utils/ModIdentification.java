package mcp.mobius.waila.utils;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.metadata.MapBackedContactInformation;
import net.fabricmc.loader.util.version.StringVersion;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ModIdentification {

    private static final Map<String, ModMetadata> CONTAINER_CACHE = Maps.newHashMap();
    private static final ModMetadata MC_MOD_INFO = new DummyModMetadata("minecraft", "Minecraft");
    private static final ModMetadata COMMON_MOD_INFO = new DummyModMetadata("c", "Common");

    static {
        CONTAINER_CACHE.put(MC_MOD_INFO.getId(), MC_MOD_INFO);
        CONTAINER_CACHE.put(COMMON_MOD_INFO.getId(), COMMON_MOD_INFO);
    }

    public static ModMetadata getModInfo(String namespace) {
        return CONTAINER_CACHE.computeIfAbsent(namespace, s -> FabricLoader.getInstance().getAllMods().stream()
                .map(ModContainer::getMetadata)
                .filter(m -> m.getId().equals(s))
                .findFirst()
                .orElse(new DummyModMetadata(s, s)));
    }

    public static ModMetadata getModInfo(Identifier id) {
        return getModInfo(id.getNamespace());
    }

    public static ModMetadata getModInfo(Block block) {
        return getModInfo(Registry.BLOCK.getId(block));
    }

    public static ModMetadata getModInfo(Item item) {
        return getModInfo(Registry.ITEM.getId(item));
    }

    public static ModMetadata getModInfo(Entity entity) {
        Identifier id = Registry.ENTITY_TYPE.getId(entity.getType());
        return getModInfo(id == null ? new Identifier("nope") : id);
    }

    private static class DummyModMetadata implements ModMetadata {
        private final String id;
        private final String name;

        public DummyModMetadata(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getType() {
            return "";
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Collection<String> getProvides() {
            return Collections.emptySet();
        }

        @Override
        public Version getVersion() {
            return new StringVersion("1");
        }

        @Override
        public ModEnvironment getEnvironment() {
            return ModEnvironment.UNIVERSAL;
        }

        @Override
        public Collection<ModDependency> getDepends() {
            return Collections.emptySet();
        }

        @Override
        public Collection<ModDependency> getRecommends() {
            return Collections.emptySet();
        }

        @Override
        public Collection<ModDependency> getSuggests() {
            return Collections.emptySet();
        }

        @Override
        public Collection<ModDependency> getConflicts() {
            return Collections.emptySet();
        }

        @Override
        public Collection<ModDependency> getBreaks() {
            return Collections.emptySet();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return "Dummy mod metadata";
        }

        @Override
        public Collection<Person> getAuthors() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Person> getContributors() {
            return Collections.emptySet();
        }

        @Override
        public ContactInformation getContact() {
            return new MapBackedContactInformation(Collections.emptyMap());
        }

        @Override
        public Collection<String> getLicense() {
            return Collections.emptySet();
        }

        @Override
        public Optional<String> getIconPath(int size) {
            return Optional.empty();
        }

        @Override
        public boolean containsCustomValue(String key) {
            return false;
        }

        @Override
        public CustomValue getCustomValue(String key) {
            return null;
        }

        @Override
        public Map<String, CustomValue> getCustomValues() {
            return new HashMap<>();
        }

        @Override
        @Deprecated
        public boolean containsCustomElement(String key) {
            return false;
        }

        @Override
        @Deprecated
        public JsonElement getCustomElement(String key) {
            return new JsonPrimitive("");
        }
    }
}
