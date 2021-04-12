package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

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
    default void appendHead(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendHead(tooltip, ((IDataAccessor) accessor), config);
    }

    @Override
    default void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendBody(tooltip, ((IDataAccessor) accessor), config);
    }

    @Override
    default void appendTail(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendTail(tooltip, ((IDataAccessor) accessor), config);
    }

    @Deprecated
    default ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    default void appendHead(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
    }

    @Deprecated
    default void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
    }

    @Deprecated
    default void appendTail(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
    }

}
