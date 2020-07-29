package ru.windcorp.optica.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.Iterators;

import ru.windcorp.optica.common.util.StashingStack;

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
		
		assertTrue("remaining", Iterators.elementsEqual(
				reference.descendingIterator(),
				stashing.iterator()
		));
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
