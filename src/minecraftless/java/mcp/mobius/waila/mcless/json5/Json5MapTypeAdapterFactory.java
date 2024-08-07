package mcp.mobius.waila.mcless.json5;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

public class Json5MapTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> @Nullable TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        var type = typeToken.getType();
        var rawType = typeToken.getRawType();
        if (!Map.class.isAssignableFrom(rawType)) return null;

        var keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(type, rawType);
        var keyType = gson.getAdapter(TypeToken.get(keyAndValueTypes[0]));
        var valueType = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));

        Supplier<Map<Object, Object>> ctor;
        if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
            ctor = ConcurrentSkipListMap::new;
        } else if (ConcurrentMap.class.isAssignableFrom(rawType)) {
            ctor = ConcurrentHashMap::new;
        } else if (SortedMap.class.isAssignableFrom(rawType)) {
            ctor = TreeMap::new;
        } else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
            ctor = LinkedHashMap::new;
        } else {
            ctor = LinkedTreeMap::new;
        }

        return new Adapter(ctor, keyType, valueType);
    }

    private static class Adapter<K, V> extends TypeAdapter<Map<K, V>> {

        final Supplier<Map<K, V>> ctor;
        final TypeAdapter<K> keyAdapter;
        final TypeAdapter<V> valueAdapter;

        private Adapter(Supplier<Map<K, V>> ctor, TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
            this.ctor = ctor;
            this.keyAdapter = keyAdapter;
            this.valueAdapter = valueAdapter;
        }

        @Override
        public @Nullable Map<K, V> read(JsonReader in) throws IOException {
            if (!(in instanceof Json5Reader json5)) throw new UnsupportedOperationException("Not a Json5Reader");

            var peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            var map = ctor.get();
            in.beginObject();
            while (in.hasNext()) {
                json5.promoteNameToValue();
                var key = keyAdapter.read(in);
                var value = valueAdapter.read(in);
                var replaced = map.put(key, value);
                if (replaced != null) {
                    throw new JsonSyntaxException("duplicate key: " + key);
                }
            }
            in.endObject();
            return map;
        }

        @Override
        public void write(JsonWriter out, Map<K, V> map) throws IOException {
            if (!(out instanceof Json5Writer)) throw new UnsupportedOperationException("Not a JsonN5Writer");

            if (map == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            for (var entry : map.entrySet()) {
                out.name(String.valueOf(entry.getKey()));
                valueAdapter.write(out, entry.getValue());
            }
            out.endObject();
        }

    }

}
