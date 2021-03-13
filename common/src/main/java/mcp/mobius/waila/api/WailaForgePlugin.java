package mcp.mobius.waila.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WailaForgePlugin {

    /**
     * The id of this plugin.
     */
    String value();

    /**
     * Mod IDs needed to this plugin to load.
     */
    String[] requires() default {};

}
