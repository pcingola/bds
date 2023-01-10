package org.bds.vm;

import org.bds.lang.BdsNode;
import org.bds.lang.value.Value;
import org.bds.run.DebugMode;
import org.bds.scope.Scope;
import org.bds.util.Gpr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Data for running a VM in debugging mode
 *
 * @author pcingola
 */
public class VmDebugger implements Serializable {

    private static final long serialVersionUID = -3444495779847695585L;

    boolean debug;
    int fp; // Stop if fp matches (used for 'step over' command
    DebugMode debugMode;
    BdsVm vm;

    public VmDebugger(BdsVm vm) {
        this.vm = vm;
        debugMode = DebugMode.STEP;
    }

    /**
     * Reached a 'breakpoint' (asm instruction)
     */
    public void breakpoint(String label) {
        if (debugMode == DebugMode.RUN_DEBUG) return; // Ignore breakpoints
        if (label != null && !label.isEmpty()) System.err.println("Breakpoint: " + label);
        debugStep();
    }

    /**
     * Running in debug mode: This method is invoked right before running 'node'
     */
    public void debug() {
        debugMode = DebugMode.STEP;
        debugStep();
    }

    /**
     * Show debug 'step' options
     */
    void debugStep() {
        BdsNode node = vm.bdsThread.getBdsNodeCurrent();

        // Show current line
        String prg = node.toString();
        if (prg.indexOf("\n") > 0) prg = "\n" + Gpr.prependEachLine("\t", prg);
        else prg = prg + " ";

        String prompt = "DEBUG [" + debugMode + "]: " //
                + Gpr.baseName(node.getFileName()) //
                + ":" + node.getLineNum() //
                + (debug ? " (" + node.getClass().getSimpleName() + ")" : "") //
                + ": " + prg //
                + "> " //
                ;

        //---
        // Wait for options
        //---
        while (true) {
            System.err.print(prompt);
            String line = readConsole();

            if (line == null) return;
            line = line.trim();

            //---
            // Parse options
            //---

            // Empty line? => Continue using the same debug mode
            if (line.isEmpty()) {
                return;
            } else if (line.toLowerCase().startsWith("v ")) { // Show variable
                // Get variable's name
                String varName = line.substring(2).trim(); // Remove leading "v " string
                // Get and show variable
                Value val = vm.getScope().getValue(varName);
                if (val == null) System.err.println("Variable '" + varName + "' not found");
                else System.err.println(val.getType() + " : " + val);
            } else {
                // All other options are just one letter
                switch (line) {
                    case "c":
                        // Show current 'scope'
                        System.err.println("Current scope:\n" + vm.getScope().toStringLocal(false));
                        break;

                    case "C":
                        // Show all scopes
                        System.err.println("Scopes:");
                        for (Scope s = vm.getScope(); s != null; s = s.getParent())
                            System.err.println(s.toStringLocal(false));
                        break;

                    case "e":
                        // Show exception & exception handler
                        Value ex = vm.getException();
                        System.err.println("Latest exception: " + (ex != null ? ex.toString() : "null"));
                        ExceptionHandler eh = vm.getExceptionHandler();
                        String ehStr = (eh != null ? Gpr.prependEachLine("\t", eh.toString()) : "null");
                        System.err.println("Exception handler:\n" + ehStr);
                        break;

                    case "?":
                    case "h":
                        // Show help
                        printHelp();
                        break;

                    case "k":
                        // Show stack
                        System.err.println(vm.toStringStackLn());
                        break;

                    case "p":
                        // Show current 'pc'
                        System.err.println("pc=" + vm.getPc() + ", fp=" + fp + ", current nodeId: " + vm.getNodeId());
                        break;

                    case "o":
                        // Switch to 'STEP_OVER' mode
                        debugMode = DebugMode.STEP_OVER;
                        return;

                    case "q":
                        // Quit debug mode
                        debugMode = DebugMode.RUN_DEBUG;
                        return;

                    case "r":
                        // Switch to 'RUN' mode
                        debugMode = DebugMode.RUN;
                        return;

                    case "s":
                        // Switch to 'STEP' mode
                        debugMode = DebugMode.STEP;
                        return;

                    case "t":
                        // Show stack trace
                        System.err.println(vm.stackTrace());
                        break;

                    default:
                        System.err.println("Unknown command '" + line + "'. Use 'h' or '?' for help.");
                }
            }
        }
    }

    /**
     * Reached a 'node' (asm instruction)
     */
    public void node() {
        switch (debugMode) {
            case RUN:
            case RUN_DEBUG:
                break;

            case STEP:
                debugStep();
                break;

            case STEP_OVER:
                // Keep running until we find a breakpoint
                if (fp >= 0 && vm.fp <= fp) debugStep();
                break;

            default:
                throw new RuntimeException("Unimplemented debug mode: " + debugMode);
        }
    }

    /**
     * Read a line from STDIN
     */
    String readConsole() {
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            String line = console.readLine();
            return line;
        } catch (Exception e) {
            return null;
        }
    }

    void printHelp() {
        System.err.println("Help:");
        System.err.println("\t[RETURN]  : " + (debugMode == DebugMode.STEP_OVER ? "step over" : "step"));
        System.err.println("\tc         : sCope. Show all variables within current scope");
        System.err.println("\tc         : Exception. Show current Exception and Exception handler");
        System.err.println("\th         : Help");
        System.err.println("\tk         : stacK. Show current stack");
        System.err.println("\to         : step Over");
        System.err.println("\tp         : Pc. Show pc (Program counter), fp (Frame pointer), and nodeId");
        System.err.println("\tq         : Quit. Quit debug mode, i.e. continue running until next 'debug' command");
        System.err.println("\tr         : Run. Run program until next breakpoint");
        System.err.println("\ts         : Step.");
        System.err.println("\tt         : stack Trace. Show stack trace");
        System.err.println("\tv varname : Variable. Show 'varname' type and value");
        System.err.println();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}
