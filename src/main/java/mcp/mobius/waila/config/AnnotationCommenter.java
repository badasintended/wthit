package mcp.mobius.waila.config;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import mcp.mobius.waila.api.IJsonConfig;
import org.jetbrains.annotations.Nullable;

public record AnnotationCommenter(
    Type type,
    Gson gson,
    IJsonConfig.Commenter extra
) implements IJsonConfig.Commenter {

    @Override
    public @Nullable String getComment(String path) {
        String annotationComment = null;
        if (type instanceof Class<?> cls) {
            AnnotatedElement element = null;
            if (path.equals("$")) {
                element = cls;
            } else {
                var split = path.substring(2).split("\\.");
                var parent = cls;
                for (var part : split) {
                    var fields = parent.getDeclaredFields();
                    for (var field : fields) {
                        var possibleFieldNames = new HashSet<String>();
                        possibleFieldNames.add(gson.fieldNamingStrategy().translateName(field));

                        var serializedName = field.getAnnotation(SerializedName.class);
                        if (serializedName != null) {
                            possibleFieldNames.add(serializedName.value());
                            possibleFieldNames.addAll(Arrays.asList(serializedName.alternate()));
                        }

                        if (possibleFieldNames.contains(part)) {
                            element = field;
                            parent = field.getType();
                        }
                    }
                }
            }

            if (element != null) {
                var comment = element.getAnnotation(IJsonConfig.Comment.class);
                if (comment != null) annotationComment = comment.value();
            }
        }

        var extraComment = extra.getComment(path);

        if (annotationComment == null) return extraComment;
        if (extraComment == null) return annotationComment;
        return annotationComment + "\n" + extraComment;
    }

}
