package mcp.mobius.waila.gui.hud;

import java.util.Objects;
import java.util.function.Function;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.ClientAccessor;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.EmptyComponent;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.play.c2s.BlockDataRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.EntityDataRequestPlayC2SPacket;
import mcp.mobius.waila.registry.PluginAware;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class ComponentHandler {

    public static @Nullable Function<PluginAware<?>, Line.Wrapper> wrapperFactory;

    public static void requestBlockData(ClientAccessor accessor) {
        var registrar = Registrar.get();
        var block = accessor.getBlock();
        var blockEntity = accessor.getBlockEntity();

        var rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (blockEntity == null || !accessor.isTimeElapsed(rate) || !Waila.CONFIG.get().getGeneral().isDisplayTooltip()) return;
        if (registrar.blockData.get(block).isEmpty() && registrar.blockData.get(blockEntity).isEmpty()) return;

        accessor.resetTimer();
        accessor.setDataAccess(false);
        DataWriter.CLIENT.reset();
        var player = accessor.getPlayer();

        for (var entry : registrar.blockDataCtx.get(block)) {
            DataWriter.CLIENT.tryAppend(player, entry.instance(), accessor, PluginConfig.CLIENT, IBlockComponentProvider::appendDataContext);
        }

        for (var entry : registrar.blockDataCtx.get(blockEntity)) {
            DataWriter.CLIENT.tryAppend(player, entry.instance(), accessor, PluginConfig.CLIENT, IBlockComponentProvider::appendDataContext);
        }

        DataWriter.CLIENT.send(PacketSender.c2s(), player);
        PacketSender.c2s().send(new BlockDataRequestPlayC2SPacket.Payload(accessor.getBlockHitResult()));
        accessor.setDataAccess(true);
    }

    public static void gatherBlock(ClientAccessor accessor, Tooltip tooltip, TooltipPosition position) {
        var block = accessor.getBlock();
        var blockEntity = accessor.getBlockEntity();

        handleBlock(accessor, tooltip, block, position);
        if (blockEntity != null) handleBlock(accessor, tooltip, blockEntity, position);
    }

    @SuppressWarnings("DuplicatedCode")
    private static void handleBlock(ClientAccessor accessor, Tooltip tooltip, Object obj, TooltipPosition position) {
        var registrar = Registrar.get();
        var providers = registrar.blockComponent.get(position).get(obj);
        for (var entry : providers) {
            var pa = entry.instance();
            var provider = pa.instance();
            if (wrapperFactory != null) tooltip.wrapper = wrapperFactory.apply(pa);
            try {
                switch (position) {
                    case HEAD -> provider.appendHead(tooltip, accessor, PluginConfig.CLIENT);
                    case BODY -> provider.appendBody(tooltip, accessor, PluginConfig.CLIENT);
                    case TAIL -> provider.appendTail(tooltip, accessor, PluginConfig.CLIENT);
                }
            } catch (Throwable e) {
                ExceptionUtil.dump(e, provider.getClass().toString(), tooltip);
            }
            if (wrapperFactory != null) tooltip.wrapper = null;
        }
    }

    public static void requestEntityData(Entity entity, ClientAccessor accessor) {
        var registrar = Registrar.get();
        var trueEntity = accessor.getEntity();

        var rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (trueEntity == null || !accessor.isTimeElapsed(rate)) return;
        if (registrar.entityData.get(trueEntity).isEmpty()) return;

        accessor.resetTimer();
        accessor.setDataAccess(false);
        DataWriter.CLIENT.reset();
        var player = accessor.getPlayer();

        for (var entry : registrar.entityDataCtx.get(entity)) {
            DataWriter.CLIENT.tryAppend(player, entry.instance(), accessor, PluginConfig.CLIENT, IEntityComponentProvider::appendDataContext);
        }

        DataWriter.CLIENT.send(PacketSender.c2s(), player);
        PacketSender.c2s().send(new EntityDataRequestPlayC2SPacket.Payload(entity.getId(), accessor.getEntityHitResult().getLocation()));
        accessor.setDataAccess(true);
    }

    @SuppressWarnings("DuplicatedCode")
    public static void gatherEntity(Entity entity, ClientAccessor accessor, Tooltip tooltip, TooltipPosition position) {
        var registrar = Registrar.get();

        var providers = registrar.entityComponent.get(position).get(entity);
        for (var entry : providers) {
            var pa = entry.instance();
            var provider = pa.instance();
            if (wrapperFactory != null) tooltip.wrapper = wrapperFactory.apply(pa);
            try {
                switch (position) {
                    case HEAD -> provider.appendHead(tooltip, accessor, PluginConfig.CLIENT);
                    case BODY -> provider.appendBody(tooltip, accessor, PluginConfig.CLIENT);
                    case TAIL -> provider.appendTail(tooltip, accessor, PluginConfig.CLIENT);
                }
            } catch (Throwable e) {
                ExceptionUtil.dump(e, provider.getClass().toString(), tooltip);
            }
            if (wrapperFactory != null) tooltip.wrapper = null;
        }
    }

    public static ITooltipComponent getIcon(HitResult target) {
        var registrar = Registrar.get();
        var data = ClientAccessor.INSTANCE;
        var config = PluginConfig.CLIENT;

        if (target.getType() == HitResult.Type.ENTITY) {
            var providers = registrar.entityIcon.get(data.getEntity());
            for (var provider : providers) {
                var pa = provider.instance();
                var icon = pa.instance().getIcon(data, config);
                if (icon != null) {
                    if (wrapperFactory != null) icon = wrapperFactory.apply(pa).wrap(null, icon);
                    return icon;
                }
            }
        } else {
            var state = data.getBlockState();
            if (state.isAir()) return EmptyComponent.INSTANCE;

            ITooltipComponent result = null;
            var priority = 0;

            for (var provider : registrar.blockIcon.get(state.getBlock())) {
                var pa = provider.instance();
                var icon = pa.instance().getIcon(ClientAccessor.INSTANCE, PluginConfig.CLIENT);
                if (icon != null) {
                    if (wrapperFactory != null) icon = wrapperFactory.apply(pa).wrap(null, icon);
                    result = icon;
                    priority = provider.priority();
                    break;
                }
            }

            var blockEntity = data.getBlockEntity();
            if (blockEntity != null) {
                for (var provider : registrar.blockIcon.get(blockEntity)) {
                    if (provider.priority() >= priority) break;

                    var pa = provider.instance();
                    var icon = pa.instance().getIcon(ClientAccessor.INSTANCE, PluginConfig.CLIENT);
                    if (icon != null) {
                        if (wrapperFactory != null) icon = wrapperFactory.apply(pa).wrap(null, icon);
                        result = icon;
                        break;
                    }
                }
            }

            if (result != null) return result;
        }

        return EmptyComponent.INSTANCE;
    }

    public static @Nullable Entity getOverrideEntity(HitResult target) {
        if (target == null || target.getType() != HitResult.Type.ENTITY) {
            return null;
        }

        var registrar = Registrar.get();
        var entity = ((EntityHitResult) target).getEntity();

        var overrideProviders = registrar.entityOverride.get(entity);
        for (var provider : overrideProviders) {
            var override = provider.instance().getOverride(ClientAccessor.INSTANCE, PluginConfig.CLIENT);
            if (override != null) {
                return override;
            }
        }

        return entity;
    }

    public static BlockState getOverrideBlock(HitResult target) {
        var registrar = Registrar.get();

        var world = Objects.requireNonNull(Minecraft.getInstance().level);
        var pos = ((BlockHitResult) target).getBlockPos();
        final var state = world.getBlockState(pos);

        BlockState override = null;
        var priority = 0;

        var providers = registrar.blockOverride.get(state.getBlock());
        for (var provider : providers) {
            var blockOverride = provider.instance().getOverride(ClientAccessor.INSTANCE, PluginConfig.CLIENT);
            if (blockOverride != null) {
                override = blockOverride;
                priority = provider.priority();
                break;
            }
        }

        var blockEntity = world.getBlockEntity(pos);
        providers = registrar.blockOverride.get(blockEntity);
        for (var provider : providers) {
            if (provider.priority() >= priority) break;

            var beOverride = provider.instance().getOverride(ClientAccessor.INSTANCE, PluginConfig.CLIENT);
            if (beOverride != null) {
                override = beOverride;
                break;
            }
        }

        return override != null ? override : state;
    }

}
