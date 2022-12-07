package org.bds.lang.nativeClasses.exception;

import org.bds.lang.nativeClasses.ClassDeclarationNative;
import org.bds.lang.statement.FieldDeclaration;
import org.bds.lang.statement.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;

import static org.bds.libraries.LibraryException.CLASS_NAME_EXCEPTION;

/**
 * Bds 'Exception' class.
 * It is initially empty, but the fields 'stackTrace' and 'value' are created and populated
 * when an exception is thrown, see BdsVm.throwException() for details
 */
public class ClassDeclarationException extends ClassDeclarationNative {

    private static final long serialVersionUID = -4115713969638658245L;

    public ClassDeclarationException() {
        super();
    }

    @Override
    protected FieldDeclaration[] createFields() {
        List<FieldDeclaration> fields = new LinkedList<>();
        // fields.add();
        return fields.toArray(new FieldDeclaration[0]);
    }

    @Override
    protected MethodDeclaration[] createMethods() {
        List<MethodDeclaration> methods = new LinkedList<>();
        methods.add(defaultConstructor());
        methods.add(new MethodExceptionConstructor(getType()));
        return methods.toArray(new MethodDeclaration[0]);
    }

    @Override
    protected void initNativeClass() {
        if (className == null) className = CLASS_NAME_EXCEPTION;
        super.initNativeClass();
    }

}
