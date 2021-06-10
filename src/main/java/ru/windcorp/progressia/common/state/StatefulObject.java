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

package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;

/**
 * An abstract class describing objects that have trackable state, such as
 * blocks, tiles or entities. This class contains the declaration of the state
 * mechanics (implementation of which is mostly delegated to
 * {@link StatefulObjectLayout}).
 * <h1>Structure</h1> Stateful objects are characterized by their
 * <i>likeness</i> and <i>state</i>.
 * <p>
 * An object's likeness is the combination of the object's runtime class (as in
 * {@link #getClass()}) and its ID. Objects that are "alike" share the same
 * internal structure, represented by a common {@linkplain StatefulObjectLayout
 * layout}. Likeness can be tested with {@link #isLike(Object)}.
 * <p>
 * An object's state is the combination of the values of an object's
 * {@linkplain StateField state fields}. State fields are different from object
 * fields as described by the Java language: not every object field is a part of
 * its state, although state fields are usually implemented as object fields.
 * Each state field is, in its turn, has the following characteristics:
 * <ul>
 * <li>ID, distinct from the ID of the stateful object to which it belongs.
 * State field IDs are only unique within fields of one likeness.</li>
 * <li>data type, which is one of Java primitive types or a compound (Object)
 * type.</li>
 * </ul>
 */
public abstract class StatefulObject extends Namespaced {

	private final StatefulObjectLayout layout;

	private final StateStorage storage;

	public StatefulObject(StatefulObjectRegistry<?> type, String id) {
		super(id);
		this.layout = type.getLayout(getId());
		this.storage = getLayout().createStorage();
	}

	/**
	 * Returns the {@link StatefulObjectLayout} describing objects that are
	 * {@linkplain #isLike(Object) "like"} this object. You probably don't need
	 * this.
	 * 
	 * @return this object's field layout
	 */
	public StatefulObjectLayout getLayout() {
		return layout;
	}

	/**
	 * Returns a {@link StateStorage} used by this object to store its state in
	 * memory. You probably don't need this.
	 * 
	 * @return this object's state storage
	 */
	public StateStorage getStorage() {
		return storage;
	}

	/*
	 * Field construction
	 */

	/**
	 * Used to keep track of the ordinal number of the next field to be
	 * requested.
	 */
	private int fieldOrdinal = 0;

	/**
	 * Returns a {@link StateFieldBuilder} set up to construct a field with the
	 * specified ID.
	 * <p>
	 * This method must only be called from the constructor, and the same
	 * sequence of invocations must occur during construction of each object
	 * with the same ID.
	 * 
	 * @param namespace
	 *            the namespace of the new field
	 * @param name
	 *            the name of the new field
	 * @return a configured builder
	 */
	protected StateFieldBuilder field(String id) {
		StateFieldBuilder builder = getLayout().getBuilder(id);

		builder.setOrdinal(fieldOrdinal);
		fieldOrdinal++;

		return builder;
	}

	/*
	 * IO
	 */

	/**
	 * Sets the state of this object according to the binary representation read
	 * from {@code input}. If {@code context == COMMS}, the state of local
	 * fields is unspecified after this operation.
	 * 
	 * @param input
	 *            a {@link DataInput} that a state can be read from
	 * @param context
	 *            the context
	 * @throws IOException
	 *             if the state is encoded poorly or an error occurs in
	 *             {@code input}
	 */
	public void read(DataInput input, IOContext context) throws IOException {
		getLayout().read(this, input, context);
	}

	/**
	 * Writes the binary representation of the state of this object to the
	 * {@code output}.
	 * 
	 * @param output
	 *            a {@link DataOutput} that a state can be written to
	 * @param context
	 *            the context
	 * @throws IOException
	 *             if an error occurs in {@code output}
	 */
	public void write(DataOutput output, IOContext context) throws IOException {
		getLayout().write(this, output, context);
	}

	/*
	 * Identity operations
	 */

	/**
	 * Turns {@code destination} into a deep copy of this object.
	 * <p>
	 * Changes the provided object so that:
	 * <ul>
	 * <li>the provided object equals this object according to
	 * {@link StatefulObject#equals(Object)}; and</li>
	 * <li>the provided object is independent of this object, meaning no change
	 * to {@code destination} can affect this object.</li>
	 * </ul>
	 * 
	 * @param destination
	 *            the object to copy this object into.
	 */
	public StatefulObject copy(StatefulObject destination) {
		Objects.requireNonNull(destination, "destination");

		if (destination == this) {
			throw new IllegalArgumentException("Cannot copy an object into itself");
		}

		if (destination.getClass() != this.getClass()) {
			throw new IllegalArgumentException(
					"Cannot copy from " + getClass() + " (ID " + getId() + ") to " + destination.getClass());
		}

		getLayout().copy(this, destination);
		return destination;
	}

	/**
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode() {
		return getLayout().computeHashCode(this);
	}

	/**
	 * Determines whether this object and {@code obj} have equal states.
	 * Stateful objects are considered equal iff they are
	 * {@linkplain #isLike(Object) "like"} and their binary representations
	 * match exactly.
	 * 
	 * @param obj
	 *            the object to examine
	 * @return {@code true} if {@code obj != null} and this object is equal to
	 *         {@code obj}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!this.isLike(obj))
			return false;

		return getLayout().areEqual(this, (StatefulObject) obj);
	}

	/**
	 * Checks whether the provided object is "like" this object.
	 * <p>
	 * Returns {@code true} iff this object and {@code obj} have the same ID and
	 * are instances of the same class.
	 * 
	 * @param obj
	 *            the object to examine
	 * @return {@code true} if {@code obj} is "like" this object
	 */
	public boolean isLike(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != this.getClass())
			return false;

		StatefulObject statefulObj = (StatefulObject) obj;

		if (statefulObj.getId().equals(this.getId()))
			return false;

		return true;
	}

}
