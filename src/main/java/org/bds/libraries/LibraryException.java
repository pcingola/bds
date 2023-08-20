package org.bds.libraries;

import org.bds.BdsLog;

/**
 * A container for Library-related code
 */
public class LibraryException implements BdsLog {

    public static final String CLASS_NAME_THROWABLE = "Throwable";
    public static final String CLASS_NAME_EXCEPTION = "Exception";

    // Exception field names
    public static final String THROWABLE_FIELD_VALUE = "value"; // Throwable 'value' field. Original value wrapped in an exception object
    public static final String THROWABLE_FIELD_STACK_TRACE = "stackTrace"; // Throwable 'stackTrace' field as a string

    // Specific built-in exceptions
    public static final String CLASS_NAME_EXCEPTION_CONCURRENT_MODIFICATION = "ConcurrentModificationException";
    public static final String CLASS_NAME_WAIT_EXCEPTION = "WaitException";
}
