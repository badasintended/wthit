package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

import java.util.List;

public class HUDHandlerDecorationEntity implements IEntityComponentProvider {

    @Override
    public void appendHead(List<TextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        tooltip.add(new StringTextComponent(String.format(Waila.config.getFormatting().getEntityName(), accessor.getEntity().getDisplayName().getFormattedText())));
    }
}
