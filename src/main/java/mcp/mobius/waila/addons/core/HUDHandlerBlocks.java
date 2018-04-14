package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerBlocks implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerBlocks();

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return currenttip;

        String name = null;

        String displayName = DisplayUtil.itemDisplayNameShort(itemStack);
        if (displayName != null && !displayName.endsWith("Unnamed"))
            name = displayName;
        if (name != null)
            currenttip.add(name);

        if (itemStack.getItem() == Items.REDSTONE) {
            int md = accessor.getMetadata();
            String redstoneMeta = "" + md;
            if (redstoneMeta.length() < 2)
                redstoneMeta = " " + redstoneMeta;
            currenttip.set(currenttip.size() - 1, name + " " + redstoneMeta);
        }
        if (currenttip.size() == 0)
            currenttip.add("\u00a7r" + String.format(FormattingConfig.blockFormat, "< Unnamed >"));
        else if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true) && !Strings.isNullOrEmpty(FormattingConfig.metaFormat))
            currenttip.add("\u00a7r" + String.format(FormattingConfig.metaFormat, accessor.getBlock().getRegistryName().toString(), accessor.getMetadata()));

        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (config.getConfig("general.showstates")) {
            IBlockState actualState = accessor.getBlockState().getBlock().getActualState(accessor.getBlockState(), accessor.getWorld(), accessor.getPosition());
            BlockStateContainer container = accessor.getBlock().getBlockState();
            for (IProperty<?> property : container.getProperties()) {
                Comparable<?> value = actualState.getValue(property);
                tooltip.add(property.getName() + ": " + (property instanceof PropertyBool ? value == Boolean.TRUE ? TextFormatting.GREEN : TextFormatting.RED : "") + value.toString());
            }
        }

        return tooltip;
    }

    @Nonnull
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return currenttip;
        String modName = ModIdentification.nameFromStack(itemStack);
        if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat))
            currenttip.add(String.format(FormattingConfig.modNameFormat, modName));

        return currenttip;
    }
}
