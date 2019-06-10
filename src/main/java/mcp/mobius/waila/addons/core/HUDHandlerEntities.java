package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.ChatFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerEntities implements IEntityComponentProvider {

    static final IEntityComponentProvider INSTANCE = new HUDHandlerEntities();

    @Override
    public void appendHead(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Component>) tooltip).setTag(HUDHandlerBlocks.OBJECT_NAME_TAG, new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getEntityName(), accessor.getEntity().getDisplayName().getFormattedText())));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Component>) tooltip).setTag(HUDHandlerBlocks.REGISTRY_NAME_TAG, new TextComponent(Registry.ENTITY_TYPE.getId(accessor.getEntity().getType()).toString()).setStyle(new Style().setColor(ChatFormat.GRAY)));
    }

    @Override
    public void appendBody(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_ENTITY_HEALTH) && accessor.getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) accessor.getEntity();
            float health = living.getHealth() / 2.0F;
            float maxHealth = living.getHealthMaximum() / 2.0F;

            if (living.getHealthMaximum() > Waila.CONFIG.get().getGeneral().getMaxHealthForRender())
                tooltip.add(new TranslatableComponent("tooltip.waila.health", String.format("%.2f", health), String.format("%.2f", maxHealth)));
            else {
                CompoundTag healthData = new CompoundTag();
                healthData.putFloat("health", health);
                healthData.putFloat("max", maxHealth);
                tooltip.add(new RenderableTextComponent(PluginCore.RENDER_ENTITY_HEALTH, healthData));
            }
        }
    }

    @Override
    public void appendTail(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Component>) tooltip).setTag(HUDHandlerBlocks.MOD_NAME_TAG, new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}