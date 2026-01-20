package mcp.mobius.waila.access;

import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;

public enum ServerAccessor implements IServerAccessor<Object> {

    INSTANCE;

    private ServerLevel level;
    private ServerPlayer player;
    private HitResult hitResult;
    private Object target;

    @SuppressWarnings("unchecked")
    public <T> IServerAccessor<T> set(ServerLevel level, ServerPlayer player, HitResult hitResult, Object target) {
        this.level = level;
        this.player = player;
        this.hitResult = hitResult;
        this.target = target;

        return (IServerAccessor<T>) this;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public ServerPlayer getPlayer() {
        return player;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <H extends HitResult> H getHitResult() {
        return (H) hitResult;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getTarget() {
        return target;
    }

    @Override
    public IDataReader getContext() {
        return DataReader.SERVER;
    }

}
