package mcp.mobius.waila.plugin.test;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;

public enum RequestDataTest implements IBlockComponentProvider, IDataProvider<BarrelBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENABLED = new ResourceLocation("test:data.ctx");
    public static final ResourceLocation RAW = new ResourceLocation("test:data.ctx.raw");
    public static final ResourceLocation TYPED = new ResourceLocation("test:data.ctx.typed");

    public static final ResourceLocation CTX = new ResourceLocation("test:data.ctx.ctx");
    public static final ResourceLocation DATA = new ResourceLocation("test:data.ctx.data");

    @Override
    public void appendDataContext(IDataWriter ctx, IBlockAccessor accessor, IPluginConfig config) {
        Preconditions.checkState(accessor.getData().raw().isEmpty());
        Preconditions.checkState(accessor.getData().get(Data.class) == null);

        if (!config.getBoolean(ENABLED)) return;

        if (config.getBoolean(RAW)) ctx.raw().putString("kyk", "NIHAHAHAHA");
        if (config.getBoolean(TYPED)) ctx.addImmediate(new Ctx("gomen yuuka"));
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BarrelBlockEntity> accessor, IPluginConfig config) {
        var raw = accessor.getContext().raw();
        if (raw.contains("kyk")) {
            data.raw().putString("kyk", raw.getString("kyk"));
        }

        data.add(Data.class, res -> {
            var ctx = accessor.getContext().get(Ctx.class);
            if (ctx == null) return;

            res.add(new Data(ctx.msg));
        });
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var raw = accessor.getData().raw();
        if (raw.contains("kyk")) {
            tooltip.addLine(Component.literal(raw.getString("kyk")));
        }

        var data = accessor.getData().get(Data.class);
        if (data != null) {
            tooltip.addLine(Component.literal(data.msg));
        }
    }

    public record Ctx(
        String msg
    ) implements IData {

        public Ctx(FriendlyByteBuf buf) {
            this(buf.readUtf());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(msg);
        }

    }

    public record Data(
        String msg
    ) implements IData {

        public Data(FriendlyByteBuf buf) {
            this(buf.readUtf());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(msg);
        }

    }


}
