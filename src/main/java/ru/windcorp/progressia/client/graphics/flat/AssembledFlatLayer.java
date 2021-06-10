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

package ru.windcorp.progressia.client.graphics.flat;

import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.backend.FaceCulling;

public abstract class AssembledFlatLayer extends Layer {

	private final AssembledFlatRenderHelper helper = new AssembledFlatRenderHelper();

	private final RenderTarget target = new RenderTarget();

	private RenderTarget.Clip[] clips = null;

	public AssembledFlatLayer(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doValidate() {
		assemble(target);
		clips = target.assemble();
	}

	protected abstract void assemble(RenderTarget target);

	@Override
	protected void doRender() {
		FaceCulling.push(false);

		for (RenderTarget.Clip clip : clips) {
			clip.render(helper);
		}

		helper.reset();

		FaceCulling.pop();
	}

}
