package io.polyaxis.api.utils.reflect.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/// Copy from ASM, with fewer modifications
/// A parser to make a  visit a ClassFile structure, as defined in the Java
/// Virtual Machine Specification (JVMS). This class parses the ClassFile content and calls the
/// appropriate visit methods of a given ClassVisitor for each field, method and bytecode
/// instruction encountered.
///
/// @author Eric Bruneton
/// @author Eugene Kuleshov
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html">JVMS 4</a>
///
/// @since 1.0
/// @date 2025/03/27
public class ClassReader {

    public static final int V25 = 0 << 16 | 69;

    /// The maximum size of array to allocate.
    private static final int MAX_BUFFER_SIZE = 1024 * 1024;

    /// The size of the temporary byte array used to read class input streams chunk by chunk.
    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;

    /// A byte array containing the JVMS ClassFile structure to be parsed.
    ///
    /// @deprecated Use [#readByte(int)] and the other read methods instead. This field will
    /// eventually be deleted.
    @Deprecated
    // DontCheck(MemberName): can't be renamed (for backward binary compatibility).
    public final byte[] b;

    /// The offset in bytes of the ClassFile's access_flags field.
    public final int header;

    /// A byte array containing the JVMS ClassFile structure to be parsed. _The content of this array
    /// must not be modified. This field is intended for  Attribute subclasses, and is normally
    /// not needed by class visitors._
    ///
    /// NOTE: the ClassFile structure can start at any offset within this array, i.e. it does not
    /// necessarily start at offset 0. Use [#getItem] and [#header] to get correct
    /// ClassFile element offsets within this byte array.
    final byte[] classFileBuffer;

    /// The offset in bytes, in [#classFileBuffer], of each cp_info entry of the ClassFile's
    /// constant_pool array, _plus one_. In other words, the offset of constant pool entry i is
    /// given by cpInfoOffsets\[i\] - 1, i.e. its cp_info's tag field is given by b\[cpInfoOffsets\[i\] -
    /// 1\].
    private final int[] cpInfoOffsets;

    /// The String objects corresponding to the CONSTANT_Utf8 constant pool items. This cache avoids
    /// multiple parsing of a given CONSTANT_Utf8 constant pool item.
    private final String[] constantUtf8Values;

    /// A conservative estimate of the maximum length of the strings contained in the constant pool of
    /// the class.
    private final int maxStringLength;

    // -----------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------

    /// Constructs a new [ClassReader] object.
    ///
    /// @param classFile the JVMS ClassFile structure to be read.
    public ClassReader(final byte[] classFile) {
        this(classFile, 0, classFile.length);
    }

    /// Constructs a new [ClassReader] object.
    ///
    /// @param classFileBuffer a byte array containing the JVMS ClassFile structure to be read.
    /// @param classFileOffset the offset in byteBuffer of the first byte of the ClassFile to be read.
    /// @param classFileLength the length in bytes of the ClassFile to be read.
    public ClassReader(
            final byte[] classFileBuffer,
            final int classFileOffset,
            final int classFileLength) { // NOPMD(UnusedFormalParameter) used for backward compatibility.
        this(classFileBuffer, classFileOffset, /* checkClassVersion = */ true);
    }

