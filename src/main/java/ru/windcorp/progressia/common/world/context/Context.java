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

/**
 * A cursor-like object for retrieving information about an in-game environment.
 * A context object typically holds a reference to some sort of data structure
 * and a cursor pointing to a location in that data structure. The exact meaning
 * of "environment" and "location" is defined by extending interfaces.
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
 * <em>Subcontexting</em> is the invocation of user-provided code with a context
 * object derived from an existing one. For example, block context provides a
 * convenience method for referencing the block's neighbor:
 * 
 * <pre>
 * blockContextA.forNeighbor(RelFace.UP, blockContextB -&gt; {
 * 	foo(blockContextA); // undefined behavior!
 * 	foo(blockContextB); // correct
 * });
 * </pre>
 * 
 * In this example, {@code forNeighbor} is a subcontexting method,
 * {@code blockContextA} is the parent context, {@code blockContextB} is the
 * subcontext, and the lambda is the context consumer.
 * <p>
 * <em>Parent contexts are invalid while the subcontexting method is
 * running.</em> Referencing {@code blockContextA} from inside the lambda
 * creates undefined behavior.
 * <p>
 * This restriction exists because some implementations of contexts may
 * implement subcontexting by simply modifying the parent context for the
 * duration of the call and presenting the temporarily modified parent context
 * as the subcontext:
 * 
 * <pre>
 * public void forNeighbor(BlockFace face, Consumer&lt;BlockContext&gt; action) {
 * 	this.position.add(face);
 * 	action.accept(this);
 * 	this.position.sub(face);
 * }
 * </pre>
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

}
