package mcp.mobius.waila.access;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record DataType<D extends IData>(ResourceLocation id) implements IData.Type<D> {

    public static final StreamCodec<RegistryFriendlyByteBuf, IData> CODEC = ResourceLocation.STREAM_CODEC
        .<RegistryFriendlyByteBuf>cast()
        .dispatch(
            data -> data.type().id(),
            id -> Registrar.get().dataCodecs.get(id));

}
