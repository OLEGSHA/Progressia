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
package ru.windcorp.progressia.client.world.entity;

import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.hud.HUDTextures;
import ru.windcorp.progressia.client.graphics.world.hud.HandsHUD;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.SpeciesData.EquipmentSlot;
import ru.windcorp.progressia.common.world.entity.SpeciesData.Hand;

public abstract class SpeciesRender extends Namespaced {

	public SpeciesRender(String id) {
		super(id);
	}

	public abstract EntityRenderable createRenderable(EntityDataPlayer entity);
	
	public abstract HandsHUD.Side getHandSide(Hand hand);
	
	public Texture getTexture(String name) {
		return HUDTextures.getHUDTexture(getNamespace() + "_" + getName() + "/" + name);
	}
	
	public Texture getHandBackground(Hand hand) {
		return getTexture("Hand" + hand.getName());
	}
	
	public Texture getEquipmentSlotBackground(EquipmentSlot slot) {
		return getTexture("EquipmentSlot" + slot.getName());
	}

}
