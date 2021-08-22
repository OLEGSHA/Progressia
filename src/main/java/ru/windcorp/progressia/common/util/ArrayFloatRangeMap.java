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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ArrayFloatRangeMap<E> implements FloatRangeMap<E> {

	protected static class Node<E> implements Comparable<Node<E>> {
		public float pos;
		public E value;

		public Node(float pos, E value) {
			this.pos = pos;
			this.value = value;
		}

		@Override
		public int compareTo(Node<E> o) {
			return Float.compare(pos, o.pos);
		}
	}

	/*
	 * Expects a random-access list
	 */
	protected final List<Node<E>> nodes;
	protected int ranges = 0;
	
	protected static final int DEFAULT_CAPACITY = 16;

	public ArrayFloatRangeMap(int capacity) {
		this.nodes = new ArrayList<>(2 * capacity);
	}
	
	public ArrayFloatRangeMap() {
		this(DEFAULT_CAPACITY);
	}

	@Override
	public int size() {
		return this.ranges;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			private int nextIndex = 0;
			
			{
				assert nodes.isEmpty() || nodes.get(nextIndex).value != null;
			}

			private void findNext() {
				while (nextIndex < nodes.size()) {
					nextIndex++;
					Node<E> node = nodes.get(nextIndex);
					if (node.value != null) return;
				}
			}
			
			@Override
			public boolean hasNext() {
				return nextIndex < nodes.size();
			}
			
			@Override
			public E next() {
				E result = nodes.get(nextIndex).value;
				findNext();
				return result;
			}
		};
	}

	/**
	 * Returns an index of the smallest {@link Node} larger than or exactly at
	 * {@code position}.
	 * 
	 * @param position the position to look up
	 * @return an index in the {@link #nodes} list containing the first
	 *         {@link Node} whose {@link Node#pos} is not smaller than
	 *         {@code position}, or {@code nodes.size()} if no such index exists
	 */
	protected int findCeiling(float position) {

		/*
		 * Implementation based on OpenJDK's
		 * Collections.indexedBinarySearch(List, Comparator)
		 */

		int low = 0;
		int high = nodes.size() - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			float midVal = nodes.get(mid).pos;
			int cmp = Float.compare(midVal, position);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		
		return low; // the insertion point is the desired index 
	}
	
	/**
	 * Returns an index of the largest {@link Node} smaller than or exactly at
	 * {@code position}.
	 * 
	 * @param position the position to look up
	 * @return an index in the {@link #nodes} list containing the last
	 *         {@link Node} whose {@link Node#pos} is not greater than
	 *         {@code position}, or {@code -1} if no such index exists
	 */
	protected int findFloor(float position) {

		/*
		 * Implementation based on OpenJDK's
		 * Collections.indexedBinarySearch(List, Comparator)
		 */

		int low = 0;
		int high = nodes.size() - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			float midVal = nodes.get(mid).pos;
			int cmp = Float.compare(midVal, position);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		
		return low - 1; // the insertion point immediately follows the desired index
	}
	
	protected Node<E> getEffectiveNode(float at) {
		int effectiveNodeIndex = findFloor(at);
		if (effectiveNodeIndex < 0) return null;
		return nodes.get(effectiveNodeIndex);
	}
	
	@Override
	public E get(float at) {
		Node<E> effectiveNode = getEffectiveNode(at);
		return effectiveNode == null ? null : effectiveNode.value;
	}
	
	@Override
	public void put(float min, float max, E element) {
		Objects.requireNonNull(element, "element");
		
		if (!(max > min)) // This funky construction also deals with NaNs since NaNs always fail any comparison
		{
			throw new IllegalArgumentException(max + " is not greater than " + min);
		}
		
		int indexOfInsertionOfMin = findCeiling(min);
		
		nodes.add(indexOfInsertionOfMin, new Node<E>(min, element));
		ranges++;
		
		ListIterator<Node<E>> it = nodes.listIterator(indexOfInsertionOfMin + 1);
		E elementEffectiveImmediatelyAfterInsertedRange = null;
		
		if (indexOfInsertionOfMin > 0) {
			elementEffectiveImmediatelyAfterInsertedRange = nodes.get(indexOfInsertionOfMin - 1).value;
		}
		
		while (it.hasNext()) {
			Node<E> node = it.next();
			
			if (node.pos >= max) {
				break;
			}
			
			elementEffectiveImmediatelyAfterInsertedRange = node.value;
			if (elementEffectiveImmediatelyAfterInsertedRange != null) {
				// Removing an actual range
				ranges--;
			}
			it.remove();
		}
		
		if (max != Float.POSITIVE_INFINITY) {
			nodes.add(indexOfInsertionOfMin + 1, new Node<E>(max, elementEffectiveImmediatelyAfterInsertedRange));
			
			if (elementEffectiveImmediatelyAfterInsertedRange != null) {
				// We might have added one right back
				ranges++;
			}			
		}
		
	}

}
