package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
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

    static final Identifier OBJECT_NAME_TAG = Waila.id("object_name");
    static final Identifier REGISTRY_NAME_TAG = Waila.id("registry_name");
    static final Identifier CONFIG_SHOW_REGISTRY = Waila.id("show_registry");

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        return new ItemStack(entity.getBlockState().getBlock().asItem());
    }

    @Override
    public void appendHead(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new LiteralText(String.format(accessor.getEntityNameFormat(), entity.getBlockState().getBlock().getName().asString())));
        if (config.get(CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(REGISTRY_NAME_TAG, new LiteralText(Registry.ENTITY_TYPE.getId(accessor.getEntity().getType()).toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

}
