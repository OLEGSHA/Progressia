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

package ru.windcorp.progressia.client.graphics.input.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import ru.windcorp.jputil.ArrayUtil;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.input.CursorEvent;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.KeyMatcher;
import ru.windcorp.progressia.client.graphics.input.WheelEvent;
import ru.windcorp.progressia.common.util.crash.CrashReports;

/**
 * An event bus optionally related to a {@link Component} that delivers input
 * events to input listeners. This bus may skip listeners based on circumstance;
 * behavior can be customized for each listener with {@link Option}s.
 * <p>
 * By default, events are filtered by four checks before being delivered to each
 * listener:
 * <ol>
 * <li><em>Consumption check</em>: unless {@link Option#RECEIVE_CONSUMED
 * RECEIVE_CONSUMED} is set, events that are consumed will not be
 * delivered.</li>
 * <li><em>Hover check</em>: for certain event types (for example,
 * {@link WheelEvent} or {@link KeyEvent} that {@link KeyEvent#isMouse()
 * isMouse()}), the event will only be delivered if the component is hovered.
 * This check may be bypassed with option {@link Option#IGNORE_HOVER
 * IGNORE_HOVER} or made mandatory for all events with
 * {@link Option#REQUIRE_HOVER REQUIRE_HOVER}. Hover check automatically
 * succeeds if no component is provided.</li>
 * <li><em>Focus check</em>: for certain event types (for example,
 * {@link KeyEvent} that {@code !isMouse()}), the event will only be delivered
 * if the component has focus. This check may be bypassed with option
 * {@link Option#IGNORE_FOCUS IGNORE_FOCUS} or made mandatory for all events
 * with {@link Option#REQUIRE_FOCUS REQUIRE_FOCUS}. Focus check automatically
 * succeeds if no component is provided.</li>
 * <li><em>Type check</em>: events of type {@code E} are only delivered to
 * listeners registered with event type {@code T} if objects of type {@code E}
 * can be cast to {@code T}.</li>
 * </ol>
 * Checks 1-3 are bypassed when option {@link Option#ALWAYS ALWAYS} is
 * specified.
 */
public class InputBus {

	/**
	 * Options that allow customization of checks for listeners.
	 */
	public enum Option {

		/**
		 * Ignore checks for consumed events, hover and focus; deliver event if
		 * at all possible. This is shorthand for {@link #RECEIVE_CONSUMED},
		 * {@link #IGNORE_HOVER} and {@link #IGNORE_FOCUS}.
		 */
		ALWAYS,

		/**
		 * Receive events that were previously consumed.
		 */
		RECEIVE_CONSUMED,

		/**
		 * Do not process events if the listener is registered with a component
		 * and the component is not hovered.
		 */
		REQUIRE_HOVER,

		/**
		 * Deliver events even if the event is limited to hovered components by
		 * default.
		 */
		IGNORE_HOVER,

		/**
		 * Do not process events if the listener is registered with a component
		 * and the component is not focused.
		 */
		REQUIRE_FOCUS,

		/**
		 * Deliver events even if the event is limited to focused components by
		 * default.
		 */
		IGNORE_FOCUS,

		/**
		 * Deliver events according to
		 * {@link KeyMatcher#matchesIgnoringAction(KeyEvent)} rather than
		 * {@link KeyMatcher#matches(KeyEvent)} when a {@link KeyMatcher} is
		 * specified.
		 */
		IGNORE_ACTION;

	}

	private enum YesNoDefault {
		YES, NO, DEFAULT;
	}

	/**
	 * A listener with check preferences resolved and type specified.
	 */
	private class WrappedListener {

		private final Class<?> type;

		private final boolean dropIfConsumed;
		private final YesNoDefault dropIfNotHovered;
		private final YesNoDefault dropIfNotFocused;

		private final InputListener<?> listener;

		public WrappedListener(
			Class<?> type,
			boolean dropIfConsumed,
			YesNoDefault dropIfNotHovered,
			YesNoDefault dropIfNotFocused,
			InputListener<?> listener
		) {
			this.type = type;
			this.dropIfConsumed = dropIfConsumed;
			this.dropIfNotHovered = dropIfNotHovered;
			this.dropIfNotFocused = dropIfNotFocused;
			this.listener = listener;
		}

		private boolean handles(InputEvent input) {
			if (dropIfConsumed && input.isConsumed())
				return false;

			switch (dropIfNotHovered) {
			case YES:
				if (!isHovered())
					return false;
				break;
			case NO:
				break;
			default:

				if (isHovered())
					break;

				if (input instanceof KeyEvent && ((KeyEvent) input).isMouse())
					return false;

				if (input instanceof CursorEvent)
					return false;

				if (input instanceof WheelEvent)
					return false;

				break;
			}

			switch (dropIfNotFocused) {
			case YES:
				if (!isFocused())
					return false;
				break;
			case NO:
				break;
			default:

				if (isFocused())
					break;

				if (input instanceof KeyEvent && !((KeyEvent) input).isMouse())
					return false;

				break;
			}

			if (!type.isInstance(input))
				return false;

			return true;
		}

		/**
		 * Invokes the listener if the event is deemed appropriate by the four
		 * checks.
		 * 
		 * @param event the event to deliver
		 */
		@SuppressWarnings("unchecked")
		public void handle(InputEvent event) {
			if (handles(event)) {
				// A runtime check of types has been performed; this is safe.
				InputListener<InputEvent> castListener = (InputListener<InputEvent>) listener;

				try {
					castListener.handle(event);
				} catch (Exception e) {
					throw CrashReports.report(
						e,
						"InputListener %s for component %s has failed to receive event %s",
						listener,
						owner,
						event
					);
				}
			}
		}

	}

