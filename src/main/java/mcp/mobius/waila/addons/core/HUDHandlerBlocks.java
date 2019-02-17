package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class HUDHandlerBlocks implements IComponentProvider {

    static final IComponentProvider INSTANCE = new HUDHandlerBlocks();
    static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation(Waila.MODID, "object_name");
    static final ResourceLocation REGISTRY_NAME_TAG = new ResourceLocation(Waila.MODID, "registry_name");
    static final ResourceLocation MOD_NAME_TAG = new ResourceLocation(Waila.MODID, "mod_name");
    static final ResourceLocation STATE_VALUE_TAG = new ResourceLocation(Waila.MODID, "state_values");

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        ((ITaggedList<ITextComponent, ResourceLocation>) tooltip).add(new TextComponentString(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), I18n.format(accessor.getStack().getTranslationKey()))), OBJECT_NAME_TAG);
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggedList<ITextComponent, ResourceLocation>) tooltip).add(new TextComponentString(accessor.getBlock().getRegistryName().toString()).setStyle(new Style().setColor(TextFormatting.GRAY)), REGISTRY_NAME_TAG);
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_STATES)) {
            IBlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.get(p);
                ITextComponent valueText = new TextComponentString(value.toString()).setStyle(new Style().setColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? TextFormatting.GREEN : TextFormatting.RED : TextFormatting.RESET));
                ((ITaggedList<ITextComponent, ResourceLocation>) tooltip).add(new TextComponentString(p.getName() + ":").appendSibling(valueText), STATE_VALUE_TAG);
            });
        }
    }

    @Override
    public void appendTail(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        String modName = ModIdentification.getModInfo(accessor.getStack().getItem()).getName();
        if (!Strings.isNullOrEmpty(modName)) {
            modName = String.format(Waila.CONFIG.get().getFormatting().getModName(), modName);
            ((ITaggedList<ITextComponent, ResourceLocation>) tooltip).add(new TextComponentString(modName), MOD_NAME_TAG);
        }
    }
}
