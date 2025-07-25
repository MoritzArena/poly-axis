package io.polyaxis.api.utils.misc;

import java.util.Arrays;

/// Object Utils.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public class ObjectUtils {

    /// Determine if the given objects are equal, returning `true` if
    /// both are `null` or `false` if only one is `null`.
    ///
    /// Compares arrays with `Arrays.equals`, performing an equality
    /// check based on the array elements rather than the array reference.
    ///
    /// @param o1 first Object to compare
    /// @param o2 second Object to compare
    /// @return whether the given objects are equal
    /// @see Object#equals(Object)
    /// @see Arrays#equals
    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /// Compare the given arrays with `Arrays.equals`, performing an equality
    /// check based on the array elements rather than the array reference.
    ///
    /// @param o1 first array to compare
    /// @param o2 second array to compare
    /// @return whether the given objects are equal
    /// @see #nullSafeEquals(Object, Object)
    /// @see Arrays#equals
    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }
}
