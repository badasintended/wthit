package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerBlocks implements IComponentProvider {

    static final IComponentProvider INSTANCE = new HUDHandlerBlocks();
    static final Identifier OBJECT_NAME_TAG = new Identifier(Waila.MODID, "object_name");
    static final Identifier REGISTRY_NAME_TAG = new Identifier(Waila.MODID, "registry_name");
    static final Identifier MOD_NAME_TAG = new Identifier(Waila.MODID, "mod_name");

    @Override
    public void appendHead(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), accessor.getBlock().getName().getString())));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(REGISTRY_NAME_TAG, new LiteralText(Registry.BLOCK.getId(accessor.getBlock()).toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

    @Override
    public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_STATES)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.get(p);
                Text valueText = new LiteralText(value.toString()).setStyle(Style.EMPTY.withColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? Formatting.GREEN : Formatting.RED : Formatting.RESET));
                tooltip.add(new LiteralText(p.getName() + ":").append(valueText));
            });
        }
    }

    @Override
    public void appendTail(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        String modName = ModIdentification.getModInfo(accessor.getStack().getItem()).getName();
        if (!Strings.isNullOrEmpty(modName)) {
            modName = String.format(Waila.CONFIG.get().getFormatting().getModName(), modName);
            ((ITaggableList<Identifier, Text>) tooltip).setTag(MOD_NAME_TAG, new LiteralText(modName));
        }
    }
}
