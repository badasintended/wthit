package mcp.mobius.waila.service;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataType;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IInstanceRegistry;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IRegistryFilter;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.ThemeType;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.registry.InstanceRegistry;
import mcp.mobius.waila.registry.RegistryFilter;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;

public abstract class ApiService implements IApiService {

    private static final Log LOG = Log.create();

    @Override
    public IModInfo getModInfo(ItemStack stack) {
        var item = stack.getItem();

        if (ResourceLocation.DEFAULT_NAMESPACE.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) {
            var enchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

            if (enchantments.size() == 1) {
                for (var entry : enchantments.entrySet()) {
                    var key = entry.getKey().unwrapKey().orElse(null);
                    if (key != null) return IModInfo.get(key.location());
                    break;
                }
            } else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
                var potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().orElse(null);
                if (potion != null) {
                    var id = BuiltInRegistries.POTION.getKey(potion.value());
                    if (id != null) return IModInfo.get(id);
                }
            } else if (item instanceof SpawnEggItem) {
                var customData = stack.get(DataComponents.ENTITY_DATA);
                if (customData != null) {
                    return IModInfo.get(customData.type());
                }
            }
        }

        return IModInfo.get(item);
    }

    @Override
    public IBlacklistConfig getBlacklistConfig() {
        return Waila.BLACKLIST_CONFIG.get().getView();
    }

    @Override
    public <T> IJsonConfig.Builder0<T> createConfigBuilder(Type type) {
        return new JsonConfig.Builder<>(type);
    }

    @Override
    public IModInfo getModInfo(String namespace) {
        return ModInfo.get(namespace);
    }

    @Override
    public IPluginInfo getPluginInfo(ResourceLocation pluginId) {
        return PluginInfo.get(pluginId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Collection<IPluginInfo> getAllPluginInfoFromMod(String modId) {
        return (Collection) PluginInfo.getAllFromMod(modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Collection<IPluginInfo> getAllPluginInfo() {
        return (Collection) PluginInfo.getAll();
    }

    @Override
    public IWailaConfig getConfig() {
        return Waila.CONFIG.get();
    }

    @Override
    public int getPairComponentColonOffset() {
        return TooltipRenderer.colonOffset;
    }

    @Override
    public int getColonFontWidth() {
        return TooltipRenderer.colonWidth;
    }

    @Override
    public int getFontColor() {
        return TooltipRenderer.state.getTheme().getDefaultTextColor() | 0xFF << 24;
    }

    @Override
    public <T extends ITheme> IThemeType.Builder<T> createThemeTypeBuilder(Class<T> clazz) {
        return new ThemeType<>(clazz);
    }

    @Override
    public String getDefaultEnergyUnit() {
        return "E";
    }

    @Override
    public Path getConfigDir() {
        return Waila.CONFIG_DIR;
    }

    @Override
    public <T> IRegistryFilter.Builder<T> createRegistryFilterBuilder(ResourceKey<? extends Registry<T>> registryKey) {
        return new RegistryFilter.Builder<>(registryKey);
    }

    @Override
    public <T> IInstanceRegistry<T> createInstanceRegistry(boolean reversed) {
        var registry = new InstanceRegistry<T>();
        if (reversed) registry.reversed();
        return registry;
    }

    @Override
    public List<ToolMaterial> getTiers() {
        return MixinService.TOOL_MATERIALS.stream()
            .filter(it -> {
                //noinspection ConstantValue
                if (it.incorrectBlocksForDrops() == null) {
                    LOG.warn("Found tier of class [{}] with null inverse tag, skipping", it.getClass().getName());
                    return false;
                }
                return true;
            }).sorted((tier1, tier2) -> {
                var tag1 = tier1.incorrectBlocksForDrops();
                var tag2 = tier2.incorrectBlocksForDrops();

                var opt1 = BuiltInRegistries.BLOCK.get(tag1);
                var opt2 = BuiltInRegistries.BLOCK.get(tag2);

                var blocks1 = opt1.isPresent() ? opt1.get() : HolderSet.<Block>empty();
                var blocks2 = opt2.isPresent() ? opt2.get() : HolderSet.<Block>empty();

                var size1 = blocks1.size();
                var size2 = blocks2.size();
                if (size1 == 0 && size2 == 0) return 0; // equals if both empty
                if (size1 == 0) return +1;              // 1 is greater than 2 if 1 empty and 2 filled
                if (size2 == 0) return -1;              // 2 is greater than 1 if 2 empty and 1 filled

                var b1inB2 = blocks1.stream().allMatch(blocks2::contains);
                var b2inB1 = blocks2.stream().allMatch(blocks1::contains);
                if (b1inB2 && b2inB1) return 0; // equals if both has the same entries
                if (b1inB2) return +1;          // 1 is greater than 2 if 2 contains all of 1
                if (b2inB1) return -1;          // 2 is greater than 1 if 1 contains all of 2

                var blocks1str = concatBlocks(blocks1);
                var blocks2str = concatBlocks(blocks2);

                LOG.error("""
                        Unsolvable tier comparison!
                        Either one of [{}] or [{}] does not contain all entries from the other one.
                        The comparison is based on the assumption that lower tier's incorrect block tag contains all entries from higher tier's tag.
                        This was fine for Vanilla, but might be not match modded behavior.
                        Please open an issue at {}
                        Tag [{}] contains:
                        \t[{}]
                        Tag [{}] contains:
                        \t[{}]
                        """,
                    tag1.location(), tag2.location(), Waila.ISSUE_URL, tag1.location(), blocks1str, tag2.location(), blocks2str);
                return 0;
            }).toList();
    }

    public static String concatBlocks(HolderSet<Block> set) {
       return String.join("\n\t", set.stream()
           .map(it -> it
               .unwrapKey()
               .orElseThrow()
               .location()
               .toString())
           .sorted()
           .toList());
    }

    @Override
    public <D extends IData> IData.Type<D> createDataType(ResourceLocation id) {
        return new DataType<>(id);
    }

    @Override
    public boolean isDevEnv() {
        return Waila.DEV;
    }

}
