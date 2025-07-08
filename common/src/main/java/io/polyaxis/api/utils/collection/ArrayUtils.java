package io.polyaxis.api.utils.collection;

import java.util.Arrays;

/// Array utils.
/// 
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class ArrayUtils {

    private ArrayUtils() {
    }
    
    /// Checks if an array of Objects is empty or `null`.
    /// 
    /// @param array  the array to test
    /// @return `true` if the array is empty or `null`
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }
    
    /// Checks if the object is in the given array.
    /// 
    /// The method returns `false` if a `null` array is passed in.
    /// 
    /// @param array the array to search through
    /// @param objectToFind  the object to find
    /// @return `true` if the array contains the object
    public static boolean contains(final Object[] array, final Object objectToFind) {
        if (array == null) {
            return false;
        }
        return Arrays.asList(array).contains(objectToFind);
    }
    
}
