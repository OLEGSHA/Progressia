#version 120

void main(void) {
	gl_Position = applyFinalTransform(vec4(inputPositions, 1.0));
	transferToFragment();
}