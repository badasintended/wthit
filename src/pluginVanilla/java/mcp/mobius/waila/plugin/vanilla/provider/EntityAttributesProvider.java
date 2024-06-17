package mcp.mobius.waila.plugin.vanilla.provider;

import java.text.DecimalFormat;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.ArmorComponent;
import mcp.mobius.waila.api.component.HealthComponent;
import mcp.mobius.waila.api.component.PositionComponent;
import mcp.mobius.waila.api.component.SpacingComponent;
import mcp.mobius.waila.api.component.SpriteComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public enum EntityAttributesProvider implements IEntityComponentProvider, IDataProvider<Entity> {

    INSTANCE;

    private static final ResourceLocation SPRITE_HEART = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation SPRITE_ARMOR = ResourceLocation.withDefaultNamespace("hud/armor_full");

    private static final DecimalFormat DECIMAL = new DecimalFormat("0.##");

    private int addSpacing(ITooltipLine line, int i) {
        if (i > 0) {
            line.with(new SpacingComponent(3, 0));
            return i - 1;
        }
        return 0;
    }

    private void addHealth(ITooltipLine line, LivingEntity entity, CompoundTag data, boolean showAbsorption) {
        var component = Component.literal(DECIMAL.format(entity.getHealth()));
        if (showAbsorption && data.contains("abs")) {
            component.append(Component.literal("+" + DECIMAL.format(data.getFloat("abs"))).withStyle(ChatFormatting.GOLD));
        }
        line.with(new SpriteComponent(SPRITE_HEART, 9, 9))
            .with(component.append("/" + DECIMAL.format(entity.getMaxHealth())).withStyle(ChatFormatting.RED));
    }

    private void addArmor(ITooltipLine line, LivingEntity entity) {
        line.with(new SpriteComponent(SPRITE_ARMOR, 9, 9))
            .with(Component.literal(String.valueOf(entity.getArmorValue())));
    }

    @Override
    @SuppressWarnings("UnusedAssignment")
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        var compact = config.getBoolean(Options.ENTITY_COMPACT);
        var showHealth = config.getBoolean(Options.ENTITY_HEALTH);
        var showAbsorption = config.getBoolean(Options.ENTITY_ABSORPTION);
        var showArmor = config.getBoolean(Options.ENTITY_ARMOR) && entity.getArmorValue() > 0;

        var data = accessor.getData().raw();

        if (compact) {
            var line = tooltip.addLine();
            var i = 0;

            if (showHealth) {
                i = addSpacing(line, i);
                addHealth(line, entity, data, showAbsorption);
                i++;
            }

            if (showArmor) {
                i = addSpacing(line, i);
                addArmor(line, entity);
                i++;
            }
        } else {
            var maxPerLine = config.getInt(Options.ENTITY_ICON_PER_LINE);

            if (showHealth) {
                var absorption = data.contains("abs") ? data.getFloat("abs") : 0f;
                if (entity.getMaxHealth() + absorption > config.getInt(Options.ENTITY_LONG_HEALTH_MAX)) {
                    addHealth(tooltip.addLine(), entity, data, showAbsorption);
                } else {
                    tooltip.addLine(new HealthComponent(entity.getHealth(), entity.getMaxHealth(), maxPerLine, false));
                    if (showAbsorption && absorption > 0) {
                        tooltip.addLine(new HealthComponent(absorption, 0, maxPerLine, true));
                    }
                }
            }

            if (showArmor) {
                if (entity.getArmorValue() > config.getInt(Options.ENTITY_LONG_ARMOR_MAX)) {
                    addArmor(tooltip.addLine(), entity);
                } else {
                    tooltip.addLine(new ArmorComponent(entity.getArmorValue(), maxPerLine));
                }
            }
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.ENTITY_POSITION)) {
            tooltip.addLine(new PositionComponent(accessor.getEntity().position()));
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof LivingEntity living) {
            if (config.getBoolean(Options.ENTITY_ABSORPTION) && living.getAbsorptionAmount() > 0) {
                data.raw().putFloat("abs", living.getAbsorptionAmount());
            }
        }
    }

}
