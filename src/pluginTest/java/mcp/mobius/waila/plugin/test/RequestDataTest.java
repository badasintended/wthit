package mcp.mobius.waila.plugin.test;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;

public enum RequestDataTest implements IBlockComponentProvider, IDataProvider<BarrelBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:data.ctx");
    public static final ResourceLocation RAW = ResourceLocation.parse("test:data.ctx.raw");
    public static final ResourceLocation TYPED = ResourceLocation.parse("test:data.ctx.typed");

    public static final IData.Type<Ctx> CTX = IData.createType(ResourceLocation.parse("test:data.ctx.ctx"));
    public static final StreamCodec<ByteBuf, Ctx> CTX_CODEC = ByteBufCodecs.STRING_UTF8.map(Ctx::new, Ctx::msg);

    public static final IData.Type<Data> DATA = IData.createType(ResourceLocation.parse("test:data.ctx.data"));
    public static final StreamCodec<ByteBuf, Data> DATA_CODEC = ByteBufCodecs.STRING_UTF8.map(Data::new, Data::msg);

    @Override
    public void appendDataContext(IDataWriter ctx, IBlockAccessor accessor, IPluginConfig config) {
        Preconditions.checkState(accessor.getData().raw().isEmpty());
        Preconditions.checkState(accessor.getData().get(DATA) == null);

        if (!config.getBoolean(ENABLED)) return;

        if (config.getBoolean(RAW)) ctx.raw().putString("kyk", "NIHAHAHAHA");
        if (config.getBoolean(TYPED)) ctx.addImmediate(new Ctx("gomen yuuka"));
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BarrelBlockEntity> accessor, IPluginConfig config) {
        var raw = accessor.getContext().raw();
        if (raw.contains("kyk")) {
            data.raw().putString("kyk", raw.getString("kyk").orElseThrow());
        }

        data.add(DATA, res -> {
            var ctx = accessor.getContext().get(CTX);
            if (ctx == null) return;

            res.add(new Data(ctx.msg));
        });
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var raw = accessor.getData().raw();
        if (raw.contains("kyk")) {
            tooltip.addLine(Component.literal(raw.getString("kyk").orElseThrow()));
        }

        var data = accessor.getData().get(DATA);
        if (data != null) {
            tooltip.addLine(Component.literal(data.msg));
        }
    }

    public record Ctx(
        String msg
    ) implements IData {

        @Override
        public Type<? extends IData> type() {
            return CTX;
        }

    }

    public record Data(
        String msg
    ) implements IData {

        @Override
        public Type<? extends IData> type() {
            return DATA;
        }

    }


}
