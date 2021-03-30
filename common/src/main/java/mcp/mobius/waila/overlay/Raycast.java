package mcp.mobius.waila.overlay;

import java.util.LinkedHashSet;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class Raycast {

    private static HitResult target = null;

    public static void fire() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            target = mc.crosshairTarget;
            return;
        }

        Entity viewpoint = mc.getCameraEntity();
        if (viewpoint == null)
            return;

        target = rayTrace(viewpoint, mc.interactionManager.getReachDistance(), 0);
    }

    public static HitResult getTarget() {
        return target;
    }

    public static ItemStack getTargetStack() {
        return target != null && target.getType() == HitResult.Type.BLOCK ? getIdentifierStack() : ItemStack.EMPTY;
    }

    public static Entity getTargetEntity() {
        return target.getType() == HitResult.Type.ENTITY ? getIdentifierEntity() : null;
    }

    public static HitResult rayTrace(Entity entity, double playerReach, float partialTicks) {
        Vec3d eyePosition = entity.getCameraPosVec(partialTicks);
        Vec3d lookVector = entity.getRotationVec(partialTicks);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        FluidHandling fluidView = PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID)
            ? FluidHandling.SOURCE_ONLY
            : FluidHandling.NONE;

        RaycastContext context = new RaycastContext(eyePosition, traceEnd, ShapeType.OUTLINE, fluidView, entity);
        return entity.getEntityWorld().raycast(context);
    }

    public static ItemStack getIdentifierStack() {
        if (target == null) {
            return ItemStack.EMPTY;
        }

        Registrar registrar = Registrar.INSTANCE;
        DataAccessor data = DataAccessor.INSTANCE;
        PluginConfig config = PluginConfig.INSTANCE;

        if (target.getType() == HitResult.Type.ENTITY) {
            LinkedHashSet<IEntityComponentProvider> providers = registrar.getEntityStack(((EntityHitResult) target).getEntity());
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

            LinkedHashSet<IComponentProvider> providers = registrar.getBlockStack(state.getBlock());
            for (IComponentProvider provider : providers) {
                ItemStack providerStack = provider.getStack(data, config);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                providers = registrar.getBlockStack(blockEntity);
                for (IComponentProvider provider : providers) {
                    ItemStack providerStack = provider.getStack(data, config);
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

    public static Entity getIdentifierEntity() {
        if (target == null || target.getType() != HitResult.Type.ENTITY)
            return null;

        Registrar registrar = Registrar.INSTANCE;
        Entity entity = ((EntityHitResult) target).getEntity();

        LinkedHashSet<IEntityComponentProvider> overrideProviders = registrar.getEntityOverride(entity);
        for (IEntityComponentProvider provider : overrideProviders) {
            Entity override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            if (override != null) {
                return override;
            }
        }

        return entity;
    }

}
