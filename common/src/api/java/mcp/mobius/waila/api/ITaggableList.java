package mcp.mobius.waila.api;

import java.util.List;
import java.util.Map;

/**
 * @deprecated use {@link ITooltip}
 * <p>
 * TODO: Remove
 */
@Deprecated
public interface ITaggableList<T, V> extends List<V> {

    void setTag(T tag, V value);

    V removeTag(T tag);

    V getTag(T tag);

    Map<T, V> getTags();

    void absorb(ITaggableList<T, V> other);

}
