package mcp.mobius.waila.plugin.extra.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.extra.config.ExtraBlacklistConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class DataProvider<A extends IData, I extends A> implements IBlockComponentProvider, IEntityComponentProvider {

    public static final Map<IData.Type<?>, DataProvider<?, ?>> INSTANCES = new HashMap<>();

    private final IData.Type<A> type;
    private final StreamCodec<RegistryFriendlyByteBuf, I> codec;

    protected final ResourceLocation enabledBlockOption;
    protected final ResourceLocation enabledEntityOption;
    protected final IJsonConfig<ExtraBlacklistConfig> blacklistConfig;

    protected DataProvider(IData.Type<A> type, StreamCodec<RegistryFriendlyByteBuf, I> codec) {
        this.type = type;
        this.codec = codec;

        enabledBlockOption = createConfigKey("enabled_block");
        enabledEntityOption = createConfigKey("enabled_entity");

        var tagId = ResourceLocation.fromNamespaceAndPath(WailaConstants.NAMESPACE, "extra/" + type.id().getPath() + "_blacklist");

        blacklistConfig = IJsonConfig.of(ExtraBlacklistConfig.class)
            .file(WailaConstants.NAMESPACE + "/extra/" + type.id().getPath() + "_blacklist")
            .json5()
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ExtraBlacklistConfig.class, new ExtraBlacklistConfig.Adapter(tagId))
                .create())
            .build();

        blacklistConfig.save();

        INSTANCES.put(type, this);
    }

    public void register(IRegistrar registrar, int priority) {
        registrar.addFeatureConfig(enabledBlockOption, false);
        registrar.addFeatureConfig(enabledEntityOption, false);
        registerAdditions(registrar, priority);
        registrar.addConfig(createConfigKey("blacklist"), blacklistConfig.getPath());

        registrar.addDataType(type, codec);
        registrar.addComponent((IBlockComponentProvider) this, TooltipPosition.BODY, BlockEntity.class, priority);
        registrar.addComponent((IEntityComponentProvider) this, TooltipPosition.BODY, Entity.class, priority);
        registrar.addBlockData(new BlockDataProvider(), BlockEntity.class, 0);
        registrar.addEntityData(new EntityDataProvider(), Entity.class, 0);
    }

    protected final ResourceLocation createConfigKey(String path) {
        return ResourceLocation.fromNamespaceAndPath(WailaConstants.NAMESPACE + "x", type.id().getPath() + "." + path);
    }

    protected void registerAdditions(IRegistrar registrar, int priority) {
    }

    protected abstract void appendBody(ITooltip tooltip, I i, IPluginConfig config, ResourceLocation objectId);

    @SuppressWarnings("unchecked")
    protected void appendBody(ITooltip tooltip, IDataReader reader, IPluginConfig config, ResourceLocation objectId) {
        var data = (I) reader.get(type);
        if (data == null) return;

        appendBody(tooltip, data, config, objectId);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(enabledBlockOption)) return;
        if (blacklistConfig.get().getView().blockFilter.matches(accessor.getBlock())) return;
        var blockEntityType = Objects.<BlockEntity>requireNonNull(accessor.getBlockEntity()).getType();
        if (blacklistConfig.get().getView().blockEntityFilter.matches(blockEntityType)) return;

        var blockEntityId = Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType));
        appendBody(tooltip, accessor.getData(), config, blockEntityId);
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(enabledEntityOption)) return;
        var entityType = accessor.getEntity().getType();
        if (blacklistConfig.get().getView().entityFilter.matches(entityType)) return;

        appendBody(tooltip, accessor.getData(), config, BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    private class BlockDataProvider implements IDataProvider<BlockEntity> {

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
            var target = accessor.getTarget();
            var state = target.getBlockState();
            var blockEntityType = target.getType();

            if (!config.getBoolean(enabledBlockOption)
                || blacklistConfig.get().getView().blockFilter.matches(state.getBlock())
                || blacklistConfig.get().getView().blockEntityFilter.matches(blockEntityType)) {
                data.blockAll(type);
            }
        }

    }

    private class EntityDataProvider implements IDataProvider<Entity> {

        @Override
        public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
            var entityType = accessor.getTarget().getType();

            if (!config.getBoolean(enabledEntityOption) || blacklistConfig.get().getView().entityFilter.matches(entityType)) {
                data.blockAll(type);
            }
        }

    }

}
