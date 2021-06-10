/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.common.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.google.common.collect.Iterables;

/**
 * A low-overhead, fixed-capacity stack that does not dispose of popped elements
 * but rather <i>stashes</i> them for later pushing. This allows the stack to
 * operate without creating new objects.
 * <p>
 * This object always contains references to {@link #getCapacity()} elements, of
 * which first {@link #getSize()} elements are present in the stack proper and
 * accessable, and the rest are <i>stashed</i>. When an element is popped, is
 * becomes stashed. When an element is pushed, it ceases to be stashed.
 * <p>
 * Stashed elements can be replaced with {@link #push(Object)}.
 * 
 * @author Javapony
 */
@SuppressWarnings("unchecked")
public class StashingStack<T> implements Iterable<T> {

	/**
	 * Stores all elements. Elements with indices
	 * <tt>[0;&nbsp;{@link #head}]</tt> are present in the stack, elements with
	 * indices <tt>({@link #head};&nbsp;contents.length]</tt> are stashed.
	 */
	private final Object[] contents;

	private transient List<T> contentsAsList;

	/**
	 * Index of the head of the stack in the {@link #contents} array, or
	 * <tt>-1</tt>, if the stack is empty.
	 */
	private int head = -1;

	protected StashingStack(Object[] stash, int dummy) {
		this.contents = stash;
	}

	/**
	 * Creates a new stack. Its stash is filled with {@code null}s.
	 * 
	 * @param capacity
	 *            stack's capacity
	 */
	public StashingStack(int capacity) {
		this((T[]) new Object[capacity], 0);
	}

	/**
	 * Creates a new stack with the supplied stash.
	 * 
	 * @param contents
	 *            elements that are put in the stash initially.
	 */
	public StashingStack(T[] contents) {
		this(contents.clone(), 0);
	}

	/**
	 * Creates a new stack with the supplied stash.
	 * 
	 * @param contents
	 *            elements that are put in the stash initially.
	 */
	public StashingStack(Iterable<T> contents) {
		this(Iterables.toArray(contents, Object.class), 0);
	}

	/**
	 * Creates a new stack. Its stash is filled with objects provided by
	 * {@code generator}. The generator's {@link Supplier#get() get()} method
	 * will only be invoked {@code capacity} times from within this constructor.
	 * 
	 * @param capacity
	 *            stack's capacity
	 * @param generator
	 *            a supplier of objects for the stash
	 */
	public StashingStack(int capacity, Supplier<T> generator) {
		this(capacity);

		for (int i = 0; i < contents.length; ++i) {
			contents[i] = generator.get();
		}
	}

	/**
	 * Returns the amount of elements this stack can store.
	 * 
	 * @return the capacity
	 */
	public int getCapacity() {
		return contents.length;
	}

	/**
	 * Returns the amount of elements that are currently in the stack.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return head + 1;
	}

	/**
	 * Checks whether this stack does not contain any elements.
	 * 
	 * @return {@code true} is this stack is empty
	 */
	public boolean isEmpty() {
		return getSize() == 0;
	}

	/**
	 * Checks whether this stack is full.
	 * 
	 * @return {@code true} is this stack is full
	 */
	public boolean isFull() {
		return getSize() == getCapacity();
	}

	/**
	 * Returns, but does not remove, the head of this stack. If the stack is
	 * empty returns {@code null}.
	 * 
	 * @return head of this stack or {@code null}
	 * @see #getHead()
	 */
	public T peek() {
		if (head < 0)
			return null;
		return (T) contents[head];
	}

	/**
	 * Returns, but does not remove, the head of this stack. If the stack is
	 * empty throws a {@link NoSuchElementException}.
	 * 
	 * @return head of this stack
	 * @throws NoSuchElementException
	 *             is the stack is empty
	 * @see #peek()
	 */
	public T getHead() {
		if (head < 0)
			throw new NoSuchElementException();
		return (T) contents[head];
	}

	/**
	 * Returns and removes the head of this stack. If the stack is empty returns
	 * {@code null}.
	 * 
	 * @return head of this stack or {@code null}
	 * @see #removeHead()
	 */
	public T pop() {
		if (head < 0)
			return null;
		return (T) contents[head--];
	}

	/**
	 * Returns and removes the head of this stack. If the stack is empty throws
	 * a {@link NoSuchElementException}.
	 * 
	 * @return head of this stack
	 * @throws NoSuchElementException
	 *             is the stack is empty
	 * @see #pop()
	 */
	public T removeHead() {
		if (head < 0)
			throw new NoSuchElementException();
		return (T) contents[head--];
	}

	/**
	 * Pushes a new element from the stash onto the stack. If the stack is
	 * already full throws an {@link IllegalStateException}. The state of the
	 * new element is not specified.
	 * 
	 * @return the new head
	 */
	public T push() {
		if (head == contents.length - 1) {
			throw new IllegalStateException();
		}

		return (T) contents[++head];
	}

	/**
	 * Pushes the specified element onto the stack. A stashed element is
	 * removed. If the stack is already full throws an
	 * {@link IllegalStateException}.
	 * 
	 * @param newElement
	 *            the element to push
	 * @return the new head
	 */
	public T push(T newElement) {
		if (head == contents.length - 1) {
			throw new IllegalStateException();
		}

		contents[++head] = newElement;
		return newElement;
	}

	/**
	 * Returns the specified element from the stack. Indexing starts from the
	 * bottom of the stack. If the index is out of bounds, an
	 * {@link IndexOutOfBoundsException} is thrown.
	 * 
	 * @param index
	 *            index of the element to retrieve,
	 *            <tt>[0;&nbsp;{@link #getSize()})</tt>
	 * @return the requested element
	 * @throws IndexOutOfBoundsException
	 *             if the index is negative or greater than head
	 */
	public T get(int index) {
		if (index > head) {
			throw new IndexOutOfBoundsException("Requested index " + index + " > head " + head);
		}

		return (T) contents[index];
	}

	/**
	 * Removes all elements from the stack.
	 */
	public void removeAll() {
		head = -1;
	}

	@Override
	public Iterator<T> iterator() {
		if (contentsAsList == null) {
			contentsAsList = Arrays.asList((T[]) contents);
		}

		return contentsAsList.subList(0, getSize()).iterator();
	}

}
