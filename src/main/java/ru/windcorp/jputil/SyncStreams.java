/*
 * JPUtil
 * Copyright (C)  2019-2021  OLEGSHA/Javapony and contributors
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

package ru.windcorp.jputil;

import java.util.function.*;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Contains static methods to create {@link Stream Streams} that synchronize
 * their <a href=
 * "https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
 * terminal operations</a> on a given monitor.
 * 
 * @author Javapony
 *         (<a href="mailto:kvadropups@gmail.com">kvadropups@gmail.com</a>)
 */

// SonarLint: "Stream.peek" should be used with caution (java:S3864)
// We are implementing Stream, so peek() is required.
@SuppressWarnings("squid:S3864")

public class SyncStreams {

	public static class SyncStream<T> implements Stream<T> {

		private final Stream<T> parent;
		private final Object monitor;

		public SyncStream(Stream<T> parent, Object monitor) {
			this.parent = parent;
			this.monitor = monitor == null ? this : monitor;
		}

		public Stream<T> getParent() {
			return parent;
		}

		public Object getMonitor() {
			return monitor;
		}

		/*
		 * Returns null when child streams should sync on themselves
		 */
		private Object getInheritableMonitor() {
			return monitor == this ? null : monitor;
		}

		@Override
		public void close() {
			parent.close();
		}

		@Override
		public Iterator<T> iterator() {
			return parent.iterator();
		}

		@Override
		public Spliterator<T> spliterator() {
			return parent.spliterator();
		}

		@Override
		public boolean isParallel() {
			return parent.isParallel();
		}

		@Override
		public Stream<T> sequential() {
			return synchronizedStream(parent.sequential(), getInheritableMonitor());
		}

		@Override
		public Stream<T> parallel() {
			return synchronizedStream(parent.parallel(), getInheritableMonitor());
		}

		@Override
		public Stream<T> unordered() {
			return synchronizedStream(parent.unordered(), getInheritableMonitor());
		}

		@Override
		public Stream<T> onClose(Runnable closeHandler) {
			return synchronizedStream(parent.onClose(closeHandler), getInheritableMonitor());
		}

		@Override
		public Stream<T> filter(Predicate<? super T> predicate) {
			return synchronizedStream(parent.filter(predicate), getInheritableMonitor());
		}

		@Override
		public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
			return synchronizedStream(parent.map(mapper), getInheritableMonitor());
		}

		@Override
		public IntStream mapToInt(ToIntFunction<? super T> mapper) {
			return synchronizedStream(parent.mapToInt(mapper), getInheritableMonitor());
		}

		@Override
		public LongStream mapToLong(ToLongFunction<? super T> mapper) {
			return synchronizedStream(parent.mapToLong(mapper), getInheritableMonitor());
		}

