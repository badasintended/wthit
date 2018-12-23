package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;

import java.util.List;

public class HUDHandlerEntities implements IWailaEntityProvider {

    static final IWailaEntityProvider INSTANCE = new HUDHandlerEntities();

    public static float maxHealthForRender = 40.0F;

    @Override
    public void appendHead(List<TextComponent> tooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        tooltip.add(new StringTextComponent(String.format(Waila.config.getFormatting().getEntityName(), accessor.getEntity().getDisplayName().getFormattedText())));
    }

    @Override
    public void appendBody(List<TextComponent> tooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (config.get(PluginCore.CONFIG_SHOW_ENTITY_HEALTH)) {
            float health = accessor.getEntity().getHealth() / 2.0F;
            float maxHealth = accessor.getEntity().getHealthMaximum() / 2.0F;

            if (accessor.getEntity().getHealthMaximum() > maxHealthForRender)
                tooltip.add(new TranslatableTextComponent("hud.msg.health", health, maxHealth));
            else {
                CompoundTag healthData = new CompoundTag();
                healthData.putFloat("health", health);
                healthData.putFloat("max", maxHealth);
                tooltip.add(new RenderableTextComponent(PluginCore.RENDER_ENTITY_HEALTH, healthData));
            }
        }
    }

    @Override
    public void appendTail(List<TextComponent> tooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        tooltip.add(new StringTextComponent(String.format(Waila.config.getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}