package com.trainstation.network;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;

/**
 * Whitelist-based deserialization filter to prevent unsafe deserialization attacks.
 * Only classes from trusted packages are allowed.
 */
public class SerializationFilter implements ObjectInputFilter {
    public static final SerializationFilter INSTANCE = new SerializationFilter();

    private static final String[] ALLOWED_PREFIXES = {
        "com.trainstation.",
        "java.util.",
        "java.time.",
        "java.lang.",
        "[L",   // arrays
        "[B",   // byte arrays
        "[I",   // int arrays
        "[J",   // long arrays
        "[D",   // double arrays
        "[F",   // float arrays
        "[Z",   // boolean arrays
    };

    private SerializationFilter() {}

    @Override
    public Status checkInput(FilterInfo info) {
        Class<?> cls = info.serialClass();
        if (cls == null) return Status.UNDECIDED;

        String name = cls.getName();
        for (String prefix : ALLOWED_PREFIXES) {
            if (name.startsWith(prefix)) return Status.ALLOWED;
        }
        return Status.REJECTED;
    }

    /** ObjectInputStream that installs the whitelist filter immediately on construction. */
    public static final class FilteredObjectInputStream extends ObjectInputStream {
        public FilteredObjectInputStream(java.io.InputStream in) throws IOException {
            super(in);
            setObjectInputFilter(INSTANCE);
        }
    }
}
