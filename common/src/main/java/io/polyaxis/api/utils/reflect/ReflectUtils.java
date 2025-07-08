package io.polyaxis.api.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/// Utils for reflect.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class ReflectUtils {

    private ReflectUtils() {
    }

    /// get filed value of obj.
    ///
    /// @param obj       obj.
    /// @param fieldName file name to get value.
    /// @return field value.
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /// get filed value of obj.
    ///
    /// @param obj       obj.
    /// @param fieldName file name to get value.
    /// @return field value.
    public static Object getFieldValue(Object obj, String fieldName, Object defaultValue) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /// Get the field represented by the supplied [field object][Field] on the specified [targetobject][Object].
    /// In accordance with [#get(Object)] semantics, the returned value is automatically wrapped if
    /// the underlying field has a primitive type.
    ///
    /// Thrown exceptions are handled via a call to [#handleReflectionException(Exception)].
    ///
    /// @param field  the field to get
    /// @param target the target object from which to get the field (or `null` for a static field)
    /// @return the field's current value
    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /// Handle the given reflection exception.
    ///
    /// Should only be called if no checked exception is expected to be thrown
    /// by a target method, or if an error occurs while accessing a method or field.
    ///
    /// Throws the underlying RuntimeException or Error in case of an
    /// InvocationTargetException with such a root cause. Throws an IllegalStateException with an appropriate message or
    /// UndeclaredThrowableException otherwise.
    ///
    /// @param ex the reflection exception to handle
    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /// Handle the given invocation target exception. Should only be called if no checked exception is expected to be
    /// thrown by the target method.
    ///
    /// Throws the underlying RuntimeException or Error in case of such a root
    /// cause. Throws an UndeclaredThrowableException otherwise.
    ///
    /// @param ex the invocation target exception to handle
    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /// Rethrow the given [exception][Throwable], which is presumably the
    /// _target exception_ of an [InvocationTargetException].
    /// Should only be called if no checked exception is expected to be thrown by the target method.
    ///
    /// Rethrows the underlying exception cast to a [RuntimeException] or
    /// [Error] if appropriate; otherwise, throws an [UndeclaredThrowableException].
    ///
    /// @param ex the exception to rethrow
    /// @throws RuntimeException the rethrown exception
    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /// Invoke the specified [Method] against the supplied target object with the supplied arguments. The target
    /// object can be `null` when invoking a static [Method].
    ///
    /// Thrown exceptions are handled via a call to [#handleReflectionException].
    ///
    /// @param method the method to invoke
    /// @param target the target object to invoke the method on
    /// @param args   the invocation arguments (maybe `null`)
    /// @return the invocation result, if any
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }
}
