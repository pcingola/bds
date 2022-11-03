package org.bds.lang.nativeFunctions;

import org.bds.BdsLog;
import org.bds.lang.nativeFunctions.math.FunctionNativeAbsInt;
import org.bds.lang.nativeFunctions.math.FunctionNativeAbsReal;
import org.bds.lang.nativeFunctions.math.FunctionNativeAcosReal;
import org.bds.lang.nativeFunctions.math.FunctionNativeAsinReal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Loads all classes used for native functions library
 *
 * @author pcingola
 *
 */
public class NativeLibraryFunctions implements BdsLog {

	@SuppressWarnings("rawtypes")
	public static Class[] classes = { //
			FunctionNativeAddResource.class //
			, FunctionNativeAssertBool.class //
			, FunctionNativeAssertBoolNoMsg.class //
			, FunctionNativeAssertInt.class //
			, FunctionNativeAssertIntNoMsg.class //
			, FunctionNativeAssertString.class //
			, FunctionNativeAssertStringNoMsg.class //
			, FunctionNativeCloneBool.class //
			, FunctionNativeCloneInt.class //
			, FunctionNativeCloneReal.class //
			, FunctionNativeCloneString.class //
			, FunctionNativeConfig.class //
			, FunctionNativeConfigOri.class //
			, FunctionNativeGetModulePath.class //
			, FunctionNativeGetVar.class //
			, FunctionNativeGetVarDefault.class //
			, FunctionNativeJson.class //
			, FunctionNativeJsonObj.class //
			, FunctionNativeHasVar.class //
			, FunctionNativeLog.class //
			, FunctionNativeLogd.class //
			, FunctionNativeMinInt.class //
			, FunctionNativeMaxInt.class //
			, FunctionNativeMinReal.class //
			, FunctionNativeMaxReal.class //
			, FunctionNativePrint.class //
			, FunctionNativePrintErr.class //
			, FunctionNativePrintHelp.class //
			, FunctionNativeRand.class //
			, FunctionNativeRandInt.class //
			, FunctionNativeRandIntRange.class //
			, FunctionNativeRandSeed.class //
			, FunctionNativeRangeInt.class //
			, FunctionNativeRangeIntStep.class //
			, FunctionNativeRangeReal.class //
			, FunctionNativeSleep.class //
			, FunctionNativeSleepReal.class //
			, FunctionNativeTasksDone.class //
			, FunctionNativeTasksRunning.class //
			, FunctionNativeTasksToRun.class //
			, FunctionNativeTime.class //
			, FunctionNativeToIntFromBool.class //
			, FunctionNativeToIntFromReal.class //
			, FunctionNativeType.class //
			//
			// Math functions
			, FunctionNativeAbsInt.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeGetExponentReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeRoundReal.class //
			, FunctionNativeAbsReal.class //
			, FunctionNativeAcosReal.class //
			, FunctionNativeAsinReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeAtanReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeAtan2RealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeCbrtReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeCeilReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeCopySignRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeCosReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeCoshReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeExpReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeExpm1Real.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeFloorReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeHypotRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeiEEEremainderRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeLogReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeLog10Real.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeLog1pReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeMaxRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeMinRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeNextAfterRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeNextUpReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativePowRealReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeRintReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeScalbRealInt.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeSignumReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeSinReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeSinhReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeSqrtReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeTanReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeTanhReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeToDegreesReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeToRadiansReal.class //
			, org.bds.lang.nativeFunctions.math.FunctionNativeUlpReal.class //
	};

	ArrayList<FunctionNative> functions;

	@SuppressWarnings("rawtypes")
	public NativeLibraryFunctions() {
		try {
			functions = new ArrayList<>();
			for (Class<FunctionNative> c : classes) {
				Constructor<FunctionNative> constructor = c.getDeclaredConstructor();
				functions.add(constructor.newInstance());
			}
		} catch (Exception e) {
			compileError("Error creating native library", e);
		}
	}

	public int size() {
		return functions.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName() + ":\n");
		for (FunctionNative m : functions)
			sb.append("\t" + m.getClass().getSimpleName() + "\n");
		return sb.toString();
	}

}
