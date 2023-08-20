package org.bds.lang.nativeMethods.string;

import org.bds.BdsLog;
import org.bds.lang.nativeMethods.MethodNative;

import java.util.ArrayList;

/**
 * Loads all classes used for native library
 *
 * @author pcingola
 */
public class NativeLibraryString implements BdsLog {

    public static Class[] classes = {org.bds.lang.nativeMethods.string.MethodNativeStringLength.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringToUpper.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringToLower.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringTrim.class //
            , MethodNativeStringSubstrStartEnd.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringSubstrStart.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringSplitRegex.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringLines.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringIsEmpty.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringStartsWithStr.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringEndsWithStr.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringIndexOfStr.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringLastIndexOfStr.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringReplaceStr1Str2.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringParseInt.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringParseReal.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringParseBool.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringReplaceRegexRepl.class //
            // String as files
            , MethodNativeStringBaseName.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringBaseNameExt.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringCanExec.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringCanRead.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringCanWrite.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringChdir.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringDelete.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringDir.class //
            , MethodNativeStringDirRegex.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringDirName.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringDirPath.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringDirPathRegex.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringDownload.class //
            , MethodNativeStringDownloadLocalname.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringExists.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringExtName.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringIsDir.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringIsFile.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringMkdir.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringPath.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringPathCanonical.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringPathName.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringRead.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringReadLines.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringRemoveExt.class //
            , MethodNativeStringRemoveExtExt.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringRm.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringRmOnExit.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringRmOnExitCancel.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringSize.class //
            , MethodNativeStringSwapExtExtNew.class //
            , MethodNativeStringSwapExtExtOldExtNew.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringUpload.class //
            , MethodNativeStringUploadLocalname.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringWriteStr.class //
            // String as task
            , org.bds.lang.nativeMethods.string.MethodNativeStringExitCode.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringIsDone.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringIsDoneOk.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringStdout.class //
            , org.bds.lang.nativeMethods.string.MethodNativeStringStderr.class //

    };

    ArrayList<MethodNative> methods;

    @SuppressWarnings({"rawtypes", "unchecke.class"})
    public NativeLibraryString() {
        try {
            methods = new ArrayList<>();

            for (Class c : classes) {
                methods.add((MethodNative) c.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            compileError("Error creating native library", e);
        }
    }

    public int size() {
        return methods.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName() + ":\n");
        for (MethodNative m : methods)
            sb.append("\t" + m.getClass().getSimpleName() + "\n");
        return sb.toString();
    }

}
