package org.bds;

import org.bds.run.BdsThreads;
import org.bds.util.Timer;

public class BdsLogStatic {

    public static void fatalError(String message) {
        var msg = "FATAL ERROR: " + (message != null ? message : "null");
        Timer.showStdErr(msg);
        throw new RuntimeException(msg);
    }

    public static void runtimeError(Object message) {
        var bdsth = BdsThreads.getInstance().getOrRoot();
        var cflp = (bdsth != null ? bdsth.toStringClassFileLinePos() : "");
        var msg = "Runtime error " + cflp + ": " + message.toString();
        Timer.showStdErr(msg);
        throw new RuntimeException(msg);
    }
}
