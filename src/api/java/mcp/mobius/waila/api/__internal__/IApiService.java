package mcp.mobius.waila.api.__internal__;

import java.util.Collection;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPickerAccessor;
import mcp.mobius.waila.api.IPickerResults;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IApiService {

    IApiService INSTANCE = Internals.loadService(IApiService.class);

    IBlacklistConfig getBlacklistConfig();

    <T> IJsonConfig.Builder0<T> createConfigBuilder(Class<T> clazz);

    IModInfo getModInfo(String namespace);

    IModInfo getModInfo(ItemStack stack);

    IPluginInfo getPluginInfo(ResourceLocation pluginId);

    Collection<IPluginInfo> getAllPluginInfoFromMod(String modId);

    Collection<IPluginInfo> getAllPluginInfo();

    IWailaConfig getConfig();

    void renderItem(int x, int y, ItemStack stack);

    void renderComponent(PoseStack matrices, ITooltipComponent component, int x, int y, float delta);

    int getPairComponentColonOffset();

    int getColonFontWidth();

    int getFontColor();

    @Deprecated
    IPickerAccessor getPickerAccessor();

    @Deprecated
    IPickerResults getPickerResults();

}
