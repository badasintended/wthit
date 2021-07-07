package mcp.mobius.waila.hud;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.debug.ExceptionHandler;
import mcp.mobius.waila.registry.TooltipRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HudProviderHandler {

    public static void gatherBlock(DataAccessor accessor, List<Text> tooltip, TooltipPosition position) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        Block block = accessor.getBlock();
        BlockEntity blockEntity = accessor.getBlockEntity();

        int rate = Waila.config.get().getGeneral().getRateLimit();

        if (blockEntity != null && accessor.isTimeElapsed(rate) && Waila.config.get().getGeneral().shouldDisplayTooltip()) {
            accessor.resetTimer();
            if (!(registrar.blockData.get(block).isEmpty() && registrar.blockData.get(blockEntity).isEmpty())) {
                Waila.packet.requestBlock(blockEntity);
            }
        }

        handleBlock(accessor, tooltip, block, position);
        handleBlock(accessor, tooltip, blockEntity, position);
    }

    private static void handleBlock(DataAccessor accessor, List<Text> tooltip, Object obj, TooltipPosition position) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        List<IBlockComponentProvider> providers = registrar.blockComponent.get(position).get(obj);
        for (IBlockComponentProvider provider : providers) {
            try {
                switch (position) {
                    case HEAD -> provider.appendHead(tooltip, accessor, PluginConfig.INSTANCE);
                    case BODY -> provider.appendBody(tooltip, accessor, PluginConfig.INSTANCE);
                    case TAIL -> provider.appendTail(tooltip, accessor, PluginConfig.INSTANCE);
                }
            } catch (Throwable e) {
                ExceptionHandler.handleErr(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    public static void gatherEntity(Entity entity, DataAccessor accessor, List<Text> tooltip, TooltipPosition position) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        Entity trueEntity = accessor.getEntity();

        int rate = Waila.config.get().getGeneral().getRateLimit();

        if (trueEntity != null && accessor.isTimeElapsed(rate)) {
            accessor.resetTimer();

            if (!registrar.entityData.get(trueEntity).isEmpty()) {
                Waila.packet.requestEntity(trueEntity);
            }
        }

        List<IEntityComponentProvider> providers = registrar.entityComponent.get(position).get(entity);
        for (IEntityComponentProvider provider : providers) {
            try {
                switch (position) {
                    case HEAD -> provider.appendHead(tooltip, accessor, PluginConfig.INSTANCE);
                    case BODY -> provider.appendBody(tooltip, accessor, PluginConfig.INSTANCE);
                    case TAIL -> provider.appendTail(tooltip, accessor, PluginConfig.INSTANCE);
                }
            } catch (Throwable e) {
                ExceptionHandler.handleErr(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    public static ItemStack getDisplayItem(HitResult target) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        DataAccessor data = DataAccessor.INSTANCE;
        PluginConfig config = PluginConfig.INSTANCE;

        if (target.getType() == HitResult.Type.ENTITY) {
            List<IEntityComponentProvider> providers = registrar.entityItem.get(data.getEntity());
            for (IEntityComponentProvider provider : providers) {
                ItemStack providerStack = provider.getDisplayItem(data, config);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }
        } else {
            World world = MinecraftClient.getInstance().world;
            BlockPos pos = ((BlockHitResult) target).getBlockPos();
            BlockState state = data.getBlockState();
            if (state.isAir())
                return ItemStack.EMPTY;

            List<IBlockComponentProvider> providers = registrar.blockItem.get(state.getBlock());
            for (IBlockComponentProvider provider : providers) {
                ItemStack providerStack = provider.getDisplayItem(data, config);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }

            BlockEntity blockEntity = data.getBlockEntity();
            if (blockEntity != null) {
                providers = registrar.blockItem.get(blockEntity);
                for (IBlockComponentProvider provider : providers) {
                    ItemStack providerStack = provider.getDisplayItem(data, config);
                    if (!providerStack.isEmpty()) {
                        return providerStack;
                    }
                }
            }

            ItemStack pick = state.getBlock().getPickStack(world, pos, state);
            if (!pick.isEmpty()) {
                return pick;
            }

            if (state.getBlock().asItem() != Items.AIR)
                return new ItemStack(state.getBlock());
        }

        return ItemStack.EMPTY;
    }

    public static Entity getOverrideEntity(HitResult target) {
        if (target == null || target.getType() != HitResult.Type.ENTITY) {
            return null;
        }

        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        Entity entity = ((EntityHitResult) target).getEntity();

        List<IEntityComponentProvider> overrideProviders = registrar.entityOverride.get(entity);
        for (IEntityComponentProvider provider : overrideProviders) {
            Entity override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            if (override != null) {
                return override;
            }
        }

        return entity;
    }

    public static BlockState getOverrideBlock(HitResult target) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;

        World world = MinecraftClient.getInstance().world;
        BlockPos pos = ((BlockHitResult) target).getBlockPos();
        BlockState state = world.getBlockState(pos);

        List<IBlockComponentProvider> providers = registrar.blockOverride.get(state.getBlock());
        for (IBlockComponentProvider provider : providers) {
            BlockState override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            if (override != null) {
                return override;
            }
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        providers = registrar.blockOverride.get(blockEntity);
        for (IBlockComponentProvider provider : providers) {
            BlockState override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            if (override != null) {
                return override;
            }
        }

        return state;
    }

}
