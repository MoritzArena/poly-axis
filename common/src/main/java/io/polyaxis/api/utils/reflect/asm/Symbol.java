package io.polyaxis.api.utils.reflect.asm;

/// Copy from ASM, with fewer modifications
/// An entry of the constant pool, of the BootstrapMethods attribute, or of the (ASM specific) type
/// table of a class.
///
/// @author Eric Bruneton
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.4">JVMS
/// 4.4</a>
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.23">JVMS
/// 4.7.23</a>
abstract class Symbol {

    /// The tag value of CONSTANT_Class_info JVMS structures.
    static final int CONSTANT_CLASS_TAG = 7;

    /// The tag value of CONSTANT_Fieldref_info JVMS structures.
    static final int CONSTANT_FIELDREF_TAG = 9;

    /// The tag value of CONSTANT_Methodref_info JVMS structures.
    static final int CONSTANT_METHODREF_TAG = 10;

    /// The tag value of CONSTANT_InterfaceMethodref_info JVMS structures.
    static final int CONSTANT_INTERFACE_METHODREF_TAG = 11;

    /// The tag value of CONSTANT_String_info JVMS structures.
    static final int CONSTANT_STRING_TAG = 8;

    /// The tag value of CONSTANT_Integer_info JVMS structures.
    static final int CONSTANT_INTEGER_TAG = 3;

    /// The tag value of CONSTANT_Float_info JVMS structures.
    static final int CONSTANT_FLOAT_TAG = 4;

    /// The tag value of CONSTANT_Long_info JVMS structures.
    static final int CONSTANT_LONG_TAG = 5;

    /// The tag value of CONSTANT_Double_info JVMS structures.
    static final int CONSTANT_DOUBLE_TAG = 6;

    /// The tag value of CONSTANT_NameAndType_info JVMS structures.
    static final int CONSTANT_NAME_AND_TYPE_TAG = 12;

    /// The tag value of CONSTANT_Utf8_info JVMS structures.
    static final int CONSTANT_UTF8_TAG = 1;

    /// The tag value of CONSTANT_MethodHandle_info JVMS structures.
    static final int CONSTANT_METHOD_HANDLE_TAG = 15;

    /// The tag value of CONSTANT_MethodType_info JVMS structures.
    static final int CONSTANT_METHOD_TYPE_TAG = 16;

    /// The tag value of CONSTANT_Dynamic_info JVMS structures.
    static final int CONSTANT_DYNAMIC_TAG = 17;

    /// The tag value of CONSTANT_InvokeDynamic_info JVMS structures.
    static final int CONSTANT_INVOKE_DYNAMIC_TAG = 18;

    /// The tag value of CONSTANT_Module_info JVMS structures.
    static final int CONSTANT_MODULE_TAG = 19;

    /// The tag value of CONSTANT_Package_info JVMS structures.
    static final int CONSTANT_PACKAGE_TAG = 20;
}
