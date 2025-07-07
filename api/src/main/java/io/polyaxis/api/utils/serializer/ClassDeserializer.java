package io.polyaxis.api.utils.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.polyaxis.api.utils.misc.StringUtils;

import java.io.IOException;
import java.util.Map;

/// [Class] deserializer.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class ClassDeserializer extends JsonDeserializer<Class<?>> {

    public static final Map<String, Class<?>> PRIMITIVE_TYPES = Map.ofEntries(
            Map.entry("byte", Byte.class),
            Map.entry("short", Short.class),
            Map.entry("int", Integer.class),
            Map.entry("long", Long.class),
            Map.entry("float", Float.class),
            Map.entry("double", Double.class),
            Map.entry("boolean", Boolean.class),
            Map.entry("char", Character.class),
            Map.entry("void", Void.class)
    );

    @Override
    public Class<?> deserialize(
            final JsonParser parser,
            final DeserializationContext context
    ) throws IOException {
        final String className = parser.getText();
        if (StringUtils.isEmpty(className)) {
            return Void.class;
        }
        for (Map.Entry<String, Class<?>> entry : PRIMITIVE_TYPES.entrySet()) {
            if (className.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
