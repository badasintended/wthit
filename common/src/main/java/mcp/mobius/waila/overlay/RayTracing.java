package mcp.mobius.waila.overlay;

import java.util.LinkedHashSet;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
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
import net.minecraft.world.World;

public class RayTracing {

    public static final RayTracing INSTANCE = new RayTracing();
    private HitResult target = null;
    private MinecraftClient mc = MinecraftClient.getInstance();

    private RayTracing() {
    }

    public void fire() {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            this.target = mc.crosshairTarget;
            return;
        }

        Entity viewpoint = mc.getCameraEntity();
        if (viewpoint == null)
            return;

        this.target = this.rayTrace(viewpoint, mc.interactionManager.getReachDistance(), 0);
    }

    public HitResult getTarget() {
        return this.target;
    }

    public ItemStack getTargetStack() {
        return target != null && target.getType() == HitResult.Type.BLOCK ? getIdentifierStack() : ItemStack.EMPTY;
    }

    public Entity getTargetEntity() {
        return target.getType() == HitResult.Type.ENTITY ? getIdentifierEntity() : null;
    }

    public HitResult rayTrace(Entity entity, double playerReach, float partialTicks) {
        Vec3d eyePosition = entity.getCameraPosVec(partialTicks);
        Vec3d lookVector = entity.getRotationVec(partialTicks);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        RaycastContext.FluidHandling fluidView = PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID) ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE;
        RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, fluidView, entity);
        return entity.getEntityWorld().raycast(context);
    }

    public ItemStack getIdentifierStack() {
        if (target == null) {
            return ItemStack.EMPTY;
        }

        if (target.getType() == HitResult.Type.ENTITY) {
            LinkedHashSet<IEntityComponentProvider> providers = WailaRegistrar.INSTANCE.getStackEntityProviders(((EntityHitResult) target).getEntity());
            for (IEntityComponentProvider provider : providers) {
                ItemStack providerStack = provider.getDisplayItem(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }
        } else {
            World world = mc.world;
            BlockPos pos = ((BlockHitResult) target).getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (state.isAir())
                return ItemStack.EMPTY;

            LinkedHashSet<IComponentProvider> providers = WailaRegistrar.INSTANCE.getStackProviders(state.getBlock());
            for (IComponentProvider provider : providers) {
                ItemStack providerStack = provider.getStack(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
                if (!providerStack.isEmpty()) {
                    return providerStack;
                }
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                providers = WailaRegistrar.INSTANCE.getStackProviders(blockEntity);
                for (IComponentProvider provider : providers) {
                    ItemStack providerStack = provider.getStack(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
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

    public Entity getIdentifierEntity() {
        if (this.target == null || this.target.getType() != HitResult.Type.ENTITY)
            return null;

        Entity entity = ((EntityHitResult) this.target).getEntity();

        LinkedHashSet<IEntityComponentProvider> overrideProviders = WailaRegistrar.INSTANCE.getOverrideEntityProviders(entity);
        for (IEntityComponentProvider provider : overrideProviders) {
            Entity override = provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            if (override != null) {
                return override;
            }
        }

        return entity;
    }

}
