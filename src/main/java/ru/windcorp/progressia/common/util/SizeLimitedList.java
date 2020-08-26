package ru.windcorp.progressia.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;

import com.google.common.collect.ForwardingList;

public class SizeLimitedList<E> extends ForwardingList<E> {
	
	private static final class RandomAccessSizeLimitedList<E>
	extends SizeLimitedList<E>
	implements RandomAccess {
		protected RandomAccessSizeLimitedList(List<E> parent, int maxSize) {
			super(parent, maxSize);
		}
	}
	
	public static <E> List<E> wrap(List<E> list, int maxSize) {
		if (list instanceof RandomAccess) {
			return new RandomAccessSizeLimitedList<>(list, maxSize);
		} else {
			return new SizeLimitedList<>(list, maxSize);
		}
	}
	
	private final List<E> delegate;
	
	private final int maxSize;

	protected SizeLimitedList(List<E> parent, int maxSize) {
		this.delegate = Objects.requireNonNull(parent, "parent");
		this.maxSize = maxSize;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return standardAddAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> elements) {
		return standardAddAll(index, elements);
	}

	public boolean add(E e) {
		checkMaxSize();
		return delegate().add(e);
	}

	@Override
	public void add(int index, final E e) {
		checkMaxSize();
		delegate().add(index, e);
	}

	private void checkMaxSize() {
		if (size() >= maxSize) {
			throw new UnsupportedOperationException(
					"Maximum size " + maxSize + " reached"
			);
		}
	}

	@Override
	protected List<E> delegate() {
		return delegate;
	}

}
