package mcp.mobius.waila.api;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link ITooltip}
 * <p>
 * TODO: Remove
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.19")
public interface ITaggableList<T, V> extends List<V> {

    void setTag(T tag, V value);

    V removeTag(T tag);

    V getTag(T tag);

    Map<T, V> getTags();

    void absorb(ITaggableList<T, V> other);

}
