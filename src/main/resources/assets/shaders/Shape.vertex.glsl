#version 120

attribute vec3 inputPositions;

attribute vec3 inputColorMultiplier;
varying vec3 varyingColorMultiplier;

attribute vec2 inputTextureCoords;
varying vec2 varyingTextureCoords;

attribute vec3 inputNormals;
varying vec3 varyingNormals;

uniform mat4 worldTransform;
uniform mat4 finalTransform;

vec4 applyFinalTransform(vec4 vector) {
	return finalTransform * vector;
}

void transferToFragment() {
	varyingColorMultiplier = inputColorMultiplier;
	varyingTextureCoords = inputTextureCoords;
	
	mat3 worldRotation = mat3(worldTransform);
	varyingNormals = normalize(worldRotation * inputNormals);
}