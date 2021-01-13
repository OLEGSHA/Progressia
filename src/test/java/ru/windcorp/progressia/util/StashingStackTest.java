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
 
package ru.windcorp.progressia.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.Iterators;

import ru.windcorp.progressia.common.util.StashingStack;

public class StashingStackTest {

	@Test
	public void normalOperation() {
		final int size = 256;
		final int operations = 2 << 16;
		final long seed = 0;

		Random random = new Random(seed);

		Deque<String> spares = new LinkedList<>();
		for (int i = 0; i < size; ++i) {
			spares.add(Integer.toString(i));
		}

		StashingStack<String> stashing = new StashingStack<>(spares);
		Deque<String> reference = new LinkedList<>();

		for (int i = 0; i < operations; ++i) {
			boolean isFull = stashing.isFull();
			boolean isEmpty = stashing.isEmpty();

			assertTrue("isFull", isFull == (reference.size() == size));
			assertTrue("isEmpty", isEmpty == reference.isEmpty());
			assertEquals("size", reference.size(), stashing.getSize());

			if (isFull || (!isEmpty && random.nextBoolean())) {
				if (random.nextBoolean()) {
					String popped = reference.pop();
					assertEquals("pop", popped, stashing.pop());
					spares.push(popped);
				} else {
					String peeked = reference.peek();
					assertEquals("peek", peeked, stashing.peek());
				}
			} else {
				reference.push(spares.pop());
				stashing.push();
			}
		}

		assertTrue(
			"remaining",
			Iterators.elementsEqual(
				reference.descendingIterator(),
				stashing.iterator()
			)
		);
	}

	@Test
	public void cornerCases() {
		StashingStack<Object> stack = new StashingStack<>(10);
		assertNull(stack.peek());
		assertNull(stack.pop());
		assertNull(stack.push());
	}

	@Test(expected = NoSuchElementException.class)
	public void noSuchElementWhenGetHead() {
		new StashingStack<>(10).getHead();
	}

	@Test(expected = NoSuchElementException.class)
	public void noSuchElementWhenRemoveHead() {
		new StashingStack<>(10).removeHead();
	}

}
