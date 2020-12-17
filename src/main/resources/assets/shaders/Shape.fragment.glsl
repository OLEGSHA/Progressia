#version 120

varying vec3 varyingColorMultiplier;
varying vec2 varyingTextureCoords;

uniform sampler2D textureSlot;

uniform bool useTexture;

void applyTexture() {
	if (!useTexture) {
		gl_FragColor = vec4(1, 1, 1, 1);
	} else {
		gl_FragColor = texture2D(textureSlot, varyingTextureCoords);
	}
}

void applyColorMultiplier() {
	gl_FragColor *= vec4(varyingColorMultiplier, 1.0);
}

void applyAlpha() {
	if (gl_FragColor.w < 0.01) {
		discard;
	}
}
