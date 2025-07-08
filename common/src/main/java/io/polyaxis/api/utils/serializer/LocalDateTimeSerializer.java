package io.polyaxis.api.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/// [LocalDateTime] serializer.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(
            final LocalDateTime value,
            final JsonGenerator gen,
            final SerializerProvider serializers
    ) throws IOException {
        if (value != null) {
            gen.writeString(value.format(FORMATTER));
        }
    }
}
