package mcp.mobius.waila.plugin.vanilla.provider;

import java.text.DecimalFormat;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.ArmorComponent;
import mcp.mobius.waila.api.component.HealthComponent;
import mcp.mobius.waila.api.component.SpacingComponent;
import mcp.mobius.waila.api.component.TextureComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public enum EntityAttributesProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {

    INSTANCE;

    private static final DecimalFormat DECIMAL = new DecimalFormat("0.##");

    private int addSpacing(ITooltipLine line, int i) {
        if (i > 0) {
            line.with(new SpacingComponent(3, 0));
            return i - 1;
        }
        return 0;
    }

    private void addHealth(ITooltipLine line, LivingEntity entity, CompoundTag data, boolean showAbsorption) {
        MutableComponent component = Component.literal(DECIMAL.format(entity.getHealth()));
        if (showAbsorption && data.contains("abs")) {
            component.append(Component.literal("+" + DECIMAL.format(data.getFloat("abs"))).withStyle(ChatFormatting.GOLD));
        }
        line.with(new TextureComponent(GuiComponent.GUI_ICONS_LOCATION, 8, 8, 52, 0, 9, 9, 256, 256))
            .with(component.append("/" + DECIMAL.format(entity.getMaxHealth())).withStyle(ChatFormatting.RED));
    }

    private void addArmor(ITooltipLine line, LivingEntity entity) {
        line.with(new TextureComponent(GuiComponent.GUI_ICONS_LOCATION, 8, 8, 34, 9, 9, 9, 256, 256))
            .with(Component.literal(String.valueOf(entity.getArmorValue())));
    }

    @Override
    @SuppressWarnings("UnusedAssignment")
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        boolean compact = config.getBoolean(Options.ATTRIBUTE_COMPACT);
        boolean showHealth = config.getBoolean(Options.ATTRIBUTE_HEALTH);
        boolean showAbsorption = config.getBoolean(Options.ATTRIBUTE_ABSORPTION);
        boolean showArmor = config.getBoolean(Options.ATTRIBUTE_ARMOR) && entity.getArmorValue() > 0;

        CompoundTag data = accessor.getServerData();

        if (compact) {
            ITooltipLine line = tooltip.addLine();
            int i = 0;

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
            int maxPerLine = config.getInt(Options.ATTRIBUTE_ICON_PER_LINE);

            if (showHealth) {
                float absorption = data.contains("abs") ? data.getFloat("abs") : 0f;
                if (entity.getMaxHealth() + absorption > config.getInt(Options.ATTRIBUTE_LONG_HEALTH_MAX)) {
                    addHealth(tooltip.addLine(), entity, data, showAbsorption);
                } else {
                    tooltip.addLine(new HealthComponent(entity.getHealth(), entity.getMaxHealth(), maxPerLine, false));
                    if (showAbsorption && absorption > 0) {
                        tooltip.addLine(new HealthComponent(absorption, 0, maxPerLine, true));
                    }
                }
            }

            if (showArmor) {
                if (entity.getArmorValue() > config.getInt(Options.ATTRIBUTE_LONG_ARMOR_MAX)) {
                    addArmor(tooltip.addLine(), entity);
                } else {
                    tooltip.addLine(new ArmorComponent(entity.getArmorValue(), maxPerLine));
                }
            }
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.ATTRIBUTE_ENTITY_POSITION)) {
            Vec3 pos = accessor.getEntity().position();
            tooltip.addLine(Component.literal("(" + DECIMAL.format(pos.x) + ", " + DECIMAL.format(pos.y) + ", " + DECIMAL.format(pos.z) + ")"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof LivingEntity living) {
            if (config.getBoolean(Options.ATTRIBUTE_ABSORPTION) && living.getAbsorptionAmount() > 0) {
                data.putFloat("abs", living.getAbsorptionAmount());
            }
        }
    }

}
