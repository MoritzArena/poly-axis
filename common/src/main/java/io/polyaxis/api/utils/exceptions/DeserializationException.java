package io.polyaxis.api.utils.exceptions;

import java.io.Serial;
import java.lang.reflect.Type;

/// Deserialization Exception.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class DeserializationException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = -2742350751684273728L;

    private static final String DEFAULT_MSG = "Deserialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "Deserialize for class [%s] failed. ";

    private static final String ERROR_MSG_FOR_SPECIFIED_CLASS = "Deserialize for class [%s] failed, cause error[%s]. ";

    private Class<?> targetClass;

    public DeserializationException() {
        super(100);
    }

    public DeserializationException(Class<?> targetClass) {
        super(100, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public DeserializationException(Type targetType) {
        super(100, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()));
    }

    public DeserializationException(Throwable throwable) {
        super(100, DEFAULT_MSG, throwable);
    }

    public DeserializationException(Class<?> targetClass, Throwable throwable) {
        super(100, String.format(ERROR_MSG_FOR_SPECIFIED_CLASS, targetClass.getName(), throwable.getMessage()),
                throwable);
        this.targetClass = targetClass;
    }

    public DeserializationException(Type targetType, Throwable throwable) {
        super(100, String.format(ERROR_MSG_FOR_SPECIFIED_CLASS, targetType.toString(), throwable.getMessage()),
                throwable);
    }
    
    public Class<?> getTargetClass() {
        return targetClass;
    }
}
