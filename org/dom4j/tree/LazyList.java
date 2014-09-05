package org.dom4j.tree;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Jirsák Filip
 * @since 2.0
 */
public class LazyList<E> extends AbstractSequentialList<E> implements Serializable {
	private static final long serialVersionUID = 0;
	protected transient E[] indexedList = null;
	protected transient final Entry<E> header;
	protected transient int size = 0;

	public LazyList() {
		this.header = new Entry<E>(null, null, null);
		this.header.next = this.header;
		this.header.previous = this.header;
	}

	protected LazyList(Entry<E> header) {
		this.header = header;
	}

	@Override
	public boolean add(E element) {
		addElement(element, this.header);
		return true;
	}

	@Override
	public void add(int index, E element) {
		addElement(element, (index == this.size ? this.header : this.getEntry(index)));
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return this.addAll(0, collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		if (collection.isEmpty()) {
			return false;
		}
		Entry<E> entry = this.getEntryHeader(index).next;
		for (E element : collection) {
			this.addElement(element, entry);
		}
		return true;
	}

	@Override
	public void clear() {
		this.indexedList = null;

		this.size = 0;
		this.header.element = null;
		this.header.next = this.header;
		this.header.previous = this.header;
		this.modCount++;
	}

	@Override
	public E get(int index) {
		createIndexedList();
		return this.indexedList[index];
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new LazyListIterator(index);
	}

	@Override
	public E remove(int index) {
		return removeEntry(getEntry(index));
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		//TODO write unit test
		Range<E> range = this.getRange(fromIndex, toIndex);
		range.lower.previous.next = range.upper;
		range.upper.previous = range.lower.previous;
	}

	@Override
	public E set(int index, E element) {
		Entry<E> entry = this.getEntry(index);
		E oldValue = entry.element;
		entry.element = element;
		if (this.indexedList != null) {
			this.indexedList[index] = element;
		}
		return oldValue;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		//TODO write unit test
		Range<E> range = this.getRange(fromIndex, toIndex);
		Entry<E> newHeader = new Entry<E>(null, range.lower, range.upper);
		Entry<E> lastEntry = newHeader;
		while (range.lower != range.upper) {
			lastEntry.next = new Entry<E>(range.lower.element, null, lastEntry);
			lastEntry = lastEntry.next;
			range.lower = range.lower.next;
		}
		newHeader.previous = lastEntry;
		return new LazyList<E>(newHeader);
	}

	@Override
	public int size() {
		return this.size;
	}

	/**
	 * Returns a shallow copy of this <code>LazyList</code>. (The elements
	 * themselves are not cloned.)
	 *
	 * @return a shallow copy of this <tt>LazyList</tt> instance
	 */
	@Override
	public LazyList<E> clone() {
		LazyList<E> clone = new LazyList<E>();
		for (Entry<E> entry = header.next; entry != header; entry = entry.next) {
			clone.add(entry.element);
		}

		return clone;
	}


	protected void createIndexedList() {
		if (this.indexedList == null) {
			this.indexedList = (E[]) new Object[this.size];
			int index = 0;
			for (E element : this) {
				this.indexedList[index++] = element;
			}
		}

	}

	/**
	 * Inserts element before specified entry.
	 *
	 * @param e     element to insert
	 * @param entry entry to insert before
	 * @return inserted entry
	 */
	protected Entry<E> addElement(E e, Entry<E> entry) {
		this.indexedList = null;

		Entry<E> newEntry = new Entry<E>(e, entry, entry.previous);
		newEntry.previous.next = newEntry;
		newEntry.next.previous = newEntry;
		this.size++;
		this.modCount++;
		return newEntry;
	}

	/**
	 * Returns the indexed entry.
	 */
	protected Entry<E> getEntry(int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException(MessageFormat.format("Index: {0}, Size: {1}", index, this.size));
		}
		if (index == 0) {
			return this.header.next;
		}

		Entry<E> e = this.header;
		if (index < (this.size >> 1)) {
			for (int i = 0; i <= index; i++) {
				e = e.next;
			}
		} else {
			for (int i = this.size; i > index; i--) {
				e = e.previous;
			}
		}
		return e;
	}

	protected Entry<E> getEntryHeader(int index) {
		if (index < 0 || index > this.size) {
			throw new IndexOutOfBoundsException(MessageFormat.format("Index: {0}, Size: {1}", index, this.size));
		}
		if (index == 0) {
			return this.header.next;
		}

		Entry<E> e = this.header;
		if (index < (this.size >> 1)) {
			for (int i = 0; i < index; i++) {
				e = e.next;
			}
		} else {
			for (int i = this.size; i > index; i--) {
				e = e.previous;
			}
		}
		return e;
	}

	private Range<E> getRange(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex > this.size || fromIndex > toIndex) {
			throw new IndexOutOfBoundsException(MessageFormat.format("FromIndex: {0}, ToIndex: {1}, Size: {2}",
					fromIndex, toIndex, this.size));
		}

