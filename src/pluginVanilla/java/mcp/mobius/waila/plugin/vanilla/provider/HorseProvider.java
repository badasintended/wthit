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
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public enum HorseProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static final Component JUMP_KEY = Component.translatable(Tl.Tooltip.Horse.Jump.KEY);
    private static final Component SPEED_KEY = Component.translatable(Tl.Tooltip.Horse.Speed.KEY);

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.HORSE_JUMP_HEIGHT)) {
            AbstractHorse horse = accessor.getEntity();

            // https://minecraft.wiki/w/Horse#Jump_strength
            // https://gist.github.com/Micalobia/f61c902d0c76d582865e8470b0fc4757
            // https://github.com/Snownee/Jade/pull/690
            var attr = (float) horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH);
            var bps = (-0.22656224f) + (1.61431730f * attr) + (4.53680079f * attr * attr);

            ChatFormatting format;
            if (bps < 2.0f)
                format = ChatFormatting.DARK_GRAY;
            else if (bps > 4.0f)
                format = ChatFormatting.GOLD;
            else
                format = ChatFormatting.RESET;

            tooltip.setLine(Options.HORSE_JUMP_HEIGHT, new PairComponent(JUMP_KEY,
                Component.translatable(Tl.Tooltip.Horse.Jump.VALUE, FORMAT.format(bps)).withStyle(format)));
        }

        if (config.getBoolean(Options.HORSE_SPEED)) {
            AbstractHorse horse = accessor.getEntity();

            // https://minecraft.wiki/w/Horse#Movement_speed
            // https://github.com/sakura-ryoko/minihud/pull/179
            var attr = (float) horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
            var bps = attr * 43.171815466666658f - 0.000000339999999f;

            ChatFormatting format;
            if (bps < 7.0f)
                format = ChatFormatting.DARK_GRAY;
            else if (bps > 11.0f)
                format = ChatFormatting.GOLD;
            else
                format = ChatFormatting.RESET;

            tooltip.setLine(Options.HORSE_SPEED, new PairComponent(SPEED_KEY,
                Component.translatable(Tl.Tooltip.Horse.Speed.VALUE, FORMAT.format(bps)).withStyle(format)));
        }
    }

}
