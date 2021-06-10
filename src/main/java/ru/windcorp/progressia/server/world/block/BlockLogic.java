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

package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericBlock;

public class BlockLogic extends Namespaced implements GenericBlock {

	public BlockLogic(String id) {
		super(id);
	}

	public boolean isSolid(BlockTickContext context, BlockFace face) {
		return isSolid(face);
	}

	public boolean isSolid(BlockFace face) {
		return true;
	}

	public boolean isTransparent(BlockTickContext context) {
		return isTransparent();
	}

	public boolean isTransparent() {
		return false;
	}

}
