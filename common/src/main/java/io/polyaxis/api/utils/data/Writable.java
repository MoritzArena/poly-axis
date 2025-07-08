package io.polyaxis.api.utils.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/// Interface of [Writable] resource class.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public interface Writable {

    void write(DataOutputStream o) throws IOException;

    void read(DataInputStream i) throws IOException;
}
