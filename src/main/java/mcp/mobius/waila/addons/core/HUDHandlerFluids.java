package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITaggableList;
import net.minecraft.ChatFormat;
import net.minecraft.block.Blocks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerFluids implements IComponentProvider {

    static final IComponentProvider INSTANCE = new HUDHandlerFluids();

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WATER)
            return new ItemStack(Items.WATER_BUCKET);
        else if (accessor.getBlock() == Blocks.LAVA)
            return new ItemStack(Items.LAVA_BUCKET);

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Component>) tooltip).setTag(HUDHandlerBlocks.OBJECT_NAME_TAG, new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getFluidName(), I18n.translate(accessor.getBlock().getTranslationKey()))));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Component>) tooltip).setTag(HUDHandlerBlocks.REGISTRY_NAME_TAG, new TextComponent(Registry.BLOCK.getId(accessor.getBlock()).toString()).setStyle(new Style().setColor(ChatFormat.GRAY)));
    }
}
