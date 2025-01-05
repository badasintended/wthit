package mcp.mobius.waila.plugin.vanilla.provider;

import java.text.DecimalFormat;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public enum HorseProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static final Component JUMP_KEY = Component.translatable(Tl.Tooltip.Horse.Jump.KEY);
    private static final Component SPEED_KEY = Component.translatable(Tl.Tooltip.Horse.Speed.KEY);

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.HORSE_JUMP_HEIGHT)) {
            AbstractHorse horse = accessor.getEntity();

            var jumpStrength = horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH);
            var jumpHeight = -0.1817584952f * Math.pow(jumpStrength, 3) + 3.689713992f * Math.pow(jumpStrength, 2) + 2.128599134f * jumpStrength - 0.343930367f;

            ChatFormatting format;
            if (jumpHeight < 2.0f)
                format = ChatFormatting.DARK_GRAY;
            else if (jumpHeight > 4.0f)
                format = ChatFormatting.GOLD;
            else
                format = ChatFormatting.RESET;

            tooltip.setLine(Options.HORSE_JUMP_HEIGHT, new PairComponent(JUMP_KEY,
                Component.translatable(Tl.Tooltip.Horse.Jump.VALUE, FORMAT.format(jumpHeight)).withStyle(format)));
        }

        if (config.getBoolean(Options.HORSE_SPEED)) {
            AbstractHorse horse = accessor.getEntity();
            var speed = horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 42.157787584f;

            ChatFormatting format;
            if (speed < 7.0f)
                format = ChatFormatting.DARK_GRAY;
            else if (speed > 11.0f)
                format = ChatFormatting.GOLD;
            else
                format = ChatFormatting.RESET;

            tooltip.setLine(Options.HORSE_SPEED, new PairComponent(SPEED_KEY,
                Component.translatable(Tl.Tooltip.Horse.Speed.VALUE, FORMAT.format(speed)).withStyle(format)));
        }
    }

}
