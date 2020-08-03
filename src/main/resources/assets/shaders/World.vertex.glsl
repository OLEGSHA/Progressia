#version 140

attribute vec3 inputNormals;
varying vec3 varyingNormals;

uniform mat4 worldTransform;

void worldTransferToFragment() {
	shapeTransferToFragment();

	mat3 worldRotation = mat3(worldTransform);
	varyingNormals = normalize(worldRotation * inputNormals);
}