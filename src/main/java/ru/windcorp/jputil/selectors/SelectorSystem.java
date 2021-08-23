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

package ru.windcorp.jputil.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

import ru.windcorp.jputil.SyntaxException;
import ru.windcorp.jputil.iterators.PeekingIterator;

public class SelectorSystem<T> {

	public static final char EXPRESSION_OPEN = '(';
	public static final char EXPRESSION_CLOSE = ')';

	private final Collection<Selector<T>> selectors = Collections.synchronizedCollection(new ArrayList<Selector<T>>());

	private final Collection<SelectorOperator> operators = Collections
			.synchronizedCollection(new ArrayList<SelectorOperator>());

	private String stackPrefix = null;

	public Collection<Selector<T>> getSelectors() {
		return this.selectors;
	}

	public Collection<SelectorOperator> getSelectorOperators() {
		return this.operators;
	}

	public String getStackPrefix() {
		return stackPrefix;
	}

	public SelectorSystem<T> setStackPrefix(String stackPrefix) {
		this.stackPrefix = stackPrefix;
		return this;
	}

	public SelectorSystem<T> add(Selector<T> selector) {
		getSelectors().add(selector);
		return this;
	}

	public SelectorSystem<T> add(SelectorOperator operator) {
		getSelectorOperators().add(operator);
		return this;
	}

	public Predicate<T> parse(Iterator<String> tokens) throws SyntaxException {
		PeekingIterator<String> peeker = new PeekingIterator<>(tokens);

		if (getStackPrefix() != null && peeker.hasNext() && getStackPrefix().equals(peeker.peek())) {
			peeker.next();
			return parseStack(peeker);
		}

		Deque<Predicate<T>> stack = new LinkedList<>();

		synchronized (getSelectorOperators()) {
			synchronized (getSelectors()) {

				while (peeker.hasNext()) {
					parseToken(stack, peeker);
				}

			}
		}

		return compress(stack);
	}

	private void parseToken(Deque<Predicate<T>> stack, Iterator<String> tokens) throws SyntaxException {

		if (!tokens.hasNext()) {
			throw new SyntaxException("Not enough tokens");
		}
		String token = tokens.next();

		for (SelectorOperator operator : getSelectorOperators()) {
			if (operator.matchesName(token.toLowerCase())) {
				parseToken(stack, tokens);
				operator.process(stack);
				return;
			}
		}

		Selector<T> tmp;
		for (Selector<T> selector : getSelectors()) {
			if ((tmp = selector.derive(token)) != null) {
				stack.push(tmp);
				return;
			}
		}

		throw new SyntaxException("Unknown token \"" + token + "\"");
	}

	public Predicate<T> parseStack(Iterator<String> tokens) throws SyntaxException {
		Deque<Predicate<T>> stack = new LinkedList<>();

		String token;

		synchronized (getSelectorOperators()) {
			synchronized (getSelectors()) {

				tokenCycle: while (tokens.hasNext()) {
					token = tokens.next();

					for (SelectorOperator operator : getSelectorOperators()) {
						if (operator.matchesName(token.toLowerCase())) {
							operator.process(stack);
							continue tokenCycle;
						}
					}

					for (Selector<T> selector : getSelectors()) {
						Selector<T> tmp;
						if ((tmp = selector.derive(token)) != null) {
							stack.push(tmp);
							continue tokenCycle;
						}
					}

					throw new SyntaxException("Unknown token \"" + token + "\"");

				}
			}
		}

		return compress(stack);
	}

	private Predicate<T> compress(Deque<Predicate<T>> stack) throws SyntaxException {
		if (stack.isEmpty()) {
			throw new SyntaxException("Stack is empty");
		}

		if (stack.size() == 1) {
			return stack.pop();
		}

		return obj -> {
			for (Predicate<? super T> predicate : stack) {
				if (predicate.test(obj)) {
					return true;
				}
			}

			return false;
		};
	}

}
