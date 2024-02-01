package mcp.mobius.waila.api;

import java.util.List;

import mcp.mobius.waila.api.__internal__.IApiService;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registry for attaching object instances to a type and its subtypes.
 *
 * @param <T> the instance type
 */
@ApiStatus.NonExtendable
public interface IInstanceRegistry<T> {

    /**
     * Creates an instance registry.
     */
    static <T> IInstanceRegistry<T> create() {
        return IApiService.INSTANCE.createInstanceRegistry(false);
    }

    /**
     * Creates an instance registry with the priority comparison reversed.
     */
    static <T> IInstanceRegistry<T> createReversed() {
        return IApiService.INSTANCE.createInstanceRegistry(true);
    }

    /**
     * Registers an instance.
     *
     * @param bound    the upper bound type for the instance to apply
     * @param instance the instance
     * @param priority the priority
     */
    void add(Class<?> bound, T instance, int priority);

    /**
     * Returns the instances that apply to the target sorted by their priority.
     */
    List<Entry<T>> get(Object target);

    @ApiStatus.NonExtendable
    interface Entry<T> {

        T instance();

        int priority();

    }

}
