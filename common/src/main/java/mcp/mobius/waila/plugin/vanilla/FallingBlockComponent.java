package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum FallingBlockComponent implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        return new ItemStack(entity.getBlockState().getBlock().asItem());
    }

    @Override
    public void appendHead(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new LiteralText(String.format(accessor.getEntityNameFormat(), entity.getBlockState().getBlock().getName().asString())));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.REGISTRY_NAME_TAG, new LiteralText(Registry.ENTITY_TYPE.getId(accessor.getEntity().getType()).toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

}
