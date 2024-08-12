package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeaconDataProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;

public enum BeaconProvider implements IBlockComponentProvider {

    INSTANCE;


    private MutableComponent getText(MobEffect effect) {
        return effect.getDisplayName().copy();
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.EFFECT_BEACON)) return;

        var data = accessor.getData().get(BeaconDataProvider.Data.class);
        if (data == null) return;

        if (data.primary() != null) {
            var text = getText(data.primary());
            if (data.primary() == data.secondary()) text.append(" II");
            tooltip.addLine(text);
        }

        if (data.secondary() != null && data.primary() != data.secondary()) {
            tooltip.addLine(getText(data.secondary()));
        }
    }

}
