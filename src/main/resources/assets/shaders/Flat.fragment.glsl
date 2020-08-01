#version 120

uniform ivec2 maskStart;
uniform ivec2 maskEnd;

void applyMask() {
	if (
			gl_FragCoord.x < maskStart.x || gl_FragCoord.x >= maskEnd.x ||
			gl_FragCoord.y < maskStart.y || gl_FragCoord.y >= maskEnd.y
	) {
		discard;
	}
}