/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.optica.client.graphics.flat;

import static org.lwjgl.opengl.GL11.*;

import com.google.common.collect.ObjectArrays;

import ru.windcorp.optica.client.graphics.backend.shaders.uniforms.Uniform2Int;
import ru.windcorp.optica.client.graphics.model.Face;
import ru.windcorp.optica.client.graphics.model.Shape;
import ru.windcorp.optica.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.optica.client.graphics.model.ShapeRenderProgram;

public class FlatRenderProgram extends ShapeRenderProgram {
	
	private static FlatRenderProgram def = null;
	
	public static void init() {
		def = new FlatRenderProgram(
				new String[] {"FlatDefault.vertex.glsl"},
				new String[] {"FlatDefault.fragment.glsl"}
		);
	}
	
	public static FlatRenderProgram getDefault() {
		return def;
	}
	
	private static final String FLAT_VERTEX_SHADER_RESOURCE =
			"Flat.vertex.glsl";
	private static final String FLAT_FRAGMENT_SHADER_RESOURCE =
			"Flat.fragment.glsl";
	
	private static final String
			MASK_START_UNIFORM_NAME = "maskStart",
			MASK_END_UNIFORM_NAME   = "maskEnd";
	
	private final Uniform2Int maskStartUniform;
	private final Uniform2Int maskEndUniform;

	public FlatRenderProgram(
			String[] vertexShaderResources,
			String[] fragmentShaderResources
	) {
		super(
				attachVertexShader(vertexShaderResources),
				attachFragmentShader(fragmentShaderResources)
		);
		
		this.maskStartUniform = getUniform(MASK_START_UNIFORM_NAME).as2Int();
		this.maskEndUniform = getUniform(MASK_END_UNIFORM_NAME).as2Int();
	}
	
	private static String[] attachVertexShader(String[] others) {
		return ObjectArrays.concat(FLAT_VERTEX_SHADER_RESOURCE, others);
	}
	
	private static String[] attachFragmentShader(String[] others) {
		return ObjectArrays.concat(FLAT_FRAGMENT_SHADER_RESOURCE, others);
	}
	
	@Override
	public void render(ShapeRenderHelper helper, Shape shape) {
		if (((FlatRenderHelper) helper).isRenderable()) {
			super.render(helper, shape);
		}
	}
	
	@Override
	protected void renderFace(Face face) {
		glDisable(GL_CULL_FACE);
		super.renderFace(face);
		glEnable(GL_CULL_FACE);
	}
	
	@Override
	protected void configure(ShapeRenderHelper argHelper) {
		super.configure(argHelper);
		FlatRenderHelper helper = ((FlatRenderHelper) argHelper);
		
		maskStartUniform.set(helper.getStartX(), helper.getStartY());
		maskEndUniform.set(helper.getEndX(), helper.getEndY());
	}

}
