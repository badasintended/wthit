package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.MobEffectDataProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;

public enum MobEffectProvider implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.EFFECT_MOB)) return;

        var data = accessor.getData().get(MobEffectDataProvider.DATA);
        if (data == null) return;

        data.list().forEach(it -> {
            var text = Component.translatable(it.getDescriptionId());
            var amplifier = it.getAmplifier();

            if (amplifier > 0) {
                if (I18n.exists("potion.potency." + amplifier)) {
                    text = Component.translatable("potion.withAmplifier", text, Component.translatable("potion.potency." + amplifier));
                } else {
                    text.append(" " + (amplifier + 1));
                }
            }

            if (it.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) text.withStyle(ChatFormatting.RED);

            tooltip.setLine(Options.EFFECT_MOB, text);
        });
    }

}
