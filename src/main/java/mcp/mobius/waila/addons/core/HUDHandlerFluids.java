package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Blocks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerFluids implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerFluids();

    @Override
    public ItemStack getStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() == Blocks.WATER)
            return new ItemStack(Items.WATER_BUCKET);
        else if (accessor.getBlock() == Blocks.LAVA)
            return new ItemStack(Items.LAVA_BUCKET);

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<TextComponent> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        tooltip.add(new StringTextComponent(String.format(Waila.config.getFormatting().getFluidName(), I18n.translate(accessor.getBlock().getTranslationKey()))));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            tooltip.add(new StringTextComponent(Registry.BLOCK.getId(accessor.getBlock()).toString()).setStyle(new Style().setColor(TextFormat.GRAY)));
    }
}
