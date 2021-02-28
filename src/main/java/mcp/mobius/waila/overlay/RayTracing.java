package mcp.mobius.waila.overlay;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
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
        List<ItemStack> items = this.getIdentifierItems();

        if (items.isEmpty())
            return ItemStack.EMPTY;

        return items.get(0);
    }

    public Entity getIdentifierEntity() {
        if (this.target == null || this.target.getType() != HitResult.Type.ENTITY)
            return null;

        List<Entity> entities = Lists.newArrayList();

        Entity entity = ((EntityHitResult) this.target).getEntity();
        if (WailaRegistrar.INSTANCE.hasOverrideEntityProviders(entity)) {
            Collection<List<IEntityComponentProvider>> overrideProviders = WailaRegistrar.INSTANCE.getOverrideEntityProviders(entity).values();
            for (List<IEntityComponentProvider> providers : overrideProviders)
                for (IEntityComponentProvider provider : providers)
                    entities.add(provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE));
        }

        return entities.size() > 0 ? entities.get(0) : entity;
    }

    public List<ItemStack> getIdentifierItems() {
        List<ItemStack> items = Lists.newArrayList();

        if (this.target == null)
            return items;

        switch (this.target.getType()) {
            case ENTITY: {
                if (WailaRegistrar.INSTANCE.hasStackEntityProviders(((EntityHitResult) target).getEntity())) {
                    Collection<List<IEntityComponentProvider>> providers = WailaRegistrar.INSTANCE.getStackEntityProviders(((EntityHitResult) target).getEntity()).values();
                    for (List<IEntityComponentProvider> providersList : providers) {
                        for (IEntityComponentProvider provider : providersList) {
                            ItemStack providerStack = provider.getDisplayItem(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
                            if (providerStack.isEmpty())
                                continue;

                            items.add(providerStack);
                        }
                    }
                }
                break;
            }
            case BLOCK: {
                World world = mc.world;
                BlockPos pos = ((BlockHitResult) target).getBlockPos();
                BlockState state = world.getBlockState(pos);
                if (state.isAir())
                    return items;

                BlockEntity tile = world.getBlockEntity(pos);

                if (WailaRegistrar.INSTANCE.hasStackProviders(state.getBlock()))
                    handleStackProviders(items, WailaRegistrar.INSTANCE.getStackProviders(state.getBlock()).values());

                if (tile != null && WailaRegistrar.INSTANCE.hasStackProviders(tile))
                    handleStackProviders(items, WailaRegistrar.INSTANCE.getStackProviders(tile).values());

                if (!items.isEmpty())
                    return items;

                ItemStack pick = state.getBlock().getPickStack(world, pos, state);
                if (!pick.isEmpty())
                    return Collections.singletonList(pick);

                if (items.isEmpty() && state.getBlock().asItem() != Items.AIR)
                    items.add(new ItemStack(state.getBlock()));

                break;
            }
        }

        return items;
    }

    private void handleStackProviders(List<ItemStack> items, Collection<List<IComponentProvider>> providers) {
        for (List<IComponentProvider> providersList : providers) {
            for (IComponentProvider provider : providersList) {
                ItemStack providerStack = provider.getStack(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
                if (providerStack.isEmpty())
                    continue;

                items.add(providerStack);
            }
        }
    }

}
