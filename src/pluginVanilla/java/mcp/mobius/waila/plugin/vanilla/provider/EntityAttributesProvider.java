package mcp.mobius.waila.plugin.vanilla.provider;

import java.text.DecimalFormat;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.SpacingComponent;
import mcp.mobius.waila.api.component.TextureComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public enum EntityAttributesProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final DecimalFormat DECIMAL = new DecimalFormat("0.#");

    private int addSpacing(ITooltipLine line, int i) {
        if (i > 0) {
            line.with(new SpacingComponent(3, 0));
        }
        return i - 1;
    }

    @Override
    @SuppressWarnings("UnusedAssignment")
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        boolean showHealth = config.getBoolean(Options.ATTRIBUTE_HEALTH);
        boolean showArmor = config.getBoolean(Options.ATTRIBUTE_ARMOR);

        if (showHealth || showArmor) {
            LivingEntity entity = accessor.getEntity();
            ITooltipLine line = tooltip.addLine();
            int i = 0;

            if (showHealth) {
                line.with(new TextureComponent(GuiComponent.GUI_ICONS_LOCATION, 8,8, 52, 0, 9, 9, 256, 256))
                    .with(Component.literal(DECIMAL.format(entity.getHealth()) + "/" + DECIMAL.format(entity.getMaxHealth())).withStyle(ChatFormatting.RED));
                i++;
            }

            if (showArmor && entity.getArmorValue() > 0) {
                i = addSpacing(line, i);
                line.with(new TextureComponent(GuiComponent.GUI_ICONS_LOCATION, 8, 8, 34, 9, 9, 9, 256, 256))
                    .with(Component.literal(String.valueOf(entity.getArmorValue())));
                i++;
            }
        }
    }

}
