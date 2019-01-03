package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerEntities implements IEntityComponentProvider {

    static final IEntityComponentProvider INSTANCE = new HUDHandlerEntities();

    public static float maxHealthForRender = 40.0F;

    @Override
    public void appendHead(List<TextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggedList<TextComponent, Identifier>) tooltip).add(new StringTextComponent(String.format(Waila.config.getFormatting().getEntityName(), accessor.getEntity().getDisplayName())), HUDHandlerBlocks.OBJECT_NAME_TAG);
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggedList<TextComponent, Identifier>) tooltip).add(new StringTextComponent(Registry.ENTITY_TYPE.getId(accessor.getEntity().getType()).toString()).setStyle(new Style().setColor(TextFormat.GRAY)), HUDHandlerBlocks.REGISTRY_NAME_TAG);
    }

    @Override
    public void appendBody(List<TextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_ENTITY_HEALTH) && accessor.getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) accessor.getEntity();
            float health = living.getHealth() / 2.0F;
            float maxHealth = living.getHealthMaximum() / 2.0F;

            if (living.getHealthMaximum() > maxHealthForRender)
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
    public void appendTail(List<TextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        tooltip.add(new StringTextComponent(String.format(Waila.config.getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}