		int[] length = new int[]{fromIndex, toIndex - fromIndex, this.size - toIndex}; // lengths of three parts of list

		Range<E> range = new Range<E>();
		if (length[0] < length[2]) {
			range.lower = this.header;
			for (int i = 0; i <= fromIndex; i++) {
				range.lower = range.lower.next;
			}
			if (length[1] < length[2]) {
				range.upper = range.lower;
				for (int i = fromIndex; i <= toIndex; i++) {
					range.upper = range.upper.next;
				}
			} else {
				range.upper = this.header;
				for (int i = this.size; i > toIndex; i--) {
					range.upper = range.upper.previous;
				}
			}
		} else {
			range.upper = this.header;
			for (int i = this.size; i > toIndex; i--) {
				range.upper = range.upper.previous;
			}
			if (length[0] < length[1]) {
				range.lower = this.header;
				for (int i = 0; i <= fromIndex; i++) {
					range.lower = range.lower.next;
				}
			} else {
				range.lower = range.upper;
				for (int i = toIndex; i > fromIndex; i--) {
					range.lower = range.lower.previous;
				}
			}
		}
		return range;
	}

	protected E removeEntry(Entry<E> entry) {
		if (entry == header) {
			throw new NoSuchElementException();
		}

		this.indexedList = null;

		entry.previous.next = entry.next;
		entry.next.previous = entry.previous;
		size--;
		modCount++;
		return entry.element;
	}

	/**
	 * Serialize <code>LazyList</code> instance to a stream.
	 *
	 * @serialData The size of the list as int followed by all of its elements.
	 */
	private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.defaultWriteObject();
		stream.writeInt(size);
		for (Entry<E> entry = header.next; entry != header; entry = entry.next) {
			stream.writeObject(entry.element);
		}
	}

	/**
	 * Restore this <code>LazyList</code> instance from a stream.
	 */
	private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
		stream.defaultReadObject();
		CloneHelper.setFinalField(LazyList.class, this, "header", new Entry<E>(null, null, null));

		assert header != null;
		header.next = header.previous = header;
		
		int size = stream.readInt();
		E[] tempIndexedList = (E[]) new Object[size];
		for (int i = 0; i < size; i++) {
			E element = (E) stream.readObject();
			addElement(element, header);
			tempIndexedList[i] = element;
		}
		this.indexedList = tempIndexedList;
	}

	protected static class Entry<E> {

		E element;
		Entry<E> next;
		Entry<E> previous;

		Entry(E element, Entry<E> next, Entry<E> previous) {
			this.element = element;
			this.next = next;
			this.previous = previous;
		}
	}

	private static class Range<E> {
		Entry<E> lower;
		Entry<E> upper;
	}

	protected class LazyListIterator implements ListIterator<E> {

		private Entry<E> lastReturned = LazyList.this.header;
		private Entry<E> next;
		private int indexNext;
		private int expectedModCount = LazyList.this.modCount;

		LazyListIterator(int index) {
			this.next = LazyList.this.getEntryHeader(index);
		}

		public boolean hasNext() {
			return this.indexNext != LazyList.this.size;
		}

		public E next() {
			checkForComodification();
			if (this.indexNext == LazyList.this.size) {
				throw new NoSuchElementException();
			}
			this.lastReturned = this.next;
			this.next = this.next.next;
			this.indexNext++;
			return this.lastReturned.element;
		}

		public boolean hasPrevious() {
			return this.indexNext != 0;
		}

		public E previous() {
			checkForComodification();
			if (this.indexNext == 0) {
				throw new NoSuchElementException();
			}
			this.next = this.next.previous;
			this.lastReturned = this.next;
			this.indexNext--;
			return this.lastReturned.element;
		}

		public int nextIndex() {
			return this.indexNext;
		}

		public int previousIndex() {
			return this.indexNext - 1;
		}

		public void remove() {
			checkForOutside();
			checkForComodification();
			Entry<E> lastNext = this.lastReturned.next;
			LazyList.this.removeEntry(this.lastReturned);
			if (this.next == this.lastReturned) {
				this.next = lastNext;
			} else {
				this.indexNext--;
			}
			this.lastReturned = LazyList.this.header;
			this.expectedModCount++;
		}

		public void set(E e) {
			checkForOutside();
			checkForComodification();
			this.lastReturned.element = e;
		}

		public void add(E e) {
			checkForComodification();
			this.lastReturned = LazyList.this.header;
			LazyList.this.addElement(e, this.next);
			this.indexNext++;
			this.expectedModCount++;
		}

		final void checkForComodification() {
			if (LazyList.this.modCount != this.expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}

		final void checkForOutside() {
			if (this.lastReturned == LazyList.this.header) {
				throw new IllegalStateException();
			}
		}
	}
}
