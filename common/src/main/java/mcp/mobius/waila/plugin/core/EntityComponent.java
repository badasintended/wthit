package mcp.mobius.waila.plugin.core;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public enum EntityComponent implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public void appendHead(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        Entity entity = accessor.getEntity();
        ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new TextComponent(String.format(accessor.getEntityNameFormat(), entity.getDisplayName().getString())));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(String.format(accessor.getRegistryNameFormat(), Registry.ENTITY_TYPE.getKey(entity.getType()))));
    }

    @Override
    public void appendBody(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(WailaCore.CONFIG_SHOW_ENTITY_HEALTH) && accessor.getEntity() instanceof LivingEntity living) {
            float health = living.getHealth();
            float maxHealth = living.getMaxHealth();

            if (living.getMaxHealth() > Waila.config.get().getGeneral().getMaxHealthForRender())
                tooltip.add(new TranslatableComponent("tooltip.waila.health", String.format("%.2f", health), String.format("%.2f", maxHealth)));
            else {
                CompoundTag healthData = new CompoundTag();
                healthData.putFloat("health", health / 2.0F);
                healthData.putFloat("max", maxHealth / 2.0F);
                tooltip.add(IDrawableText.of(WailaCore.RENDER_ENTITY_HEALTH, healthData));
            }
        }
    }

    @Override
    public void appendTail(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(WailaConstants.CONFIG_SHOW_MOD_NAME))
            ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.MOD_NAME_TAG, new TextComponent(String.format(accessor.getModNameFormat(), ModInfo.get(accessor.getEntity()).name())));
    }

}
