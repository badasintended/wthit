package mcp.mobius.waila.overlay;

import com.google.common.collect.Lists;
import mcp.mobius.waila.Waila;
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
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.FluidRayTraceMode;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RayTracing {

    public static final RayTracing INSTANCE = new RayTracing();
    private HitResult target = null;
    private MinecraftClient mc = MinecraftClient.getInstance();

    private RayTracing() {
    }

    public void fire() {
        if (mc.hitResult != null && mc.hitResult.type == HitResult.Type.ENTITY) {
            this.target = mc.hitResult;
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
        return target != null && target.type == HitResult.Type.BLOCK ? getIdentifierStack() : ItemStack.EMPTY;
    }

    public Entity getTargetEntity() {
        return target.type == HitResult.Type.ENTITY ? getIdentifierEntity() : null;
    }

    public HitResult rayTrace(Entity entity, double playerReach, float partialTicks) {
        Vec3d eyePosition = entity.getCameraPosVec(partialTicks);
        Vec3d lookVector = entity.getRotationVec(partialTicks);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        return entity.getEntityWorld().rayTrace(eyePosition, traceEnd, Waila.config.getGeneral().shouldDisplayFluids() ? FluidRayTraceMode.SOURCE_ONLY : FluidRayTraceMode.NONE);
    }

    public ItemStack getIdentifierStack() {
        List<ItemStack> items = this.getIdentifierItems();

        if (items.isEmpty())
            return ItemStack.EMPTY;

        return items.get(0);
    }

    public Entity getIdentifierEntity() {
        if (this.target == null)
            return null;

        List<Entity> entities = Lists.newArrayList();

        if (WailaRegistrar.INSTANCE.hasOverrideEntityProviders(this.target.entity)) {
            Collection<List<IEntityComponentProvider>> overrideProviders = WailaRegistrar.INSTANCE.getOverrideEntityProviders(this.target.entity).values();
            for (List<IEntityComponentProvider> providers : overrideProviders)
                for (IEntityComponentProvider provider : providers)
                    entities.add(provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE));
        }

        return entities.size() > 0 ? entities.get(0) : target.entity;
    }

    public List<ItemStack> getIdentifierItems() {
        List<ItemStack> items = Lists.newArrayList();

        if (this.target == null)
            return items;

        World world = mc.world;
        BlockPos pos = target.getBlockPos();
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

        if (items.isEmpty() && state.getBlock().getItem() != Items.AIR)
            items.add(new ItemStack(state.getBlock()));

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