    /// Constructs a new [ClassReader] object. _This internal constructor must not be exposed
    /// as a public API_.
    ///
    /// @param classFileBuffer   a byte array containing the JVMS ClassFile structure to be read.
    /// @param classFileOffset   the offset in byteBuffer of the first byte of the ClassFile to be read.
    /// @param checkClassVersion whether to check the class version or not.
    ClassReader(
            final byte[] classFileBuffer,
            final int classFileOffset,
            final boolean checkClassVersion
    ) {
        this.classFileBuffer = classFileBuffer;
        this.b = classFileBuffer;
        // Check the class' major_version. This field is after the magic and minor_version fields, which
        // use 4 and 2 bytes respectively.
        if (checkClassVersion && readShort(classFileOffset + 6) > V25) {
            throw new IllegalArgumentException(
                    "Unsupported class file major version " + readShort(classFileOffset + 6));
        }
        // Create the constant pool arrays. The constant_pool_count field is after the magic,
        // minor_version and major_version fields, which use 4, 2 and 2 bytes respectively.
        int constantPoolCount = readUnsignedShort(classFileOffset + 8);
        cpInfoOffsets = new int[constantPoolCount];
        constantUtf8Values = new String[constantPoolCount];
        // Compute the offset of each constant pool entry, as well as a conservative estimate of the
        // maximum length of the constant pool strings. The first constant pool entry is after the
        // magic, minor_version, major_version and constant_pool_count fields, which use 4, 2, 2 and 2
        // bytes respectively.
        int currentCpInfoIndex = 1;
        int currentCpInfoOffset = classFileOffset + 10;
        int currentMaxStringLength = 0;
        boolean hasBootstrapMethods = false;
        boolean hasConstantDynamic = false;
        // The offset of the other entries depend on the total size of all the previous entries.
        while (currentCpInfoIndex < constantPoolCount) {
            cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
            int cpInfoSize;
            switch (classFileBuffer[currentCpInfoOffset]) {
                case Symbol.CONSTANT_FIELDREF_TAG:
                case Symbol.CONSTANT_METHODREF_TAG:
                case Symbol.CONSTANT_INTERFACE_METHODREF_TAG:
                case Symbol.CONSTANT_INTEGER_TAG:
                case Symbol.CONSTANT_FLOAT_TAG:
                case Symbol.CONSTANT_NAME_AND_TYPE_TAG:
                    cpInfoSize = 5;
                    break;
                case Symbol.CONSTANT_DYNAMIC_TAG:
                    cpInfoSize = 5;
                    hasBootstrapMethods = true;
                    hasConstantDynamic = true;
                    break;
                case Symbol.CONSTANT_INVOKE_DYNAMIC_TAG:
                    cpInfoSize = 5;
                    hasBootstrapMethods = true;
                    break;
                case Symbol.CONSTANT_LONG_TAG:
                case Symbol.CONSTANT_DOUBLE_TAG:
                    cpInfoSize = 9;
                    currentCpInfoIndex++;
                    break;
                case Symbol.CONSTANT_UTF8_TAG:
                    cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
                    if (cpInfoSize > currentMaxStringLength) {
                        // The size in bytes of this CONSTANT_Utf8 structure provides a conservative estimate
                        // of the length in characters of the corresponding string, and is much cheaper to
                        // compute than this exact length.
                        currentMaxStringLength = cpInfoSize;
                    }
                    break;
                case Symbol.CONSTANT_METHOD_HANDLE_TAG:
                    cpInfoSize = 4;
                    break;
                case Symbol.CONSTANT_CLASS_TAG:
                case Symbol.CONSTANT_STRING_TAG:
                case Symbol.CONSTANT_METHOD_TYPE_TAG:
                case Symbol.CONSTANT_PACKAGE_TAG:
                case Symbol.CONSTANT_MODULE_TAG:
                    cpInfoSize = 3;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            currentCpInfoOffset += cpInfoSize;
        }
        maxStringLength = currentMaxStringLength;
        // The Classfile's access_flags field is just after the last constant pool entry.
        header = currentCpInfoOffset;
    }

    /// Constructs a new [ClassReader] object.
    ///
    /// @param inputStream an input stream of the JVMS ClassFile structure to be read. This input
    ///                    stream must contain nothing more than the ClassFile structure itself. It is read from its
    ///                    current position to its end.
    /// @throws IOException if a problem occurs during reading.
    public ClassReader(final InputStream inputStream) throws IOException {
        this(readStream(inputStream, false));
    }

    /// Constructs a new [ClassReader] object.
    ///
    /// @param className the fully qualified name of the class to be read. The ClassFile structure is
    ///                  retrieved with the current class loader's [#getSystemResourceAsStream].
    /// @throws IOException if an exception occurs during reading.
    public ClassReader(final String className) throws IOException {
        this(
                readStream(
                        ClassLoader.getSystemResourceAsStream(
                                className.replace('.', '/') + ".class"),
                        true)
        );
    }

    /// Reads the given input stream and returns its content as a byte array.
    ///
    /// @param inputStream an input stream.
    /// @param close       true to close the input stream after reading.
    /// @return the content of the given input stream.
    /// @throws IOException if a problem occurs during reading.
    private static byte[] readStream(final InputStream inputStream, final boolean close)
            throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        int bufferSize = calculateBufferSize(inputStream);
        try (var outputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[bufferSize];
            int bytesRead;
            int readCount = 0;
            while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
                outputStream.write(data, 0, bytesRead);
                readCount++;
            }
            outputStream.flush();
            if (readCount == 1) {
                // SPRING PATCH: some misbehaving InputStreams return -1 but still write to buffer (gh-27429)
                // return data;
                // END OF PATCH
            }
            return outputStream.toByteArray();
        } finally {
            if (close) {
                inputStream.close();
            }
        }
    }

    private static int calculateBufferSize(final InputStream inputStream) throws IOException {
        int expectedLength = inputStream.available();
        /*
         * Some implementations can return 0 while holding available data
         * (e.g. new FileInputStream("/proc/a_file"))
         * Also in some pathological cases a very small number might be returned,
         * and in this case we use default size
         */
        if (expectedLength < 256) {
            return INPUT_STREAM_DATA_CHUNK_SIZE;
        }
        return Math.min(expectedLength, MAX_BUFFER_SIZE);
    }

    // -----------------------------------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------------------------------

    /// Returns the class's access flags . This value may not reflect Deprecated
    /// and Synthetic flags when bytecode is before 1.5 and those flags are represented by attributes.
    ///
    /// @return the class access flags.
    public int getAccess() {
        return readUnsignedShort(header);
    }

    /// the internal class name.
    public String getClassName() {
        // this_class is just after the access_flags field (using 2 bytes).
        return readClass(header + 2, new char[maxStringLength]);
    }

    public String getSuperName() {
        // super_class is after the access_flags and this_class fields (2 bytes each).
        return readClass(header + 4, new char[maxStringLength]);
    }

    /// the internal names of the directly implemented interfaces. Inherited implemented
    /// interfaces are not returned.
    public String[] getInterfaces() {
        // interfaces_count is after the access_flags, this_class and super_class fields (2 bytes each).
        int currentOffset = header + 6;
        int interfacesCount = readUnsignedShort(currentOffset);
        String[] interfaces = new String[interfacesCount];
        if (interfacesCount > 0) {
            char[] charBuffer = new char[maxStringLength];
            for (int i = 0; i < interfacesCount; ++i) {
                currentOffset += 2;
                interfaces[i] = readClass(currentOffset, charBuffer);
            }
        }
        return interfaces;
    }

    // ----------------------------------------------------------------------------------------------
    // Methods to parse attributes
    // ----------------------------------------------------------------------------------------------

    /// Returns the offset in [#classFileBuffer] of the first ClassFile's 'attributes' array
    /// field entry.
    ///
    /// @return the offset in [#classFileBuffer] of the first ClassFile's 'attributes' array
    /// field entry.
    final int getFirstAttributeOffset() {
        // Skip the access_flags, this_class, super_class, and interfaces_count fields (using 2 bytes
        // each), as well as the interfaces array field (2 bytes per interface).
        int currentOffset = header + 8 + readUnsignedShort(header + 6) * 2;

        // Read the fields_count field.
        int fieldsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        // Skip the 'fields' array field.
        while (fieldsCount-- > 0) {
            // Invariant: currentOffset is the offset of a field_info structure.
            // Skip the access_flags, name_index and descriptor_index fields (2 bytes each), and read the
            // attributes_count field.
            int attributesCount = readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            // Skip the 'attributes' array field.
            while (attributesCount-- > 0) {
                // Invariant: currentOffset is the offset of an attribute_info structure.
                // Read the attribute_length field (2 bytes after the start of the attribute_info) and skip
                // this many bytes, plus 6 for the attribute_name_index and attribute_length fields
                // (yielding the total size of the attribute_info structure).
                currentOffset += 6 + readInt(currentOffset + 2);
            }
        }

        // Skip the methods_count and 'methods' fields, using the same method as above.
        int methodsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (methodsCount-- > 0) {
            int attributesCount = readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (attributesCount-- > 0) {
                currentOffset += 6 + readInt(currentOffset + 2);
            }
        }

        // Skip the ClassFile's attributes_count field.
        return currentOffset + 2;
    }

    // -----------------------------------------------------------------------------------------------
    // Utility methods: low level parsing
    // -----------------------------------------------------------------------------------------------

    /// Returns the number of entries in the class's constant pool table.
    ///
    /// @return the number of entries in the class's constant pool table.
    public int getItemCount() {
        return cpInfoOffsets.length;
    }

    /// Returns the start offset in this [ClassReader] of a JVMS 'cp_info' structure (i.e. a
    /// constant pool entry), plus one. _This method is intended for  Attribute sub classes,
    /// and is normally not needed by class generators or adapters._
    ///
    /// @param constantPoolEntryIndex the index a constant pool entry in the class's constant pool
    ///                               table.
    /// @return the start offset in this [ClassReader] of the corresponding JVMS 'cp_info'
    /// structure, plus one.
    public int getItem(final int constantPoolEntryIndex) {
        return cpInfoOffsets[constantPoolEntryIndex];
    }

    /// Returns a conservative estimate of the maximum length of the strings contained in the class's
    /// constant pool table.
    ///
    /// @return a conservative estimate of the maximum length of the strings contained in the class's
    /// constant pool table.
    public int getMaxStringLength() {
        return maxStringLength;
    }

    /// Reads a byte value in this [ClassReader]. _This method is intended for
    /// Attribute subclasses, and is normally not needed by class generators or adapters._
    ///
    /// @param offset the start offset of the value to be read in this [ClassReader].
    /// @return the read value.
    public int readByte(final int offset) {
        return classFileBuffer[offset] & 0xFF;
    }

    /// Reads an unsigned short value in this [ClassReader]. _This method is intended for
    ///  Attribute subclasses, and is normally not needed by class generators or adapters._
    ///
    /// @param offset the start index of the value to be read in this [ClassReader].
    /// @return the read value.
    public int readUnsignedShort(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return ((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF);
    }

    /// Reads a signed short value in this [ClassReader]. _This method is intended for
    /// Attribute subclasses, and is normally not needed by class generators or adapters._
    ///
    /// @param offset the start offset of the value to be read in this [ClassReader].
    /// @return the read value.
    public short readShort(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return (short) (((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF));
    }

    /// Reads a signed int value in this [ClassReader]. _This method is intended for
    /// Attribute subclasses, and is normally not needed by class generators or adapters._
    ///
    /// @param offset the start offset of the value to be read in this [ClassReader].
    /// @return the read value.
    public int readInt(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return ((classBuffer[offset] & 0xFF) << 24)
                | ((classBuffer[offset + 1] & 0xFF) << 16)
                | ((classBuffer[offset + 2] & 0xFF) << 8)
                | (classBuffer[offset + 3] & 0xFF);
    }

    /// Reads a signed long value in this [ClassReader]. _This method is intended for
    /// Attribute subclasses, and is normally not needed by class generators or adapters._
    ///
    /// @param offset the start offset of the value to be read in this [ClassReader].
    /// @return the read value.
    public long readLong(final int offset) {
        long l1 = readInt(offset);
        long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
        return (l1 << 32) | l0;
    }

    /// Reads a CONSTANT_Utf8 constant pool entry in this [ClassReader]. _This method is
    /// intended for Attribute subclasses, and is normally not needed by class generators or
    /// adapters._
    ///
    /// @param offset     the start offset of an unsigned short value in this [ClassReader], whose
    ///                   value is the index of a CONSTANT_Utf8 entry in the class's constant pool table.
    /// @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
    ///                   large. It is not automatically resized.
    /// @return the String corresponding to the specified CONSTANT_Utf8 entry.
    // DontCheck(AbbreviationAsWordInName): can't be renamed (for backward binary compatibility).
    public String readUtf8(final int offset, final char[] charBuffer) {
        int constantPoolEntryIndex = readUnsignedShort(offset);
        if (offset == 0 || constantPoolEntryIndex == 0) {
            return null;
        }
        return readUtf(constantPoolEntryIndex, charBuffer);
    }

    /// Reads a CONSTANT_Utf8 constant pool entry in [#classFileBuffer].
    ///
    /// @param constantPoolEntryIndex the index of a CONSTANT_Utf8 entry in the class's constant pool
    ///                               table.
    /// @param charBuffer             the buffer to be used to read the string. This buffer must be sufficiently
    ///                               large. It is not automatically resized.
    /// @return the String corresponding to the specified CONSTANT_Utf8 entry.
    final String readUtf(final int constantPoolEntryIndex, final char[] charBuffer) {
        String value = constantUtf8Values[constantPoolEntryIndex];
        if (value != null) {
            return value;
        }
        int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
        return constantUtf8Values[constantPoolEntryIndex] =
                readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
    }

    /// Reads an UTF8 string in [#classFileBuffer].
    ///
    /// @param utfOffset  the start offset of the UTF8 string to be read.
    /// @param utfLength  the length of the UTF8 string to be read.
    /// @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
    ///                   large. It is not automatically resized.
    /// @return the String corresponding to the specified UTF8 string.
    private String readUtf(final int utfOffset, final int utfLength, final char[] charBuffer) {
        int currentOffset = utfOffset;
        int endOffset = currentOffset + utfLength;
        int strLength = 0;
        byte[] classBuffer = classFileBuffer;
        while (currentOffset < endOffset) {
            int currentByte = classBuffer[currentOffset++];
            if ((currentByte & 0x80) == 0) {
                charBuffer[strLength++] = (char) (currentByte & 0x7F);
            } else if ((currentByte & 0xE0) == 0xC0) {
                charBuffer[strLength++] =
                        (char) (((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
            } else {
                charBuffer[strLength++] =
                        (char)
                                (((currentByte & 0xF) << 12)
                                        + ((classBuffer[currentOffset++] & 0x3F) << 6)
                                        + (classBuffer[currentOffset++] & 0x3F));
            }
        }
        return new String(charBuffer, 0, strLength);
    }

    /// Reads a CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module or
    /// CONSTANT_Package constant pool entry in [#classFileBuffer]. _This method is intended
    /// for  Attribute subclasses, and is normally not needed by class generators or
    /// adapters._
    ///
    /// @param offset     the start offset of an unsigned short value in [#classFileBuffer], whose
    ///                   value is the index of a CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType,
    ///                   CONSTANT_Module or CONSTANT_Package entry in class's constant pool table.
    /// @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
    ///                   large. It is not automatically resized.
    /// @return the String corresponding to the specified constant pool entry.
    private String readStringish(final int offset, final char[] charBuffer) {
        // Get the start offset of the cp_info structure (plus one), and read the CONSTANT_Utf8 entry
        // designated by the first two bytes of this cp_info.
        return readUtf8(cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
    }

    /// Reads a CONSTANT_Class constant pool entry in this [ClassReader]. _This method is
    /// intended for  Attribute subclasses, and is normally not needed by class generators or
    /// adapters._
    ///
    /// @param offset     the start offset of an unsigned short value in this [ClassReader], whose
    ///                   value is the index of a CONSTANT_Class entry in class's constant pool table.
    /// @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
    ///                   large. It is not automatically resized.
    /// @return the String corresponding to the specified CONSTANT_Class entry.
    public String readClass(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    /// Reads a CONSTANT_Module constant pool entry in this [ClassReader]. _This method is
    /// intended for Attribute subclasses, and is normally not needed by class generators or
    /// adapters._
    ///
    /// @param offset     the start offset of an unsigned short value in this [ClassReader], whose
    ///                   value is the index of a CONSTANT_Module entry in class's constant pool table.
    /// @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
    ///                   large. It is not automatically resized.
    /// @return the String corresponding to the specified CONSTANT_Module entry.
    public String readModule(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    /// Reads a CONSTANT_Package constant pool entry in this [ClassReader]. _This method is
    /// intended for Attribute subclasses, and is normally not needed by class generators or
    /// adapters._
    ///
    /// @param offset     the start offset of an unsigned short value in this [ClassReader], whose
    ///                   value is the index of a CONSTANT_Package entry in class's constant pool table.
    /// @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
    ///                   large. It is not automatically resized.
    /// @return the String corresponding to the specified CONSTANT_Package entry.
    public String readPackage(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }
}
