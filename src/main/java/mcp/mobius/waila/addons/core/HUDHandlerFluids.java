package mcp.mobius.waila.addons.core;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
    public void appendHead(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Text>) tooltip).setTag(HUDHandlerBlocks.OBJECT_NAME_TAG, new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getFluidName(), accessor.getBlock().getName().getString())));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(HUDHandlerBlocks.REGISTRY_NAME_TAG, new LiteralText(Registry.BLOCK.getId(accessor.getBlock()).toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

}
