package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * TODO: Remove
 *
 * @deprecated use {@link IBlockComponentProvider}
 */
@Deprecated
public interface IComponentProvider extends IBlockComponentProvider {

    @Override
    default ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        return getStack((IDataAccessor) accessor, config);
    }

    @Override
    default void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendHead((List<Component>) tooltip, ((IDataAccessor) accessor), config);
    }

    @Override
    default void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendBody((List<Component>) tooltip, ((IDataAccessor) accessor), config);
    }

    @Override
    default void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendTail((List<Component>) tooltip, ((IDataAccessor) accessor), config);
    }

    @Deprecated
    default ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    default void appendHead(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
    }

    @Deprecated
    default void appendBody(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
    }

    @Deprecated
    default void appendTail(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
    }

}
