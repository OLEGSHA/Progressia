#version 120

attribute vec3 inputPositions;

attribute vec4 inputColorMultiplier;
varying vec4 varyingColorMultiplier;

attribute vec2 inputTextureCoords;
varying vec2 varyingTextureCoords;

uniform mat4 finalTransform;

vec4 applyFinalTransform(vec4 vector) {
	return finalTransform * vector;
}

void shapeTransferToFragment() {
	varyingColorMultiplier = inputColorMultiplier;
	varyingTextureCoords = inputTextureCoords;
}
