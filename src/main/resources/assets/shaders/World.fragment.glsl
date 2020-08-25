#version 120

varying vec3 varyingNormals;

void applyShading() {
	vec3 light = normalize(vec3(0.5, -0.2, 1.0));
	vec3 normal = varyingNormals;

	float angleCos = dot(normal, light);
	float lightness = (angleCos + 1.5) / 2;

	linearMultiply(gl_FragColor, vec4(lightness, lightness, lightness, 1.0));
}
