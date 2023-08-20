package org.bds.vm;

import org.bds.lang.value.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Exception handlers are added when try/catch/finally blocks are executed
 *
 * @author pcingola
 */
public class ExceptionHandler implements Serializable {

	private static final long serialVersionUID = 1193221593865738652L;

	boolean catchStart, finallyStart; // Exception handling started
	List<CatchBlockInfo> catchBlocks; // Catch blocks (sorted in order of appearance)
	String finallyLabel; // Finally label for this try/catch/block
	Value pendingException; // Pending exception (to be re-thrown)

	public ExceptionHandler(String finallyLabel) {
		this.finallyLabel = finallyLabel;
		pendingException = null;
	}

	public void addHandler(String handlerLabel, String exceptionClassName, String variableName) {
		if (catchBlocks == null) catchBlocks = new ArrayList<>();
		CatchBlockInfo cb = new CatchBlockInfo(handlerLabel, exceptionClassName, variableName);
		catchBlocks.add(cb);
	}

	public void catchStart() {
		catchStart = true;
		finallyStart = false;
	}

	public void finallyStart() {
		catchStart = false;
		finallyStart = true;
	}

	/**
	 * Get catch block info if available for 'exception'
	 */
	public CatchBlockInfo getCatchBlockInfo(Value exception) {
		if (catchBlocks == null) return null;
		var exType = exception.getType();
		for(CatchBlockInfo cb: catchBlocks) {
			if( cb.handles(exType))
				return cb;
		}
		return null;
	}

	public String getFinallyLabel() {
		return finallyLabel;
	}

	public Value getPendingException() {
		return pendingException;
	}

	public boolean isCatchStart() {
		return catchStart;
	}

	public boolean isFinallyStart() {
		return finallyStart;
	}

	public void setPendingException(Value ex) {
		pendingException = ex;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (catchBlocks != null) {
			sb.append("Catch blocks:\n");
			for (CatchBlockInfo cb: catchBlocks)
				sb.append("\t" + cb + "\n");
		}
		sb.append("Finally block: " + finallyLabel);
		if (isCatchStart()) sb.append(", catchStart: true");
		if (isFinallyStart()) sb.append(", finalStart: true");
		sb.append("\n");
		sb.append("Pending exception: " + (pendingException != null ? pendingException.getType().getCanonicalName() : "null") + "\n");
		return sb.toString();
	}

}
