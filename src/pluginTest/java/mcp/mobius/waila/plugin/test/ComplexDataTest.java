package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public enum ComplexDataTest implements IBlockComponentProvider, IDataProvider<ChestBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:data.complex");
    public static final ResourceLocation BLOCK = ResourceLocation.parse("test:data.complex.block");
    public static final ResourceLocation MULTIPLE_ADDITION = ResourceLocation.parse("test:data.complex.multiple_addition");

    public static final IData.Type<Data> DATA = IData.createType(ENABLED);
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, Data::value,
        Data::new);

    public record Data(String value) implements IData {

        @Override
        public Type<? extends IData> type() {
            return DATA;
        }

    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getData().get(DATA);
        if (data != null) tooltip.addLine(new PairComponent(Component.literal("data"), Component.literal(data.value)));
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<ChestBlockEntity> accessor, IPluginConfig config) {
        if (!config.getBoolean(ENABLED)) return;

        if (config.getBoolean(MULTIPLE_ADDITION)) data.add(DATA, result -> {
            result.add(new Data("one"));
            try {
                result.add(new Data("two"));
            } catch (IllegalStateException e) {
                // no-op
            }
        });

        if (config.getBoolean(BLOCK)) data.add(DATA, IDataWriter.Result::block);
        data.add(DATA, ctx -> ctx.add(new Data("moe moe kyun")));
    }

}
