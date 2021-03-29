package mcp.mobius.waila.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated will be removed in 1.17 release
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WailaPlugin {

    /**
     * Defines a modid required before this plugin can be loaded. If this modid is not found, the class will not be loaded.
     *
     * @return a modid required for this plugin
     */
    String value() default "";

}
