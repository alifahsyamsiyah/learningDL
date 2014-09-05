/////////////////////////////////////////////////////////////////////////////
// $Id$  
package org.dom4j.util;

import org.dom4j.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jirsák Filip
 * @version $Revision$
 */
public class DoubleNameMap<T> {

	private Map<String, T> namedMap = new HashMap<String, T>();
	private Map<QName, T> qNamedMap = new HashMap<QName, T>();

	public void put(QName qName, T value) {
		qNamedMap.put(qName, value);
		namedMap.put(qName.getName(), value);
	}

	public T get(String name) {
		return namedMap.get(name);
	}

	public T get(QName qName) {
		return qNamedMap.get(qName);
	}

	public void remove(QName qName) {
		this.qNamedMap.remove(qName);
		this.namedMap.remove(qName.getName());
	}
}
