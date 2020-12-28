package ru.windcorp.progressia.server;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.PlayerData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.comms.ClientPlayer;

public class Player extends PlayerData implements ChunkLoader {
	
	private final Server server;
	private final ClientPlayer client;

	public Player(EntityData entity, Server server, ClientPlayer client) {
		super(entity);
		this.server = server;
		this.client = client;
		
		client.setPlayer(this);
	}
	
	public Server getServer() {
		return server;
	}
	
	public ClientPlayer getClient() {
		return client;
	}

	@Override
	public void requestChunksToLoad(Consumer<Vec3i> chunkConsumer) {
		Vec3i start = getEntity().getPosition().round_();
		Coordinates.convertInWorldToChunk(start, start);
		
		Vec3i cursor = new Vec3i();
		float radius = getServer().getLoadDistance(this);
		float radiusSq = radius / Units.get(16.0f, "m");
		radiusSq *= radiusSq;
		int iRadius = (int) Math.ceil(radius);
		
		for (cursor.x = -iRadius; cursor.x <= +iRadius; ++cursor.x) {
			for (cursor.y = -iRadius; cursor.y <= +iRadius; ++cursor.y) {
				for (cursor.z = -iRadius; cursor.z <= +iRadius; ++cursor.z) {
					if (cursor.x * cursor.x + cursor.y * cursor.y + cursor.z * cursor.z <= radius) {
						
						cursor.add(start);
						chunkConsumer.accept(cursor);
						cursor.sub(start);
					}
				}
			}
		}
	}
	
}
