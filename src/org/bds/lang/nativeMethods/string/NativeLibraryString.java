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

    public static Class[] classes = {org.bds.lang.nativeMethods.string.MethodNative_string_length.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_toUpper.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_toLower.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_trim.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_substr_start_end.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_substr_start.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_split_regex.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_lines.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_isEmpty.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_startsWith_str.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_endsWith_str.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_indexOf_str.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_lastIndexOf_str.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_replace_str1_str2.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_parseInt.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_parseReal.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_parseBool.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_replace_regex_repl.class //
            // String as files
            , org.bds.lang.nativeMethods.string.MethodNative_string_baseName.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_baseName_ext.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_canExec.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_canRead.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_canWrite.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_chdir.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_delete.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_dir.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_dir_regex.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_dirName.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_dirPath.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_dirPath_regex.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_download.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_download_localname.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_exists.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_extName.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_isDir.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_isFile.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_mkdir.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_path.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_pathCanonical.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_pathName.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_read.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_readLines.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_removeExt.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_removeExt_ext.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_rm.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_rmOnExit.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_rmOnExitCancel.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_size.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_swapExt_extNew.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_swapExt_extOld_extNew.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_upload.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_upload_localname.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_write_str.class //
            // String as task
            , org.bds.lang.nativeMethods.string.MethodNative_string_exitCode.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_isDone.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_isDoneOk.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_stdout.class //
            , org.bds.lang.nativeMethods.string.MethodNative_string_stderr.class //

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
