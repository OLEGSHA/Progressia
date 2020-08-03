#version 140

varying vec3 varyingColorMultiplier;
varying vec2 varyingTextureCoords;

uniform sampler2D textureSlot;
uniform vec2 textureStart;
uniform vec2 textureSize;

uniform bool useTexture;

void applyTexture() {
	if (!useTexture) {
		gl_FragColor = vec4(1, 1, 1, 1);
	} else {
		gl_FragColor = texture2D(
				textureSlot,
				vec2(
					varyingTextureCoords[0] * textureSize[0] + textureStart[0],
					varyingTextureCoords[1] * textureSize[1] + textureStart[1]
				)
		);
	}
}

void multiply(inout vec4 vector, float scalar) {
	vector.x *= scalar;
	vector.y *= scalar;
	vector.z *= scalar;
	vector.w *= scalar;
}

void linearMultiply(inout vec4 vector, vec4 scalars) {
	vector.x *= scalars.x;
	vector.y *= scalars.y;
	vector.z *= scalars.z;
	vector.w *= scalars.w;
}

void applyColorMultiplier() {
	linearMultiply(gl_FragColor, vec4(varyingColorMultiplier, 1.0));
}

void applyAlpha() {
	if (gl_FragColor.w < 0.01) {
		discard;
	}
}