	/**
	 * The component queried for focus and hover. May be {@code null}.
	 */
	private final Component owner;

	/**
	 * Registered listeners.
	 */
	private final Collection<WrappedListener> listeners = new ArrayList<>(4);

	/**
	 * Creates a new input bus that consults the specified {@link Component} to
	 * determine hover and focus.
	 * 
	 * @param owner the component to use for hover and focus tests
	 * @see #InputBus()
	 */
	public InputBus(Component owner) {
		this.owner = Objects.requireNonNull(owner, "owner");
	}

	/**
	 * Creates a new input bus that assumes all hover and focus checks are
	 * successful.
	 * 
	 * @see #InputBus(Component)
	 */
	public InputBus() {
		this.owner = null;
	}

	/**
	 * Determines whether hover should be assumed for this event bus.
	 * 
	 * @return {@code true} iff no component is linked or the linked component
	 *         is hovered
	 */
	private boolean isHovered() {
		return owner == null ? true : owner.isHovered();
	}

	/**
	 * Determines whether focus should be assumed for this event bus.
	 * 
	 * @return {@code true} iff no component is linked or the linked component
	 *         is focused
	 */
	private boolean isFocused() {
		return owner == null ? true : owner.isFocused();
	}

	/**
	 * Dispatches (delivers) the provided event to all appropriate listeners.
	 * 
	 * @param event the event to process
	 */
	public void dispatch(InputEvent event) {
		Objects.requireNonNull(event, "event");
		for (WrappedListener listener : listeners) {
			listener.handle(event);
		}
	}

	/**
	 * Registers a listener on this bus.
	 * <p>
	 * {@code type} specifies the class of events that should be passed to this
	 * listener. Only events of types that extend, implement or equal
	 * {@code type} are processed.
	 * <p>
	 * Zero or more {@link Option}s may be specified to enable or disable the
	 * processing of certain events in certain circumstances. See
	 * {@linkplain InputBus class description} for a detailed breakdown of the
	 * checks performed and the effects of various options. When providing
	 * options to this method, later options override the effects of previous
	 * options.
	 * <p>
	 * Option {@link Option#IGNORE_ACTION IGNORE_ACTION} is ignored silently.
	 * 
	 * @param type     the event class to deliver
	 * @param listener the listener
	 * @param options  the options for this listener
	 */
	public <T extends InputEvent> void register(
		Class<? extends T> type,
		InputListener<T> listener,
		Option... options
	) {
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(listener, "listener");

		boolean dropIfConsumed = true;
		YesNoDefault dropIfNotHovered = YesNoDefault.DEFAULT;
		YesNoDefault dropIfNotFocused = YesNoDefault.DEFAULT;

		if (options != null) {
			for (Option option : options) {
				switch (option) {
				case ALWAYS:
					dropIfConsumed = false;
					dropIfNotHovered = YesNoDefault.NO;
					dropIfNotFocused = YesNoDefault.NO;
					break;
				case RECEIVE_CONSUMED:
					dropIfConsumed = false;
					break;
				case REQUIRE_HOVER:
					dropIfNotHovered = YesNoDefault.YES;
					break;
				case IGNORE_HOVER:
					dropIfNotFocused = YesNoDefault.NO;
					break;
				case REQUIRE_FOCUS:
					dropIfNotHovered = YesNoDefault.YES;
					break;
				case IGNORE_FOCUS:
					dropIfNotFocused = YesNoDefault.NO;
					break;
				case IGNORE_ACTION:
					// Ignore
					break;
				default:
					throw new IllegalArgumentException("Unexpected option " + option);
				}
			}
		}

		listeners.add(new WrappedListener(type, dropIfConsumed, dropIfNotHovered, dropIfNotFocused, listener));
	}

	/**
	 * Registers a {@link KeyEvent} listener on this bus. An event has to match
	 * the provided {@link KeyMatcher} to be delivered to the listener.
	 * <p>
	 * Zero or more {@link Option}s may be specified to enable or disable the
	 * processing of certain events in certain circumstances. See
	 * {@linkplain InputBus class description} for a detailed breakdown of the
	 * checks performed and the effects of various options. When providing
	 * options to this method, later options override the effects of previous
	 * options.
	 * <p>
	 * Option {@link Option#IGNORE_ACTION IGNORE_ACTION} requests that events
	 * are delivered according to
	 * {@link KeyMatcher#matchesIgnoringAction(KeyEvent)} rather than
	 * {@link KeyMatcher#matches(KeyEvent)}.
	 * specified.
	 * 
	 * @param matcher  an event filter
	 * @param listener the listener
	 * @param options  the options for this listener
	 */
	public void register(KeyMatcher matcher, InputListener<? super KeyEvent> listener, Option... options) {
		Objects.requireNonNull(matcher, "matcher");
		Objects.requireNonNull(listener, "listener");

		InputListener<KeyEvent> filteringListener;

		if (ArrayUtil.firstIndexOf(options, Option.IGNORE_ACTION) != -1) {
			filteringListener = e -> {
				if (matcher.matchesIgnoringAction(e)) {
					listener.handle(e);
				}
			};
		} else {
			filteringListener = e -> {
				if (matcher.matches(e)) {
					listener.handle(e);
				}
			};
		}

		register(KeyEvent.class, filteringListener, options);
	}

	/**
	 * Removes all occurrences of the provided listener from this bus.
	 * 
	 * @param listener the listener to unregister
	 */
	public void unregister(InputListener<?> listener) {
		if (listener == null) {
			return;
		}

		listeners.removeIf(l -> l.listener == listener);
	}

}
