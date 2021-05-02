package mcp.mobius.waila.overlay;

import java.util.List;

import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.plugin.core.WailaCore;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class Raycast {

    private static HitResult target = null;

    public static void fire() {
        MinecraftClient client = MinecraftClient.getInstance();

        Entity viewpoint = client.getCameraEntity();
        if (viewpoint == null)
            return;

        target = raycast(viewpoint, client.interactionManager.getReachDistance(), client.getTickDelta());
    }

    public static HitResult getTarget() {
        return target;
    }

    public static ItemStack getTargetStack() {
        return target != null && target.getType() == HitResult.Type.BLOCK ? getDisplayItem() : ItemStack.EMPTY;
    }

    public static HitResult raycast(Entity entity, double playerReach, float tickDelta) {
        World world = entity.world;
        Vec3d eyePosition = entity.getCameraPosVec(tickDelta);
        Vec3d lookVector = entity.getRotationVec(tickDelta);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        if (PluginConfig.INSTANCE.get(WailaCore.CONFIG_SHOW_ENTITY)) {
            EntityHitResult result = ProjectileUtil.getEntityCollision(world, entity, eyePosition, traceEnd, new Box(eyePosition, traceEnd), null);
            if (result != null) {
                return result;
            }
        }

        FluidHandling fluidView = PluginConfig.INSTANCE.get(WailaCore.CONFIG_SHOW_FLUID)
            ? FluidHandling.SOURCE_ONLY
            : FluidHandling.NONE;

        RaycastContext context = new RaycastContext(eyePosition, traceEnd, ShapeType.OUTLINE, fluidView, entity);
        return world.raycast(context);
    }

    public static ItemStack getDisplayItem() {
        if (target == null) {
            return ItemStack.EMPTY;
        }

        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        DataAccessor data = DataAccessor.INSTANCE;
        PluginConfig config = PluginConfig.INSTANCE;

        if (target.getType() == HitResult.Type.ENTITY) {
            List<IEntityComponentProvider> providers = registrar.entityItem.get(((EntityHitResult) target).getEntity());
            for (IEntityComponentProvider provider : providers) {
                ItemStack providerStack = provider.getDisplayItem(data, config);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }
        } else {
            World world = MinecraftClient.getInstance().world;
            BlockPos pos = ((BlockHitResult) target).getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (state.isAir())
                return ItemStack.EMPTY;

            List<IBlockComponentProvider> providers = registrar.blockItem.get(state.getBlock());
            for (IBlockComponentProvider provider : providers) {
                ItemStack providerStack = provider.getDisplayItem(data, config);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
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

    public static Entity getOverrideEntity() {
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

    public static BlockState getOverrideBlock() {
        if (target == null || target.getType() != HitResult.Type.BLOCK) {
            return Blocks.AIR.getDefaultState();
        }

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
