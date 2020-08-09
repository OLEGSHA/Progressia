#version 120

uniform int maskCount;

uniform vec2 masks[3 * 2 * 16];

mat2 inverse(mat2 matrix) {
	mat2 result = mat2(
		+matrix[1][1], -matrix[1][0],
		-matrix[0][1], +matrix[0][0]
	);
	
	float det = matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
	
	return result / det;
}

bool isInMaskPrimitive(int primitive) {
	vec2 origin = masks[3 * primitive + 0];
	vec2 width  = masks[3 * primitive + 1];
	vec2 height = masks[3 * primitive + 2];
	
	vec2 current = gl_FragCoord.xy - origin;
	
	mat2 matrix = mat2(width, height);
	vec2 relative = inverse(matrix) * current;
	
	return relative.x >= 0 && relative.y >= 0 && relative.x + relative.y <= 1;
}

void applyMask() {
	for (int i = 0; i < maskCount; ++i) {
		if (!(isInMaskPrimitive(2 * i + 0) || isInMaskPrimitive(2 * i + 1))) {
			discard;
		}
	}
}
