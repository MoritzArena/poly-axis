package io.polyaxis.api.utils.io.net;

import java.util.regex.Pattern;

/// ipv4 ipv6 check util.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class IPValidator {
    
    private IPValidator() {
    }
    
    private static final String PERCENT = "%";
    
    private static final String DOUBLE_COLON = "::";
    
    private static final String DOUBLE_COLON_FFFF = "::ffff:";
    
    private static final String FE80 = "fe80:";
    
    private static final int ZERO = 0;
    
    private static final int SEVEN = 7;
    
    private static final int FIVE = 5;
    
    private static final Pattern IPV4_PATTERN = Pattern
            .compile("^" + "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)" + "(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}" + "$");
    
    private static final Pattern IPV6_STD_PATTERN = Pattern
            .compile("^" + "(?:[0-9a-fA-F]{1,4}:){7}" + "[0-9a-fA-F]{1,4}" + "$");
    
    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern
            .compile("^" + "(" + "(?:[0-9A-Fa-f]{1,4}" + "(?::[0-9A-Fa-f]{1,4})*)?" + ")" + "::"
                    
                    + "(" + "(?:[0-9A-Fa-f]{1,4}" + "(?::[0-9A-Fa-f]{1,4})*)?" + ")" + "$");
    
    private static final Pattern IPV6_MIXED_COMPRESSED_REGEX = Pattern.compile(
            "^" + "(" + "(?:[0-9A-Fa-f]{1,4}" + "(?::[0-9A-Fa-f]{1,4})*)?" + ")" + "::" + "(" + "(?:[0-9A-Fa-f]{1,4}:"
                    + "(?:[0-9A-Fa-f]{1,4}:)*)?" + ")" + "$");
    
    private static final Pattern IPV6_MIXED_UNCOMPRESSED_REGEX = Pattern
            .compile("^" + "(?:[0-9a-fA-F]{1,4}:){6}" + "$");
    
    /// Check if <code>input</code> is a valid IPv4 address. The format is 'xxx.xxx.xxx.xxx'. Four blocks of integer
    /// numbers ranging from 0 to 255 are required. Letters are not allowed.
    ///
    /// @param input ip-address to check
    /// @return true if <code>input</code> is in correct IPv4 notation.
    public static boolean isIPv4Address(final String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }
    
    /// Check if the given address is a valid IPv6 address in the standard format The format is
    /// 'xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx'. Eight blocks of hexadecimal digits are required.
    ///
    /// @param input ip-address to check
    /// @return true if <code>input</code> is in correct IPv6 notation.
    public static boolean isIPv6StdAddress(final String input) {
        return IPV6_STD_PATTERN.matcher(input).matches();
    }
    
    /// Check if the given address is a valid IPv6 address in the hex-compressed notation The format is
    /// 'xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx'. If all digits in a block are '0' the block can be left empty.
    ///
    /// @param input ip-address to check
    /// @return true if <code>input</code> is in correct IPv6 (hex-compressed) notation.
    public static boolean isIPv6HexCompressedAddress(final String input) {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
    }
    
    /// Check if <code>input</code> is a IPv6 address. Possible notations for valid IPv6 are: - Standard IPv6 address -
    /// Hex-compressed IPv6 address - Link-local IPv6 address - IPv4-mapped-to-IPV6 address - IPv6 mixed address
    ///
    /// @param input ip-address to check
    /// @return true if <code>input</code> is in correct IPv6 notation.
    public static boolean isIPv6Address(final String input) {
        return isIPv6StdAddress(input)
                || isIPv6HexCompressedAddress(input)
                || isLinkLocalIPv6WithZoneIndex(input)
                || isIPv6IPv4MappedAddress(input)
                || isIPv6MixedAddress(input);
    }
    
    /// Check if the given address is a valid IPv6 address in the mixed-standard or mixed-compressed notation. IPV6 Mixed
    /// mode consists of two parts, the first 96 bits (up to 6 blocks of 4 hex digits) are IPv6 the IPV6 part can be
    /// either compressed or uncompressed the second block is a full IPv4 address e.g. '0:0:0:0:0:0:172.12.55.18'
    ///
    /// @param input ip-address to check
    /// @return true if <code>input</code> is in correct IPv6 (mixed-standard or mixed-compressed) notation.
    public static boolean isIPv6MixedAddress(final String input) {
        int splitIndex = input.lastIndexOf(':');
        
        if (splitIndex == -1) {
            return false;
        }
        
        //the last part is an ipv4 address
        boolean ipv4PartValid = isIPv4Address(input.substring(splitIndex + 1));
        
        String ipV6Part = input.substring(ZERO, splitIndex + 1);
        if (DOUBLE_COLON.equals(ipV6Part)) {
            return ipv4PartValid;
        }
        
        boolean ipV6UncompressedDetected = IPV6_MIXED_UNCOMPRESSED_REGEX.matcher(ipV6Part).matches();
        boolean ipV6CompressedDetected = IPV6_MIXED_COMPRESSED_REGEX.matcher(ipV6Part).matches();
        
        return ipv4PartValid && (ipV6UncompressedDetected || ipV6CompressedDetected);
    }
    
    /// Check if <code>input</code> is an IPv4 address mapped into a IPv6 address. These are starting with "::ffff:"
    /// followed by the IPv4 address in a dot-seperated notation. The format is '::ffff:d.d.d.d'
    ///
    /// @param input ip-address to check
    /// @return true if <code>input</code> is in correct IPv6 notation containing an IPv4 address
    public static boolean isIPv6IPv4MappedAddress(final String input) {
        if (input.length() > SEVEN && input.substring(ZERO, SEVEN).equalsIgnoreCase(DOUBLE_COLON_FFFF)) {
            String lowerPart = input.substring(SEVEN);
            return isIPv4Address(lowerPart);
        }
        return false;
    }
    
    /// Check if <code>input</code> is a link local IPv6 address starting with "fe80:" and containing a zone index with
    /// "%xxx". The zone index will not be checked.
    ///
    /// @param input ip-address to check
    /// @return true if address part of <code>input</code> is in correct IPv6 notation.
    public static boolean isLinkLocalIPv6WithZoneIndex(String input) {
        if (input.length() > FIVE && input.substring(ZERO, FIVE).equalsIgnoreCase(FE80)) {
            int lastIndex = input.lastIndexOf(PERCENT);
            if (lastIndex > ZERO && lastIndex < (input.length() - 1)) {
                String ipPart = input.substring(ZERO, lastIndex);
                return isIPv6StdAddress(ipPart) || isIPv6HexCompressedAddress(ipPart);
            }
        }
        return false;
    }
}
