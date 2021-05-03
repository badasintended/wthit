package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public enum ItemEntityComponent implements IEntityComponentProvider {

    INSTANCE;

    static final Identifier OBJECT_NAME_TAG = Waila.id("object_name");

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        return ((ItemEntity) accessor.getEntity()).getStack();
    }

    @Override
    public void appendHead(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        String name = ((ItemEntity) accessor.getEntity()).getStack().getName().getString();
        ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new LiteralText(String.format(
            Waila.config.get().getFormatting().getEntityName(),
            name)));
    }

}
