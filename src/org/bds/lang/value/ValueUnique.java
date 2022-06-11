package org.bds.lang.value;

import java.util.HashMap;
import java.util.Map;

import org.bds.lang.type.Type;

/**
 * Unique values, such as 'null' and 'void'
 *
 * @author pcingola
 */
public class ValueUnique extends ValuePrimitive {

	private static final long serialVersionUID = 8806864117675019213L;

	private static final Map<Type, ValueUnique> valueUniqueByType = new HashMap<>();

	protected Type type;

	public static ValueUnique get(Type type) {
		if (!valueUniqueByType.containsKey(type)) valueUniqueByType.put(type, new ValueUnique(type));
		return valueUniqueByType.get(type);
	}

	private ValueUnique(Type type) {
		super();
		this.type = type;
	}

	@Override
	public boolean asBool() {
		runtimeError("Cannot convert type '" + getType() + "' to bool");
		return false;
	}

	@Override
	public long asInt() {
		runtimeError("Cannot convert type '" + getType() + "' to int");
		return 0L;
	}

	@Override
	public double asReal() {
		runtimeError("Cannot convert type '" + getType() + "' to real");
		return 0.0;
	}

	@Override
	public String asString() {
		return toString();
	}

	@Override
	public Value clone() {
		return this; // Note that unique values are not cloned
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public void setValue(Value v) {
		// nothing to do: Value cannot be set.
	}
}
