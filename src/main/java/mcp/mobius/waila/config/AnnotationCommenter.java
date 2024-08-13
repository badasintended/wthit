package mcp.mobius.waila.config;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import mcp.mobius.waila.api.IJsonConfig;
import org.jetbrains.annotations.Nullable;

public class AnnotationCommenter implements IJsonConfig.Commenter {

    private final Class<?> cls;
    private final Gson gson;

    private final Map<Class<?>, Field[]> fields = new HashMap<>();
    private final Map<Field, HashSet<String>> possibleFieldNames = new HashMap<>();

    public AnnotationCommenter(Class<?> cls, Gson gson) {
        this.cls = cls;
        this.gson = gson;
    }

    @Override
    public @Nullable String getComment(List<String> path) {
        AnnotatedElement element = null;

        if (path.isEmpty()) {
            element = cls;
        } else {
            var parent = cls;
            for (var part : path) {
                var fields = this.fields.computeIfAbsent(parent, Class::getDeclaredFields);
                for (var field : fields) {
                    var possibleFieldNames = this.possibleFieldNames.computeIfAbsent(field, f -> {
                        var set = new HashSet<String>();
                        set.add(field.getName());
                        set.add(gson.fieldNamingStrategy().translateName(field));
                        var serializedName = field.getAnnotation(SerializedName.class);
                        if (serializedName != null) {
                            set.add(serializedName.value());
                            set.addAll(Arrays.asList(serializedName.alternate()));
                        }
                        return set;
                    });

                    if (possibleFieldNames.contains(part)) {
                        element = field;
                        parent = field.getType();
                    }
                }
            }
        }

        if (element != null) {
            var comment = element.getAnnotation(IJsonConfig.Comment.class);
            if (comment != null) return comment.value();
        }

        return null;
    }

}
