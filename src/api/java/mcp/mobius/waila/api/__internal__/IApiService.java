package mcp.mobius.waila.api.__internal__;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public interface IApiService {

    IApiService INSTANCE = Internals.loadService(IApiService.class);

    IBlacklistConfig getBlacklistConfig();

    <T> IJsonConfig.Builder0<T> createConfigBuilder(Type type);

    IModInfo getModInfo(String namespace);

    IModInfo getModInfo(ItemStack stack);

    IPluginInfo getPluginInfo(ResourceLocation pluginId);

    Collection<IPluginInfo> getAllPluginInfoFromMod(String modId);

    Collection<IPluginInfo> getAllPluginInfo();

    IWailaConfig getConfig();

    int getPairComponentColonOffset();

    int getColonFontWidth();

    int getFontColor();

    <T extends ITheme> IThemeType.Builder<T> createThemeTypeBuilder(Class<T> clazz);

    String getDefaultEnergyUnit();

    Path getConfigDir();

    <T> IRegistryFilter.Builder<T> createRegistryFilterBuilder(ResourceKey<? extends Registry<T>> registryKey);

    <T> IInstanceRegistry<T> createInstanceRegistry(boolean reversed);

    List<ToolMaterial> getTiers();

    <D extends IData> IData.Type<D> createDataType(ResourceLocation id);

    boolean isDevEnv();

}
