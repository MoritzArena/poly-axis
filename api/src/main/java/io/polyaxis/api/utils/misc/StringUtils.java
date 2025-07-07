package io.polyaxis.api.utils.misc;

import io.polyaxis.api.utils.collection.ArrayUtils;
import io.polyaxis.api.utils.collection.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.StringTokenizer;

/// [StringUtils] support for [String] modifications and detections.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class StringUtils {
    
    private StringUtils() {
    }

    private static final int INDEX_NOT_FOUND = -1;

    private static final String[] EMPTY_STRING_ARRAY = {};

    // region default signs
    public static final String EMPTY = "";

    public static final String BLANK = " ";

    public static final String AT = "@";

    public static final String SHARP = "#";

    public static final String DOT = ".";

    public static final String COLON = ":";

    public static final String UNDERLINE = "_";

    public static final String QUESTION_MARK = "?";
    
    public static final String COMMA = ",";
    
    public static final String LF = "\n";
    
    public static final String TOP_PATH = "..";
    
    public static final String UNIX_FOLDER_SEPARATOR = "/";
    
    public static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    // endregion

    /// Create a string with encoding format as utf8.
    /// 
    /// @param bytes the bytes that make up the string
    /// @return created string
    public static String newStringForUtf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /// Checks if a string is  empty (""), null and whitespace only.
    ///
    /// @param cs the string to check
    /// @return `true` if the string is empty and null and whitespace
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /// Checks if a string is not empty (""), not null and not whitespace only.
    ///
    /// @param str the string to check, may be null
    /// @return `true` if the string is not empty and not null and not whitespace
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /// Checks if a str is not empty ("") or not null.
    ///
    /// @param str the str to check, may be null
    /// @return `true` if the str is not empty or not null
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /// Checks if a str is empty ("") or null.
    ///
    /// @param str the str to check, may be null
    /// @return `true` if the str is empty or null
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /// Returns either the passed in CharSequence, or if the CharSequence is empty or `null`,
    /// the value of `defaultStr`.
    ///
    /// @param str the CharSequence to check, may be null
    /// @param defaultStr the default CharSequence to return if the input is empty ("") or `null`, may be null
    /// @return the passed in CharSequence, or the default
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
    
    /// Returns either the passed in CharSequence, or if the CharSequence is
    /// empty or `null` or whitespace only, the value of `defaultStr`.
    ///
    /// @param str the CharSequence to check, may be null, may be whitespace only
    /// @param defaultStr the default CharSequence to return if the input is empty ("") or `null`, may be null
    /// @return the passed in CharSequence, or the default
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }
    
    /// Returns either the passed in CharSequence, or if the CharSequence is
    /// empty or `null` or whitespace only, the value of `EmptyString`.
    ///
    /// @param str the CharSequence to check, may be null, may be whitespace only
    /// @return the passed in CharSequence, or the empty string
    public static String defaultEmptyIfBlank(String str) {
        return defaultIfBlank(str, EMPTY);
    }
    
    /// Compares two CharSequences, returning `true` if they represent equal sequences of characters.
    ///
    /// @param str1 the first string, may be `null`
    /// @param str2 the second string, may be `null`
    /// @return `true` if the string are equal (case-sensitive), or both `null`
    /// @see Object#equals(Object)
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }
    
    /// Removes control characters (char &lt;= 32) from both ends of this String,
    /// handling `null` by returning `null`.
    ///
    /// @param str the String to be trimmed, may be null
    /// @return the trimmed string, `null` if null String input
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }
    
    /// Substring between two index.
    ///
    /// @param str string
    /// @param open start index to sub
    /// @param close end index to sub
    /// @return substring
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
    
    /// Joins the elements of the provided array into a single String containing the provided list of elements.
    ///
    /// @param collection the Collection of values to join together, may be null
    /// @param separator  the separator string to use
    /// @return the joined String, `null` if null array input
    public static String join(Collection<Object> collection, String separator) {
        if (collection == null) {
            return null;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        Object[] objects = collection.toArray();
        
        for (int i = 0; i < collection.size(); i++) {
            if (objects[i] != null) {
                stringBuilder.append(objects[i]);
                if (i != collection.size() - 1 && separator != null) {
                    stringBuilder.append(separator);
                }
            }
        }
        
        return stringBuilder.toString();
    }
    
    /// Checks if CharSequence contains a search CharSequence irrespective of case, handling `null`.
    /// Case-insensitivity is defined as by [String#equalsIgnoreCase(String)].
    ///
    /// A `null` CharSequence will return `false`.
    ///
    /// @param str the CharSequence to check, may be null
    /// @param searchStr the CharSequence to find, may be null
    /// @return true if the CharSequence contains the search CharSequence irrespective of case
    /// or false if not or `null` string input
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        String str1 = str.toString().toLowerCase();
        String str2 = searchStr.toString().toLowerCase();
        return str1.contains(str2);
    }
    
    /// Checks if CharSequence contains a search CharSequence.
    ///
    /// @param str the CharSequence to check, may be null
    /// @param searchStr the CharSequence to find, may be null
    /// @return true if the CharSequence contains the search CharSequence
    public static boolean contains(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toString().contains(searchStr);
    }
    
    /// Checks if none of the CharSequences are blank ("") or null and whitespace only.
    ///
    /// @param css the CharSequences to check, may be null or empty
    /// @return `true` if none of the CharSequences are blank or null or whitespace only
    public static boolean isNoneBlank(final CharSequence... css) {
        return !isAnyBlank(css);
    }
    
    /// Checks if any one of the CharSequences are blank ("") or null and not whitespace only.
    ///
    /// @param css the CharSequences to check, may be null or empty
    /// @return `true` if any of the CharSequences are blank or null or whitespace only
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return true;
        }
        for (final CharSequence cs : css) {
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }
    
    /// Check if a CharSequence starts with a specified prefix.
    ///
    /// `null`s are handled without exceptions. Two `null`
    /// references are considered to be equal. The comparison is case-sensitive.
    ///
    /// @param str the CharSequence to check, may be null
    /// @param prefix the prefix to find, may be null
    /// @return `true` if the CharSequence starts with the prefix, case-sensitive, or both `null`
    /// @see String#startsWith(String)
    public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, false);
    }
    
    /// Case-insensitive check if a CharSequence starts with a specified prefix.
    ///
    /// `null`s are handled without exceptions. Two `null`
    /// references are considered to be equal. The comparison is case-insensitive.
    ///
    /// @param str the CharSequence to check, may be null
    /// @param prefix the prefix to find, may be null
    /// @return `true` if the CharSequence starts with the prefix, case-insensitive, or both `null`
    /// @see String#startsWith(String)
    public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, true);
    }
    
    /// Deletes all whitespaces from a String as defined by [Character#isWhitespace(char)].
    ///
    /// @param str the String to delete whitespace from, may be null
    /// @return the String without whitespaces, `null` if null String input
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }
    
    /// Compares two CharSequences, returning `true` if they represent
    /// equal sequences of characters, ignoring case.
    ///
    /// @param str1 the first string, may be null
    /// @param str2 the second string, may be null
    /// @return `true` if the string are equal, case-insensitive, or both `null`
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }
    
    /// Splits the provided text into an array with a maximum length, separators specified. If separatorChars is empty,
    /// divide by blank.
    ///
    /// @param str the String to parse, may be null
    /// @return an array of parsed Strings
    public static String[] split(final String str, String separatorChars) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new String[0];
        }
        if (separatorChars == null) {
            separatorChars = " +";
        }
        return str.split(separatorChars);
    }
    
    /// Tokenize the given `String` into a `String` array via a [StringTokenizer].
    ///
    /// The given `delimiters` string can consist of any number of delimiter characters. Each
    /// of those characters can be used to separate tokens. A delimiter is always a single character.
    ///
    /// @param str the `String` to tokenize (potentially `null` or empty)
    /// @param delimiters the delimiter characters, assembled as a `String` (each of the characters is
    /// individually considered as a delimiter)
    /// @param trimTokens trim the tokens via [String#trim()]
    /// @param ignoreEmptyTokens omit empty tokens from the result array (only applies to tokens that are empty after
    /// trimming; StringTokenizer will not consider subsequent delimiters as token in the first place).
    /// @return an array of the tokens
    /// @see StringTokenizer
    /// @see String#trim()
    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
            boolean ignoreEmptyTokens) {
        
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || !token.isEmpty()) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }
    
    /// Copy the given [Collection] into a `String` array.
    ///
    /// The `Collection` must contain `String` elements only.
    ///
    /// @param collection the `Collection` to copy (potentially `null` or empty)
    /// @return the resulting `String` array
    public static String[] toStringArray(Collection<String> collection) {
        return (!CollectionUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }
    
    /// Check whether the given `String` contains actual <em>text</em>.
    ///
    /// More specifically, this method returns `true` if the `String` is not `null`,
    /// its length is greater than 0, and it contains at least one non-whitespace character.
    ///
    /// @param str the `String` to check (maybe `null`)
    /// @return `true` if the `String` is not `null`, its length is greater than 0, and it does not
    /// contain whitespace only
    /// @see Character#isWhitespace
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }
    
    /// Normalize the path by suppressing sequences like `path/..` and inner simple dots.
    ///
    /// The result is convenient for path comparison. For other uses,
    /// notice that Windows separators ("\") are replaced by simple slashes.
    ///
    /// **NOTE** that `cleanPath` should not be depended upon in a security context. Other
    /// mechanisms should be used to prevent path-traversal issues.
    ///
    /// @param path the original path
    /// @return the normalized path
    public static String cleanPath(String path) {
        if (!hasLength(path)) {
            return path;
        }
        
        String normalizedPath = replace(path, WINDOWS_FOLDER_SEPARATOR, UNIX_FOLDER_SEPARATOR);
        String pathToUse = normalizedPath;
        
        // Shortcut if there is no work to do
        if (!pathToUse.contains(DOT)) {
            return pathToUse;
        }
        
        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(':');
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains(UNIX_FOLDER_SEPARATOR)) {
                prefix = "";
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith(UNIX_FOLDER_SEPARATOR)) {
            prefix = prefix + UNIX_FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }
        
        String[] pathArray = delimitedListToStringArray(pathToUse, UNIX_FOLDER_SEPARATOR);
        // we never require more elements than pathArray and in the common case the same number
        Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
        int tops = 0;
        
        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.addFirst(element);
                }
            }
        }
        
        // All path elements stayed the same - shortcut
        if (pathArray.length == pathElements.size()) {
            return normalizedPath;
        }
        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.addFirst(TOP_PATH);
        }
        // If nothing else left, at least explicitly point to current path.
        if (pathElements.size() == 1 && pathElements.getLast().isEmpty() && !prefix.endsWith(UNIX_FOLDER_SEPARATOR)) {
            pathElements.addFirst(DOT);
        }
        
        final String joined = collectionToDelimitedString(pathElements, UNIX_FOLDER_SEPARATOR);
        // avoid string concatenation with empty prefix
        return prefix.isEmpty() ? joined : prefix + joined;
    }
    
    /// Convert a `Collection` into a delimited `String` (e.g. CSV).
    ///
    /// Useful for `toString()` implementations.
    ///
    /// @param coll  the `Collection` to convert (potentially `null` or empty)
    /// @param delim the delimiter to use (typically a ",")
    /// @return the delimited `String`
    public static String collectionToDelimitedString(Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }
    
    /// Convert a [Collection] to a delimited `String` (e.g. CSV).
    ///
    /// Useful for `toString()` implementations.
    ///
    /// @param coll the `Collection` to convert (potentially `null` or empty)
    /// @param delim the delimiter to use (typically a ",")
    /// @param prefix the `String` to start each element with
    /// @param suffix the `String` to end each element with
    /// @return the delimited `String`
    public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
        
        if (CollectionUtils.isEmpty(coll)) {
            return "";
        }
        
        int totalLength = coll.size() * (prefix.length() + suffix.length()) + (coll.size() - 1) * delim.length();
        for (Object element : coll) {
            totalLength += String.valueOf(element).length();
        }
        
        StringBuilder sb = new StringBuilder(totalLength);
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }
    
    /// Check that the given `String` is neither `null` nor of length 0.
    ///
    /// Note: this method returns `true` for a `String` that purely consists of whitespace.
    ///
    /// @param str the `String` to check (maybe `null`)
    /// @return `true` if the `String` is not `null` and has length
    /// @see #hasText(String)
    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }
    
    /// Take a `String` that is a delimited list and convert it into a `String` array.
    ///
    /// A single `delimiter` may consist of more than one character, but it will still be considered
    /// as a single delimiter string, rather than as a bunch of potential delimiter characters, in
    /// contrast to [#tokenizeToStringArray].
    ///
    /// @param str the input `String` (potentially `null` or empty)
    /// @param delimiter the delimiter between elements (this is a single delimiter, rather than a bunch individual
    /// delimiter characters)
    /// @return an array of the tokens in the list
    /// @see #tokenizeToStringArray
    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }
    
    /// Take a `String` that is a delimited list and convert it into a `String` array.
    ///
    /// A single `delimiter` may consist of more than one character, but it will still be considered
    /// as a single delimiter string, rather than as a bunch of potential delimiter characters,
    /// in contrast to [#tokenizeToStringArray].
    ///
    /// @param str the input `String` (potentially `null` or empty)
    /// @param delimiter the delimiter between elements (this is a single delimiter, rather than a bunch individual
    /// delimiter characters)
    /// @param charsToDelete a set of characters to delete; useful for deleting unwanted line breaks: e.g. "\r\n\f" will
    /// delete all new lines and line feeds in a `String`
    /// @return an array of the tokens in the list
    /// @see #tokenizeToStringArray
    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        if (delimiter == null) {
            return new String[] {str};
        }
        
        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (!str.isEmpty() && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }
    
    /// Delete any character in a given `String`.
    ///
    /// @param inString the original `String`
    /// @param charsToDelete a set of characters to delete. E.g. "az\n" will delete 'a's, 'z's and new lines.
    /// @return the resulting `String`
    public static String deleteAny(String inString, String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        
        int lastCharIndex = 0;
        char[] result = new char[inString.length()];
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                result[lastCharIndex++] = c;
            }
        }
        if (lastCharIndex == inString.length()) {
            return inString;
        }
        return new String(result, 0, lastCharIndex);
    }
    
    /// Replace all occurrences of a substring within a string with another string.
    ///
    /// @param inString `String` to examine
    /// @param oldPattern `String` to replace
    /// @param newPattern `String` to insert
    /// @return a `String` with the replacements
    public static String replace(String inString, String oldPattern, String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            // no occurrence -> can return input as-is
            return inString;
        }
        
        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);
        
        int pos = 0;
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString, pos, index);
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        
        // append any characters to the right of a match
        sb.append(inString, pos, inString.length());
        return sb.toString();
    }
    
    /// Apply the given relative path to the given Java resource path, assuming standard Java folder separation (i.e. "/"
    /// separators).
    ///
    /// @param path the path to start from (usually a full file path)
    /// @param relativePath the relative path to apply (relative to the full file path above)
    /// @return the full file path that results from applying the relative path
    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(UNIX_FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(UNIX_FOLDER_SEPARATOR)) {
                newPath += UNIX_FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }
    
    /// Extract the filename from the given Java resource path, e.g. `"myPath/myFile.txt" &rarr; "myFile.txt"`.
    ///
    /// @param path the file path (maybe `null`)
    /// @return the extracted filename, or `null` if none
    public static String getFilename(String path) {
        if (path == null) {
            return null;
        }
        
        int separatorIndex = path.lastIndexOf(UNIX_FOLDER_SEPARATOR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }
    
    /// Capitalize a `String`, changing the first letter to upper case as per [Character#toUpperCase(char)].
    /// No other letters are changed.
    ///
    /// @param str the `String` to capitalize
    /// @return the capitalized `String`
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str);
    }

    /// Remove `remove` from the start of `str`.
    ///
    /// @param str raw string
    /// @param remove target string which to be removed from the start
    /// @return head removed string
    public static String removeStart(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /// If str is in expected string list.
    ///
    /// @param str input
    /// @param expected expected string list
    /// @return if input is in expected
    public static boolean exactMatch(final String str, final String... expected) {
        if (isBlank(str)) {
            return false;
        }
        return CollectionUtils.contains(Arrays.stream(expected).toList(), str);
    }

    /// If str is in expected string list.
    ///
    /// @param strs inputs
    /// @param expected expected string list
    /// @return if input is in expected
    public static boolean exactMatch(final List<String> strs, final String... expected) {
        if (Objects.isNull(strs) || strs.isEmpty()) {
            return false;
        }
        for (String str : strs) {
            if (!exactMatch(str, expected)) {
                return false;
            }
        }
        return true;
    }

    /// Joint all given strings with `@` as separator.
    ///
    /// @param str each string part
    /// @return joined string
    public static String concatAt(final String... str) {
        final StringJoiner joiner = new StringJoiner(AT);
        for (final String part : str) {
            joiner.add(part);
        }
        return joiner.toString();
    }

    // region private methods
    /// Check if a CharSequence starts with a specified prefix (optionally case-insensitive).
    ///
    /// @param str the CharSequence to check, may be null
    /// @param prefix the prefix to find, may be null
    /// @param ignoreCase indicates whether the compare should ignore case (case-insensitive) or not.
    /// @return `true` if the CharSequence starts with the prefix or both `null`
    /// @see String#startsWith(String)
    private static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        if (ignoreCase) {
            String lowerCaseStr = str.toString().toLowerCase();
            String lowerCasePrefix = prefix.toString().toLowerCase();
            return lowerCaseStr.startsWith(lowerCasePrefix);
        } else {
            return str.toString().startsWith(prefix.toString());
        }
    }

    private static String changeFirstCharacterCase(String str) {
        if (!hasLength(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        updatedChar = Character.toUpperCase(baseChar);
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    // endregion
}
