package org.bds.util;

import java.util.HashMap;

/**
 * A Hash that creates new elements if they don't exists
 * @author pcingola
 */
public class AutoHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 255818677257868365L;

	V instance;

	public AutoHashMap(V instance) {
		super();
		this.instance = instance;
	}

	@SuppressWarnings("unchecked")
	public V getOrCreate(K key) {
		V v = get(key);
		if (v == null) {
			try {
				v = (V) instance.getClass().getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			put(key, v);
		}
		return v;
	}

}
