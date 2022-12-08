package org.bds.libraries;

import org.bds.BdsLog;

/**
 * A container for Library-related code
 */
public class LibraryException implements BdsLog {

    public static final String CLASS_NAME_EXCEPTION = "Exception";
    public static final String FIELD_NAME_VALUE = "value"; // Exception value (hidden field). Original value wrapped in an exception object
    public static final String FIELD_NAME_STACK_TRACE = "stackTrace"; // Stack trace as a string (hidden field)

    public static final String CLASS_NAME_EXCEPTION_CONCURRENT_MODIFICATION = "ConcurrentModificationException";
}
