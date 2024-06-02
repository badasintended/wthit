package mcp.mobius.waila.api.__internal__;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.vertex.BufferBuilder;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IInstanceRegistry;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IRegistryFilter;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

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

    void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, DeltaTracker delta);

    int getPairComponentColonOffset();

    int getColonFontWidth();

    int getFontColor();

    void fillGradient(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int start, int end);

    void renderRectBorder(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd);

    <T extends ITheme> IThemeType.Builder<T> createThemeTypeBuilder(Class<T> clazz);

    String getDefaultEnergyUnit();

    Path getConfigDir();

    <T> IRegistryFilter.Builder<T> createRegistryFilterBuilder(ResourceKey<? extends Registry<T>> registryKey);

    <T> IInstanceRegistry<T> createInstanceRegistry(boolean reversed);

    List<Tier> getTiers();

    <D extends IData> IData.Type<D> createDataType(ResourceLocation id);

    boolean isDevEnv();

}
