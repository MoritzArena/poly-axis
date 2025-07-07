package io.polyaxis.api.utils.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/// [LocalDateTime] deserializer.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(
            final JsonParser parser,
            final DeserializationContext context
    ) throws IOException {
        final String text = parser.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(text, FORMATTER);
    }
}
