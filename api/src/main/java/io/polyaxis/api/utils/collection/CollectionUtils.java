package io.polyaxis.api.utils.collection;

import io.polyaxis.api.utils.misc.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.StreamSupport;

/// Copy from org.apache.commons.collections.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class CollectionUtils {

    private CollectionUtils() {
    }
    
    /// Returns the `index`-th value in `object`, throwing [IndexOutOfBoundsException] if there is no such element or
    /// `IllegalArgumentException` if `object` is not an instance of one of the supported types.
    /// 
    /// The supported types, and associated semantics are:
    /// - Map -- the value returned is the [Map.Entry] in position `index` in the map's `entrySet` iterator,
    /// if there is such an entry.
    /// - List -- this method is equivalent to the list's get method.
    /// - Array -- the `index`-th array entry is returned, if there is such an entry; otherwise an 
    /// [IndexOutOfBoundsException] is thrown.
    /// - Collection -- the value returned is the `index`-th object returned by the collection's default iterator, 
    /// if there is such an element.
    /// - Iterator or Enumeration -- the value returned is the `index`-th object in the Iterator/Enumeration, if there
    /// is such an element.  The Iterator/Enumeration is advanced to `index` (or to the end, if `index` exceeds the
    /// number of entries) as a side effect of this method.
    /// 
    /// @param object the object to get a value from
    /// @param index  the index to get
    /// @return the object at the specified index
    /// @throws IndexOutOfBoundsException if the index is invalid
    /// @throws IllegalArgumentException  if the object type is invalid
    public static Object get(Object object, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
        switch (object) {
            case final Map<?, ?> map -> {
                Iterator<?> iterator = map.entrySet().iterator();
                return get(iterator, index);
            }
            case final List<?> list -> {
                return list.get(index);
            }
            case final Object[] objects -> {
                return objects[index];
            }
            case final Iterator<?> it -> {
                while (it.hasNext()) {
                    index--;
                    if (index == -1) {
                        return it.next();
                    } else {
                        it.next();
                    }
                }
                throw new IndexOutOfBoundsException("Entry does not exist: " + index);
            }
            case Collection<?> collection -> {
                final Iterator<?> iterator = collection.iterator();
                return get(iterator, index);
            }
            case final Enumeration<?> it -> {
                while (it.hasMoreElements()) {
                    index--;
                    if (index == -1) {
                        return it.nextElement();
                    } else {
                        it.nextElement();
                    }
                }
                throw new IndexOutOfBoundsException("Entry does not exist: " + index);
            }
            case null -> throw new IllegalArgumentException("Unsupported object type: null");
            default -> {
                try {
                    return Array.get(object, index);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
                }
            }
        }
    }
    
    /// Gets the size of the collection/iterator specified.
    /// 
    /// This method can handle objects as follows:
    /// - Collection - the collection size
    /// - Map - the map size
    /// - Array - the array size
    /// - Iterator - the number of elements remaining in the iterator
    /// - Enumeration - the number of elements remaining in the enumeration
    /// 
    /// @param object the object to get the size of
    /// @return the size of the specified collection
    /// @throws IllegalArgumentException thrown if object is not recognised or null
    /// @since Commons Collections 3.1
    public static int size(Object object) {
        int total = 0;
        switch (object) {
            case final Map<?, ?> map -> total = map.size();
            case final Collection<?> collection -> total = collection.size();
            case final Object[] objects -> total = objects.length;
            case final Iterator<?> it -> {
                while (it.hasNext()) {
                    total++;
                    it.next();
                }
            }
            case final Enumeration<?> it -> {
                while (it.hasMoreElements()) {
                    total++;
                    it.nextElement();
                }
            }
            case null -> throw new IllegalArgumentException("Unsupported object type: null");
            default -> {
                try {
                    total = Array.getLength(object);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
                }
            }
        }
        return total;
    }
    
    /// Judge whether object is empty.
    /// 
    /// @param object object
    /// @return true if object is empty, otherwise false
    /// @throws IllegalArgumentException if object has no length or size
    public static boolean sizeIsEmpty(Object object) {
        switch (object) {
            case final Collection<?> collection -> {
                return collection.isEmpty();
            }
            case final Map<?, ?> map -> {
                return map.isEmpty();
            }
            case final Object[] objects -> {
                return objects.length == 0;
            }
            case final Iterator<?> iterator -> {
                return !iterator.hasNext();
            }
            case final Enumeration<?> enumeration -> {
                return !enumeration.hasMoreElements();
            }
            case null -> throw new IllegalArgumentException("Unsupported object type: null");
            default -> {
                try {
                    return Array.getLength(object) == 0;
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
                }
            }
        }
    }
    
    /// Whether contain item in collection.
    /// @param coll collection
    /// @param target target value
    /// @param <T> General Type
    /// @return true if contains, otherwise false
    public static <T> boolean contains(Collection<T> coll, T target) {
        if (isEmpty(coll)) {
            return false;
        }
        return coll.contains(target);
    }
    
    /// Null-safe check if the specified collection is empty.
    /// 
    /// Null returns true.
    /// 
    /// @param coll the collection to check, may be null
    /// @return true if empty or null
    /// @since Commons Collections 3.2
    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }
    
    /// Null-safe check if the specified collection is not empty.
    /// 
    /// Null returns false.
    /// 
    /// @param coll the collection to check, may be null
    /// @return true if non-null and non-empty
    /// @since Commons Collections 3.2
    public static boolean isNotEmpty(Collection<?> coll) {
        return !CollectionUtils.isEmpty(coll);
    }
    
    /// Returns the value to which the specified index, or `defaultValue` if this collection contains no value for
    /// the index.
    /// 
    /// @param obj the object to get a value from
    /// @param index the index to get
    /// @param defaultValue default value
    /// @param <T> General Type
    /// @return the value to which the specified index, or `defaultValue` if this collection contains no value for
    /// the index.
    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(Object obj, int index, T defaultValue) {
        try {
            return (T) get(obj, index);
        } catch (IndexOutOfBoundsException e) {
            return defaultValue;
        }
    }
    
    /// return an arraylist containing all input parameters.
    ///
    /// @param elements element array
    /// @return arraylist containing all input parameters
    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Expected an array of elements (or empty array) but received a null.");
        }
        ArrayList<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
    
    /// Return a set containing all input parameters.
    ///
    /// @param elements elements element array
    /// @return set containing all input parameters
    @SafeVarargs
    public static <T> Set<T> set(T... elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Expected an array of elements (or empty array) but received a null.");
        } else {
            return new LinkedHashSet<>(Arrays.asList(elements));
        }
    }
    
    /// Return the first element, if the iterator contains multiple elements, will throw [IllegalArgumentException].
    ///
    /// @throws NoSuchElementException if the iterator is empty
    /// @throws IllegalArgumentException if the iterator contains multiple elements. The state of the iterator is
    /// unspecified.
    public static <T> T getOnlyElement(Iterable<T> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("iterable cannot be null.");
        }
        Iterator<T> iterator = iterable.iterator();
        T first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }
        throw new IllegalArgumentException(buildExceptionMessage(iterator, first));
    }

    /// Convert a [Iterable] to a [List].
    ///
    /// @param iterable the iterable instance
    /// @param <T> element type
    /// @return return the converted result
    public static <T> List<T> convertIterableToList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }
    
    /// Check list is equal.
    ///
    /// @param firstList first list.
    /// @param secondList second list.
    public static boolean isListEqual(List<String> firstList, List<String> secondList) {
        if (firstList == null && secondList == null) {
            return true;
        }
        if (firstList == null || secondList == null) {
            return false;
        }
        
        if (firstList == secondList) {
            return true;
        }
        
        if (firstList.size() != secondList.size()) {
            return false;
        }
        
        boolean flag1 = new HashSet<>(firstList).containsAll(secondList);
        boolean flag2 = new HashSet<>(secondList).containsAll(firstList);
        return flag1 && flag2;
    }

    /// Return `true` if the supplied Map is `null` or empty. Otherwise, return `false`.
    ///
    /// @param map the Map to check
    /// @return whether the given Map is empty
    public static boolean isMapEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    // region private methods
    private static <T> String buildExceptionMessage(Iterator<T> iterator, T first) {
        StringBuilder msg = new StringBuilder();
        msg.append("expected one element but was: <");
        msg.append(first);
        for (int i = 0; i < 4 && iterator.hasNext(); i++) {
            msg.append(", ");
            msg.append(iterator.next());
        }
        if (iterator.hasNext()) {
            msg.append(", ...");
        }
        msg.append('>');
        return msg.toString();
    }
    // endregion
}
