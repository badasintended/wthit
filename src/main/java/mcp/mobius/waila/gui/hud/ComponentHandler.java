package mcp.mobius.waila.gui.hud;

import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.component.EmptyComponent;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ComponentHandler {

    public static void gatherBlock(DataAccessor accessor, Tooltip tooltip, TooltipPosition position) {
        var registrar = Registrar.INSTANCE;
        var block = accessor.getBlock();
        var blockEntity = accessor.getBlockEntity();

        var rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (blockEntity != null && accessor.isTimeElapsed(rate) && Waila.CONFIG.get().getGeneral().isDisplayTooltip()) {
            accessor.resetTimer();
            if (!(registrar.blockData.get(block).isEmpty() && registrar.blockData.get(blockEntity).isEmpty())) {
                var buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeBlockHitResult(accessor.getBlockHitResult());
                PacketSender.c2s().send(Packets.BLOCK, buf);
            }
        }

        handleBlock(accessor, tooltip, block, position);
        handleBlock(accessor, tooltip, blockEntity, position);
    }

    @SuppressWarnings("DuplicatedCode")
    private static void handleBlock(DataAccessor accessor, Tooltip tooltip, Object obj, TooltipPosition position) {
        var registrar = Registrar.INSTANCE;
        var providers = registrar.blockComponent.get(position).get(obj);
        for (var entry : providers) {
            var provider = entry.value();
            try {
                switch (position) {
                    case HEAD -> provider.appendHead(tooltip, accessor, PluginConfig.CLIENT);
                    case BODY -> provider.appendBody(tooltip, accessor, PluginConfig.CLIENT);
                    case TAIL -> provider.appendTail(tooltip, accessor, PluginConfig.CLIENT);
                }
            } catch (Throwable e) {
                ExceptionUtil.dump(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void gatherEntity(Entity entity, DataAccessor accessor, Tooltip tooltip, TooltipPosition position) {
        var registrar = Registrar.INSTANCE;
        var trueEntity = accessor.getEntity();

        var rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (trueEntity != null && accessor.isTimeElapsed(rate)) {
            accessor.resetTimer();

            if (!registrar.entityData.get(trueEntity).isEmpty()) {
                var buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeVarInt(entity.getId());
                var hitPos = accessor.getEntityHitResult().getLocation();
                buf.writeDouble(hitPos.x);
                buf.writeDouble(hitPos.y);
                buf.writeDouble(hitPos.z);
                PacketSender.c2s().send(Packets.ENTITY, buf);
            }
        }

        var providers = registrar.entityComponent.get(position).get(entity);
        for (var entry : providers) {
            var provider = entry.value();
            try {
                switch (position) {
                    case HEAD -> provider.appendHead(tooltip, accessor, PluginConfig.CLIENT);
                    case BODY -> provider.appendBody(tooltip, accessor, PluginConfig.CLIENT);
                    case TAIL -> provider.appendTail(tooltip, accessor, PluginConfig.CLIENT);
                }
            } catch (Throwable e) {
                ExceptionUtil.dump(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    public static ITooltipComponent getIcon(HitResult target) {
        var registrar = Registrar.INSTANCE;
        var data = DataAccessor.INSTANCE;
        var config = PluginConfig.CLIENT;

        if (target.getType() == HitResult.Type.ENTITY) {
            var providers = registrar.entityIcon.get(data.getEntity());
            for (var provider : providers) {
                var icon = provider.value().getIcon(data, config);
                if (icon != null) {
                    return icon;
                }
            }
        } else {
            var state = data.getBlockState();
            if (state.isAir()) return EmptyComponent.INSTANCE;

            ITooltipComponent result = null;
            var priority = 0;

            for (var provider : registrar.blockIcon.get(state.getBlock())) {
                var icon = provider.value().getIcon(DataAccessor.INSTANCE, PluginConfig.CLIENT);
                if (icon != null) {
                    result = icon;
                    priority = provider.priority();
                    break;
                }
            }

            var blockEntity = data.getBlockEntity();
            if (blockEntity != null) {
                for (var provider : registrar.blockIcon.get(blockEntity)) {
                    if (provider.priority() >= priority) break;

                    var icon = provider.value().getIcon(DataAccessor.INSTANCE, PluginConfig.CLIENT);
                    if (icon != null) {
                        result = icon;
                        break;
                    }
                }
            }

            if (result != null) return result;
        }

        return EmptyComponent.INSTANCE;
    }

    public static Entity getOverrideEntity(HitResult target) {
        if (target == null || target.getType() != HitResult.Type.ENTITY) {
            return null;
        }

        var registrar = Registrar.INSTANCE;
        var entity = ((EntityHitResult) target).getEntity();

        var overrideProviders = registrar.entityOverride.get(entity);
        for (var provider : overrideProviders) {
            var override = provider.value().getOverride(DataAccessor.INSTANCE, PluginConfig.CLIENT);
            if (override != null) {
                return override;
            }
        }

        return entity;
    }

    public static BlockState getOverrideBlock(HitResult target) {
        var registrar = Registrar.INSTANCE;

        Level world = Minecraft.getInstance().level;
        if (world == null) return null;

        var pos = ((BlockHitResult) target).getBlockPos();
        final var state = world.getBlockState(pos);

        BlockState override = null;
        var priority = 0;

        var providers = registrar.blockOverride.get(state.getBlock());
        for (var provider : providers) {
            var blockOverride = provider.value().getOverride(DataAccessor.INSTANCE, PluginConfig.CLIENT);
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

            var beOverride = provider.value().getOverride(DataAccessor.INSTANCE, PluginConfig.CLIENT);
            if (beOverride != null) {
                override = beOverride;
                break;
            }
        }

        return override != null ? override : state;
    }

}
