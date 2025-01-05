package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeaconDataProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;

public enum BeaconProvider implements IBlockComponentProvider {

    INSTANCE;

    private MutableComponent getText(Holder<MobEffect> effect) {
        return effect.value().getDisplayName().copy();
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.EFFECT_BEACON)) return;

        var data = accessor.getData().get(BeaconDataProvider.DATA);
        if (data == null || data.primary() == null) return;

        var text = getText(data.primary());
        if (data.primary() == data.secondary()) text.append(" II");

        if (data.secondary() != null && data.primary() != data.secondary()) {
            text.append(CommonComponents.NEW_LINE).append(getText(data.secondary()));
        }

        tooltip.setLine(Options.EFFECT_BEACON, text);
    }

}
