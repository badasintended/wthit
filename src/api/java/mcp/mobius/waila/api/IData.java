package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * A typed data for syncing complex data.
 * <p>
 * Register data types with {@link ICommonRegistrar#dataType}
 * <p>
 * For simple data, consider using {@linkplain IDataWriter#raw() raw NBT data} instead,
 * as it is easier to do and enough for most purpose.
 * <p>
 * See {@link mcp.mobius.waila.api.data} for built-in implementations.
 */
public interface IData {

    /**
     * Creates a data type.
     * <p>
     * Save the returned value to a static final variable.
     *
     * @param id the data id
     */
    static <D extends IData> Type<D> createType(ResourceLocation id) {
        return IApiService.INSTANCE.createDataType(id);
    }

    /**
     * Returns the type of the data, should be a constant variable.
     *
     * @see #createType(ResourceLocation)
     */
    Type<? extends IData> type();


    /**
     * @see #createType(ResourceLocation)
     */
    @SuppressWarnings("unused")
    @ApiStatus.NonExtendable
    interface Type<D extends IData> {

        ResourceLocation id();

    }

}
