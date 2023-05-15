package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.plugin.extra.config.Options;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class DataProvider<T extends IData> implements IBlockComponentProvider, IEntityComponentProvider, IDataProvider {

    private final ResourceLocation id;
    private final Class<T> type;
    private final IData.Serializer<T> serializer;
    private final ResourceLocation option;

    protected DataProvider(ResourceLocation id, Class<T> type, IData.Serializer<T> serializer, ResourceLocation option) {
        this.id = id;
        this.type = type;
        this.serializer = serializer;
        this.option = option;
    }

    public void register(IRegistrar registrar, int priority) {
        registrar.addMergedSyncedConfig(option, true, false);
        registrar.addDataType(id, type, serializer);
        registrar.addComponent((IBlockComponentProvider) this, TooltipPosition.BODY, Block.class, priority);
        registrar.addComponent((IEntityComponentProvider) this, TooltipPosition.BODY, Entity.class, priority);
        registrar.addBlockData(this, Block.class, 0);
        registrar.addEntityData(this, Entity.class, 0);
    }

    protected abstract void appendBody(ITooltip tooltip, T t, IPluginConfig config);

    private void appendBody(ITooltip tooltip, IDataReader reader, IPluginConfig config) {
        T data = reader.get(type);
        if (data == null) return;
        if (!config.getBoolean(Options.ENERGY)) return;

        appendBody(tooltip, data, config);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        appendBody(tooltip, accessor.getData(), config);
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        appendBody(tooltip, accessor.getData(), config);
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(option)) data.add(type, IDataWriter.Result::block);
    }

}
