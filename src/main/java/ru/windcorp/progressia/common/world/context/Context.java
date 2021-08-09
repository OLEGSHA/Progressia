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
package ru.windcorp.progressia.common.world.context;

import ru.windcorp.progressia.common.world.generic.context.AbstractContextRO;

/**
 * A cursor-like object for retrieving information about an in-game environment.
 * A context object typically holds a reference to some sort of data structure
 * and a cursor pointing to a location in that data structure. The exact meaning
 * of "environment" and "location" is defined by extending interfaces. The terms
 * <em>relevant</em> and <em>implied</em> should be understood to refer to the
 * aforementioned location.
 * <p>
 * Context objects are intended to be the primary way of interacting for in-game
 * content. Wherever possible, context objects should be preferred over other
 * means of accessing game structures.
 * <h2 id="validity">Context Validity</h2>
 * Context objects may only be used while they are valid to avoid undefined
 * behavior. There exists no programmatic way to determine a context's validity;
 * it is the responsibility of the programmer to avoid interacting with invalid
 * contexts.
 * <p>
 * Contexts are usually acquired as method parameters. Unless stated otherwise,
 * the context is valid until the invoked method returns; the only exception to
 * this rule is subcontexting (see below). Consequently, contexts should never
 * be stored outside their intended methods.
 * <p>
 * In practice, context objects are typically highly volatile. They are <em>not
 * thread-safe</em> and are often pooled and reused.
 * <p>
 * <h2 id="subcontexting">Subcontexting</h2>
 * Context objects allow <em>subcontexting</em>. Subcontexting is the temporary
 * modification of the context object. Contexts use a stack approach to
 * modification: all modifications must be reverted in the reversed order they
 * were applied.
 * <p>
 * Modification methods are usually named <em>{@code pushXXX}</em>. To revert
 * the most recent non-reverted modification, use {@link #pop()}. As a general
 * rule, a method that is given a context must always {@link #pop()} every
 * change it has pushed. Failure to abide by this contract results in bugs that
 * is difficult to trace.
 * <p>
 * Although various push methods declare differing result types, the same object
 * is always returned:
 * 
 * <pre>
 * someContext.pushXXX() == someContext
 * </pre>
 * 
 * Therefore invoking {@link #pop()} is valid using both the original reference
 * and the obtained reference.
 * <h3>Subcontexting example</h3>
 * Given a {@link ru.windcorp.progressia.common.world.context.BlockDataContext
 * BlockDataContext} {@code a} one can process the tile stack on the top of the
 * relevant block by using
 * 
 * <pre>
 * TileStackDataContext b = a.push(RelFace.TOP);
 * processTileStack(b);
 * b.pop();
 * </pre>
 * 
 * One can improve readability by eliminating the temporary variable:
 * 
 * <pre>
 * processTileStack(a.push(RelFace.TOP));
 * a.pop();
 * </pre>
 * 
 * Notice that {@code a.pop()} and {@code b.pop()} are interchangeable.
 * 
 * @see AbstractContextRO
 * @author javapony
 */
public interface Context {

	/**
	 * Tests whether the environment is "real". Any actions carried out in an
	 * environment that is not "real" should not have any side effects outside
	 * of the environment.
	 * <p>
	 * A typical "real" environment is the world of the client that is actually
	 * displayed or a world of the server that the clients actually interact
	 * with. An example of a non-"real" environment is a fake world used by
	 * developer tools to query the properties or behaviors of in-game content.
	 * While in-game events may well trigger global-scope actions, such as
	 * writing files, this may become an unintended or even harmful byproduct in
	 * some scenarios that are not actually linked to an actual in-game world.
	 * <p>
	 * This flag should generally only be consulted before taking action through
	 * means other than a provided changer object. The interactions with the
	 * context should otherwise remain unaltered.
	 * <p>
	 * When querying game content for purposes other than directly applying
	 * results in-game, {@code isReal()} should return {@code false}. In all
	 * other cases, where possible, the call should be delegated to a provided
	 * context object.
	 * 
	 * @return {@code false} iff side effects outside the environment should be
	 *         suppressed
	 */
	boolean isReal();

	/**
	 * Reverts the more recent modification to this object that has not been
	 * reverted yet.
	 * <p>
	 * Context objects may be modified temporarily with various push methods
	 * (see <a href="#subcontexting">subcontexting</a>). To revert the most
	 * recent non-reverted modification, use {@link #pop()}. As a general rule,
	 * a method that is given a context must always {@link #pop()} every change
	 * it has pushed. Failure to abide by this contract results in bugs that is
	 * difficult to trace.
	 * <p>
	 * This method may be invoked using either the original reference or the
	 * reference provided by push method.
	 * <p>
	 * This method fails with an {@link IllegalStateException} when there are no
	 * modifications to revert.
	 */
	void pop();
	
	default <T> T popAndReturn(T result) {
		pop();
		return result;
	}
	
	default boolean popAndReturn(boolean result) {
		pop();
		return result;
	}
	
	default int popAndReturn(int result) {
		pop();
		return result;
	}
	
	default float popAndReturn(float result) {
		pop();
		return result;
	}

}