		@Override
		public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
			return synchronizedStream(parent.mapToDouble(mapper), getInheritableMonitor());
		}

		@Override
		public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
			return synchronizedStream(parent.flatMap(mapper), getInheritableMonitor());
		}

		@Override
		public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
			return synchronizedStream(parent.flatMapToInt(mapper), getInheritableMonitor());
		}

		@Override
		public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
			return synchronizedStream(parent.flatMapToLong(mapper), getInheritableMonitor());
		}

		@Override
		public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
			return synchronizedStream(parent.flatMapToDouble(mapper), getInheritableMonitor());
		}

		@Override
		public Stream<T> distinct() {
			return synchronizedStream(parent.distinct(), getInheritableMonitor());
		}

		@Override
		public Stream<T> sorted() {
			return synchronizedStream(parent.sorted(), getInheritableMonitor());
		}

		@Override
		public Stream<T> sorted(Comparator<? super T> comparator) {
			return synchronizedStream(parent.sorted(comparator), getInheritableMonitor());
		}

		@Override
		public Stream<T> peek(Consumer<? super T> action) {
			return synchronizedStream(parent.peek(action), getInheritableMonitor());
		}

		@Override
		public Stream<T> limit(long maxSize) {
			return synchronizedStream(parent.limit(maxSize), getInheritableMonitor());
		}

		@Override
		public Stream<T> skip(long n) {
			return synchronizedStream(parent.skip(n), getInheritableMonitor());
		}

		@Override
		public T reduce(T identity, BinaryOperator<T> accumulator) {
			synchronized (monitor) {
				return parent.reduce(identity, accumulator);
			}
		}

		@Override
		public Optional<T> reduce(BinaryOperator<T> accumulator) {
			synchronized (monitor) {
				return parent.reduce(accumulator);
			}
		}

		@Override
		public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
			synchronized (monitor) {
				return parent.reduce(identity, accumulator, combiner);
			}
		}

		@Override
		public void forEach(Consumer<? super T> action) {
			synchronized (monitor) {
				parent.forEach(action);
			}
		}

		@Override
		public void forEachOrdered(Consumer<? super T> action) {
			synchronized (monitor) {
				parent.forEachOrdered(action);
			}
		}

		@Override
		public Object[] toArray() {
			synchronized (monitor) {
				return parent.toArray();
			}
		}

		@Override
		public <A> A[] toArray(IntFunction<A[]> generator) {
			synchronized (monitor) {
				return parent.toArray(generator);
			}
		}

		@Override
		public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
			synchronized (monitor) {
				return parent.collect(supplier, accumulator, combiner);
			}
		}

		@Override
		public <R, A> R collect(Collector<? super T, A, R> collector) {
			synchronized (monitor) {
				return parent.collect(collector);
			}
		}

		@Override
		public Optional<T> min(Comparator<? super T> comparator) {
			synchronized (monitor) {
				return parent.min(comparator);
			}
		}

		@Override
		public Optional<T> max(Comparator<? super T> comparator) {
			synchronized (monitor) {
				return parent.max(comparator);
			}
		}

		@Override
		public long count() {
			synchronized (monitor) {
				return parent.count();
			}
		}

		@Override
		public boolean anyMatch(Predicate<? super T> predicate) {
			synchronized (monitor) {
				return parent.anyMatch(predicate);
			}
		}

		@Override
		public boolean allMatch(Predicate<? super T> predicate) {
			synchronized (monitor) {
				return parent.allMatch(predicate);
			}
		}

		@Override
		public boolean noneMatch(Predicate<? super T> predicate) {
			synchronized (monitor) {
				return parent.noneMatch(predicate);
			}
		}

		@Override
		public Optional<T> findFirst() {
			synchronized (monitor) {
				return parent.findFirst();
			}
		}

		@Override
		public Optional<T> findAny() {
			synchronized (monitor) {
				return parent.findAny();
			}
		}

	}

	public static class SyncIntStream implements IntStream {

		private final IntStream parent;
		private final Object monitor;

		public SyncIntStream(IntStream parent, Object monitor) {
			this.parent = parent;
			this.monitor = monitor == null ? this : monitor;
		}

		public IntStream getParent() {
			return parent;
		}

		public Object getMonitor() {
			return monitor;
		}

		/*
		 * Returns null when child streams should sync on themselves
		 */
		private Object getInheritableMonitor() {
			return monitor == this ? null : monitor;
		}

		@Override
		public void close() {
			parent.close();
		}

		@Override
		public PrimitiveIterator.OfInt iterator() {
			return parent.iterator();
		}

		@Override
		public Spliterator.OfInt spliterator() {
			return parent.spliterator();
		}

		@Override
		public boolean isParallel() {
			return parent.isParallel();
		}

		@Override
		public IntStream sequential() {
			return synchronizedStream(parent.sequential(), getInheritableMonitor());
		}

		@Override
		public IntStream parallel() {
			return synchronizedStream(parent.parallel(), getInheritableMonitor());
		}

		@Override
		public IntStream unordered() {
			return synchronizedStream(parent.unordered(), getInheritableMonitor());
		}

		@Override
		public IntStream onClose(Runnable closeHandler) {
			return synchronizedStream(parent.onClose(closeHandler), getInheritableMonitor());
		}

		@Override
		public IntStream filter(IntPredicate predicate) {
			return synchronizedStream(parent.filter(predicate), getInheritableMonitor());
		}

		@Override
		public IntStream map(IntUnaryOperator mapper) {
			return synchronizedStream(parent.map(mapper), getInheritableMonitor());
		}

		@Override
		public LongStream mapToLong(IntToLongFunction mapper) {
			return synchronizedStream(parent.mapToLong(mapper), getInheritableMonitor());
		}

		@Override
		public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
			return synchronizedStream(parent.mapToDouble(mapper), getInheritableMonitor());
		}

		@Override
		public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
			return synchronizedStream(parent.mapToObj(mapper), getInheritableMonitor());
		}

		@Override
		public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
			return synchronizedStream(parent.flatMap(mapper), getInheritableMonitor());
		}

		@Override
		public IntStream distinct() {
			return synchronizedStream(parent.distinct(), getInheritableMonitor());
		}

		@Override
		public IntStream sorted() {
			return synchronizedStream(parent.sorted(), getInheritableMonitor());
		}

		@Override
		public IntStream peek(IntConsumer action) {
			return synchronizedStream(parent.peek(action), getInheritableMonitor());
		}

		@Override
		public IntStream limit(long maxSize) {
			return synchronizedStream(parent.limit(maxSize), getInheritableMonitor());
		}

		@Override
		public IntStream skip(long n) {
			return synchronizedStream(parent.skip(n), getInheritableMonitor());
		}

		@Override
		public LongStream asLongStream() {
			return synchronizedStream(parent.asLongStream(), getInheritableMonitor());
		}

		@Override
		public DoubleStream asDoubleStream() {
			return synchronizedStream(parent.asDoubleStream(), getInheritableMonitor());
		}

		@Override
		public Stream<Integer> boxed() {
			return synchronizedStream(parent.boxed(), getInheritableMonitor());
		}

		@Override
		public int reduce(int identity, IntBinaryOperator accumulator) {
			synchronized (monitor) {
				return parent.reduce(identity, accumulator);
			}
		}

		@Override
		public OptionalInt reduce(IntBinaryOperator accumulator) {
			synchronized (monitor) {
				return parent.reduce(accumulator);
			}
		}

		@Override
		public void forEach(IntConsumer action) {
			synchronized (monitor) {
				parent.forEach(action);
			}
		}

		@Override
		public void forEachOrdered(IntConsumer action) {
			synchronized (monitor) {
				parent.forEachOrdered(action);
			}
		}

		@Override
		public int[] toArray() {
			synchronized (monitor) {
				return parent.toArray();
			}
		}

		@Override
		public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
			synchronized (monitor) {
				return parent.collect(supplier, accumulator, combiner);
			}
		}

		@Override
		public int sum() {
			synchronized (monitor) {
				return parent.sum();
			}
		}

		@Override
		public OptionalInt min() {
			synchronized (monitor) {
				return parent.min();
			}
		}

		@Override
		public OptionalInt max() {
			synchronized (monitor) {
				return parent.max();
			}
		}

		@Override
		public long count() {
			synchronized (monitor) {
				return parent.count();
			}
		}

		@Override
		public OptionalDouble average() {
			synchronized (monitor) {
				return parent.average();
			}
		}

		@Override
		public IntSummaryStatistics summaryStatistics() {
			synchronized (monitor) {
				return parent.summaryStatistics();
			}
		}

		@Override
		public boolean anyMatch(IntPredicate predicate) {
			synchronized (monitor) {
				return parent.anyMatch(predicate);
			}
		}

		@Override
		public boolean allMatch(IntPredicate predicate) {
			synchronized (monitor) {
				return parent.allMatch(predicate);
			}
		}

		@Override
		public boolean noneMatch(IntPredicate predicate) {
			synchronized (monitor) {
				return parent.noneMatch(predicate);
			}
		}

		@Override
		public OptionalInt findFirst() {
			synchronized (monitor) {
				return parent.findFirst();
			}
		}

		@Override
		public OptionalInt findAny() {
			synchronized (monitor) {
				return parent.findAny();
			}
		}

	}

	public static class SyncLongStream implements LongStream {

		private final LongStream parent;
		private final Object monitor;

		public SyncLongStream(LongStream parent, Object monitor) {
			this.parent = parent;
			this.monitor = monitor == null ? this : monitor;
		}

		public LongStream getParent() {
			return parent;
		}

		public Object getMonitor() {
			return monitor;
		}

		/*
		 * Returns null when child streams should sync on themselves
		 */
		private Object getInheritableMonitor() {
			return monitor == this ? null : monitor;
		}

		@Override
		public void close() {
			parent.close();
		}

		@Override
		public PrimitiveIterator.OfLong iterator() {
			return parent.iterator();
		}

		@Override
		public Spliterator.OfLong spliterator() {
			return parent.spliterator();
		}

		@Override
		public boolean isParallel() {
			return parent.isParallel();
		}

		@Override
		public LongStream sequential() {
			return synchronizedStream(parent.sequential(), getInheritableMonitor());
		}

		@Override
		public LongStream parallel() {
			return synchronizedStream(parent.parallel(), getInheritableMonitor());
		}

		@Override
		public LongStream unordered() {
			return synchronizedStream(parent.unordered(), getInheritableMonitor());
		}

		@Override
		public LongStream onClose(Runnable closeHandler) {
			return synchronizedStream(parent.onClose(closeHandler), getInheritableMonitor());
		}

		@Override
		public LongStream filter(LongPredicate predicate) {
			return synchronizedStream(parent.filter(predicate), getInheritableMonitor());
		}

		@Override
		public LongStream map(LongUnaryOperator mapper) {
			return synchronizedStream(parent.map(mapper), getInheritableMonitor());
		}

		@Override
		public IntStream mapToInt(LongToIntFunction mapper) {
			return synchronizedStream(parent.mapToInt(mapper), getInheritableMonitor());
		}

		@Override
		public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
			return synchronizedStream(parent.mapToDouble(mapper), getInheritableMonitor());
		}

		@Override
		public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
			return synchronizedStream(parent.mapToObj(mapper), getInheritableMonitor());
		}

		@Override
		public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
			return synchronizedStream(parent.flatMap(mapper), getInheritableMonitor());
		}

		@Override
		public LongStream distinct() {
			return synchronizedStream(parent.distinct(), getInheritableMonitor());
		}

		@Override
		public LongStream sorted() {
			return synchronizedStream(parent.sorted(), getInheritableMonitor());
		}

		@Override
		public LongStream peek(LongConsumer action) {
			return synchronizedStream(parent.peek(action), getInheritableMonitor());
		}

		@Override
		public LongStream limit(long maxSize) {
			return synchronizedStream(parent.limit(maxSize), getInheritableMonitor());
		}

		@Override
		public LongStream skip(long n) {
			return synchronizedStream(parent.skip(n), getInheritableMonitor());
		}

		@Override
		public DoubleStream asDoubleStream() {
			return synchronizedStream(parent.asDoubleStream(), getInheritableMonitor());
		}

		@Override
		public Stream<Long> boxed() {
			return synchronizedStream(parent.boxed(), getInheritableMonitor());
		}

		@Override
		public long reduce(long identity, LongBinaryOperator accumulator) {
			synchronized (monitor) {
				return parent.reduce(identity, accumulator);
			}
		}

		@Override
		public OptionalLong reduce(LongBinaryOperator accumulator) {
			synchronized (monitor) {
				return parent.reduce(accumulator);
			}
		}

		@Override
		public void forEach(LongConsumer action) {
			synchronized (monitor) {
				parent.forEach(action);
			}
		}

		@Override
		public void forEachOrdered(LongConsumer action) {
			synchronized (monitor) {
				parent.forEachOrdered(action);
			}
		}

		@Override
		public long[] toArray() {
			synchronized (monitor) {
				return parent.toArray();
			}
		}

		@Override
		public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
			synchronized (monitor) {
				return parent.collect(supplier, accumulator, combiner);
			}
		}

		@Override
		public long sum() {
			synchronized (monitor) {
				return parent.sum();
			}
		}

		@Override
		public OptionalLong min() {
			synchronized (monitor) {
				return parent.min();
			}
		}

		@Override
		public OptionalLong max() {
			synchronized (monitor) {
				return parent.max();
			}
		}

		@Override
		public long count() {
			synchronized (monitor) {
				return parent.count();
			}
		}

		@Override
		public OptionalDouble average() {
			synchronized (monitor) {
				return parent.average();
			}
		}

		@Override
		public LongSummaryStatistics summaryStatistics() {
			synchronized (monitor) {
				return parent.summaryStatistics();
			}
		}

		@Override
		public boolean anyMatch(LongPredicate predicate) {
			synchronized (monitor) {
				return parent.anyMatch(predicate);
			}
		}

		@Override
		public boolean allMatch(LongPredicate predicate) {
			synchronized (monitor) {
				return parent.allMatch(predicate);
			}
		}

		@Override
		public boolean noneMatch(LongPredicate predicate) {
			synchronized (monitor) {
				return parent.noneMatch(predicate);
			}
		}

		@Override
		public OptionalLong findFirst() {
			synchronized (monitor) {
				return parent.findFirst();
			}
		}

		@Override
		public OptionalLong findAny() {
			synchronized (monitor) {
				return parent.findAny();
			}
		}

	}

	public static class SyncDoubleStream implements DoubleStream {

		private final DoubleStream parent;
		private final Object monitor;

		public SyncDoubleStream(DoubleStream parent, Object monitor) {
			this.parent = parent;
			this.monitor = monitor == null ? this : monitor;
		}

		public DoubleStream getParent() {
			return parent;
		}

		public Object getMonitor() {
			return monitor;
		}

		/*
		 * Returns null when child streams should sync on themselves
		 */
		private Object getInheritableMonitor() {
			return monitor == this ? null : monitor;
		}

		@Override
		public void close() {
			parent.close();
		}

		@Override
		public PrimitiveIterator.OfDouble iterator() {
			return parent.iterator();
		}

		@Override
		public Spliterator.OfDouble spliterator() {
			return parent.spliterator();
		}

		@Override
		public boolean isParallel() {
			return parent.isParallel();
		}

		@Override
		public DoubleStream sequential() {
			return synchronizedStream(parent.sequential(), getInheritableMonitor());
		}

		@Override
		public DoubleStream parallel() {
			return synchronizedStream(parent.parallel(), getInheritableMonitor());
		}

		@Override
		public DoubleStream unordered() {
			return synchronizedStream(parent.unordered(), getInheritableMonitor());
		}

		@Override
		public DoubleStream onClose(Runnable closeHandler) {
			return synchronizedStream(parent.onClose(closeHandler), getInheritableMonitor());
		}

		@Override
		public DoubleStream filter(DoublePredicate predicate) {
			return synchronizedStream(parent.filter(predicate), getInheritableMonitor());
		}

		@Override
		public DoubleStream map(DoubleUnaryOperator mapper) {
			return synchronizedStream(parent.map(mapper), getInheritableMonitor());
		}

		@Override
		public IntStream mapToInt(DoubleToIntFunction mapper) {
			return synchronizedStream(parent.mapToInt(mapper), getInheritableMonitor());
		}

		@Override
		public LongStream mapToLong(DoubleToLongFunction mapper) {
			return synchronizedStream(parent.mapToLong(mapper), getInheritableMonitor());
		}

		@Override
		public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
			return synchronizedStream(parent.mapToObj(mapper), getInheritableMonitor());
		}

		@Override
		public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
			return synchronizedStream(parent.flatMap(mapper), getInheritableMonitor());
		}

		@Override
		public DoubleStream distinct() {
			return synchronizedStream(parent.distinct(), getInheritableMonitor());
		}

		@Override
		public DoubleStream sorted() {
			return synchronizedStream(parent.sorted(), getInheritableMonitor());
		}

		@Override
		public DoubleStream peek(DoubleConsumer action) {
			return synchronizedStream(parent.peek(action), getInheritableMonitor());
		}

		@Override
		public DoubleStream limit(long maxSize) {
			return synchronizedStream(parent.limit(maxSize), getInheritableMonitor());
		}

		@Override
		public DoubleStream skip(long n) {
			return synchronizedStream(parent.skip(n), getInheritableMonitor());
		}

		@Override
		public Stream<Double> boxed() {
			return synchronizedStream(parent.boxed(), getInheritableMonitor());
		}

		@Override
		public double reduce(double identity, DoubleBinaryOperator accumulator) {
			synchronized (monitor) {
				return parent.reduce(identity, accumulator);
			}
		}

		@Override
		public OptionalDouble reduce(DoubleBinaryOperator accumulator) {
			synchronized (monitor) {
				return parent.reduce(accumulator);
			}
		}

		@Override
		public void forEach(DoubleConsumer action) {
			synchronized (monitor) {
				parent.forEach(action);
			}
		}

		@Override
		public void forEachOrdered(DoubleConsumer action) {
			synchronized (monitor) {
				parent.forEachOrdered(action);
			}
		}

		@Override
		public double[] toArray() {
			synchronized (monitor) {
				return parent.toArray();
			}
		}

		@Override
		public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
			synchronized (monitor) {
				return parent.collect(supplier, accumulator, combiner);
			}
		}

		@Override
		public double sum() {
			synchronized (monitor) {
				return parent.sum();
			}
		}

		@Override
		public OptionalDouble min() {
			synchronized (monitor) {
				return parent.min();
			}
		}

		@Override
		public OptionalDouble max() {
			synchronized (monitor) {
				return parent.max();
			}
		}

		@Override
		public long count() {
			synchronized (monitor) {
				return parent.count();
			}
		}

		@Override
		public OptionalDouble average() {
			synchronized (monitor) {
				return parent.average();
			}
		}

		@Override
		public DoubleSummaryStatistics summaryStatistics() {
			synchronized (monitor) {
				return parent.summaryStatistics();
			}
		}

		@Override
		public boolean anyMatch(DoublePredicate predicate) {
			synchronized (monitor) {
				return parent.anyMatch(predicate);
			}
		}

		@Override
		public boolean allMatch(DoublePredicate predicate) {
			synchronized (monitor) {
				return parent.allMatch(predicate);
			}
		}

		@Override
		public boolean noneMatch(DoublePredicate predicate) {
			synchronized (monitor) {
				return parent.noneMatch(predicate);
			}
		}

		@Override
		public OptionalDouble findFirst() {
			synchronized (monitor) {
				return parent.findFirst();
			}
		}

		@Override
		public OptionalDouble findAny() {
			synchronized (monitor) {
				return parent.findAny();
			}
		}

	}

	/**
	 * Wraps the given {@link Stream} to make all <a href=
	 * "https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
	 * terminal operations</a> acquire the provided monitor's lock before
	 * execution. Intermediate operations return streams that are also
	 * synchronized on the same object. The created stream will behave
	 * identically to the provided stream in all other aspects. Use this to
	 * synchronize access to stream's source.
	 * <p>
	 * <i>The returned {@code Stream}'s {@link Stream#iterator() iterator()} and
	 * {@link Stream#spliterator() spliterator()} methods return regular
	 * non-synchronized iterators and spliterators respectively</i>. It is the
	 * user's responsibility to avoid concurrency issues:
	 * 
	 * <pre>
	 * synchronized (stream.getMonitor()) {
	 *     Iterator<T> it = stream.iterator();
	 *         ...
	 * }
	 * </pre>
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * Set&lt;Object&gt; s = Collections.synchronizedSet(new HashSet&lt;&gt;());
	 *    ...
	 * Stream&lt;?&gt; stream = SyncStreams.synchronizedStream(s.stream(), s);
	 * stream = stream.map(Object::toString); // Still synchronized
	 * stream.forEach(System.out::println); // Should never throw a ConcurrentModificationException
	 * </pre>
	 * 
	 * @param <T>
	 *            the class of objects in the Stream
	 * @param stream
	 *            the stream to wrap.
	 * @param monitor
	 *            the object that the stream will use for synchronization. When
	 *            {@code null}, the stream will synchronize on itself.
	 * @return a {@link SyncStream SyncStream&lt;T&gt;} synchronized on
	 *         {@code monitor} and backed by {@code stream}.
	 * @throws NullPointerException
	 *             if {@code stream == null}.
	 */
	public static <T> SyncStream<T> synchronizedStream(Stream<T> stream, Object monitor) {
		Objects.requireNonNull(stream, "stream cannot be null");
		return new SyncStream<>(stream, monitor);
	}

	/**
	 * Wraps the given {@link IntStream} to make all <a href=
	 * "https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
	 * terminal operations</a> acquire the provided monitor's lock before
	 * execution. Intermediate operations return streams that are also
	 * synchronized on the same object. The created stream will behave
	 * identically to the provided stream in all other aspects. Use this to
	 * synchronize access to stream's source.
	 * <p>
	 * <i>The returned {@code IntStream}'s {@link IntStream#iterator()
	 * iterator()} and {@link IntStream#spliterator() spliterator()} methods
	 * return regular non-synchronized iterators and spliterators
	 * respectively</i>. It is the user's responsibility to avoid concurrency
	 * issues:
	 * 
	 * <pre>
	 * synchronized (stream.getMonitor()) {
	 *     PrimitiveIterator.OfInt it = stream.iterator();
	 *         ...
	 * }
	 * </pre>
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * Set&lt;Object&gt; s = Collections.synchronizedSet(new HashSet&lt;&gt;());
	 *    ...
	 * IntStream stream = SyncStreams.synchronizedStream(s.stream().mapToInt(Object::hashCode), s);
	 * stream = stream.map(i -&gt; i % 67); // Still synchronized
	 * stream.forEach(System.out::println); // Should never throw a ConcurrentModificationException
	 * </pre>
	 * 
	 * @param stream
	 *            the stream to wrap.
	 * @param monitor
	 *            the object that the stream will use for synchronization. When
	 *            {@code null}, the stream will synchronize on itself.
	 * @return a {@link SyncIntStream} synchronized on {@code monitor} and
	 *         backed by {@code stream}.
	 * @throws NullPointerException
	 *             if {@code stream == null}.
	 */
	public static SyncIntStream synchronizedStream(IntStream stream, Object monitor) {
		Objects.requireNonNull(stream, "stream cannot be null");
		return new SyncIntStream(stream, monitor);
	}

	/**
	 * Wraps the given {@link LongStream} to make all <a href=
	 * "https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
	 * terminal operations</a> acquire the provided monitor's lock before
	 * execution. Intermediate operations return streams that are also
	 * synchronized on the same object. The created stream will behave
	 * identically to the provided stream in all other aspects. Use this to
	 * synchronize access to stream's source.
	 * <p>
	 * <i>The returned {@code LongStream}'s {@link LongStream#iterator()
	 * iterator()} and {@link LongStream#spliterator() spliterator()} methods
	 * return regular non-synchronized iterators and spliterators
	 * respectively</i>. It is the user's responsibility to avoid concurrency
	 * issues:
	 * 
	 * <pre>
	 * synchronized (stream.getMonitor()) {
	 *     PrimitiveIterator.OfLong it = stream.iterator();
	 *         ...
	 * }
	 * </pre>
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * Set&lt;Object&gt; s = Collections.synchronizedSet(new HashSet&lt;&gt;());
	 *    ...
	 * LongStream stream = SyncStreams.synchronizedStream(s.stream().mapToLong(o -&gt; (long) o.hashCode()), s);
	 * stream = stream.map(i -&gt; i % 67); // Still synchronized
	 * stream.forEach(System.out::println); // Should never throw a ConcurrentModificationException
	 * </pre>
	 * 
	 * @param stream
	 *            the stream to wrap.
	 * @param monitor
	 *            the object that the stream will use for synchronization. When
	 *            {@code null}, the stream will synchronize on itself.
	 * @return a {@link SyncLongStream} synchronized on {@code monitor} and
	 *         backed by {@code stream}.
	 * @throws NullPointerException
	 *             if {@code stream == null}.
	 */
	public static SyncLongStream synchronizedStream(LongStream stream, Object monitor) {
		Objects.requireNonNull(stream, "stream cannot be null");
		return new SyncLongStream(stream, monitor);
	}

	/**
	 * Wraps the given {@link DoubleStream} to make all <a href=
	 * "https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
	 * terminal operations</a> acquire the provided monitor's lock before
	 * execution. Intermediate operations return streams that are also
	 * synchronized on the same object. The created stream will behave
	 * identically to the provided stream in all other aspects. Use this to
	 * synchronize access to stream's source.
	 * <p>
	 * <i>The returned {@code DoubleStream}'s {@link DoubleStream#iterator()
	 * iterator()} and {@link DoubleStream#spliterator() spliterator()} methods
	 * return regular non-synchronized iterators and spliterators
	 * respectively</i>. It is the user's responsibility to avoid concurrency
	 * issues:
	 * 
	 * <pre>
	 * synchronized (stream.getMonitor()) {
	 *     PrimitiveIterator.OfDouble it = stream.iterator();
	 *         ...
	 * }
	 * </pre>
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * Set&lt;Object&gt; s = Collections.synchronizedSet(new HashSet&lt;&gt;());
	 *    ...
	 * DoubleStream stream = SyncStreams.synchronizedStream(s.stream().mapToLong(o -&gt; (double) o.hashCode()), s);
	 * stream = stream.map(Math::sin); // Still synchronized
	 * stream.forEach(System.out::println); // Should never throw a ConcurrentModificationException
	 * </pre>
	 * 
	 * @param stream
	 *            the stream to wrap.
	 * @param monitor
	 *            the object that the stream will use for synchronization. When
	 *            {@code null}, the stream will synchronize on itself.
	 * @return a {@link SyncDoubleStream} synchronized on {@code monitor} and
	 *         backed by {@code stream}.
	 * @throws NullPointerException
	 *             if {@code stream == null}.
	 */
	public static SyncDoubleStream synchronizedStream(DoubleStream stream, Object monitor) {
		Objects.requireNonNull(stream, "stream cannot be null");
		return new SyncDoubleStream(stream, monitor);
	}

	/*
	 * Private constructor
	 */
	private SyncStreams() {
	}

}
