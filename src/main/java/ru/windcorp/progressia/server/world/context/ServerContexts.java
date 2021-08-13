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
package ru.windcorp.progressia.server.world.context;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.generic.ChunkGenericRO;
import ru.windcorp.progressia.common.world.generic.TileGenericReferenceRO;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class ServerContexts {
	
	/*
	 * RW
	 */
	
	public static ServerBlockContext pushAbs(ServerWorldContext context, Vec3i blockInWorld) {
		Vec3i contextLocation = Vectors.grab3i();
		context.toContext(blockInWorld, contextLocation);
		ServerBlockContext blockContext = context.push(contextLocation);
		Vectors.release(contextLocation);
		return blockContext;
	}
	
	public static ServerBlockContext pushAbs(ServerWorldContext context, ChunkGenericRO<?, ?, ?, ?, ?> chunk, Vec3i blockInChunk) {
		Vec3i contextLocation = Vectors.grab3i();
		Coordinates.getInWorld(chunk.getPosition(), blockInChunk, contextLocation);
		context.toContext(contextLocation, contextLocation);
		ServerBlockContext blockContext = context.push(contextLocation);
		Vectors.release(contextLocation);
		return blockContext;
	}
	
	public static ServerTileStackContext pushAbs(ServerWorldContext context, Vec3i blockInWorld, AbsFace face) {
		Vec3i contextLocation = Vectors.grab3i();
		context.toContext(blockInWorld, contextLocation);
		RelFace contextFace = context.toContext(face);
		ServerTileStackContext tileStackContext = context.push(contextLocation, contextFace);
		Vectors.release(contextLocation);
		return tileStackContext;
	}
	
	public static ServerTileStackContext pushAbs(ServerWorldContext context, ChunkGenericRO<?, ?, ?, ?, ?> chunk, Vec3i blockInChunk, AbsFace face) {
		Vec3i contextLocation = Vectors.grab3i();
		Coordinates.getInWorld(chunk.getPosition(), blockInChunk, contextLocation);
		context.toContext(contextLocation, contextLocation);
		RelFace contextFace = context.toContext(face);
		ServerTileStackContext tileStackContext = context.push(contextLocation, contextFace);
		Vectors.release(contextLocation);
		return tileStackContext;
	}
	
	public static ServerTileStackContext pushAbs(ServerBlockContext context, AbsFace face) {
		return context.push(context.toContext(face));
	}
	
	public static ServerTileContext pushAbs(ServerWorldContext context, AbsFace up, TileGenericReferenceRO<?, ?, ?, ?, ?> ref) {
		Vec3i contextLocation = Vectors.grab3i();
		ref.getStack().getBlockInWorld(contextLocation);
		context.toContext(contextLocation, contextLocation);
		RelFace contextFace = context.toContext(ref.getStack().getFace().resolve(up));
		ServerTileContext tileContext = context.push(contextLocation, contextFace, ref.getIndex());
		Vectors.release(contextLocation);
		return tileContext;
	}
	
	/*
	 * RO
	 */
	
	public static ServerBlockContextRO pushAbs(ServerWorldContextRO context, Vec3i blockInWorld) {
		Vec3i contextLocation = Vectors.grab3i();
		context.toContext(blockInWorld, contextLocation);
		ServerBlockContextRO blockContextRO = context.push(contextLocation);
		Vectors.release(contextLocation);
		return blockContextRO;
	}
	
	public static ServerBlockContextRO pushAbs(ServerWorldContextRO context, ChunkGenericRO<?, ?, ?, ?, ?> chunk, Vec3i blockInChunk) {
		Vec3i contextLocation = Vectors.grab3i();
		Coordinates.getInWorld(chunk.getPosition(), blockInChunk, contextLocation);
		context.toContext(contextLocation, contextLocation);
		ServerBlockContextRO blockContextRO = context.push(contextLocation);
		Vectors.release(contextLocation);
		return blockContextRO;
	}
	
	public static ServerTileStackContextRO pushAbs(ServerWorldContextRO context, Vec3i blockInWorld, AbsFace face) {
		Vec3i contextLocation = Vectors.grab3i();
		context.toContext(blockInWorld, contextLocation);
		RelFace contextFace = context.toContext(face);
		ServerTileStackContextRO tileStackContextRO = context.push(contextLocation, contextFace);
		Vectors.release(contextLocation);
		return tileStackContextRO;
	}
	
	public static ServerTileStackContextRO pushAbs(ServerWorldContextRO context, ChunkGenericRO<?, ?, ?, ?, ?> chunk, Vec3i blockInChunk, AbsFace face) {
		Vec3i contextLocation = Vectors.grab3i();
		Coordinates.getInWorld(chunk.getPosition(), blockInChunk, contextLocation);
		context.toContext(contextLocation, contextLocation);
		RelFace contextFace = context.toContext(face);
		ServerTileStackContextRO tileStackContextRO = context.push(contextLocation, contextFace);
		Vectors.release(contextLocation);
		return tileStackContextRO;
	}
	
	public static ServerTileStackContextRO pushAbs(ServerBlockContextRO context, AbsFace face) {
		return context.push(context.toContext(face));
	}
	
	public static ServerTileContextRO pushAbs(ServerWorldContextRO context, AbsFace up, TileGenericReferenceRO<?, ?, ?, ?, ?> ref) {
		Vec3i contextLocation = Vectors.grab3i();
		ref.getStack().getBlockInWorld(contextLocation);
		context.toContext(contextLocation, contextLocation);
		RelFace contextFace = context.toContext(ref.getStack().getFace().resolve(up));
		ServerTileContextRO tileContextRO = context.push(contextLocation, contextFace, ref.getIndex());
		Vectors.release(contextLocation);
		return tileContextRO;
	}

	private ServerContexts() {
	}

}
