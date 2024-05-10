package mcp.mobius.waila.plugin.vanilla.provider;

import java.util.List;
import java.util.Objects;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public enum MobEffectProvider implements IEntityComponentProvider, IDataProvider<LivingEntity> {

    INSTANCE;

    public static final ResourceLocation DATA = new ResourceLocation("mob_effects");

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.EFFECT_MOB)) return;

        var data = accessor.getData().get(Data.class);
        if (data == null) return;

        data.list.forEach(it -> {
            var text = Component.translatable(it.getDescriptionId());
            var amplifier = it.getAmplifier();

            if (amplifier > 0) {
                if (I18n.exists("potion.potency." + amplifier)) {
                    text = Component.translatable("potion.withAmplifier", text, Component.translatable("potion.potency." + amplifier));
                } else {
                    text.append(" " + (amplifier + 1));
                }
            }

            if (it.getEffect().getCategory() == MobEffectCategory.HARMFUL) text.withStyle(ChatFormatting.RED);

            tooltip.addLine(text);
        });
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<LivingEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.EFFECT_MOB)) data.add(Data.class, res -> res.add(new Data(accessor
            .getTarget()
            .getActiveEffects()
            .stream()
            .filter(it -> it.isVisible() || config.getBoolean(Options.EFFECT_HIDDEN_MOB))
            .toList())));
    }

    public record Data(
        List<MobEffectInstance> list
    ) implements IData {

        public Data(FriendlyByteBuf buf) {
            this(buf.readList(b -> {
                var effect = b.readById(BuiltInRegistries.MOB_EFFECT);
                var amplifier = b.readVarInt();
                return new MobEffectInstance(Objects.requireNonNull(effect), -1, amplifier);
            }));
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeCollection(list, (b, m) -> {
                b.writeId(BuiltInRegistries.MOB_EFFECT, m.getEffect());
                b.writeVarInt(m.getAmplifier());
            });
        }

    }

}
