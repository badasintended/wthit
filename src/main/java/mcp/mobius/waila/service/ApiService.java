package mcp.mobius.waila.service;

import java.util.Collection;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.hud.TooltipHandler;
import mcp.mobius.waila.hud.component.DrawableComponent;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.util.DisplayUtil;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class ApiService implements IApiService {

    @Override
    public IBlacklistConfig getBlacklistConfig() {
        return Waila.BLACKLIST_CONFIG.get();
    }

    @Override
    public IDrawableText createDrawableText() {
        return new DrawableComponent();
    }

    @Override
    public <T> IJsonConfig.Builder0<T> createConfigBuilder(Class<T> clazz) {
        return new JsonConfig.Builder<>(clazz);
    }

    @Override
    public IModInfo getModInfo(String namespace) {
        return ModInfo.get(namespace);
    }

    @Override
    public IPluginInfo getPluginInfo(ResourceLocation pluginId) {
        return PluginInfo.get(pluginId);
    }

    @Override
    public Collection<IPluginInfo> getAllPluginInfoFromMod(String modId) {
        return PluginInfo.getAllFromMod(modId);
    }

    @Override
    public Collection<IPluginInfo> getAllPluginInfo() {
        return PluginInfo.getAll();
    }

    @Override
    public IWailaConfig getConfig() {
        return Waila.CONFIG.get();
    }

    @Override
    public void renderItem(int x, int y, ItemStack stack) {
        DisplayUtil.renderStack(x, y, stack);
    }

    @Override
    public int getPairComponentColonOffset() {
        return TooltipHandler.colonOffset;
    }

    @Override
    public int getColonFontWidth() {
        return TooltipHandler.colonWidth;
    }

}
