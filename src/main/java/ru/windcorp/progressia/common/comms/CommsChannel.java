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

package ru.windcorp.progressia.common.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ru.windcorp.progressia.common.comms.packets.Packet;

public abstract class CommsChannel {

	public static enum State {
		/**
		 * Client is currently establishing connection.
		 */
		CONNECTING,

		/**
		 * Client is ready to receive and send packets.
		 */
		CONNECTED,

		/**
		 * Client is being disconnected.
		 */
		DISCONNECTING,

		/**
		 * Communication is not possible. The client may have been disconnected
		 * after connecting or may have never connected.
		 */
		DISCONNECTED
	}

	private State state = State.CONNECTING;

	private final Collection<CommsListener> listeners = Collections.synchronizedCollection(new ArrayList<>());

	protected abstract void doSendPacket(Packet packet) throws IOException;

	private synchronized void sendPacket(Packet packet, State expectedState, String errorMessage) {
		if (getState() != expectedState) {
			throw new IllegalStateException(String.format(errorMessage, this, getState()));
		}

		try {
			doSendPacket(packet);
		} catch (IOException e) {
			onIOError(e, "Could not send packet");
		}
	}

	public synchronized void sendPacket(Packet packet) {
		sendPacket(packet, State.CONNECTED, "Client %s is in state %s and cannot receive packets normally");
	}

	public synchronized void sendConnectingPacket(Packet packet) {
		sendPacket(packet, State.CONNECTING, "Client %s is in state %s and is no longer connecting");
	}

	public synchronized void sendDisconnectingPacket(Packet packet) {
		sendPacket(packet, State.CONNECTING, "Client %s is in state %s and is no longer disconnecting");
	}

	public abstract void disconnect();

	protected void onPacketReceived(Packet packet) {
		listeners.forEach(l -> l.onPacketReceived(packet));
	}

	public void addListener(CommsListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CommsListener listener) {
		listeners.remove(listener);
	}

	protected void onIOError(IOException e, String string) {
		// TODO implement
		e.printStackTrace();
		listeners.forEach(l -> l.onIOError(e));
	}

	public synchronized State getState() {
		return state;
	}

	public boolean isReady() {
		return getState() == State.CONNECTED;
	}

	public synchronized void setState(State state) {
		this.state = state;
	}

}
