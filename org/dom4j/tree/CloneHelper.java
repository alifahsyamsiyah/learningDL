package org.dom4j.tree;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: filip
 * Date: 5.4.2010
 * Time: 11:41:31
 * To change this template use File | Settings | File Templates.
 */
final class CloneHelper {
	public static <T> void setFinalField(Class<T> clazz, T object, String fieldName, Object value) {
		try {
			Field headerField = clazz.getDeclaredField(fieldName);
			headerField.setAccessible(true);
			headerField.set(object, value);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void setFinalLazyList(Class<T> clazz, T object, String fieldName) {
		setFinalField(clazz, object, fieldName, new LazyList<Object>());
	}

	public static <T> void setFinalContent(Class<T> clazz, T object) {
		setFinalField(clazz, object, "content", new LazyList<Object>());
	}
}
