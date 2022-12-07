package org.bds.libraries;

import org.bds.BdsLog;

/**
 * A container for Library-related code
 */
public class Library implements BdsLog {

    public static final String LIBRARIES_PATH = "libraries";
    public static final String[] LIBRARIES = { //
            "exceptions.bds" // Define throwable, exception, etc.
            , "stdlib.bds" //
            , "z.bds" // Only used for testing new library code
    };


}
