package io.polyaxis.api.utils.exceptions;

import java.io.Serial;

/// Serialization Exception.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class SerializationException extends ApplicationException {
    
    @Serial
    private static final long serialVersionUID = -4308536346316915612L;
    
    private static final String DEFAULT_MSG = "Serialize failed. ";
    
    private static final String MSG_FOR_SPECIFIED_CLASS = "Serialize for class [%s] failed. ";
    
    private Class<?> serializedClass;
    
    public SerializationException() {
        super(101);
    }
    
    public SerializationException(Class<?> serializedClass) {
        super(101, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()));
        this.serializedClass = serializedClass;
    }
    
    public SerializationException(Throwable throwable) {
        super(101, DEFAULT_MSG, throwable);
    }
    
    public SerializationException(Class<?> serializedClass, Throwable throwable) {
        super(101, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()),
                throwable);
        this.serializedClass = serializedClass;
    }
    
    public Class<?> getSerializedClass() {
        return serializedClass;
    }
}
