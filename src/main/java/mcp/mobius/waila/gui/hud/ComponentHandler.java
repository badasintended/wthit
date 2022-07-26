package mcp.mobius.waila.gui.hud;

import java.util.List;

import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.component.EmptyComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// TODO: remove deprecated method calls
@SuppressWarnings("deprecation")
public class ComponentHandler {

    public static void gatherBlock(DataAccessor accessor, Tooltip tooltip, TooltipPosition position) {
        Registrar registrar = Registrar.INSTANCE;
        Block block = accessor.getBlock();
        BlockEntity blockEntity = accessor.getBlockEntity();

        int rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (blockEntity != null && accessor.isTimeElapsed(rate) && Waila.CONFIG.get().getGeneral().isDisplayTooltip()) {
            accessor.resetTimer();
            if (!(registrar.blockData.get(block).isEmpty() && registrar.blockData.get(blockEntity).isEmpty())) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeBlockHitResult((BlockHitResult) accessor.getHitResult());
                PacketSender.c2s().send(Packets.BLOCK, buf);
            }
        }

        handleBlock(accessor, tooltip, block, position);
        handleBlock(accessor, tooltip, blockEntity, position);
    }

    @SuppressWarnings("DuplicatedCode")
    private static void handleBlock(DataAccessor accessor, Tooltip tooltip, Object obj, TooltipPosition position) {
        Registrar registrar = Registrar.INSTANCE;
        List<IBlockComponentProvider> providers = registrar.blockComponent.get(position).get(obj);
        for (IBlockComponentProvider provider : providers) {
            try {
                switch (position) {
                    case HEAD -> {
                        provider.appendHead((ITooltip) tooltip, accessor, PluginConfig.INSTANCE);
                        provider.appendHead((List<Component>) tooltip, accessor, PluginConfig.INSTANCE);
                    }
                    case BODY -> {
                        provider.appendBody((ITooltip) tooltip, accessor, PluginConfig.INSTANCE);
                        provider.appendBody((List<Component>) tooltip, accessor, PluginConfig.INSTANCE);
                    }
                    case TAIL -> {
                        provider.appendTail((ITooltip) tooltip, accessor, PluginConfig.INSTANCE);
                        provider.appendTail((List<Component>) tooltip, accessor, PluginConfig.INSTANCE);
                    }
                }
            } catch (Throwable e) {
                ExceptionUtil.dump(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void gatherEntity(Entity entity, DataAccessor accessor, Tooltip tooltip, TooltipPosition position) {
        Registrar registrar = Registrar.INSTANCE;
        Entity trueEntity = accessor.getEntity();

        int rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (trueEntity != null && accessor.isTimeElapsed(rate)) {
            accessor.resetTimer();

            if (!registrar.entityData.get(trueEntity).isEmpty()) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeVarInt(entity.getId());
                Vec3 hitPos = accessor.getHitResult().getLocation();
                buf.writeDouble(hitPos.x);
                buf.writeDouble(hitPos.y);
                buf.writeDouble(hitPos.z);
                PacketSender.c2s().send(Packets.ENTITY, buf);
            }
        }

        List<IEntityComponentProvider> providers = registrar.entityComponent.get(position).get(entity);
        for (IEntityComponentProvider provider : providers) {
            try {
                switch (position) {
                    case HEAD -> {
                        provider.appendHead((ITooltip) tooltip, accessor, PluginConfig.INSTANCE);
                        provider.appendHead((List<Component>) tooltip, accessor, PluginConfig.INSTANCE);
                    }
                    case BODY -> {
                        provider.appendBody((ITooltip) tooltip, accessor, PluginConfig.INSTANCE);
                        provider.appendBody((List<Component>) tooltip, accessor, PluginConfig.INSTANCE);
                    }
                    case TAIL -> {
                        provider.appendTail((ITooltip) tooltip, accessor, PluginConfig.INSTANCE);
                        provider.appendTail((List<Component>) tooltip, accessor, PluginConfig.INSTANCE);
                    }
                }
            } catch (Throwable e) {
                ExceptionUtil.dump(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    public static ITooltipComponent getIcon(HitResult target) {
        Registrar registrar = Registrar.INSTANCE;
        DataAccessor data = DataAccessor.INSTANCE;
        PluginConfig config = PluginConfig.CLIENT;

        if (target.getType() == HitResult.Type.ENTITY) {
            List<IEntityComponentProvider> providers = registrar.entityIcon.get(data.getEntity());
            for (IEntityComponentProvider provider : providers) {
                ITooltipComponent icon = provider.getIcon(data, config);
                if (icon != null) {
                    return icon;
                }
                ItemStack providerStack = provider.getDisplayItem(data, config);
                if (!providerStack.isEmpty()) {
                    return new ItemComponent(providerStack);
                }
            }
        } else {
            BlockState state = data.getBlockState();
            if (state.isAir()) {
                return EmptyComponent.INSTANCE;
            }

            ITooltipComponent component = getBlockIcon(registrar.blockIcon.get(state.getBlock()));
            if (component != null) {
                return component;
            }

            BlockEntity blockEntity = data.getBlockEntity();
            if (blockEntity != null) {
                component = getBlockIcon(registrar.blockIcon.get(blockEntity));
                if (component != null) {
                    return component;
                }
            }
        }

        return EmptyComponent.INSTANCE;
    }

    @Nullable
    private static ITooltipComponent getBlockIcon(List<IBlockComponentProvider> providers) {
        for (IBlockComponentProvider provider : providers) {
            ITooltipComponent icon = provider.getIcon(DataAccessor.INSTANCE, PluginConfig.CLIENT);
            if (icon != null) {
                return icon;
            }
            ItemStack providerStack = provider.getDisplayItem(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            if (!providerStack.isEmpty()) {
                return new ItemComponent(providerStack);
            }
        }
        return null;
    }

    public static Entity getOverrideEntity(HitResult target) {
        if (target == null || target.getType() != HitResult.Type.ENTITY) {
            return null;
        }

        Registrar registrar = Registrar.INSTANCE;
        Entity entity = ((EntityHitResult) target).getEntity();

        List<IEntityComponentProvider> overrideProviders = registrar.entityOverride.get(entity);
        for (IEntityComponentProvider provider : overrideProviders) {
            Entity override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.CLIENT);
            if (override != null) {
                return override;
            }
        }

        return entity;
    }

    public static BlockState getOverrideBlock(HitResult target) {
        Registrar registrar = Registrar.INSTANCE;

        Level world = Minecraft.getInstance().level;
        BlockPos pos = ((BlockHitResult) target).getBlockPos();
        //noinspection ConstantConditions
        BlockState state = world.getBlockState(pos);

        List<IBlockComponentProvider> providers = registrar.blockOverride.get(state.getBlock());
        for (IBlockComponentProvider provider : providers) {
            BlockState override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.CLIENT);
            if (override != null) {
                return override;
            }
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        providers = registrar.blockOverride.get(blockEntity);
        for (IBlockComponentProvider provider : providers) {
            BlockState override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.CLIENT);
            if (override != null) {
                return override;
            }
        }

        return state;
    }

}
