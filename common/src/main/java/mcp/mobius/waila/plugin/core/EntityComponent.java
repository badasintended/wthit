package mcp.mobius.waila.plugin.core;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.ModIdentification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum EntityComponent implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public void appendHead(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        Entity entity = accessor.getEntity();
        ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new LiteralText(String.format(accessor.getEntityNameFormat(), entity.getDisplayName().getString())));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.REGISTRY_NAME_TAG, new LiteralText(String.format(accessor.getRegistryNameFormat(), Registry.ENTITY_TYPE.getId(entity.getType()))));
    }

    @Override
    public void appendBody(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(WailaCore.CONFIG_SHOW_ENTITY_HEALTH) && accessor.getEntity() instanceof LivingEntity living) {
            float health = living.getHealth();
            float maxHealth = living.getMaxHealth();

            if (living.getMaxHealth() > Waila.config.get().getGeneral().getMaxHealthForRender())
                tooltip.add(new TranslatableText("tooltip.waila.health", String.format("%.2f", health), String.format("%.2f", maxHealth)));
            else {
                NbtCompound healthData = new NbtCompound();
                healthData.putFloat("health", health / 2.0F);
                healthData.putFloat("max", maxHealth / 2.0F);
                tooltip.add(IDrawableText.of(WailaCore.RENDER_ENTITY_HEALTH, healthData));
            }
        }
    }

    @Override
    public void appendTail(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(WailaConstants.CONFIG_SHOW_MOD_NAME))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.MOD_NAME_TAG, new LiteralText(String.format(accessor.getModNameFormat(), ModIdentification.getModInfo(accessor.getEntity()).name())));
    }

}