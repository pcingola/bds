package org.bds.lang.nativeFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

/**
 * Log a message to STDERR
 *
 * @author pcingola
 */
public class FunctionNativeLog extends FunctionNative {

	private static final long serialVersionUID = 4328132832275759104L;

	SimpleDateFormat format;

	public FunctionNativeLog() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "log";
		returnType = Types.STRING;

		// Show time in GMT timezone
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		String argNames[] = { "str" };
		Type argTypes[] = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		// Get `log(msg)` argument
		String str = bdsThread.getString("str");

		// Show current time (GMT)
		String ymdhms = format.format(new Date());
		String out = ymdhms + '\t' + str;

		// Show log message to stderr
		System.err.println(out);
		return out;
	}

}
