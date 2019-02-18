package mcp.mobius.waila.api;

import java.util.List;
import java.util.Map;

public interface ITaggableList<TAG, VALUE> extends List<VALUE> {

    void setTag(TAG tag, VALUE value);

    VALUE removeTag(TAG tag);

    VALUE getTag(TAG tag);

    Map<TAG, VALUE> getTags();

    void absorb(ITaggableList<TAG, VALUE> other);
}
