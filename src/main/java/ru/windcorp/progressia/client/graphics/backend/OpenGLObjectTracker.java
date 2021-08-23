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

package ru.windcorp.progressia.client.graphics.backend;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.IntConsumer;

public class OpenGLObjectTracker {

	public interface OpenGLDeletable {
		int getHandle();
	}

	private static final Collection<GLPhantomReference<OpenGLDeletable>> TO_DELETE = new ArrayList<>();
	private static final ReferenceQueue<OpenGLDeletable> DELETE_QUEUE = new ReferenceQueue<>();

	public synchronized static void register(OpenGLDeletable object, IntConsumer glDeleter) {
		GLPhantomReference<OpenGLDeletable> glRef = new GLPhantomReference<>(object, DELETE_QUEUE, object.getHandle(),
				glDeleter);
		TO_DELETE.add(glRef);
	}

	public static void deleteAllObjects() {
		for (GLPhantomReference<OpenGLDeletable> glRef : TO_DELETE) {
			glRef.clear();
		}
	}

	public static void deleteEnqueuedObjects() {
		while (true) {
			GLPhantomReference<?> glRef;
			glRef = (GLPhantomReference<?>) DELETE_QUEUE.poll();
			if (glRef == null) {
				break;
			} else {
				glRef.delete();
			}
		}
	}

	private static class GLPhantomReference<T> extends PhantomReference<T> {

		private final int referentGLhandle;
		private final IntConsumer GLDeleter;

		/**
		 * Creates a new phantom reference that refers to the given object and
		 * is registered with the given queue.
		 * <p>
		 * It is possible to create a phantom reference with a {@code null}
		 * queue, but such a reference is completely useless: Its {@code get}
		 * method will always return {@code null} and, since it does not have a
		 * queue, it will never be enqueued.
		 *
		 * @param referent
		 *            the object the new phantom reference will refer to
		 * @param q
		 *            the queue with which the reference is to be registered, or
		 *            {@code null} if registration is not required
		 */
		public GLPhantomReference(T referent, ReferenceQueue<? super T> q, int referentGLhandle,
				IntConsumer GLDeleter) {
			super(referent, q);
			this.referentGLhandle = referentGLhandle;
			this.GLDeleter = GLDeleter;
		}

		public void delete() {
			GLDeleter.accept(referentGLhandle);
		}
	}
}
