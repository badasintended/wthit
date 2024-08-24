package mcp.mobius.waila.plugin.extra.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
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
            .commenter(() -> ExtraBlacklistConfig.commenter(tagId))
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ExtraBlacklistConfig.class, new ExtraBlacklistConfig.Adapter(tagId))
                .create())
            .build();

        blacklistConfig.save();

        INSTANCES.put(type, this);
    }

    public void register(ICommonRegistrar registrar, int priority) {
        registrar.featureConfig(enabledBlockOption, false);
        registrar.featureConfig(enabledEntityOption, false);
        registerAdditions(registrar, priority);
        registrar.externalConfig(createConfigKey("blacklist"), blacklistConfig.getPath());

        registrar.dataType(type, codec);
        registrar.blockData(new BlockDataProvider(), BlockEntity.class, 0);
        registrar.entityData(new EntityDataProvider(), Entity.class, 0);
    }

    public void register(IClientRegistrar registrar, int priority) {
        registerAdditions(registrar, priority);

        registrar.body((IBlockComponentProvider) this, BlockEntity.class, priority);
        registrar.body((IEntityComponentProvider) this, Entity.class, priority);
    }

    protected final ResourceLocation createConfigKey(String path) {
        return ResourceLocation.fromNamespaceAndPath(WailaConstants.NAMESPACE + "x", type.id().getPath() + "." + path);
    }

    protected void registerAdditions(ICommonRegistrar registrar, int priority) {
    }

    protected void registerAdditions(IClientRegistrar registrar, int priority) {
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
