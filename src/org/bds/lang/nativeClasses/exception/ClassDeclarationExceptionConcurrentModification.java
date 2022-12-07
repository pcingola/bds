package org.bds.lang.nativeClasses.exception;

import org.bds.lang.statement.MethodDeclaration;
import org.bds.lang.type.TypeClass;
import org.bds.lang.type.Types;

import java.util.LinkedList;
import java.util.List;

public class ClassDeclarationExceptionConcurrentModification extends ClassDeclarationException {

    public static final String CLASS_NAME_EXCEPTION_CONCURRENT_MODIFICATION = "ConcurrentModificationException";
    private static final long serialVersionUID = -2260672994052335846L;

    public ClassDeclarationExceptionConcurrentModification() {
        super();
    }

    @Override
    protected MethodDeclaration[] createMethods() {
        List<MethodDeclaration> methods = new LinkedList<>();
        methods.add(new MethodExceptionConstructor(getType()));
        return methods.toArray(new MethodDeclaration[0]);
    }

    @Override
    protected void initNativeClass() {
        if (className == null) className = CLASS_NAME_EXCEPTION_CONCURRENT_MODIFICATION;
        if (classNameParent == null) classNameParent = CLASS_NAME_EXCEPTION;

        super.initNativeClass();
    }

}
