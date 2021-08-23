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

import java.nio.FloatBuffer;

import com.google.common.collect.ObjectArrays;

import ru.windcorp.progressia.client.graphics.backend.shaders.uniforms.Uniform1Int;
import ru.windcorp.progressia.client.graphics.backend.shaders.uniforms.Uniform2Float;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;

public class FlatRenderProgram extends ShapeRenderProgram {

	private static FlatRenderProgram def = null;

	public static void init() {
		def = new FlatRenderProgram(new String[] { "FlatDefault.vertex.glsl" },
				new String[] { "FlatDefault.fragment.glsl" });
	}

	public static FlatRenderProgram getDefault() {
		return def;
	}

	public static final int MASK_STACK_SIZE = 16; // As in Flat.fragment.glsl

	private static final String FLAT_VERTEX_SHADER_RESOURCE = "Flat.vertex.glsl";
	private static final String FLAT_FRAGMENT_SHADER_RESOURCE = "Flat.fragment.glsl";

	private static final String MASK_COUNT_UNIFORM_NAME = "maskCount", MASKS_UNIFORM_NAME = "masks";

	private final Uniform1Int maskCountUniform;
	private final Uniform2Float masksUniform;

	public FlatRenderProgram(String[] vertexShaderResources, String[] fragmentShaderResources) {
		super(attachVertexShader(vertexShaderResources), attachFragmentShader(fragmentShaderResources));

		this.maskCountUniform = getUniform(MASK_COUNT_UNIFORM_NAME).as1Int();
		this.masksUniform = getUniform(MASKS_UNIFORM_NAME).as2Float();
	}

	private static String[] attachVertexShader(String[] others) {
		return ObjectArrays.concat(FLAT_VERTEX_SHADER_RESOURCE, others);
	}

	private static String[] attachFragmentShader(String[] others) {
		return ObjectArrays.concat(FLAT_FRAGMENT_SHADER_RESOURCE, others);
	}

	@Override
	public void render(ShapeRenderHelper helper, Shape shape) {
		super.render(helper, shape);
	}

	@Override
	protected void configure(ShapeRenderHelper argHelper) {
		super.configure(argHelper);
		FlatRenderHelper helper = ((FlatRenderHelper) argHelper);

		configureMasks(helper.getMasks());
	}

	private void configureMasks(FloatBuffer masks) {
		int pos = masks.position();
		int limit = masks.limit();
		int size = pos / TransformedMask.SIZE_IN_FLOATS;

		maskCountUniform.set(size);

		masks.flip();
		masksUniform.set(masks);

		masks.limit(limit);
		masks.position(pos);
	}

}
