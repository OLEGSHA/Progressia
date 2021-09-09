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
package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.world.hud.InventoryHUD;
import ru.windcorp.progressia.client.graphics.world.hud.HandsHUD;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.client.world.entity.SpeciesRender;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.SpeciesData.EquipmentSlot;
import ru.windcorp.progressia.common.world.entity.SpeciesData.Hand;

public class SpeciesRenderHuman extends SpeciesRender {
	
	private final HumanModelFactory modelFactory = new HumanModelFactory();

	public SpeciesRenderHuman(String id) {
		super(id);
	}

	@Override
	public EntityRenderable createRenderable(EntityDataPlayer entity) {
		return modelFactory.createRenderable(entity);
	}

	@Override
	public HandsHUD.Side getHandSide(Hand hand) {
		return hand.getIndex() == 0 ? HandsHUD.Side.RIGHT : HandsHUD.Side.LEFT;
	}
	
	@Override
	public InventoryHUD.Side getEquipmentSlotSide(EquipmentSlot equipmentSlot) {
		return InventoryHUD.Side.LEFT;
	}

}
