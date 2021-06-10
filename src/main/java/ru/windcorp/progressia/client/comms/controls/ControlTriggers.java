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

package ru.windcorp.progressia.client.comms.controls;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.common.comms.controls.ControlData;

public class ControlTriggers {

	public static ControlTriggerInputBased of(String id, BiConsumer<InputEvent, ControlData> dataWriter,
			Predicate<InputEvent> predicate) {
		return new ControlTriggerLambda(id, predicate, dataWriter);
	}

	public static ControlTriggerInputBased of(String id, Consumer<ControlData> dataWriter,
			Predicate<InputEvent> predicate) {
		return of(id, (input, control) -> dataWriter.accept(control), predicate);
	}

	public static ControlTriggerInputBased of(String id, Predicate<InputEvent> predicate) {
		return of(id, (input, control) -> {
		}, predicate);
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased of(String id, Class<I> inputType,
			BiConsumer<I, ControlData> dataWriter, Predicate<I>... predicates) {
		return of(id, createCheckedDataWriter(inputType, dataWriter),
				createCheckedCompoundPredicate(inputType, predicates));
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased of(String id, Class<I> inputType,
			Consumer<ControlData> dataWriter, Predicate<I>... predicates) {
		return of(id, inputType, (input, control) -> dataWriter.accept(control), predicates);
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased of(String id, Class<I> inputType,
			Predicate<I>... predicates) {
		return of(id, (input, control) -> {
		}, createCheckedCompoundPredicate(inputType, predicates));
	}

	@SafeVarargs
	public static ControlTriggerInputBased of(String id, BiConsumer<InputEvent, ControlData> dataWriter,
			Predicate<InputEvent>... predicates) {
		return of(id, InputEvent.class, dataWriter, predicates);
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased of(String id, Consumer<ControlData> dataWriter,
			Predicate<InputEvent>... predicates) {
		return of(id, (input, control) -> dataWriter.accept(control), predicates);
	}

	@SafeVarargs
	public static ControlTriggerInputBased of(String id, Predicate<InputEvent>... predicates) {
		return of(id, InputEvent.class, (input, control) -> {
		}, predicates);
	}

	//
	//
	///
	///
	//
	//
	//
	//
	//
	//
	//
	//
	//

	public static ControlTriggerInputBased localOf(String id, Consumer<InputEvent> action,
			Predicate<InputEvent> predicate) {
		return new ControlTriggerLocalLambda(id, predicate, action);
	}

	public static ControlTriggerInputBased localOf(String id, Runnable action, Predicate<InputEvent> predicate) {
		return localOf(id, input -> action.run(), predicate);
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased localOf(String id, Class<I> inputType,
			Consumer<I> action, Predicate<I>... predicates) {
		return localOf(id, createCheckedAction(inputType, action),
				createCheckedCompoundPredicate(inputType, predicates));
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased localOf(String id, Class<I> inputType,
			Runnable action, Predicate<I>... predicates) {
		return localOf(id, inputType, input -> action.run(), predicates);
	}

	@SafeVarargs
	public static ControlTriggerInputBased localOf(String id, Consumer<InputEvent> action,
			Predicate<InputEvent>... predicates) {
		return localOf(id, InputEvent.class, action, predicates);
	}

	@SafeVarargs
	public static <I extends InputEvent> ControlTriggerInputBased localOf(String id, Runnable action,
			Predicate<InputEvent>... predicates) {
		return of(id, input -> action.run(), predicates);
	}

	private static <I extends InputEvent> BiConsumer<InputEvent, ControlData> createCheckedDataWriter(
			Class<I> inputType, BiConsumer<I, ControlData> dataWriter) {
		return (inputEvent, control) -> dataWriter.accept(inputType.cast(inputEvent), control);
	}

	private static <I extends InputEvent> Consumer<InputEvent> createCheckedAction(Class<I> inputType,
			Consumer<I> action) {
		return inputEvent -> action.accept(inputType.cast(inputEvent));
	}

	private static <I extends InputEvent> Predicate<InputEvent> createCheckedCompoundPredicate(Class<I> inputType,
			Predicate<I>[] predicates) {
		return new CompoundCastPredicate<>(inputType, predicates);
	}

	private static class CompoundCastPredicate<I extends InputEvent> implements Predicate<InputEvent> {

		private final Class<I> inputType;
		private final Predicate<I>[] predicates;

		public CompoundCastPredicate(Class<I> inputType, Predicate<I>[] predicates) {
			this.inputType = inputType;
			this.predicates = predicates;
		}

		@Override
		public boolean test(InputEvent inputEvent) {
			if (!inputType.isInstance(inputEvent)) {
				return false;
			}

			I castEvent = inputType.cast(inputEvent);

			for (Predicate<I> predicate : predicates) {
				if (!predicate.test(castEvent)) {
					return false;
				}
			}

			return true;
		}

	}

	private ControlTriggers() {
	}

}
