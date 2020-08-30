package ru.windcorp.progressia.common.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

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

	public static enum Role {
		GAME,
		CHAT,
		RCON
		// TODO create role for that thingy that only connects to get server status
	}

	private State state = State.CONNECTING;
	
	protected final Set<Role> roles;
	
	private final Collection<CommsListener> listeners =
			Collections.synchronizedCollection(new ArrayList<>());

	public CommsChannel(Role... roles) {
		this.roles = Sets.immutableEnumSet(Arrays.asList(roles));
	}

	protected abstract void doSendPacket(Packet packet) throws IOException;

	private synchronized void sendPacket(
			Packet packet,
			State expectedState, String errorMessage
	) {
		if (getState() != expectedState) {
			throw new IllegalStateException(
					String.format(errorMessage, this, getState())
			);
		}
		
		try {
			doSendPacket(packet);
		} catch (IOException e) {
			onIOError(e, "Could not send packet");
		}
	}

	public synchronized void sendPacket(Packet packet) {
		sendPacket(
				packet,
				State.CONNECTED,
				"Client %s is in state %s and cannot receive packets normally"
		);
	}

	public synchronized void sendConnectingPacket(Packet packet) {
		sendPacket(
				packet,
				State.CONNECTING,
				"Client %s is in state %s and is no longer connecting"
		);
	}

	public synchronized void sendDisconnectingPacket(Packet packet) {
		sendPacket(
				packet,
				State.CONNECTING,
				"Client %s is in state %s and is no longer disconnecting"
		);
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
	
	public Set<Role> getRoles() {
		return roles;
	}

	public boolean isReady() {
		return getState() == State.CONNECTED;
	}

	public synchronized void setState(State state) {
		this.state = state;
	}

}