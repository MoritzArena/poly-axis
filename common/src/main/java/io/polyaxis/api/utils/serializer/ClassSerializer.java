package io.polyaxis.api.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/// [Class] serializer.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class ClassSerializer extends JsonSerializer<Class<?>> {

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER;

    static {
        PRIMITIVE_TO_WRAPPER = new HashMap<>();
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(void.class, Void.class);
    }

    @Override
    public void serialize(Class<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Class<?> actual = PRIMITIVE_TO_WRAPPER.getOrDefault(value, value);
        gen.writeString(actual.getName());
    }
}
