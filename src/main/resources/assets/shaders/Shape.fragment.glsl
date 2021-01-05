#version 120

uniform vec4 uniformColorMultiplier;

varying vec4 varyingColorMultiplier;
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
	gl_FragColor *= varyingColorMultiplier;
	gl_FragColor *= uniformColorMultiplier;
}

void applyAlpha() {
	if (gl_FragColor.w < (1 / 256.0)) {
		discard;
	}
}
