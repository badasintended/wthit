package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerBlocks implements IComponentProvider {

    static final IComponentProvider INSTANCE = new HUDHandlerBlocks();
    static final Identifier MOD_NAME_TAG = new Identifier(Waila.MODID, "mod_name");

    @Override
    public void appendHead(List<TextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        tooltip.add(new StringTextComponent(String.format(Waila.config.getFormatting().getEntityName(), I18n.translate(accessor.getStack().getTranslationKey()))));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            tooltip.add(new StringTextComponent(Registry.BLOCK.getId(accessor.getBlock()).toString()).setStyle(new Style().setColor(TextFormat.GRAY)));
    }

    @Override
    public void appendBody(List<TextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_STATES)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.get(p);
                TextComponent valueText = new StringTextComponent(value.toString()).setStyle(new Style().setColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? TextFormat.GREEN : TextFormat.RED : TextFormat.RESET));
                tooltip.add(new StringTextComponent(p.getName() + ":").append(valueText));
            });
        }
    }

    @Override
    public void appendTail(List<TextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        String modName = ModIdentification.getModInfo(accessor.getStack().getItem()).getName();
        if (!Strings.isNullOrEmpty(modName)) {
            modName = String.format(Waila.config.getFormatting().getModName(), modName);
            ((ITaggedList<TextComponent, Identifier>) tooltip).add(new StringTextComponent(modName), MOD_NAME_TAG);
        }
    }
}
