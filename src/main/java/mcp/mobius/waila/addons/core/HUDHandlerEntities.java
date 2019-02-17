package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.List;

public class HUDHandlerEntities implements IEntityComponentProvider {

    static final IEntityComponentProvider INSTANCE = new HUDHandlerEntities();

    @Override
    public void appendHead(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ((ITaggedList<ITextComponent, ResourceLocation>) tooltip).add(new TextComponentString(String.format(Waila.CONFIG.get().getFormatting().getEntityName(), accessor.getEntity().getDisplayName().getFormattedText())), HUDHandlerBlocks.OBJECT_NAME_TAG);
        if (config.get(PluginCore.CONFIG_SHOW_REGISTRY))
            ((ITaggedList<ITextComponent, ResourceLocation>) tooltip).add(new TextComponentString(accessor.getEntity().getType().getRegistryName().toString()).setStyle(new Style().setColor(TextFormatting.GRAY)), HUDHandlerBlocks.REGISTRY_NAME_TAG);
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(PluginCore.CONFIG_SHOW_ENTITY_HEALTH) && accessor.getEntity() instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) accessor.getEntity();
            float health = living.getHealth() / 2.0F;
            float maxHealth = living.getMaxHealth() / 2.0F;

            if (living.getMaxHealth() > Waila.CONFIG.get().getGeneral().getMaxHealthForRender())
                tooltip.add(new TextComponentTranslation("tooltip.waila.health", String.format("%.2f", health), String.format("%.2f", maxHealth)));
            else {
                NBTTagCompound healthData = new NBTTagCompound();
                healthData.putFloat("health", health);
                healthData.putFloat("max", maxHealth);
                tooltip.add(new RenderableTextComponent(PluginCore.RENDER_ENTITY_HEALTH, healthData));
            }
        }
    }

    @Override
    public void appendTail(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        tooltip.add(new TextComponentString(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}