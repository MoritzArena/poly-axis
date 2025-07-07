package io.polyaxis.api.utils.misc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.polyaxis.api.utils.exceptions.DeserializationException;
import io.polyaxis.api.utils.exceptions.SerializationException;
import io.polyaxis.api.utils.serializer.ClassDeserializer;
import io.polyaxis.api.utils.serializer.ClassSerializer;
import io.polyaxis.api.utils.serializer.LocalDateTimeDeserializer;
import io.polyaxis.api.utils.serializer.LocalDateTimeSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map;

/// Jackson Util.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
@SuppressWarnings("unchecked")
public final class JacksonUtils {

    private JacksonUtils() {
    }
    
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        final SimpleModule module = new SimpleModule();

        // ⇢ LocalDateTime.class
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        // ⇢ Class.class
        module.addSerializer((Class<Class<?>>) (Class<?>) Class.class, new ClassSerializer());
        module.addDeserializer(Class.class, new ClassDeserializer());

        MAPPER.registerModule(module);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
    
    /// Object to json string.
    ///
    /// @param obj obj
    /// @return json string
    /// @throws SerializationException if transfer failed
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }

    /// Object to json string by given mapper.
    ///
    /// @param obj obj
    /// @return json string
    /// @throws SerializationException if transfer failed
    public static String toJsonByGivenMapper(ObjectMapper outerMapper, Object obj) {
        try {
            return outerMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }
    
    /// Object to json string byte array.
    ///
    /// @param obj obj
    /// @return json string byte array
    /// @throws SerializationException if transfer failed
    public static byte[] toJsonBytes(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }

    /// Object to json string byte array by given mapper.
    ///
    /// @param obj obj
    /// @return json string byte array
    /// @throws SerializationException if transfer failed
    public static byte[] toJsonBytesByGivenMapper(ObjectMapper outerMapper, Object obj) {
        try {
            return outerMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param json json string
    /// @param cls  class of object
    /// @param <T>  General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(byte[] json, Class<T> cls) {
        try {
            return MAPPER.readValue(json, cls);
        } catch (Exception e) {
            throw new DeserializationException(cls, e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param json json string
    /// @param cls  [Type] of object
    /// @param <T>  General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(byte[] json, Type cls) {
        try {
            return MAPPER.readValue(json, MAPPER.constructType(cls));
        } catch (Exception e) {
            throw new DeserializationException(e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param inputStream json string input stream
    /// @param cls         class of object
    /// @param <T>         General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(InputStream inputStream, Class<T> cls) {
        try {
            return MAPPER.readValue(inputStream, cls);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param json          json string byte array
    /// @param typeReference [TypeReference] of object
    /// @param <T>           General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(byte[] json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new DeserializationException(e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param json json string
    /// @param cls  class of object
    /// @param <T>  General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(String json, Class<T> cls) {
        try {
            return MAPPER.readValue(json, cls);
        } catch (IOException e) {
            throw new DeserializationException(cls, e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param json json string
    /// @param type [Type] of object
    /// @param <T>  General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(String json, Type type) {
        try {
            return MAPPER.readValue(json, MAPPER.constructType(type));
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param json          json string
    /// @param typeReference [TypeReference] of object
    /// @param <T>           General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new DeserializationException(typeReference.getClass(), e);
        }
    }
    
    /// Json string deserialize to Object.
    ///
    /// @param inputStream json string input stream
    /// @param type        [Type] of object
    /// @param <T>         General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(InputStream inputStream, Type type) {
        try {
            return MAPPER.readValue(inputStream, MAPPER.constructType(type));
        } catch (IOException e) {
            throw new DeserializationException(type, e);
        }
    }
    
    /// Json string deserialize to Jackson [JsonNode].
    ///
    /// @param json json string
    /// @return [JsonNode]
    /// @throws DeserializationException if deserialize failed
    public static JsonNode toObj(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    public static <T> T mapToObj(Map<String, Object> map, Class<T> clazz) {
        return toObj(toJson(map), clazz);
    }

    /// Parse to target class by given class type.
    ///
    /// @param outerMapper given custom mapper
    /// @param is   input stream
    /// @param cls  class of object
    /// @param <T>  General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(
            ObjectMapper outerMapper, InputStream is, Class<T> cls) {
        try {
            return outerMapper.readValue(is, cls);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    /// Parse to target class by given class type.
    ///
    /// @param outerMapper given custom mapper
    /// @param json json string
    /// @param cls  class of object
    /// @param <T>  General type
    /// @return object
    /// @throws DeserializationException if deserialize failed
    public static <T> T toObj(
            ObjectMapper outerMapper, String json, Class<T> cls) {
        try {
            return outerMapper.readValue(json, cls);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
    
    /// Register subtype for child class.
    ///
    /// @param clz  child class
    /// @param type type name of child class
    public static void registerSubtype(Class<?> clz, String type) {
        MAPPER.registerSubtypes(new NamedType(clz, type));
    }
    
    /// Create a new empty Jackson [ObjectNode].
    ///
    /// @return [ObjectNode]
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(MAPPER.getNodeFactory());
    }
    
    /// Create a new empty Jackson [ArrayNode].
    ///
    /// @return [ArrayNode]
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(MAPPER.getNodeFactory());
    }
    
    /// Parse object to Jackson [JsonNode].
    ///
    /// @param obj object
    /// @return [JsonNode]
    public static JsonNode transferToJsonNode(Object obj) {
        return MAPPER.valueToTree(obj);
    }
    
    /// construct java type -> Jackson Java Type.
    ///
    /// @param type java type
    /// @return JavaType [JavaType]
    public static JavaType constructJavaType(Type type) {
        return MAPPER.constructType(type);
    }
}
