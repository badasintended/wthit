package mcp.mobius.waila.api.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.IExtraService;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public abstract sealed class BuiltinData implements IData permits
    EnergyData,
    FluidData,
    ItemData,
    ProgressData {

    /**
     * Bootstrap needed data types, making it available to use.
     */
    @SafeVarargs
    public static void bootstrap(Class<? extends BuiltinData>... types) {
        Preconditions.checkArgument(types.length > 0, "Need some types");

        for (Class<? extends BuiltinData> type : types) {
            IExtraService.INSTANCE.bootstrapData(type);
        }
    }

    /**
     * Indicates that this method can be called without needing to bootstrap the data type.
     */
    @Documented
    @ApiStatus.Internal
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface BootstrapUnneeded {

    }

    @ApiStatus.Internal
    protected static ResourceLocation rl(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE + "x", path);
    }

}
