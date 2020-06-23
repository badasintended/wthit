package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class HUDHandlerEntities implements IEntityComponentProvider {

    static final IEntityComponentProvider INSTANCE = new HUDHandlerEntities();

    @Override
    public void appendHead(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Text>) tooltip).setTag(HUDHandlerBlocks.OBJECT_NAME_TAG, new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getEntityName(), accessor.getEntity().getDisplayName().getString())));
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(HUDHandlerBlocks.REGISTRY_NAME_TAG, new LiteralText(Registry.ENTITY_TYPE.getId(accessor.getEntity().getType()).toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    }

    @Override
    public void appendBody(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_ENTITY_HEALTH) && accessor.getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) accessor.getEntity();
            float health = living.getHealth() / 2.0F;
            float maxHealth = living.getMaxHealth() / 2.0F;

            if (living.getMaxHealth() > Waila.CONFIG.get().getGeneral().getMaxHealthForRender())
                tooltip.add(new TranslatableText("tooltip.waila.health", String.format("%.2f", health), String.format("%.2f", maxHealth)));
            else {
                CompoundTag healthData = new CompoundTag();
                healthData.putFloat("health", health);
                healthData.putFloat("max", maxHealth);
                tooltip.add(new RenderableTextComponent(PluginCore.RENDER_ENTITY_HEALTH, healthData));
            }
        }
    }

    @Override
    public void appendTail(List<Text> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggableList<Identifier, Text>) tooltip).setTag(HUDHandlerBlocks.MOD_NAME_TAG, new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}