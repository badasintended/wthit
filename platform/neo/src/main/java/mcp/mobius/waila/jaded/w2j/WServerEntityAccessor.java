package mcp.mobius.waila.jaded.w2j;

import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.EntityAccessor;

public class WServerEntityAccessor implements EntityAccessor {

    private final IDataWriter data;
    private final IServerAccessor<Entity> accessor;

    public WServerEntityAccessor(IDataWriter data, IServerAccessor<Entity> accessor) {
        this.data = data;
        this.accessor = accessor;
    }

    @Override
    public Entity getEntity() {
        return accessor.getTarget();
    }

    @Override
    public Level getLevel() {
        return accessor.getWorld();
    }

    @Override
    public Player getPlayer() {
        return accessor.getPlayer();
    }

    @Override
    public @NotNull CompoundTag getServerData() {
        return data.raw();
    }

    @Override
    public EntityHitResult getHitResult() {
        return accessor.getHitResult();
    }

    @Override
    public boolean isServerConnected() {
        return true;
    }

    @Override
    public ItemStack getPickedResult() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showDetails() {
        return true;
    }

    @Override
    public Object getTarget() {
        return accessor.getTarget();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public boolean verifyData(CompoundTag compoundTag) {
        return true;
    }

}
