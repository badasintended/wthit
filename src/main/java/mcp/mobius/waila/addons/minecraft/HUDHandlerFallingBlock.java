package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.ChatFormat;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerFallingBlock implements IEntityComponentProvider {

    static final HUDHandlerFallingBlock INSTANCE = new HUDHandlerFallingBlock();

    static final Identifier OBJECT_NAME_TAG = new Identifier(Waila.MODID, "object_name");
    static final Identifier REGISTRY_NAME_TAG = new Identifier(Waila.MODID, "registry_name");
    static final Identifier MOD_NAME_TAG = new Identifier(Waila.MODID, "mod_name");
    static final Identifier CONFIG_SHOW_REGISTRY = new Identifier(Waila.MODID, "show_registry");

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        return new ItemStack(entity.getBlockState().getBlock().asItem());
    }

    @Override
    public void appendHead(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        ((ITaggableList<Identifier, Component>) tooltip).setTag(OBJECT_NAME_TAG, new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getEntityName(), entity.getBlockState().getBlock().getTextComponent().getFormattedText())));
        if (config.get(CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Component>) tooltip).setTag(REGISTRY_NAME_TAG, new TextComponent(Registry.ENTITY_TYPE.getId(accessor.getEntity().getType()).toString()).setStyle(new Style().setColor(ChatFormat.GRAY)));
    }

    @Override
    public void appendTail(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Component>) tooltip).setTag(MOD_NAME_TAG, new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}
