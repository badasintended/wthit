package mcp.mobius.waila.api.__internal__;

import java.util.Collection;

import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.impl.Impl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IApiService {

    IApiService INSTANCE = Impl.loadService(IApiService.class);

    IBlacklistConfig getBlacklistConfig();

    <T> IJsonConfig.Builder0<T> createConfigBuilder(Class<T> clazz);

    IModInfo getModInfo(String namespace);

    IModInfo getModInfo(ItemStack stack);

    IPluginInfo getPluginInfo(ResourceLocation pluginId);

    Collection<IPluginInfo> getAllPluginInfoFromMod(String modId);

    Collection<IPluginInfo> getAllPluginInfo();

    IWailaConfig getConfig();

    void renderItem(int x, int y, ItemStack stack);

    int getPairComponentColonOffset();

    int getColonFontWidth();

}
