package org.bds.lang.value;

import java.util.Set;

/**
 * Define a map
 * @author pcingola
 */
public abstract class ValuePrimitive extends Value {

	private static final long serialVersionUID = 1464083196134715844L;

	public ValuePrimitive() {
		super();
	}

	protected void toString(StringBuilder sb, Set<Value> done) {
		sb.append(toString());
	}

}
