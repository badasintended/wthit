package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.ChatFormat;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerBlocks implements IComponentProvider {

    static final IComponentProvider INSTANCE = new HUDHandlerBlocks();
    static final Identifier OBJECT_NAME_TAG = new Identifier(Waila.MODID, "object_name");
    static final Identifier REGISTRY_NAME_TAG = new Identifier(Waila.MODID, "registry_name");
    static final Identifier MOD_NAME_TAG = new Identifier(Waila.MODID, "mod_name");

    @Override
    public void appendHead(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        ((ITaggableList<Identifier, Component>) tooltip).setTag(OBJECT_NAME_TAG, new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), I18n.translate(accessor.getStack().getTranslationKey()))));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Component>) tooltip).setTag(REGISTRY_NAME_TAG, new TextComponent(Registry.BLOCK.getId(accessor.getBlock()).toString()).setStyle(new Style().setColor(ChatFormat.GRAY)));
    }

    @Override
    public void appendBody(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_STATES)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.get(p);
                Component valueText = new TextComponent(value.toString()).setStyle(new Style().setColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? ChatFormat.GREEN : ChatFormat.RED : ChatFormat.RESET));
                tooltip.add(new TextComponent(p.getName() + ":").append(valueText));
            });
        }
    }

    @Override
    public void appendTail(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
        String modName = ModIdentification.getModInfo(accessor.getStack().getItem()).getName();
        if (!Strings.isNullOrEmpty(modName)) {
            modName = String.format(Waila.CONFIG.get().getFormatting().getModName(), modName);
            ((ITaggableList<Identifier, Component>) tooltip).setTag(MOD_NAME_TAG, new TextComponent(modName));
        }
    }
}
