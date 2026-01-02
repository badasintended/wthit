package mcp.mobius.waila.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@code waila_plugins.json} file, see {@link mcp.mobius.waila.api} javadocs for more info.
 */
@Deprecated
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("DeprecatedIsStillUsed")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
public @interface WailaPlugin {

    /**
     * This plugin ID.
     * <br>
     * Must be a valid {@link ResourceLocation}.
     */
    String id();

    /**
     * Require specified mods to load this plugin.
     */
    String[] required() default {};

    /**
     * The environment in which to load this plugin on.
     */
    IPluginInfo.Side side() default IPluginInfo.Side.BOTH;

}
